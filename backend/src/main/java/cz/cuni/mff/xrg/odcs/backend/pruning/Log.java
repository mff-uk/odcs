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
package cz.cuni.mff.xrg.odcs.backend.pruning;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.DbLogRead;

/**
 * Component for logs pruning based on settings provided in configuration file.
 * 
 * @author Petyr
 */
@Component
class Log {

    @Autowired
    private DbLogRead dbLog;

    @Autowired
    private AppConfig config;

    /**
     * If set then delete (prune) old logs.
     * Spring will run this at every midnight.
     */
    @Async
    @Scheduled(cron = "0 0 0 * * *")
    private void execute() {

        // get user settings
        Integer history = null;
        try {
            history = config.getInteger(ConfigProperty.EXECUTION_LOG_HISTORY);
        } finally {
        }

        if (history == null || history <= -1) {
            // do not prune .. 
            return;
        }

        Calendar pruneLine = Calendar.getInstance();
        pruneLine.setTime(new Date());
        // move to the past
        pruneLine.add(Calendar.DAY_OF_YEAR, -history);

        // call delete
        dbLog.prune(pruneLine.getTime());

    }

}
