package eu.unifiedviews.master.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import eu.unifiedviews.master.model.PipelineScheduleDTO;

public class ScheduleDTOConverter {
    private static final String DATE_FORMAT = "yyyyMMdd'T'HH:mm:ss.SSSZ";

    private static final DateFormat df = new SimpleDateFormat(DATE_FORMAT);

    public static PipelineScheduleDTO convertToDTO(Schedule schedule) {
        PipelineScheduleDTO dto = null;
        if (schedule != null) {
            dto = new PipelineScheduleDTO();
            dto.setId(schedule.getId());
            dto.setDescription(schedule.getDescription());
            dto.setEnabled(schedule.isEnabled());
            if (schedule.getFirstExecution() != null) {
                dto.setFirstExecution(df.format(schedule.getFirstExecution()));
            } else {
                dto.setFirstExecution("");
            }
            dto.setJustOnce(schedule.isJustOnce());
            if (schedule.getLastExecution() != null) {
                dto.setLastExecution(df.format(schedule.getLastExecution()));
            } else {
                dto.setLastExecution("");
            }
            dto.setPeriod(schedule.getPeriod());
            dto.setPeriodUnit(schedule.getPeriodUnit());
            dto.setScheduleType(schedule.getType());
            Set<Pipeline> pipelines = schedule.getAfterPipelines();
            List<Long> pipelineIds = new ArrayList<Long>();
            if (pipelines != null && pipelines.size() > 0) {
                for (Pipeline pipeline : pipelines) {
                    pipelineIds.add(pipeline.getId());
                }
            }
            dto.setAfterPipelines(pipelineIds);
        }
        return dto;
    }

    public static List<PipelineScheduleDTO> convertToDTOs(List<Schedule> schedules) {
        List<PipelineScheduleDTO> dtos = null;
        if (schedules != null) {
            dtos = new ArrayList<PipelineScheduleDTO>();
            for (Schedule schedule : schedules) {
                PipelineScheduleDTO dto = convertToDTO(schedule);
                if (dto != null) {
                    dtos.add(dto);
                }

            }
        }
        return dtos;
    }

    public static Schedule convertFromDTO(PipelineScheduleDTO dto, Schedule schedule) {
        schedule.setEnabled(dto.isEnabled());
        return schedule;
    }
}
