package cz.cuni.mff.xrg.odcs.loader.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

public class Test {

    @org.junit.Test
    public void test() throws Exception {
        FileLoader fileLoader = new FileLoader();
        FileLoaderConfig config = new FileLoaderConfig();
        config.setValidDataBefore(true);
        fileLoader.configureDirectly(config);
        config.setPenetrable(false);
        File tempFile = File.createTempFile("temp", ".rdf");
        String fileUrl = tempFile.toURI().getPath();
        config.setFilePath(fileUrl);

        TestEnvironment env =  new TestEnvironment();
        try {
            RDFDataUnit input = env.createRdfInput("input", false);
            RDFDataUnit output = env.createRdfOutput("input_redirection", false);
            env.createRdfOutput("validationDataUnit", false);
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
        } finally {
            // release resources
            env.release();
            tempFile.delete();
        }
    }

}
