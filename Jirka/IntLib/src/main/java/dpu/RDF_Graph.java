package dpu;

/**
 *
 * @author Jiri Tomes
 */
public class RDF_Graph {

    private String graphName;
    private String userName;
    private String host;
    private String password;
    private int port;

    public RDF_Graph(String graphName, int port) {
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
