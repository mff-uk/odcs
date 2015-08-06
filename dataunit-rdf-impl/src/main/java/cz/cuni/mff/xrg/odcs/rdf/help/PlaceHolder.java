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
package cz.cuni.mff.xrg.odcs.rdf.help;

/**
 * Class responsible for keeping mapping between DPU name a graph name for this
 * DPU. Until the graph name for DPU is not set is used TEMP graph name
 * generated for DPU name.
 * 
 * @author Jiri Tomes
 */
public class PlaceHolder {

    private String DPUName;

    private String graphName;

    /**
     * Create new instance of {@link PlaceHolder} for given DPU name.
     * 
     * @param DPUName
     *            string value of DPU name.
     */
    public PlaceHolder(String DPUName) {
        this.DPUName = DPUName;
        setTempGraphName();
    }

    private void setTempGraphName() {
        graphName = "http://graphForDataUnit_" + DPUName;
    }

    /**
     * Set Graph mapping to DPU.
     * 
     * @param graphName
     *            URI representation of graph for DPU defined by DPU name.
     */
    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    /**
     * Returns string value of DPU name.
     * 
     * @return DPU name.
     */
    public String getDPUName() {
        return DPUName;
    }

    /**
     * Returns name of graph for DPU. If graph has not been set, the
     * tempGraphName is returned.
     * 
     * @return String value of URI representation of graph set for this DPU
     *         name.
     */
    public String getGraphName() {
        return graphName;
    }
}
