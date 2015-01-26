package eu.unifiedviews.commons.relational.repository;

import java.sql.Connection;
import java.sql.SQLException;

public interface ManagableRelationalRepository {

    public static final String BASE_DATABASE_NAME = "dataUnitDb";

    public static enum Type {
        IN_MEMORY
    }

    Connection getDatabaseConnection() throws SQLException;

    void release() throws Exception;

}
