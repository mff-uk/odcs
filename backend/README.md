UnifiedViews backend
==============
Backend is responsible for pipeline execution. 

Configuration file
-------------------
Backend requires loading configuration from properties configuration file.
Sample configuration file is located in `/conf/config.properties` in the root of
the project. Configuration is to be stored in `$HOME/.odcs/config.properties`.

The path to the configuration file can be set by program parameter
`-c path_to_config.properties`. This is obviously unachievable for frontend
web application, where the default path must be used.