package cz.cuni.mff.xrg.odcs.loader.file;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.openrdf.model.*;
import org.openrdf.repository.RepositoryConnection;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Test {

    @org.junit.Test
    public void test() throws Exception {
        FileLoader fileLoader = new FileLoader();
        FileLoaderConfig config = new FileLoaderConfig();
        fileLoader.configureDirectly(config);
        config.setPenetrable(false);
        File tempFile = File.createTempFile("temp", ".rdf");
        String fileUrl = tempFile.toURI().getPath();
        config.setFilePath(fileUrl);

        TestEnvironment env = TestEnvironment.create();
        try {
            RDFDataUnit input = env.createRdfInput("input", false);
            RDFDataUnit output = env.createRdfOutput("input_redirection", false);
            RepositoryConnection connectionInput = input.getConnection();
            ValueFactory factory = connectionInput.getValueFactory();
            Resource subject = factory.createURI("http://my.subject");
            URI predicate = factory.createURI("http://my.predicate");
            Value object = factory.createLiteral("My company s.r.o. \"HOME\"");
            connectionInput.add(subject, predicate, object, input.getDataGraph());
            connectionInput.commit();
            long expectedSize = connectionInput.size(input.getDataGraph());
            env.run(fileLoader);
            RDFFormat format = Rio.getParserFormatForFileName(tempFile.getName());

            RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
            URL documentUrl = new URL(tempFile.toURI().toURL().toExternalForm());
            InputStream inputStream = documentUrl.openStream();
            org.openrdf.model.Model myGraph = new org.openrdf.model.impl.LinkedHashModel();
            rdfParser.setRDFHandler(new StatementCollector(myGraph));
            Model results = Rio.parse(inputStream, fileUrl, format);
            int actualSize = results.size();

            // we compare amount of the triplets
            assertEquals(expectedSize, actualSize);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // release resources
            env.release();
            tempFile.delete();
        }
    }

}
