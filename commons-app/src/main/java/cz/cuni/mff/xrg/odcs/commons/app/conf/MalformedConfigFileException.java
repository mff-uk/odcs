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
 * Represents syntax error in configuration file.
 * 
 * @author Jan Vojt
 */
public class MalformedConfigFileException extends RuntimeException {

    /**
     * Creates a new instance of <code>MalformedConfigFileException</code> without detail message.
     */
    public MalformedConfigFileException() {
    }

    /**
     * Constructs an instance of <code>MalformedConfigFileException</code> with
     * the specified detail message.
     * 
     * @param msg
     *            the detail message.
     */
    public MalformedConfigFileException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>MalformedConfigFileException</code> with
     * the specified detail message and cause.
     * 
     * @param message
     * @param cause
     */
    public MalformedConfigFileException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an instance of <code>MalformedConfigFileException</code> with
     * the specified cause.
     * 
     * @param cause
     */
    public MalformedConfigFileException(Throwable cause) {
        super(cause);
    }

}
