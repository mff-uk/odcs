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
package cz.cuni.mff.xrg.odcs.backend.context;

/**
 * Close and save the {@link Context} does not delete the data so {@link Context} can be reconstructed later.
 * 
 * @author Petyr
 */
class ContextCloser {

    /**
     * Closet the given context. The context should not be
     * called after is closed by this method.
     * 
     * @param context
     */
    public void close(Context context) {
        // release data
        context.getInputsManager().release();
        context.getOutputsManager().release();

        // we do not delete any directories or files
    }

}
