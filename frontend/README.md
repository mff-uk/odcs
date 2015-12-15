UnifiedViews frontend
===============

Web frontend deployable on Apache Tomcat server for managing ODCS application.
Web frontend can run independently from backend, however some functionality may
not work or activities may be postponed until backend is started. Frontend makes
use of global configuration in `$HOME/.odcs/config.properties`.

Developing notes:
-----------------
- To access application instance or data access service use `auxiliaries.App`
  class.
- To redirect on some view (page) use `AppEntry.getNavigator().navigateTo(URL)`
  as URL use value from `gui.ViewNames`.
- Serialization on Tomcat Restart is turned off, because we have non-serializable
  class in session.