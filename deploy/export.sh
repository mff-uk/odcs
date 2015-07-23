#*******************************************************************************
# This file is part of UnifiedViews.
#
# UnifiedViews is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# UnifiedViews is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
#*******************************************************************************
#!/bin/bash
#
# The purpose of this script is to export current state of ODCS application
# into a single file archive.
#
# author: Jan Vojt

script=`readlink -f $0`
basedir="`dirname $script`/.."
config="$HOME/.odcs/config.properties"
target="odcs-export"
warfile="frontend/target/odcleanstore.war"
jarfile="backend/target/backend-*.jar"
shellconf="/tmp/odcs-conf.sh"
dumpv="dbdump"

usage () {
	echo "Usage: export [-p path] [-c path] [-t path] [-v path]"
	echo
	echo "-p	Path to the project root."
	echo "  	By default directory above the script location is used."
	echo
	echo "-c	Path to the backend configuration of the application to be exported."
	echo "  	By default this is the path '$HOME/.odcs/config.properties'"
	echo
	echo "-t	Path to the directory where the export is to be created."
	echo "  	By default this is the current working directory."
	echo
	echo "-v	Command to use as Virtuoso dumping tool."
	echo "  	By default 'dbdump' is used."
	echo
	echo "-h	Help."
}

while getopts hp:c:t:v: opt; do
	case $opt in
		p) basedir="$OPTARG";;
		c) config="$OPTARG";;
		t) target="$OPTARG/odcs-export";;
		v) dumpv="$OPTARG";;
                *) usage; exit;;
	esac
done

echo "Loading application configuration from: '$config' ..."

# Process property file, properties will be declared as system variables.
# Dots in properties will be replaced with underscores in variables.
cat "$config" | awk 'BEGIN {
    FS="=";
    print "# BEGIN PROPERTY PARSING";
    n="";
    v="";
    c=0; # Not a line continuation.
}
/^\#/ { # The line is a comment.  Breaks line continuation.
    c=0;
    next;
}
/\\$/ && (c==0) && (NF>=2) { # Name value pair with a line continuation...
    e=index($0,"=");
    n=substr($0,1,e-1);
    v=substr($0,e+1,length($0) - e - 1);    # Trim off the backslash.
    c=1;                                    # Line continuation mode.
    next;
}
/^[^\\]+\\$/ && (c==1) { # Line continuation.  Accumulate the value.
    v= "" v substr($0,1,length($0)-1);
    next;
}
((c==1) || (NF>=2)) && !/^[^\\]+\\$/ { # End of line continuation, or a single line name/value pair
    if (c==0) {  # Single line name/value pair
        e=index($0,"=");
    n=substr($0,1,e-1);
        v=substr($0,e+1,length($0) - e);
    } else { # Line continuation mode - last line of the value.
        c=0; # Turn off line continuation mode.
        v= "" v $0;
    }
    # Replace whitespace at the end of variable name
    gsub(/\s+$/,"",n);
    # Make sure the name is a legal shell variable name
    gsub(/[^A-Za-z0-9_]/,"_",n);
    # Remove whitespaces from the beginning of value
    gsub(/^\s+/,"",v);
    # Remove newlines from the value.
    gsub(/[\n\r]/,"",v);
    print n "=\"" v "\"";
    n = "";
    v = "";
}
END {
    print "# END OF PROPERTY PARSING";
}' > "$shellconf"

# declare config variables
. "$shellconf"
rm "$shellconf"

# check wheter target directory exists, if so ask user to allow deletion
if [ -d "$target" ]; then
	echo -n "Target directory '$target' exists, do you wish to delete it? (y/[n]): "
	read x
	if [ "$x" == "y" ]; then
		echo "Deleting directory '$target' ..."
		rm -rf "$target"
	fi
fi

# make sure the target directory does not exist
if [ -d "$target" ]; then
	echo "Target directory exists -> ABORTING."
	exit
fi

# check whether target archive with exported data already exists
if [ -f "$target.tar.gz" ]; then
        echo "Target archive '$target.tar.gz' already exists,"
	echo -n "do you wish to overwrite it? (y/[n]): "
        read x
        if [ "$x" != "y" ]; then
                echo "Target archive '$target.tar.gz' already exists -> ABORTING"
		exit
        fi
fi


# create target directory
mkdir -p "$target"
chmod 0755 "$target"

# check once more target directory was created to avoid permission issues
if [ ! -d "$target" ]; then
	echo "Target directory '$target' could not be created -> ABORTING."
	exit
fi

echo "Exporting application configuration from '$config' ..."
cp "$config" "$target"

echo "Exporting war file located at '$basedir/$warfile' ..."
cp "$basedir/$warfile" "$target"

echo "Exporting backend JAR file located at '$basedir/$jarfile' ..."
# no quotes for backend JAR, so we can use asterist for version
cp $basedir/$jarfile "$target"

echo "Exporting DPU modules and libraries located at '$module_path' ..."
cp -r "$module_path/." "$target/target"
cp -r "$general_workingdir/." "$target/workingdir"

# Copy database schema according to configured platform
echo "Exporting database schema located at '$basedir/db/$database_sql_platform/rdbms/schema.sql' ..."
cp "$basedir/db/$database_sql_platform/rdbms/schema.sql" "$target"

# Dump Virtuoso database data
if [ "$database_sql_platform" == "virtuoso" ]; then

	dbdatafile="$target/data.sql"
	echo "Dumping data in Virtuoso database into '$dbdatafile' ..."

	# dump data in relational database for ODCS
	echo "fk_check_input_values(0);" > "$dbdatafile"
	$dumpv "${database_sql_hostname}:${database_sql_port}" \
		"$database_sql_user" "$database_sql_password" \
		tablename=db.odcs.% -c \
        	| sed 's/^EXIT;$//' >> "$dbdatafile"
	echo "fk_check_input_values(1);" >> "$dbdatafile"

	# generate sequence setters
	cat "${target}/schema.sql" | egrep "^(CREATE TABLE|sequence_set)" \
        	| awk  '/^sequence_set/ { seq1=$1; seq2=$3;}
                	/^CREATE TABLE/ { if(seq1) print seq1, "(SELECT COALESCE(MAX(id)+1,1) FROM "$3"),", seq2; seq1="";}' \
	        >> "$dbdatafile"
fi

# Dump MySQL database data
if [ "$database_sql_platform" == "mysql" ]; then

	dbdatafile="$target/data.sql"
	echo "Dumping data in MySQL database into '${target}/data.sql' ..."

	# MySQL uses charecter set names without dash
	# -> make sure it is not there
	mysql_charset=`echo "$database_sql_charset" | sed 's/-//g'`
	echo $mysql_charset

	# dump data
	echo "SET NAMES '${mysql_charset}';" > "${target}/data.sql"
	mysqldump --skip-triggers --no-create-info \
		-h"$database_sql_hostname" -u"$database_sql_user" \
		-p"$database_sql_password" "$database_sql_dbname" >> "${target}/data.sql"
fi

echo "Compressing exported data into '$target.tar.gz' ..."
tar -czf "$target.tar.gz" "$target"

rm -rf "$target"

echo "Export FINISHED."
