
package cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas;

import com.vaadin.ui.Component;
import java.util.EventObject;

/**
 * Listener for closing of DPU detail in PipelineEdit.
 *
 * @author Bogo
 */
public interface DetailClosedListener extends Component.Listener {
  void detailClosed(EventObject e);
}

