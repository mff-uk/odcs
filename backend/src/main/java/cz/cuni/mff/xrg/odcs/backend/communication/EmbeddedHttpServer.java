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

import javax.annotation.PostConstruct;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.conf.MissingConfigPropertyException;

/**
 * Embedded Jetty HTTP server serving as probe for backend process
 * When called "/probe", database access is checked
 */
public class EmbeddedHttpServer {

    private static final int DEFAULT_PROBE_HTTP_PORT = 8066;

    private int httpPort = DEFAULT_PROBE_HTTP_PORT;

    private Server httpServer;

    @Autowired
    private AppConfig appConfig;

    @Autowired(required = true)
    private ProbeHandler probeHandler;

    private static Logger LOG = LoggerFactory.getLogger(EmbeddedHttpServer.class);

    @PostConstruct
    public void init() {
        try {
            this.httpPort = this.appConfig.getInteger(ConfigProperty.BACKEND_HTTP_PROBE_PORT);
        } catch (MissingConfigPropertyException e) {
            LOG.info("No HTTP probe port found configuration, using default port {}", DEFAULT_PROBE_HTTP_PORT);
        }
        this.httpServer = new Server(this.httpPort);

        ContextHandler context = new ContextHandler();
        context.setContextPath("/probe");
        context.setHandler(this.probeHandler);

        this.httpServer.setHandler(context);

    }

    public void startServer() throws Exception {
        this.httpServer.start();
        this.httpServer.join();
    }

    public void stopServer() throws Exception {
        this.httpServer.stop();
        this.httpServer.destroy();
    }

}
