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
package cz.cuni.mff.xrg.odcs.commons.app.execution.message;

import eu.unifiedviews.dpu.DPUContext;


/**
 * Types for execution.record.
 * 
 * @author Petyr
 */
public enum MessageRecordType {
    /**
     * Debug information from DPURecord.
     */
    DPU_DEBUG,
    /**
     * Closer unspecified information from DPURecord.
     */
    DPU_INFO,
    /**
     * Warning from DPURecord.
     */
    DPU_WARNING,
    /**
     * Error from DPURecord.
     */
    DPU_ERROR,
    /**
     * Termination request.
     */
    DPU_TERMINATION_REQUEST,
    /**
     * Represent error message from pipeline.
     */
    PIPELINE_ERROR,
    /**
     * Represent information about pipeline execution.
     */
    PIPELINE_INFO;

    /**
     * Convert {@link MessageType} to {@link MessageRecordType}.
     * 
     * @param type
     *            message type
     * @return record type
     */
    public static MessageRecordType fromMessageType(DPUContext.MessageType type) {
        switch (type) {
            case DEBUG:
                return MessageRecordType.DPU_DEBUG;
            case INFO:
                return MessageRecordType.DPU_INFO;
            case WARNING:
                return MessageRecordType.DPU_WARNING;
            case ERROR:
                return MessageRecordType.DPU_ERROR;
            default:
                return MessageRecordType.DPU_INFO;
        }
    }
}
