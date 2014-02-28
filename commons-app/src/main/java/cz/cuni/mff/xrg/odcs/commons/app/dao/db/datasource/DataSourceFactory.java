package cz.cuni.mff.xrg.odcs.commons.app.dao.db.datasource;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Factory bean choosing correct data source specified by application
 * configuration.
 *
 * @author Jan Vojt
 */
public class DataSourceFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(DataSourceFactory.class);
	
	public static final String MYSQL_VALUE = "mysql";
	public static final String VIRTUOSO_VALUE = "virtuoso";

	@Autowired
	private AppConfig configuration;
	
	private DataSource dataSource;

	public DataSource getDataSource() {
		
		// initialize data source only once (singleton)
		if (dataSource == null) {
			AppConfig rdbmsConf = configuration.getSubConfiguration(ConfigProperty.RDBMS);
			
			String platform = rdbmsConf.getString(ConfigProperty.DATABASE_PLATFORM);
			switch (platform) {
				case VIRTUOSO_VALUE :
					dataSource = new VirtuosoDataSource(rdbmsConf);
					break;
				case MYSQL_VALUE :
					dataSource = new MySQLDataSource(rdbmsConf);
					break;
				default :
					throw new RuntimeException("Unexpected value for database platform found: '"
							+ platform + "'.");
			}
		}
		
		return dataSource;
	}
	
}
