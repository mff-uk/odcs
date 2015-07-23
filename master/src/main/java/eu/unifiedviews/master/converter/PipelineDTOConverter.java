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
import eu.unifiedviews.master.model.PipelineDTO;

public class PipelineDTOConverter {

    public static PipelineDTO convert(Pipeline pipeline) {
        PipelineDTO dto = null;
        if (pipeline != null) {
            dto = new PipelineDTO();
            dto.setId(pipeline.getId());
            dto.setName(pipeline.getName());
            dto.setDescription(pipeline.getDescription());
            if (pipeline.getOwner() != null) {
                dto.setUserExternalId(pipeline.getOwner().getExternalIdentifier());
            } else {
                dto.setUserExternalId(null);
            }
            if (pipeline.getActor() != null) {
                dto.setUserActorExternalId(pipeline.getActor().getExternalId());
            }
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

    public static Pipeline convertFromDTO(PipelineDTO dto, Pipeline pipeline) {
        pipeline.setName(dto.getName());
        pipeline.setDescription(dto.getDescription());
        return pipeline;
    }
}
