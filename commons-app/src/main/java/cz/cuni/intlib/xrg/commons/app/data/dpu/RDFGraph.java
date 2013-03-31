package cz.cuni.intlib.xrg.commons.app.data.dpu;

/**
 * For working with graph contains RDF data.
 *
 * @author Jiri Tomes
 */
public class RDFGraph {

    private String graphName;
    private String userName;
    private String host;
    private String password;
    private int port;

    public RDFGraph(String graphName, int port) {
        this.graphName = graphName;
        this.port = port;
    }

    public void setConnection(String host, String password, String userName) {
        this.host = host;
        this.password = password;
        this.userName = userName;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String newGraphName) {
        graphName = newGraphName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String newHost) {
        host = newHost;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        password = newPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String newUserName) {
        userName = newUserName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int newPort) {
        port = newPort;
    }
}
