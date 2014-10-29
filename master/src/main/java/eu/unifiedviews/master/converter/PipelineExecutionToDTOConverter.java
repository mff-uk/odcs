package eu.unifiedviews.master.converter;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import eu.unifiedviews.master.model.PipelineExecutionDTO;

public class PipelineExecutionToDTOConverter {

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
            dto.setSilentMode(execution.getSilentMode());
            dto.setStop(execution.getStop());
            dto.setLastChange(ConvertUtils.dateToString(execution.getLastChange()));
        }
        return dto;
    }

    public static List<PipelineExecutionDTO> convert(List<PipelineExecution> executions) {
        List<PipelineExecutionDTO> dtos = null;
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
}
