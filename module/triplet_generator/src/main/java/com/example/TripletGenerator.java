package com.example;

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.memory.model.MemValueFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.NonConfigurableBase;
import cz.cuni.mff.xrg.odcs.rdf.WritableRDFDataUnit;

// TODO 1: You can choose AsLoader or AsExtractor instead of AsTransformer
@AsExtractor
public class TripletGenerator extends NonConfigurableBase
{

    @OutputDataUnit(name = "output")
    public WritableRDFDataUnit rdfOutput;

    @Override
    public void execute(DPUContext context)
            throws DPUException,
            DataUnitException {
        RepositoryConnection connection = null;
        try {
            connection = rdfOutput.getConnection();
            ValueFactory f = new MemValueFactory();
            connection.begin();
            int j = 1;
            for (int i = 0; i < 3875000; i++) {
                connection.add(f.createStatement(
                        f.createURI("http://example.org/people/d" + String.valueOf(j++)),
                        f.createURI("http://example.org/ontology/e" + String.valueOf(j++)),
                        f.createLiteral("Alice" + String.valueOf(j++))
                        ), rdfOutput.getWriteContext());
                if ((i % 25000) == 0) {
                    connection.commit();
                    context.sendMessage(MessageType.DEBUG, "Number of triples " + String.valueOf(i));
                    if (context.canceled()) {
                        break;
                    }
                    connection.begin();
                }
            }
            connection.commit();
            context.sendMessage(MessageType.DEBUG, "Number of triples " + String.valueOf(connection.size(rdfOutput.getWriteContext())));
        } catch (RepositoryException ex) {
            context.sendMessage(MessageType.ERROR, ex.getMessage(), ex
                    .fillInStackTrace().toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    context.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }
    }

}
