package eu.unifiedviews.dataunit.relational;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataUnitDatabaseConnectionProvider {

    Connection getDatabaseConnection() throws SQLException;

    void release() throws Exception;

}
