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
package cz.cuni.mff.xrg.odcs.frontend.monitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import cz.cuni.mff.xrg.odcs.frontend.i18n.Messages;

/**
 * Simple probe for monitoring purposes
 * When servlet called, database connection is checked and if successful,
 * HTTP OK status is sent together with text message
 */
public class ProbeServlet extends HttpServlet {

    private static final long serialVersionUID = 3380633496546339831L;

    private static final Logger LOG = LoggerFactory.getLogger(ProbeServlet.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, servletConfig.getServletContext());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean isRunning = true;
        Connection conn = null;
        Statement stmnt = null;
        try {
            conn = this.dataSource.getConnection();
            stmnt = conn.createStatement();
            stmnt.execute("SELECT 1");
        } catch (Exception e) {
            LOG.error("Connection to database could not be obtained", e);
            isRunning = false;
        } finally {
            tryCloseDbResources(conn, stmnt);
        }

        if (isRunning) {
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.println(Messages.getString("ProbeServlet.function.ok"));
        } else {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }

    private static void tryCloseDbResources(Connection conn, Statement stmnt) {
        if (stmnt != null) {
            try {
                stmnt.close();
            } catch (Exception e) {
                LOG.warn("Failed to close statement", e);
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                LOG.warn("Failed to close connection", e);
            }
        }
    }

}
