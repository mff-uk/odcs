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
package cz.cuni.mff.xrg.odcs.backend.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Filter log events base on MDC values. The filter test if there is
 * value with given key in MDC. If not, the message is DENY. Otherwise
 * return NEUTRAL.
 * 
 * @author Petyr
 */
public class MdcFilter extends ch.qos.logback.core.filter.Filter<ILoggingEvent> {

    /**
     * Required MDC key.
     */
    private String requiredKey;

    public MdcFilter() {
        this.requiredKey = "";
    }

    public MdcFilter(String requiredKey) {
        this.requiredKey = requiredKey;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMDCPropertyMap().containsKey(requiredKey)) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }

    public void setRequiredKey(String requiredKey) {
        this.requiredKey = requiredKey;
    }

}
