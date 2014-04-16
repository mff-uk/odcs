#!/bin/bash
#
# Script to build bundles from DPU's provided dependencies. This script 
# is part of ODCleanStore project https://github.com/mff-uk/ODCS 
#
# @author Petr Škoda 
#
# Use command: Set-ExecutionPolicy unrestricted 
# in administration power shell console to enable this script execution 
# the Set-ExecutionPolicy Restricted is called automatically at the end of the script
#
# This script should be executed in project directory where pom.xml is located

function generatePom() { # [String]$pomFile, [String]$jarName, [String]$jarVersion

echo '<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>cz.cuni.mff.xrg.odcs.bundles</groupId>
' > $1
echo "	<artifactId>$2</artifactId>" >> $1
echo "	<version>$3</version>" >> $1
	
echo '    <packaging>bundle</packaging>
    <description>ODCS bundle maven plugin</description>
	
    <dependencies>
        <dependency>
            <groupId>cz.cuni.mff.xrg.odcs.bundles</groupId>
            <artifactId>${project.artifactId}</artifactId>            
            <version>${project.version}</version>
            <scope>system</scope>
            <systemPath>${basedir}/${project.artifactId}-${project.version}.jar</systemPath>
        </dependency>
    </dependencies>

    <build>
        <finalName>${project.artifactId}-${project.version}-bundle</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.felix</groupId>
                <artifactId>maven-bundle-plugin</artifactId>
                <version>2.3.7</version>
                <extensions>true</extensions>
                <configuration>
                    <instructions>
                        <Bundle-SymbolicName>${project.artifactId}-bundle</Bundle-SymbolicName>
						<Export-Package>*</Export-Package>
						<Import-Package>*</Import-Package>
                        <Embed-StripGroup>true</Embed-StripGroup>
                    </instructions>
                </configuration>                
            </plugin>

		  <plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-dependency-plugin</artifactId>
			<version>2.8</version>
			<executions>
			  <execution>
				<id>src-dependencies</id>
				<phase>package</phase>
				<goals>
				  <goal>unpack-dependencies</goal>
				</goals>
				<configuration>
				  <classifier>sources</classifier>
				  <failOnMissingClassifierArtifact>false</failOnMissingClassifierArtifact>
				  <outputDirectory>${project.build.directory}/sources</outputDirectory>
				</configuration>
			  </execution>
			</executions>
		  </plugin>
		
        </plugins>
    </build>
                        
</project>
' >> $1	
}

# we first need to download all the dependencies, but we do not want the transitive
# dependencies from odcs here .. so we modify the pom.xml a little

echo -n "Downloading dependencies ... "

cat pom.xml | sed -e '/<parent>/d' -e '/<artifactId>module-base<\/artifactId>/d' -e '/<\/parent>/d' -e 's/<packaging>bundle<\/packaging>/<packaging>jar<\/packaging>/' -e '/<artifactId>module-base<\/artifactId>/d' > "super-pom.xml"

# we use special pom.xml
# we also use scope filtering see http://maven.apache.org/plugins/maven-dependency-plugin/copy-dependencies-mojo.html
# for more details .. in simple: compile scope gives compile, provided, and system dependencies,
# !!! the pom.xml scope is not maven scope .. for example test = all

mvn dependency:copy-dependencies -f super-pom.xml -DincludeScope=compile $2>/dev/null

# and remove our super pom.xml
rm -f "super-pom.xml"

echo "done"

# check if we have downloaded something
if [ -d "./target/dependency/" ] ; then
	# we have some data
	:
else
	echo -e "\e[31No dependencies have been downloaded ..\e[0m"
fi

################################################################
# We downloaded the libs and now lets create some OSGI bundles #
################################################################

# used variables
workDirName="/bundle-script/"
workDir="$(pwd)$workDirName"
outDir="$(pwd)/libs/"

# remove directories if it does exist before
if [ -d "$workDir" ] ; then
	rm -rf "$workDir"
fi
# clean output directory
if [ -d "$outDir" ] ; then
	echo "Cleaning output directory"
	rm -rf "$outDir"
fi
if [ -d "$outDir" ] ; then
	# ok, the directory exist
	:
else
	mkdir "$outDir"
fi

####
# black list .. 
####
blackListFile=("commons-cli-1.2" "commons-jexl-2.1.1" "commons-logging-1.1.1" "cssparser-0.9.5" "jsoup-1.6.3" "sac-1.3" "servlet-api-2.4" "validation-api-1.0.0.GA" "xmlpull-1.1.3.1" "xpp3_min-1.1.4c" "xstream-1.4.4")
# commons-web
# commons-module

blackListBaseName=("slf4j-api" "log4j-over-slf4j" "jul-to-slf4j" "vaadin-client" "vaadin-client-compiled" "vaadin-server" "vaadin-shared" "vaadin-shared-deps" "vaadin-theme-compiler" "vaadin-themes")
# commons
# commons-web vaadin

# help function
function containsElement () {
	local e
	for e in "${@:2}"; do [[ "$e" == "$1" ]] && echo 1 && return; done
	echo 0
}

echo "Creating bundles:"
# for each object = dependency = library
for fileName in ./target/dependency/*.jar ; do
	baseName=$(echo "$fileName" | sed -e 's/\.\/target\/dependency\///' -e 's/\(.*\)-[^-]*/\1/' )
	version=$(echo "$fileName" | sed 's/.*-\([^-]*\).jar/\1/' )

	if [ $(containsElement "$baseName" "${blackListFile[@]}") = 1 ]; then
		echo -e  "\e[93m		the item '$baseName' ignored as it's on black list (probably provided by ODCS)\e[0m"
		continue
	fi
	
	if [ $(containsElement "$baseName" "${blackListBaseName[@]}") = 1 ]; then
		echo -e  "\e[93m		the item '$baseName' ignored as it's on black list (probably provided by ODCS)\e[0m"
		continue
	fi

	# check version .. it should contains dots ..
	if [ "${version/.}" = "$version" ]; then
		echo -e "\e[31m		unrecognized version '$version' for '$baseName' the dependency is ignored\e[0m"
		continue
	fi
	
	# create working directory
	if [ -d "$workDir" ] ; then
		:
	else
		mkdir "$workDir"
	fi
	# copy the dependency
	cp "$fileName" "$workDir/"
	
	# create pom.xml file	
	pomFile="$workDir/pom.xml"
	generatePom "$pomFile" "$baseName" "$version"
	
	# enter directory end run maven
	cd "$workDir"
	
	# unpack, so we can explore .. 
	mvn dependency:unpack-dependencies $2>/dev/null
	# now .. we have to read the manifest.mf
	manifestRelativePath="./target/dependency/META-INF/MANIFEST.MF"	
		
	if grep -Fxq "Bundle-" "$manifestRelativePath"; then
		# the dependency is already a bundle ..
		echo -e  "\e[93m		dependency '$baseName' is already bundle,\e[0m"
		echo -e  "\e[93m		the transitive dependencies may not be resolved!\e[0m"
		# copy to the output
		cp *.jar "$outDir"
		# leave the location
		cd ..
		# delete working directory
		rm -rf $workDir
		continue
	fi
		
	# otherwise create a bundle	
	mvn install $2>/dev/null
	cd target
	
	# copy the output jar file
	cp *.jar "$outDir"
	
	cd ../..
	# delete directory
	rm -rf "$workDir"
	
done

# delete directory with downloaded dependencies
rm -rf "$workDir/../target/dependency"

echo "We are done here"
