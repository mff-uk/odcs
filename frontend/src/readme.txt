Configuration steps:
1) set credentials to Tomcat server in pom.xml
2) set connection string, user name and password in main/webapp/WEB-INF/classes/META-INF/persistence.xml

- To install frontend, all dependency must be installed first.
  So project commons, commons-app, commons-module must all have been installed before installing frontend.

- To deploy application on Apache Tomcat use maven:deploy command.
  Or by full name org.apache.tomcat.maven:tomcat7-maven-plugin:2.0:deploy

Developing notes:
- to access application instance or data access service use auxiliaries.App class
- to redirect on some view (page) use AppEntry.getNavigator().navigateTo(ulr .. ) 
  as URL use value from gui.ViewNames.
- serialization on Tomcat Restart is turned off, because we have non-serializable class in session 