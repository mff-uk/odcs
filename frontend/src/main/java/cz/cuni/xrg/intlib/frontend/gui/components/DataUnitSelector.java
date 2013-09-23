package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import java.util.List;
import java.util.Set;

/**
 * Component for selecting from available DPUs and their DataUnits.
 *
 * @author Bogo
 */
public class DataUnitSelector {
	
	private PipelineExecution pipelineExec;
	
	GridLayout mainLayout;
	
	ComboBox dpuSelector;
	private DPUInstanceRecord debugDpu;
	private ExecutionContextInfo ctxReader;
	
	private CheckBox inputDataUnits;
	private CheckBox outputDataUnits;
	private ComboBox dataUnitSelector;
	
	public DataUnitSelector(PipelineExecution execution) {
		pipelineExec = execution;
		buildMainLayout();
	}
	
	private void buildMainLayout() {
		loadExecutionContextReader();
		
		mainLayout = new GridLayout(5, 2);
		dpuSelector = buildDpuSelector();
		mainLayout.addComponent(dpuSelector, 0, 0, 0, 1);
		
		Label dataUnitLabel = new Label("Select Data Unit:");
		mainLayout.addComponent(dataUnitLabel, 1, 0);
		
		inputDataUnits = new CheckBox("Input");
		inputDataUnits.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				refreshDataUnitSelector();
			}
		});
		mainLayout.addComponent(inputDataUnits, 2, 0);
		
		outputDataUnits = new CheckBox("Output");
		outputDataUnits.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				refreshDataUnitSelector();
			}
		});
		mainLayout.addComponent(outputDataUnits, 3, 0);
		
		dataUnitSelector = new ComboBox();
		mainLayout.addComponent(dataUnitSelector, 1, 1, 3, 1);
		
		Button browse = new Button("Browse");
		browse.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
		});
		mainLayout.addComponent(browse, 4, 1);
		
		
	}
	
	    /**
     * Tries to load context for given pipeline execution.
     *
     * @return Load was successful.
     */
    private boolean loadExecutionContextReader() {
        ctxReader = pipelineExec.getContextReadOnly();
        return ctxReader != null;
    }
	
	public void refresh(PipelineExecution exec) {
		pipelineExec = exec;
		if(loadExecutionContextReader()) {
			refreshDpuSelector();
		}
	}
	
	
	/**
     * DPU select box factory.
     */
    private ComboBox buildDpuSelector() {
        dpuSelector = new ComboBox("Select DPU:");
        dpuSelector.setImmediate(true);
        if (ctxReader != null) {
            refreshDpuSelector();
        }
        dpuSelector.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
				
                if (value != null && value.getClass() == DPUInstanceRecord.class) {
                    debugDpu = (DPUInstanceRecord) value;
                } else {
                    debugDpu = null;
                }
                refreshDataUnitSelector();
            }
        });
        return dpuSelector;
    }
	
	/**
     * Fills DPU selector with DPUs for which there are debug information
     * available.
     */
    private void refreshDpuSelector() {
        Set<DPUInstanceRecord> contextDpuIndexes = ctxReader.getDPUIndexes();
        for (DPUInstanceRecord dpu : contextDpuIndexes) {
            if (!dpuSelector.containsId(dpu)) {
                dpuSelector.addItem(dpu);
                if (dpu.equals(debugDpu)) {
                    dpuSelector.select(debugDpu);
                }
            }
        }
    }
	
	private void refreshDataUnitSelector() {
		if(debugDpu == null) {
			dataUnitSelector.removeAllItems();
		}
		List<DataUnitInfo> dataUnits = ctxReader.getDPUInfo(debugDpu).getDataUnits();
		Object selected = dataUnitSelector.getValue();
		for(DataUnitInfo dataUnit : dataUnits) {
			boolean isInput = dataUnit.isInput();
			if((isInput && inputDataUnits.getValue()) || (!isInput && outputDataUnits.getValue())) {
				if(!dataUnitSelector.containsId(dataUnit)) {
					dataUnitSelector.addItem(dataUnit);
				}
			} else {
				if(dataUnitSelector.containsId(dataUnit)) {
					dataUnitSelector.removeItem(dataUnit);
				}
			}
		}
		if(selected != null) {
			dataUnitSelector.setValue(selected);
		}
	}
	
	public DPUInstanceRecord getSelectedDPU() {
		return debugDpu;
	}
	
	public DataUnitInfo getSelectedDataUnit() {
		return (DataUnitInfo)dataUnitSelector.getValue();
	}
	
	public ExecutionContextInfo getContext() {
		return ctxReader;
	}

}
