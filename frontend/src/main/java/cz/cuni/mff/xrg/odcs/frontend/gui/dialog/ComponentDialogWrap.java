package cz.cuni.mff.xrg.odcs.frontend.gui.dialog;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

/**
 * Enables wrapping components into dialogs.
 *
 * @author Škoda Petr
 * @param <C>
 */
public class ComponentDialogWrap<C extends Component> extends Window {

	private final C component;

	private boolean result = false;

	/**
	 * Create resizable modal dialog wrap. The size is taken from the given
 component if the component size is not set then 640x480 is used.
	 *
	 * @param dialogInfo
	 * @param caption
	 */
	public ComponentDialogWrap(C dialogInfo, String caption) {
		super(caption);
		this.component = dialogInfo;
		// setup the dialog
		setResizable(true);
		setModal(true);

		// set the component
		Component content = dialogInfo.getComponent();
		// we need to set size for this dialog so we can use relative size
		// for the component
		if (content.getWidth() < 0.0) {
			// undefined
			setWidth("640px");
		} else {
			setWidth(content.getWidth(), content.getWidthUnits());
		}
		if (content.getHeight() < 0.0) {
			// undefined
			setHeight("480px");
		} else {
			setHeight(content.getHeight(), content.getHeightUnits());
		}
		// set content to fit out size
		content.setWidth("100%");
		content.setHeight("100%");
		setContent(content);
	}

	public C getDialogInfo() {
		return component;
	}

	/**
	 * Close this dialog with given result.
	 *
	 * @param result
	 */
	public void close(boolean result) {
		this.result = result;
		close();
	}

	/**
	 * True it the encapsulated component end as on accept button click.
	 *
	 * @return
	 */
	public boolean isResult() {
		return result;
	}

}
