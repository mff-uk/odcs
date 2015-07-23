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
package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

/**
 * Configuration class for {@link ExportService}.
 * 
 * @author Škoda Petr
 */
public class ExportSetting {

    private boolean exportDPUUserData;

    private boolean exportJars;

    private boolean chbExportSchedule;

    public ExportSetting(boolean exportDPUUserData, boolean exportJars, boolean chbExportSchedule) {
        this.exportDPUUserData = exportDPUUserData;
        this.exportJars = exportJars;
        this.chbExportSchedule = chbExportSchedule;
    }

    public boolean isExportDPUUserData() {
        return exportDPUUserData;
    }

    public void setExportDPUUserData(boolean exportDPUUserData) {
        this.exportDPUUserData = exportDPUUserData;
    }

    public boolean isExportJars() {
        return exportJars;
    }

    public void setExportJars(boolean exportJars) {
        this.exportJars = exportJars;
    }

    public boolean isChbExportSchedule() {
        return chbExportSchedule;
    }

    public void setChbExportSchedule(boolean chbExportSchedule) {
        this.chbExportSchedule = chbExportSchedule;
    }
}
