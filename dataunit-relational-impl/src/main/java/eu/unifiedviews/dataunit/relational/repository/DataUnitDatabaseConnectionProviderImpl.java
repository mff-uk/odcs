/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.unifiedviews.dataunit.relational.repository;

import java.sql.Connection;
import java.sql.SQLException;

import eu.unifiedviews.dataunit.relational.db.DataUnitDatabaseConnectionProvider;
import eu.unifiedviews.dataunit.relational.db.DatabaseWrapperIF;

/**
 * Implementation of {@link DataUnitDatabaseConnectionProvider) interface which provides the database
 * connections to the underlying relational database to data unit
 */
public class DataUnitDatabaseConnectionProviderImpl implements DataUnitDatabaseConnectionProvider {
    private DatabaseWrapperIF databaseWrapper;

    public DataUnitDatabaseConnectionProviderImpl(DatabaseWrapperIF databaseWrapper) {
        this.databaseWrapper = databaseWrapper;
    }

    @Override
    public Connection getDatabaseConnection() throws SQLException {
        return this.databaseWrapper.getConnection();
    }

    @Override
    public void release() throws Exception {
        this.databaseWrapper.shutdown();
    }

    @Override
    public boolean isActive() {
        return this.databaseWrapper.isActive();
    }

    @Override
    public Connection getDatabaseConnectionForUser(String userName, String password) throws SQLException {
        return this.databaseWrapper.getConnectionForUser(userName, password);
    }

}
