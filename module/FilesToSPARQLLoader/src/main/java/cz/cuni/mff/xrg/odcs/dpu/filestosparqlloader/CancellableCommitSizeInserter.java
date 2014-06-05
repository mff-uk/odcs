package cz.cuni.mff.xrg.odcs.dpu.filestosparqlloader;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFCancelException;

public class CancellableCommitSizeInserter extends RDFInserter {

    private int commitSize = 50000;

    private boolean transactionOpen = false;

    private int statementCounter = 0;
    
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
