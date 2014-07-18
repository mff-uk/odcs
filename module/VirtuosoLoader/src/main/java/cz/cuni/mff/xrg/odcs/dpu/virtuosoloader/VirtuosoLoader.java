package cz.cuni.mff.xrg.odcs.dpu.virtuosoloader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import virtuoso.sesame2.driver.VirtuosoRepository;

import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUCancelledException;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;

@DPU.AsLoader
public class VirtuosoLoader extends ConfigurableBase<VirtuosoLoaderConfig> implements ConfigDialogProvider<VirtuosoLoaderConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(VirtuosoLoader.class);

    private static final String LD_DIR = "ld_dir (?, ?, ?)";

    private static final String LD_DIR_ALL = "ld_dir_all (?, ?, ?)";

    private static final String NOW = "select now()";

    private static final String STOP = "rdf_load_stop()";

    private static final String STATUS_COUNT_DONE = "select count(*) from DB.DBA.load_list where ll_file like ? and ll_state = 2";

    private static final String STATUS_COUNT_PROCESSING = "select count(*) from DB.DBA.load_list where ll_file like ? and ll_state <> 2";

    private static final String STATUS_ERROR = "select * from DB.DBA.load_list where ll_file like ? and ll_error IS NOT NULL";

    private static final String DELETE = "delete from DB.DBA.load_list where ll_file like ?";

    private static final String MOVE_QUERY = "DEFINE sql:log-enable 3 MOVE <%s> TO <%s>";

    private static final String ADD_QUERY = "DEFINE sql:log-enable 3 ADD <%s> TO <%s>";

    private static final String CLEAR_QUERY = "DEFINE sql:log-enable 3 CLEAR GRAPH <%s>";

    private static final String RUN = "rdf_loader_run()";

    public VirtuosoLoader() {
        super(VirtuosoLoaderConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: VirtuosoUrl: %s, username: %s, password: %s, loadDirectoryPath: %s, "
                + "includeSubdirectories: %s, targetContext: %s, statusUpdateInterval: %s, threadCount: %s",
                config.getVirtuosoUrl(), config.getUsername(), "***", config.getLoadDirectoryPath(),
                config.isIncludeSubdirectories(), config.getTargetContext(), config.getStatusUpdateInterval(),
                config.getThreadCount());
        LOG.info(shortMessage + " " + longMessage);

        try {
            Class.forName("virtuoso.jdbc4.Driver");
        } catch (ClassNotFoundException ex) {
            throw new DPUException("Error loading driver", ex);
        }

        VirtuosoRepository virtuosoRepository = null;
        RepositoryConnection repositoryConnection = null;
        try {
            virtuosoRepository = new VirtuosoRepository(config.getVirtuosoUrl(), config.getUsername(), config.getPassword());
            virtuosoRepository.initialize();
            repositoryConnection = virtuosoRepository.getConnection();
            if (config.isClearDestinationGraph()) {
                LOG.info("Clearing destination graph");
                Update update = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, String.format(CLEAR_QUERY, config.getTargetContext()));
                update.execute();
                LOG.info("Cleared destination graph");
            }
//            else {
//                LOG.info("Adding loaded data to destination graph");
//                Update update = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, String.format(ADD_QUERY, config.getTargetTempContext(), config.getTargetContext()));    
//                update.execute();
//                LOG.info("Added data to destination graph.");
//                LOG.info("Clearing temporary graph.");
//                Update update2 = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, String.format(CLEAR_QUERY, config.getTargetTempContext()));    
//                update2.execute();
//                LOG.info("Cleared temporary graph.");
//            }
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException ex) {
            throw new DPUException("Error working with Virtuoso using Repository API", ex);
        } finally {
            if (repositoryConnection != null) {
                try {
                    repositoryConnection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error closing repository connection", ex);
                }
            }
            if (virtuosoRepository != null) {
                try {
                    virtuosoRepository.shutDown();
                } catch (RepositoryException ex) {
                    LOG.warn("Error shutdown repository", ex);
                }
            }
        }

        Connection connection = null;
        boolean started = false;
        ExecutorService executor = null;
        try {
            connection = DriverManager.getConnection(config.getVirtuosoUrl(), config.getUsername(), config.getPassword());
            Statement statementNow = connection.createStatement();
            ResultSet resultSetNow = statementNow.executeQuery(NOW);
            resultSetNow.next();
            Timestamp startTimestamp = resultSetNow.getTimestamp(1);
            resultSetNow.close();
            statementNow.close();
            LOG.info("Start time {}", startTimestamp);

            PreparedStatement statementLdDir = connection.prepareStatement(config.isIncludeSubdirectories() ? LD_DIR_ALL : LD_DIR);
            statementLdDir.setString(1, config.getLoadDirectoryPath());
            statementLdDir.setString(2, config.getLoadFilePattern());
//            statementLdDir.setString(3, config.getTargetTempContext());
            statementLdDir.setString(3, config.getTargetContext());
            ResultSet resultSetLdDir = statementLdDir.executeQuery();
            resultSetLdDir.close();
            statementLdDir.close();
            LOG.info("Executed " + (config.isIncludeSubdirectories() ? "LD_DIR_ALL" : "LD_DIR"));

            PreparedStatement statementStatusCountProcessing = connection.prepareStatement(STATUS_COUNT_PROCESSING);
            PreparedStatement statementStatusCountDone = connection.prepareStatement(STATUS_COUNT_DONE);
            statementStatusCountProcessing.setString(1, config.getLoadDirectoryPath() + "%");
            statementStatusCountDone.setString(1, config.getLoadDirectoryPath() + "%");

            ResultSet resultSetProcessing = statementStatusCountProcessing.executeQuery();
            resultSetProcessing.next();
            int all = resultSetProcessing.getInt(1);
            LOG.info("Load list holds {} files to process", all);
            resultSetProcessing.close();
            if (all == 0) {
                LOG.info("Nothing to do. Stopping.");
                return;
            }

            executor = Executors.newFixedThreadPool(config.getThreadCount());
            for (int i = 0; i < config.getThreadCount(); i++) {
                executor.execute(new Runnable() {

                    @Override
                    public void run() {
                        Connection connection = null;
                        try {
                            connection = DriverManager.getConnection(config.getVirtuosoUrl(), config.getUsername(), config.getPassword());
                            Statement statementRun = connection.createStatement();
                            ResultSet resultSetRun = statementRun.executeQuery(RUN);
                            resultSetRun.close();
                            statementRun.close();
                        } catch (SQLException ex) {
                            LOG.error("Error in worker", ex);
                        } finally {
                            if (connection != null) {
                                try {
                                    connection.close();
                                } catch (SQLException ex) {
                                    LOG.warn("Error closing connection", ex);
                                }
                            }
                        }
                    }
                });
            }
            executor.shutdown();
            started = true;
            LOG.info("Started {} load threads", config.getThreadCount());

            int done = 0;
            boolean shouldContinue = !dpuContext.canceled();
            while ((shouldContinue) && (!executor.awaitTermination(config.getStatusUpdateInterval(), TimeUnit.SECONDS))) {
                ResultSet resultSetDoneLoop = statementStatusCountDone.executeQuery();
                resultSetDoneLoop.next();
                done = resultSetDoneLoop.getInt(1);
                resultSetDoneLoop.close();

                LOG.info("Processing {}/{} files", done, all);
                shouldContinue = !dpuContext.canceled();
            }
            LOG.info("Finished all threads");

            ResultSet resultSetDoneLoop = statementStatusCountDone.executeQuery();
            resultSetDoneLoop.next();
            done = resultSetDoneLoop.getInt(1);
            resultSetDoneLoop.close();
            LOG.info("Processed {}/{} files", done, all);

            PreparedStatement statementsErrorRows = connection.prepareStatement(STATUS_ERROR);
            statementsErrorRows.setString(1, config.getLoadDirectoryPath() + "%");
            ResultSet resultSetErrorRows = statementsErrorRows.executeQuery();
            while (resultSetErrorRows.next()) {
                dpuContext.sendMessage(
                        config.isSkipOnError() ? DPUContext.MessageType.WARNING : DPUContext.MessageType.ERROR,
                        "Error processing file " + resultSetErrorRows.getString(1) + ", error " + resultSetErrorRows.getString(8));
            }
            resultSetErrorRows.close();
            statementsErrorRows.close();

            LOG.info("Done.");
        } catch (SQLException ex) {
            throw new DPUException("Error executing query", ex);
        } finally {
            LOG.info("User cancelled.");
            if (connection != null && started) {
                try {
                    Statement stop = connection.createStatement();
                    stop.executeQuery(STOP).close();
                    stop.close();
                } catch (SQLException ex1) {
                    LOG.error("Error executing query", ex1);
                }
            }
            if (executor != null && started) {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        LOG.error("Pool did not terminate");
                    }
                }
            }
            try {
                PreparedStatement delete = connection.prepareStatement(DELETE);
                delete.setString(1, config.getLoadDirectoryPath() + "%");
                delete.executeUpdate();
                delete.close();
                LOG.info("Deleted rows");
            } catch (SQLException ex) {
                LOG.error("Error deleting rows", ex);
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
        }
    }

    @Override
    public AbstractConfigDialog<VirtuosoLoaderConfig> getConfigurationDialog() {
        return new VirtuosoLoaderConfigDialog();
    }

    public static String appendNumber(long number) {
        String value = String.valueOf(number);
        if (value.length() > 1) {
            // Check for special case: 11 - 13 are all "th".
            // So if the second to last digit is 1, it is "th".
            char secondToLastDigit = value.charAt(value.length() - 2);
            if (secondToLastDigit == '1')
                return value + "th";
        }
        char lastDigit = value.charAt(value.length() - 1);
        switch (lastDigit) {
            case '1':
                return value + "st";
            case '2':
                return value + "nd";
            case '3':
                return value + "rd";
            default:
                return value + "th";
        }
    }
}
