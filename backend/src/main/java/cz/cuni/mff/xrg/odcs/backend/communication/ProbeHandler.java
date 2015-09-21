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
package cz.cuni.mff.xrg.odcs.backend.communication;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.backend.i18n.Messages;
import eu.unifiedviews.commons.util.DbPropertiesTableUtils;

/**
 * ProbeHandler for {@link EmbeddedHttpServer}
 * Checks database access (SELECT, INSERT, DELETE) and returns HTTP OK and predefined string
 */
public class ProbeHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ProbeHandler.class);

    @Autowired(required = true)
    private DbPropertiesTableUtils dbUtils;

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean isDbRunning = true;
        try {
            isDbRunning = this.dbUtils.trySelectInsertDeleteInDb();
        } catch (Exception e) {
            LOG.error("Exception occured during testing connection to database", e);
            isDbRunning = false;
        }

        if (isDbRunning) {
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.println(Messages.getString("ProbeHandler.function.ok"));
        } else {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        baseRequest.setHandled(true);

    }

}
