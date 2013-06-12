package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUType;
import cz.cuni.xrg.intlib.commons.app.rdf.LocalRDFRepo;
import cz.cuni.xrg.intlib.commons.data.rdf.InvalidQueryException;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple query view for querying debug data.
 *
 * @author Bogo
 */
public class QueryView extends CustomComponent {

    private DebuggingView parent;
    private TextArea queryText;
    private IntlibPagedTable resultTable;
    private NativeSelect graphSelect;
	
	private final static String IN_GRAPH = "Input Graph";
	private final static String OUT_GRAPH = "Output Graph";
	
	private final static Logger LOG = LoggerFactory.getLogger(QueryView.class);

    public QueryView(DebuggingView parent) {
        this.parent = parent;
        VerticalLayout mainLayout = new VerticalLayout();

        HorizontalLayout topLine = new HorizontalLayout();

        graphSelect = new NativeSelect("Graph:");
        graphSelect.setImmediate(true);
		graphSelect.setNullSelectionAllowed(false);
        topLine.addComponent(graphSelect);

        Button queryButton = new Button("Query");
        queryButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    Map<String, List<String>> data = query();

                    IndexedContainer container = buildDataSource(data);
                    resultTable.setContainerDataSource(container);

                } catch (InvalidQueryException e) {
                    Notification.show("Query Validator",
                            "Query is not valid: "
                            + e.getCause().getMessage(),
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        topLine.addComponent(queryButton);
        topLine.setComponentAlignment(queryButton, Alignment.BOTTOM_RIGHT);
        topLine.setSpacing(true);
        mainLayout.addComponent(topLine);

        queryText = new TextArea("SPARQL Query:");
        queryText.setWidth("100%");
        queryText.setHeight("30%");
        mainLayout.addComponent(queryText);

        //TODO: Change to table - resolve container issue
        resultTable = new IntlibPagedTable();
        resultTable.setWidth("100%");
        resultTable.setHeight("60%");
        mainLayout.addComponent(resultTable);
        mainLayout.addComponent(resultTable.createControls());


        mainLayout.setSizeFull();
        setCompositionRoot(mainLayout);
    }

    private Map<String, List<String>> query() throws InvalidQueryException {
        boolean onInputGraph = graphSelect.getValue().equals("Input Graph");
        String query = queryText.getValue();
        String repoPath = parent.getRepositoryPath(onInputGraph);
        File repoDir = parent.getRepositoryDirectory(onInputGraph);

        if (repoPath == null || repoDir == null) {
            return new HashMap<>();
        }

        // FileName is from backend LocalRdf.dumpName = "dump_dat.ttl"; .. store somewhere else ?
        LOG.debug("Create LocalRDFRepo in directory={} dumpDirname={}", repoDir.toString(), repoPath);

        try (LocalRDFRepo repository = new LocalRDFRepo(repoDir.getAbsolutePath(), repoPath)) {
			File dumpFile = new File(repoDir, "dump_dat.ttl");

			try {
				repository.load(dumpFile);
			} catch (ExtractException e) {
				LOG.error(e.getMessage(), e);
			}

			Map<String, List<String>> data = repository.makeQueryOverRepository(query);
			return data;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    private IndexedContainer buildDataSource(Map<String, List<String>> data) {
        IndexedContainer result = new IndexedContainer();
        if (data.isEmpty()) {
            return result;
        }

        Set<String> columns = data.keySet();

        result.addContainerProperty("#", Integer.class, "");
        for (String column : columns) {
            //		if (p.equals("exeid")==false)
            result.addContainerProperty(column, String.class, "");
        }


        int count = data.get(columns.iterator().next()).size();

        for (int i = 0; i < count; i++) {
            Object num = result.addItem();
            result.getContainerProperty(num, "#").setValue(i);
            for (String column : columns) {
                String value = data.get(column).get(i);
                result.getContainerProperty(num, column).setValue(value);
            }
        }

        return result;
    }

	/**
	 * Populates select box for RDF graph to query.
	 * 
	 * @param type DPU type to query
	 */
    public void setGraphs(DPUType type) {
        graphSelect.removeAllItems();
        if (DPUType.Extractor.equals(type)) {
			graphSelect.addItem(IN_GRAPH);
			graphSelect.select(IN_GRAPH);
		} else if (DPUType.Transformer.equals(type)) {
			graphSelect.addItem(IN_GRAPH);
            graphSelect.addItem(OUT_GRAPH);
			graphSelect.select(OUT_GRAPH);
		} else if (DPUType.Loader.equals(type)) {
            graphSelect.addItem(OUT_GRAPH);
			graphSelect.select(OUT_GRAPH);
		}
	}

}
