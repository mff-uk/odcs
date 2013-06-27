package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;

import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;

class Initial extends ViewComponent {

	private AbsoluteLayout mainLayout;

	private Label label;

	public Initial() { }

	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("1024px");
		mainLayout.setHeight("768px");
		
		// top-level component properties
		this.setWidth("100.0%");
		this.setHeight("100.0%");
		
		// label
		label = new Label();
		label.setImmediate(false);
		label.setWidth("-1px");
		label.setHeight("-1px");
		label.setValue("<p>Welcome to the administration interface of the ETL tool for RDF data.</p>" +
				"<p>The tool uses data processing pipelines for obtaining, processing, and storing RDF data;<br/>" +
				"makes data processing highly customizable by employing custom data processing units;<br/>" +
				"provides data processing monitoring, debugging, and schedulling capabilities.<br/>" + 
				"</p>" +
				"<p>Linked Data management tool is a joint effort of <a href=\"http://xrg.ksi.ms.mff.cuni.cz/\">XRG Research Group</a> of Charles University in Prague " +
				"and Semantic Web Company, Vienna. <br/>The tool is build on the idea of <a href=\"http://sourceforge.net/p/odcleanstore/home/Home/\">ODCleanStore</a> " +
				"(developed by Charles University in Prague) and LDM tools (developed by Semantic Web Company, Vienna). <br/>" +
				"For more information and latest version, please visit the <a href=\"https://github.com/mff-uk/intlib\"> website of the tool </a>. </p><p> For description of iteration 1 features and acceptance tests (examples of use), see <a href=\"https://grips.semantic-web.at/display/LOD2/1.+iteration\"> (requires access to confluence) </p>");
						
		label.setContentMode(ContentMode.HTML);
		mainLayout.addComponent(label, "top:40.0px;left:80.0px;");
		
		return mainLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

}
