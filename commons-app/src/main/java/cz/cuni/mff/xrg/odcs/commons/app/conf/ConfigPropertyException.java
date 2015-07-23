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
package cz.cuni.mff.xrg.odcs.commons.app.conf;

/**
 * Represents an error caused property in configuration.
 * 
 * @author Jan Vojt
 */
public abstract class ConfigPropertyException extends RuntimeException {

    /**
     * Name of missing property.
     */
    protected ConfigProperty property;

    /**
     * Constructs an instance of <code>MissingConfigPropertyException</code> with the specified
     * property printed in message.
     * 
     * @param property
     *            name
     */
    public ConfigPropertyException(ConfigProperty property) {
        this.property = property;
    }

    /**
     * @return configuration property, where exception occurred
     */
    public ConfigProperty getProperty() {
        return this.property;
    }

    /**
     * @return error message.
     */
    @Override
    public String getMessage() {
        return "Config is missing property: " + property + ".";
    }

}
