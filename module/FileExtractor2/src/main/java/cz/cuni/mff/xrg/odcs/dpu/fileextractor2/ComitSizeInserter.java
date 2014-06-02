package cz.cuni.mff.xrg.odcs.dpu.fileextractor2;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;

public class ComitSizeInserter extends RDFInserter {

    private int commitSize = 50000;

    private boolean transactionOpen = false;

    private int statementCounter = 0;

    public ComitSizeInserter(RepositoryConnection con, int commitSize) {
        super(con);
        this.commitSize = commitSize;
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
