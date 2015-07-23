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

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import eu.unifiedviews.master.model.PipelineExecutionDTO;

public class PipelineExecutionDTOConverter {

    public static PipelineExecutionDTO convert(PipelineExecution execution) {
        PipelineExecutionDTO dto = null;
        if (execution != null) {
            dto = new PipelineExecutionDTO();
            dto.setId(execution.getId());
            dto.setStatus(execution.getStatus());
            dto.setDebugging(execution.isDebugging());
            dto.setOrderNumber(execution.getOrderNumber());
            dto.setStart(ConvertUtils.dateToString(execution.getStart()));
            dto.setEnd(ConvertUtils.dateToString(execution.getEnd()));
            if (execution.getSchedule() != null) {
                dto.setSchedule(execution.getSchedule().getId());
            } else {
                dto.setSchedule(null);
            }
            if (execution.getOwner() != null) {
                dto.setUserExternalId(execution.getOwner().getExternalIdentifier());
            } else {
                dto.setUserExternalId(null);
            }
            if (execution.getActor() != null) {
                dto.setUserActorExternalId(execution.getActor().getExternalId());
            }

            dto.setStop(execution.getStop());
            dto.setLastChange(ConvertUtils.dateToString(execution.getLastChange()));
        }
        return dto;
    }

    public static List<PipelineExecutionDTO> convert(List<PipelineExecution> executions) {
        List<PipelineExecutionDTO> dtos = new ArrayList<>();
        if (executions != null) {
            dtos = new ArrayList<PipelineExecutionDTO>();
            for (PipelineExecution execution : executions) {
                PipelineExecutionDTO dto = convert(execution);
                if (dto != null) {
                    dtos.add(dto);
                }

            }
        }
        return dtos;
    }

    public static PipelineExecution createPipelineExecution(PipelineExecutionDTO dto, Pipeline pipeline) {
        PipelineExecution execution = new PipelineExecution(pipeline);
        execution.setDebugging(dto.isDebugging());
        execution.setOrderNumber(1L);
        return execution;
    }

}
