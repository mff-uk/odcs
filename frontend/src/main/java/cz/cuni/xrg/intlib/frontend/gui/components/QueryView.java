package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.app.rdf.LocalRDFRepo;
import java.io.File;
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

    public QueryView(DebuggingView parent) {
        this.parent = parent;
        VerticalLayout mainLayout = new VerticalLayout();

        HorizontalLayout topLine = new HorizontalLayout();

        graphSelect = new NativeSelect("Graph:");
        graphSelect.setImmediate(true);
//		graphSelect.addItem("Input Graph");
//		graphSelect.addItem("Output Graph");
        topLine.addComponent(graphSelect);

        Button queryButton = new Button("Query");
        queryButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Map<String, List<String>> data = query();
                IndexedContainer container = buildDataSource(data);
                resultTable.setContainerDataSource(container);
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

    private Map<String, List<String>> query() {
        boolean onInputGraph = graphSelect.getValue().equals("Input Graph");
        String query = queryText.getValue();
        String repoPath = parent.getRepositoryPath(onInputGraph);
        File repoDir = parent.getRepositoryDirectory(onInputGraph);

        if (repoPath == null || repoDir == null) {
            return new HashMap<>();
        }

        Logger logger = LoggerFactory.getLogger(QueryView.class);

        // FileName is from backend LocalRdf.dumpName = "dump_dat.ttl"; .. store somewhere else ?
        logger.debug("Create LocalRDFRepo in directory={} dumpDirname={}", repoDir.toString(), repoPath);

        LocalRDFRepo repository = new LocalRDFRepo(repoDir.getAbsolutePath(), repoPath);
        File dumpFile = new File(repoDir, "dump_dat.ttl");

        try {
            repository.load(dumpFile);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        
        Map<String, List<String>> data = repository.makeQueryOverRepository(query);
        repository.shutDown();
        return data;
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

    public void setGraphs(DpuType type) {
        graphSelect.removeAllItems();
        if (type != DpuType.EXTRACTOR) {
            graphSelect.addItem("Input Graph");
        }
        if (type != DpuType.LOADER) {
            graphSelect.addItem("Output Graph");
        }
    }
}
