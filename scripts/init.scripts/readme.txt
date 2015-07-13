The files: unifiedviews-backend, run_unifiedviews_backend, unifiedviews.conf are init.d script for linux.
virtuoso is init.d script for linux. It's just a light version.
The scripts were inspirited by tomcat6 

Please read the configuration files and set the variables according your environment.


Installation steps for unifiedviews-backend:
unifiedviews-backend is needed to be placed into the /etc/init.d/

We suggest to place run_unifiedviews_backend into the folder /usr/sbin/ and unifiedviews.conf into the /etc/unifiedviews/.

The configuration script "unifiedviews.conf" is expected to be in :"/etc/unifiedviews/unifiedviews.conf". If you need to change that, please adjust run_unifiedviews_backend and unifiedviews-backend correspondingly.

cp run_unifiedviews_backend /usr/sbin/
# allow execution
chmod +x /usr/sbin/run_unifiedviews_backend
cp unifiedviews-backend /etc/init.d/
# allow execution
chmod +x /etc/init.d/unifiedviews-backend
mkdir /etc/unifiedviews/
cp unifiedviews.conf /etc/unifiedviews
# if not exists then
mkdir /var/lock/subsys


When somenting goes wrong please have a look at log file: /var/log/unifiedviews-backend-initd.log
Usage: service unifiedviews-backend start|stop|restart|status
