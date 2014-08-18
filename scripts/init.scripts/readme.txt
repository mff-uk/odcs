The files: unifiedviews_backend, run_unifiedviews_backend, unifiedviews.conf are init.d script for linux.
virtuoso is init.d script for linux. It's just a light version.
The scripts were inspirited by tomcat6 

Please read the configuration files and set the variables according your environment.


Install steps for unifiedviews_backend:
unifiedviews_backend is needed to be placed into the /etc/init.d/
We suggest to place run_unifiedviews_backend into the folder /usr/sbin/ and unifiedviews.conf into the /etc/unifiedviews/.


cp run_unifiedviews_backend /usr/sbin/
# allow execution
chmod +x /usr/sbin/run_unifiedviews_backend
cp unifiedviews_backend /etc/init.d/
# allow execution
chmod +x /etc/init.d/unifiedviews_backend
mkdir /etc/unifiedviews/
cp unifiedviews.conf /etc/unifiedviews
# if not exists then
mkdir /var/lock/subsys

