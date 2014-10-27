package sk.eea.xxx.domain;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;

public class PipelineToDTOConverter {

    public static PipelineDTO convert(Pipeline pipeline) {
        PipelineDTO dto = null;
        if (pipeline != null) {
            dto = new PipelineDTO();
            dto.setId(pipeline.getId());
            dto.setName(pipeline.getName());
            dto.setDescription(pipeline.getDescription());
        }
        return dto;
    }

    public static List<PipelineDTO> convert(List<Pipeline> pipelines) {
        List<PipelineDTO> dtos = null;
        if (pipelines != null) {
            dtos = new ArrayList<PipelineDTO>();
            for (Pipeline pipeline : pipelines) {
                PipelineDTO dto = convert(pipeline);
                if (dto != null) {
                    dtos.add(dto);
                }

            }
        }
        return dtos;
    }
}
