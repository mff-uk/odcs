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
 * Represents an error when configuration file cannot be read.
 * 
 * @author Jan Vojt
 */
public class ConfigFileNotFoundException extends RuntimeException {

    /**
     * Creates a new instance of <code>ConfigFileNotFoundException</code> without detail message.
     */
    public ConfigFileNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ConfigFileNotFoundException</code> with
     * the specified detail message.
     * 
     * @param msg
     *            the detail message.
     */
    public ConfigFileNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ConfigFileNotFoundException</code> with
     * the specified detail message and root cause.
     * 
     * @param message
     * @param cause
     */
    public ConfigFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an instance of <code>ConfigFileNotFoundException</code> with
     * the specified root cause.
     * 
     * @param cause
     */
    public ConfigFileNotFoundException(Throwable cause) {
        super(cause);
    }

}
