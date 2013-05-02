/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

/**
 *
 * @author Alex Kreiser
 */
public class PipelineStartedEvent extends PipelineEvent {

    public PipelineStartedEvent(Pipeline pipeline, String runId, Object source) {
        super(pipeline, runId, source);
    }
}
