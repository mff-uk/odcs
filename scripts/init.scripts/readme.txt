The files: odcs_backend, run_odcs_backend, odcs.conf	are connected with odcleanstore.
There are init.d service for linux.
These scripts was inspirited by tomcat6 

odcs_backend is needed to be placed into the /etc/init.d/
I suggest to place run_odcs_backend into the folder /usr/sbin/ and odcs.conf into the /etc/odcs/.

Please read the configuration file and set the variables according your environment.

virtuoso is init.d service for linux. It's just a light version.
