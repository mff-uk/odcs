package cz.cuni.mff.xrg.odcs.dpu.filestosparqlloader;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFCancelException;

public class CancellableCommitSizeInserter extends RDFInserter {
    private static final Logger LOG = LoggerFactory.getLogger(CancellableCommitSizeInserter.class);

    private int commitSize = 50000;

    private boolean transactionOpen = false;

    private int statementCounter = 0;
    
    private long realStatementCounter = 0L;

    private DPUContext dpuContext;

    public CancellableCommitSizeInserter(RepositoryConnection con, int commitSize, DPUContext dpuContext) {
        super(con);
        this.commitSize = commitSize;
        this.dpuContext = dpuContext;
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        if (!transactionOpen) {
            try {
                con.begin();
            } catch (RepositoryException e) {
                throw new RDFHandlerException(e);
            }
            transactionOpen = true;
        }
        super.handleStatement(st);
        statementCounter++;
        if (transactionOpen && (statementCounter == commitSize)) {
            if (dpuContext.canceled()) {
                throw new RDFCancelException("Cancelled by user");
            }
            try {
                con.commit();
                if (LOG.isDebugEnabled()) {
                    realStatementCounter+= statementCounter;
                    LOG.debug("Commit {}", realStatementCounter);
                }
            } catch (RepositoryException e) {
                try {
                    con.rollback();
                } catch (RepositoryException e1) {
                    throw new RDFHandlerException(e1);
                }
                throw new RDFHandlerException(e);
            }
            statementCounter = 0;
            transactionOpen = false;
        }
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        if (transactionOpen) {
            try {
                con.commit();
            } catch (RepositoryException e) {
                try {
                    con.rollback();
                } catch (RepositoryException e1) {
                    throw new RDFHandlerException(e1);
                }
                throw new RDFHandlerException(e);
            }
        }
        super.endRDF();
    }
}
