package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;

import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.xpoft.vaadin.VaadinView;

@Component
@Scope("prototype")
@VaadinView(Initial.NAME)
class Initial extends ViewComponent {

	/**
	 * View name.
	 */
	public static final String NAME = "";
	private AbsoluteLayout mainLayout;
	private Label label;

	public Initial() {
	}

	@Override
	public boolean isModified() {
		//There are no editable fields.
		return false;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

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
		label.setValue("<p>Welcome to the administration interface of ODCleanStore - ETL tool for RDF data.</p>"
				+ "<p>The tool uses data processing pipelines for obtaining, processing, and storing RDF data;<br/>"
				+ "makes data processing highly customizable by allowing to use custom plugins -- data processing units (DPUs) -- on the pipelines;<br/>"
				+ "provides monitoring, debugging, and schedulling of ETL tasks.<br/>"
				+ "</p>"
				+ "<p>For more information, please visit the <a href=\"http://www.ksi.mff.cuni.cz/~knap/odcs/\"> website of the tool </a>. </p>");

		label.setContentMode(ContentMode.HTML);
		mainLayout.addComponent(label, "top:40.0px;left:80.0px;");

		return mainLayout;
	}
}
