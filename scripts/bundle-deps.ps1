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

Function generatePom {
	Param ([String]$pomFile, [String]$jarName, [String]$jarVersion)
@'
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>cz.cuni.mff.xrg.odcs.bundles</groupId>
'@ > $pomFile
    "    <artifactId>$jarName</artifactId>" >> $pomFile
    "    <version>$jarVersion</version>" >> $pomFile
@'	
    <packaging>bundle</packaging>
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
'@ >> $pomFile

}

# we first need to download all the dependencies, but we do not want the transitive
# dependencies from odcs here .. so we modify the pom.xml a little

Write-Host -NoNewline "Downloading dependencies ... "

# just remove the parent project ..
(Get-Content "pom.xml") | Foreach-Object {
    $_ -replace '<parent>', '' `
       -replace '<artifactId>module-base</artifactId>', '' `
       -replace '</parent>', '' `
	   -replace '<packaging>bundle</packaging>', '<packaging>jar</packaging>' `
	   -replace '<artifactId>module-base</artifactId>', '' `
    } | Set-Content "super-pom.xml"


# we use special pom.xml
# we also use scope filtering see http://maven.apache.org/plugins/maven-dependency-plugin/copy-dependencies-mojo.html
# for more details .. in simple: compile scope gives compile, provided, and system dependencies,
# !!! the pom.xml scope is not maven scope .. for example test = all
mvn dependency:copy-dependencies -f super-pom.xml -DincludeScope=compile 2>&1 | Out-Null

# and remove our super pom.xml
Remove-Item -Force "super-pom.xml"

Write-Host "done"

# check if we have downloaded something
If (Test-Path "./target/dependency/") {
	# we have some data
} else {
	Write-Host -foregroundcolor "red" "No dependencies have been downloaded .."
	exit
}

################################################################
# We downloaded the libs and now lets create some OSGI bundles #
################################################################

# used variables
$workDirName = "/bundle-script/"
$workDir = (Get-Location).path + $workDirName
$outDir = (Get-Location).path + "/libs/"

# remove directories if it does exist before
If (Test-Path $workDir) {
	Remove-Item -Recurse -Force $workDir
}
# clean output directory
If (Test-Path $outDir) {
	Write-Host "Cleaning output directory"
	Remove-Item -Recurse -Force $outDir
} 
If (Test-Path $outDir) {
	# ok, the directory exist
} else {
	New-item $outDir -itemtype directory | Out-Null
}

####
# black list .. 
####
[array]$blackListFile = @(
	# commons-web
	"commons-cli-1.2"
	,"commons-jexl-2.1.1"
	,"commons-logging-1.1.1"
	,"cssparser-0.9.5"
	,"jsoup-1.6.3"
	,"sac-1.3"
	,"servlet-api-2.4"
	,"validation-api-1.0.0.GA"
	# commons-module
	,"xmlpull-1.1.3.1"
	,"xpp3_min-1.1.4c"
	,"xstream-1.4.4"
	)

[array]$blackListBaseName = @(
	# commons
	"slf4j-api"
	,"log4j-over-slf4j"
	,"jul-to-slf4j"
	# commons-web vaadin
	,"vaadin-client"
	,"vaadin-client-compiled"
	,"vaadin-server"
	,"vaadin-shared"
	,"vaadin-shared-deps"
	,"vaadin-theme-compiler"
	,"vaadin-themes"
	)

Write-Host "Creating bundles:"
# for each object = dependecy = library
Get-ChildItem "./target/dependency" -Filter *.jar | Foreach-Object {
	$delimiterPos = $_.BaseName.lastIndexOf("-")
	[String]$baseName = $_.BaseName.substring(0, $delimiterPos)
	[String]$version = $_.BaseName.substring($delimiterPos + 1)
	
	Write-Host "	$baseName"
	
	# check black list
	if ($blackListFile -contains $_.BaseName) {
		Write-Host -foregroundcolor "magenta" "		the item '$baseName' ignored as it's on black list (probably provided by ODCS)"
		return
	}
	if ($blackListBaseName -contains $baseName) {
		Write-Host -foregroundcolor "magenta" "		the item '$baseName' ignored as it's on black list (probably provided by ODCS)"
		return
	}
	
	# check version .. it should contains dots ..
	if ($version.contains(".")) {
	} else {
		Write-Host -foregroundcolor "red" "		unrecognized version '$version' for '$baseName' the dependency is ignored"
		return
	}
	
	# create working directory
	New-item $workDir -itemtype directory | Out-Null

	# copy the dependency
	Copy-Item $_.FullName $workDir

	# create pom.xml file	
	$pomFile = $workDir + "pom.xml"
	generatePom $pomFile $baseName $version
	
	# enter directory end run maven
	Set-Location ./$workDirName
	
	# unpack, so we can explore .. 
	mvn dependency:unpack-dependencies 2>&1 | Out-Null
	# now .. we have to read the manifest.mf
	$manifestRelativePath = "./target/dependency/META-INF/MANIFEST.MF"
	
	$bundleLines = Select-String -Simple "Bundle-" $manifestRelativePath
	$isBundle = $bundleLines.length -gt 0
	if ($isBundle) {
		# the dependency is already a bundle ..
		Write-host -foregroundcolor "yellow" "		dependency '$baseName' is already bundle,"
		Write-host -foregroundcolor "yellow" "		the transitive dependencies may not be resolved!"
		# copy to the output
		Copy-Item "*.jar" "$outDir"
		# leave the location
		Set-Location ..
		# delete working directory
		Remove-Item -Recurse -Force $workDir
		return
	}

	# otherwise create a bundle	
	mvn install 2>&1 | Out-Null
	Set-Location target
	
		# copy the output jar file
		Copy-Item "*.jar" "$outDir"
	
	Set-Location ../..	
	# delete directory
	Remove-Item -Recurse -Force $workDir
}

# delete directory with downloaded dependencies
Remove-Item -Recurse -Force "$workDir/../target/dependency"

Write-Host "We are done here"

# Set-ExecutionPolicy Restricted



