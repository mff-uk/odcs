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
package cz.cuni.mff.xrg.odcs.commons.app.data;

/**
 * Contains definition of instruction that can be used on edges in the pipeline graph.
 * The grammar for the instruction is:
 * Start -> Element | Element; Start
 * Element -> Name Command // The commands apply on all the DataUnits with given name
 * Command -> rename Name // rename DataUnit
 * 
 * @author Petyr
 */
public enum EdgeInstructions {
    /**
     * Command for rename operation, represents mapping.
     */
    Rename("->"),
    /**
     * No data are transfered, it's just run after edge.
     */
    RunAfter("run_after"),
    /**
     * Represent separation of two commands.
     */
    Separator(";");

    /**
     * The command name.
     */
    private final String command;

    private EdgeInstructions(String command) {
        this.command = command;
    }

    /**
     * @return String representation of instruction.
     */
    public String getValue() {
        return this.command;
    }

}
