The files: unifiedviews_backend, run_unifiedviews_backend, unifiedviews.conf	are connected with odcleanstore.
There are init.d service for linux.
These scripts was inspirited by tomcat6 

unifiedviews_backend is needed to be placed into the /etc/init.d/
I suggest to place run_unifiedviews_backend into the folder /usr/sbin/ and unifiedviews.conf into the /etc/unifiedviews/.

Please read the configuration file and set the variables according your environment.

virtuoso is init.d service for linux. It's just a light version.

install steps for unifiedviews_backend

cp run_unifiedviews_backend /usr/sbin/
chmod +x /usr/sbin/run_unifiedviews_backend
cp unifiedviews_backend /etc/init.d/
mkdir /etc/unifiedviews/
cp unifiedviews.conf /etc/unifiedviews

