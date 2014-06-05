package cz.cuni.mff.xrg.odcs.commons.app.dao.db.datasource;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;

/**
 * Factory bean choosing correct data source specified by application
 * configuration.
 * 
 * @author Jan Vojt
 */
public class DataSourceFactory {

    public static final String MYSQL_VALUE = "mysql";

    public static final String VIRTUOSO_VALUE = "virtuoso";


}
