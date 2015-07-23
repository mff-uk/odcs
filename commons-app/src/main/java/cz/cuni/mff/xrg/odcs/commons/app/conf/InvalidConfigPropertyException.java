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
 * Represents error caused by invalid configuration value for given property.
 * 
 * @author Jan Vojt
 */
public class InvalidConfigPropertyException extends ConfigPropertyException {

    /**
     * Invalid value found in configuration.
     */
    private String value;

    public InvalidConfigPropertyException(ConfigProperty property) {
        this(property, "unknown");
    }

    public InvalidConfigPropertyException(ConfigProperty property, String value) {
        super(property);
        this.value = value;
    }

    /**
     * @return error message.
     */
    @Override
    public String getMessage() {
        return "Config property '" + property + "' has invalid value of '" + value + "'.";
    }

}
