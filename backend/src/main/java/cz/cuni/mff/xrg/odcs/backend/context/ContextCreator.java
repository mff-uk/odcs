package cz.cuni.mff.xrg.odcs.backend.context;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import cz.cuni.mff.xrg.odcs.backend.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import eu.unifiedviews.dataunit.DataUnit;

/**
 * Component that is used to create {@link Context} for give {@link DPUInstanceRecord} and {@link ExecutionContextInfo}.
 * If context has some previous data ie. {@link ExecutionContextInfo} is not
 * empty data are not loaded. To load data use {@link ContextRestore}
 *
 * @author Petyr
 */
abstract class ContextCreator {

    /**
     * Factory used to create {@link DataUnit}s.
     */
    @Autowired
    private DataUnitFactory dataUnitFactory;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private AutowireCapableBeanFactory autowireBeanFactory;

    /**
     * Create context for given {@link DPUInstanceRecord} and {@link ExecutionContextInfo}. The context is ready for use. Data from {@link ExecutionContextInfo}
     * are not loaded into context.
     *
     * @param dpuInstance
     * @param contextInfo
     * @param lastSuccExec
     * @return
     */
    public Context createContext(DPUInstanceRecord dpuInstance,
            ExecutionContextInfo contextInfo, Date lastSuccExec) {
        // create empty context
        Context newContext = createPureContext();
        // fill context with data

        newContext.setDPU(dpuInstance);
        newContext.setContextInfo(contextInfo);
        newContext.setLastSuccExec(lastSuccExec);
        newContext.setLocale(new Locale("en", "US"));

        // prepare DataUnitManagers
        final File workingDir = new File(
                appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));

        newContext.setInputsManager(DataUnitManager.createInputManager(
                dpuInstance, dataUnitFactory, contextInfo, workingDir,
                appConfig));

        newContext.setOutputsManager(DataUnitManager.createOutputManager(
                dpuInstance, dataUnitFactory, contextInfo, workingDir,
                appConfig));

        return newContext;
    }

    /**
     * Method for spring that create new {@link Context}.
     *
     * @return
     */
    protected abstract Context createPureContext();

}
