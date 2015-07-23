/*******************************************************************************
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
 *******************************************************************************/
/*******************************************************************************
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
 *******************************************************************************/
package eu.unifiedviews.commons.dataunit.core;

import eu.unifiedviews.dataunit.relational.db.DataUnitDatabaseConnectionProvider;

/**
 * @author Škoda Petr
 */
public class CoreServiceBusImpl implements CoreServiceBus {

    private final ConnectionSource connectionSource;

    private final FaultTolerant faultTolerant;

    private DataUnitDatabaseConnectionProvider dataUnitDatabase;

    public CoreServiceBusImpl(ConnectionSource connectionSource, FaultTolerant faultTolerant) {
        this.connectionSource = connectionSource;
        this.faultTolerant = faultTolerant;
    }

    public CoreServiceBusImpl(ConnectionSource connectionSource, FaultTolerant faultTolerant, DataUnitDatabaseConnectionProvider dataUnitDatabase) {
        this.connectionSource = connectionSource;
        this.faultTolerant = faultTolerant;
        this.dataUnitDatabase = dataUnitDatabase;
    }

    @Override
    public <T> T getService(Class<T> serviceClass) throws IllegalArgumentException {
        if (serviceClass.isAssignableFrom(ConnectionSource.class)) {
            return (T) connectionSource;
        } else if (serviceClass.isAssignableFrom(FaultTolerant.class)) {
            return (T) faultTolerant;
        } else if (serviceClass.isAssignableFrom(DataUnitDatabaseConnectionProvider.class)) {
            return (T) this.dataUnitDatabase;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
