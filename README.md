Intlib
======

The tool uses data processing pipelines for obtaining, processing, and storing
RDF data. It makes data processing highly customizable by employing custom data
processing units, also provides data processing monitoring, debugging, and
schedulling capabilities.

Configuration steps
-------------------
1. Set credentials to Tomcat server in `frontend/pom.xml`.
2. Set connection string, user name and password in `main/webapp/WEB-INF/classes/META-INF/persistence.xml`.
3. Create configuration file.
  - Sample configuration file is located in `/conf/config.properties` in the root
    of the project. Configuration is to be stored in `$HOME/.intlib/config.properties`.

Installation steps
------------------
- To deploy application to Apache Tomcat use `maven:deploy` target in project
  `frontend` (or full name `org.apache.tomcat.maven:tomcat7-maven-plugin:2.0:deploy`)
- Run backend application.