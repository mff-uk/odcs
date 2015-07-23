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
package eu.unifiedviews.master.converter;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import eu.unifiedviews.master.model.DPUInstanceDTO;
import eu.unifiedviews.master.model.PipelineExecutionEventDTO;

public class PipelineExecutionEventDTOConverter {

    public static PipelineExecutionEventDTO convert(MessageRecord event) {
        PipelineExecutionEventDTO dto = null;
        if (event != null) {
            dto = new PipelineExecutionEventDTO();
            dto.setId(event.getId());
            dto.setTime(ConvertUtils.dateToString(event.getTime()));
            dto.setType(event.getType());
            dto.setShortMessage(event.getShortMessage());
            dto.setFullMessage(event.getFullMessage());
            if (event.getDpuInstance() != null) {
                DPUInstanceDTO dpuInstance = new DPUInstanceDTO();
                dpuInstance.setId(event.getDpuInstance().getId());
                dpuInstance.setName(event.getDpuInstance().getName());
                dpuInstance.setDescription(event.getDpuInstance().getDescription());
                dpuInstance.setSerializedConfiguration(event.getDpuInstance().getRawConf());
                dto.setDpuInstance(dpuInstance);
            } else {
                dto.setDpuInstance(null);
            }
        }
        return dto;
    }

    public static List<PipelineExecutionEventDTO> convert(List<MessageRecord> events) {
        List<PipelineExecutionEventDTO> dtos = null;
        if (events != null) {
            dtos = new ArrayList<PipelineExecutionEventDTO>();
            for (MessageRecord event : events) {
                PipelineExecutionEventDTO dto = convert(event);
                if (dto != null) {
                    dtos.add(dto);
                }

            }
        }
        return dtos;
    }
}
