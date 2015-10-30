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
package cz.cuni.mff.xrg.odcs.backend.dpu.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;

/**
 * Class for representing DPURecord messages send by ProcessingContext
 * sendMessage.
 * 
 * @author Petyr
 */
public final class DPUMessage extends DPUEvent {

    private static final Logger LOG = LoggerFactory.getLogger(DPUMessage.class);

    public DPUMessage(String shortMessage,
            String longMessage,
            DPUContext.MessageType type,
            Context context,
            Object source) {
        super(context, source, MessageRecordType.fromMessageType(type),
                shortMessage, longMessage);
        // log based on type of message
        switch (type) {
            case DEBUG:
                LOG.debug("DPU '{}' publish message short: '{}' long: '{}'",
                        context.getDPU().getName(),
                        shortMessage,
                        longMessage);
                break;
            case ERROR:
                LOG.error("DPU '{}' publish message short: '{}' long: '{}'",
                        context.getDPU().getName(),
                        shortMessage,
                        longMessage);
                break;
            case INFO:
                LOG.info("DPU '{}' publish message short: '{}' long: '{}'",
                        context.getDPU().getName(),
                        shortMessage,
                        longMessage);
                break;
            case WARNING:
                LOG.warn("DPU '{}' publish message short: '{}' long: '{}'",
                        context.getDPU().getName(),
                        shortMessage,
                        longMessage);
                break;
        }
    }

}
