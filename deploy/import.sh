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
# The purpose of this script is to unpack ODCS application from exported
# archive, import relational database, copy backend configuration to specified
# destination, copy DPU modules, libs and cache to working directory, copy
# backend application to correct destination, optionally deploy frontend
# application on Apache Tomcat, and optionally run backend application.
#
# author: Jan Vojt

script=`readlink -f $0`
basedir="`dirname $script`/.."
config="$HOME/.odcs/config.properties"
target="odcs-export"
warfile="frontend/target/odcleanstore.war"
shellconf="/tmp/odcs-conf.sh"
isqlv="isql"

usage () {
	echo "Usage: export [-p path] [-c path] [-v path] odcs-export"
	echo
	echo "-p	Path to the project root."
	echo "  	By default directory above the script location is used."
	echo
	echo "-c	Path where the backend configuration is to be created."
	echo "  	By default this is the path '$HOME/.odcs/config.properties'"
	echo
	echo "-v	Command to use for Virtuoso isql client."
	echo "  	By default 'isql' is used."
	echo
	echo "-h	Help."
}

while getopts hp:c:t:v: opt; do
        case $opt in
                p) basedir="$OPTARG";;
                c) config="$OPTARG";;
                t) target="$OPTARG/odcs-export";;
		v) isqlv="$OPTARG";;
                *) usage; exit;;
        esac
done

# Process the last argument - path to exported archive.
shift $(($OPTIND - 1))
archive=$1
if [ ! -f "$archive" ]; then
	echo "Given archive file '$archive' cannot be found -> ABORTING."
	exit 1;
fi

# Function for interactively cleaning a directory.
# If directory does not exist it is created.
# If directory exists and is non-empty, user is asked whether he wishes to delete it.
cleandir () {
	if [ -d "$1" -a -z "`ls -A $1`" ]; then
		# directory exists and is empty
		return 0
	elif [ -d "$1" ]; then
		# directory exists and is non-empty -> promt user to delete
		echo -n "Directory '$1' is non-empty, do you want to delete its contents? (y/[n]): "
		read x
		if [ "$x" == "y" ]; then
			rm -rf "$1"
		else
			return 1
		fi
	fi
	mkdir -p "$1"
	return $?
}

echo "Unpacking exported archive from '$archive' ..."
tar -xzf "$archive"


# Check if extraction was successful.
if [ $? -gt 0 ]; then
	usage
	exit
fi

# Check if we got expected directory
unpackeddir="$basedir/odcs-export"
if [ ! -d "$unpackeddir" ]; then
	echo "You provided invalid archive. Please provide a valid ODCS export."
	exit
fi

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


if [ "$database_sql_platform" == "virtuoso" ]; then

	# Check if virtuoso command is executable.
	if [ ! -x "`which $isqlv | head -n 1`" ]; then
		echo "Command '$isqlv' given for isql client is not executable or cannot be found -> ABORTING."
		exit 1
	fi

	echo "Importing Virtuoso database ..."
	cat "${unpackeddir}/schema.sql" "${unpackeddir}/data.sql" | \
	        $isqlv "${database_sql_hostname}:${database_sql_port}" \
			"$database_sql_user" "$database_sql_password"
fi

if [ "$database_sql_platform" == "mysql" ]; then

        # MySQL uses charecter set names without dash
        # -> make sure it is not there
        mysql_charset=`echo "$database_sql_charset" | sed 's/-//g'`
        echo $mysql_charset

	echo "Importing MySQL database ..."
	echo -e "CREATE DATABASE IF NOT EXISTS ${database_sql_dbname} DEFAULT CHARACTER SET ${mysql_charset};"\
		"\nUSE ${database_sql_dbname};"\
		| cat - "${unpackeddir}/schema.sql" "${unpackeddir}/data.sql"\
                | mysql -h"$database_sql_hostname" -u"$database_sql_user" -p"$database_sql_password"
fi


echo "Copying backend working directory ..."
mkdir -p "$general_workingdir"
# Check if directory was created.
if [ -d "$general_workingdir" ]; then
	cp -r "$unpackeddir/workingdir/." "$general_workingdir"
else
	echo "Working directory for backend could not be created at '$general_workingdir' -> SKIPPING."
fi


echo "Copying DPU modules and libraries ..."
cleandir "$module_path"
if [ -d "$module_path" ]; then
	cp -r "$unpackeddir/target/." "$module_path"
else
	echo "Directory for DPU modules and libraries could not be created at '$module_path' -> SKIPPING"
fi

jarfile=`ls $unpackeddir/backend-*.jar | grep -o [^/]*\$`
echo "Copying backend JAR file to '$jarfile' ..."
mkdir -p "$basedir/backend/target"
if [ -d "$basedir/backend/target" ]; then
	cp "$unpackeddir/$jarfile" "$basedir/backend/target"
else
	echo "Directory for backend JAR file could not be created at '$basedir/backend/target' -> SKIPPING."
fi


echo "Copying configuration for backend application to '$config' ..."
configdir=`dirname "$config"`
mkdir -p "$configdir"
if [ -d "$configdir" ]; then
	cp "$unpackeddir/config.properties" "$config"
else
	echo "Directory for backend configuration could not be created at '$configdir' -> SKIPPING."
fi

echo "Copying frontend WAR file to '$basedir/frontend/target/odcleanstore.war' ..."
mkdir -p "$basedir/frontend/target"
if [ -d "$basedir/frontend/target" ]; then
	cp "$unpackeddir/odcleanstore.war" "$basedir/frontend/target/odcleanstore.war"
else
	echo "Directory for frontend WAR file could not be created at '$basedir/frontend/target' -> SKIPPING."
fi


# Deploy frontend to Apache Tomcat.
echo -n "Do you wish to deploy frontend application to Apache Tomcat? ([y]/n): "
read x
if [ "$x" == "" -o "$x" == "y" ]; then

	echo -n "Enter host on which Apache Tomcat resides [localhost]: "
	read tomcat_host
	[ -z "$tomcat_host" ] && tomcat_host="localhost"

	echo -n "Enter port on which Apache Tomcat manager is listening [8080]: "
	read tomcat_port
	[ -z "$tomcat_port" ] && tomcat_port="8080"

	echo -n "Enter username of the manager allowed to deploy applications [manager]: "
	read tomcat_user
	[ -z "$tomcat_user" ] && tomcat_user="manager"

	echo -n "Enter password for user '$tomcat_user' [$tomcat_user]: "
	read tomcat_password
	[ -z "$tomcat_password" ] && tomcat_password="$tomcat_user"

	echo "Uploading application to Apache Tomcat ..."
	curl -T "$unpackeddir/odcleanstore.war"\
		"http://${tomcat_user}:${tomcat_password}@${tomcat_host}:${tomcat_port}/manager/text/deploy?path=/odcleanstore&update=true"

fi

# Clean up.
rm -rf "$basedir/odcs-export"

# Launch backend as a background process
echo -n "Do you want to launch exported backend application in background? ([y]/n): "
read x
if [ -z "$x" -o "$x" == "y" ]; then
	echo "Launching `which java` with JAVA_HOME='$JAVA_HOME' ..."
	java -jar "$basedir/backend/target/$jarfile" &
else
	echo "Import FINISHED."
fi
