package cz.cuni.xrg.intlib.backend.execution.dpu.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.backend.data.DataUnitFactory;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.backend.execution.dpu.PreExecutor;
import cz.cuni.xrg.intlib.commons.app.dpu.annotation.AnnotationContainer;
import cz.cuni.xrg.intlib.commons.app.dpu.annotation.AnnotationGetter;
import cz.cuni.xrg.intlib.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.ManagableDataUnit;
import cz.cuni.xrg.intlib.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;

/**
 * Examine the given DPU instance for annotations. If there is
 * {@link OutputDataUnit} annotation on field then create or assign suitable
 * DataUnit. If there is no {@link DataUnit} suitable then publish event and
 * return false.
 * 
 * Executed for every state.
 * 
 * @author Petyr
 * 
 */
@Component
public class AnnotationsOutput implements PreExecutor {

	public static final int ORDER = ContextPreparator.ORDER + 1000;
	
	private static final Logger LOG = LoggerFactory
			.getLogger(AnnotationsOutput.class);
	
	/**
	 * DataUnit factory used to create new {@link DataUnit}s.
	 */
	@Autowired
	private DataUnitFactory dataUnitFactory;

	/**
	 * Event publisher used to publish error event.
	 */
	@Autowired
	private ApplicationEventPublisher eventPublish;
	
	@Override
	public int getPreExecutorOrder() {
		return ORDER;
	}

	@Override
	public boolean preAction(Node node,
			Map<Node, Context> contexts,
			Object dpuInstance,
			PipelineExecution execution,
			ProcessingUnitInfo unitInfo) {
		// get current context and DPUInstanceRecord
		Context context = contexts.get(node);

		// OutputDataUnit annotation
		List<AnnotationContainer<OutputDataUnit>> outputAnnotations = AnnotationGetter
				.getAnnotations(dpuInstance, OutputDataUnit.class);
		for (AnnotationContainer<OutputDataUnit> item : outputAnnotations) {
			if (annotationOutput(item, dpuInstance, context)) {
				// ok
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Set value to given field for given instance. In case of error publish
	 * event and return false.
	 * 
	 * @param field Field to set.
	 * @param instance Instance.
	 * @param value Value to set.
	 * @param context Used to publish exception.
	 * @return True if the field has been set.
	 */
	protected boolean setDataUnit(Field field,
			Object instance,
			Object value,
			Context context) {
		try {
			field.set(instance, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// create message
			final String message = "Failed to set value for '"
					+ field.getName() + "' exception: " + e.getMessage();
			eventPublish.publishEvent(DPUEvent.createPreExecutorFailed(context,
					this, message));
			return false;
		}
		return true;
	}
	
	/**
	 * Translate {@link Class} into {@link DataUnitType}.
	 * 
	 * @param classType
	 * @return Null if the class can not be translated.
	 */
	protected DataUnitType classToDataUnitType(Class<?> classType) {
		if (classType == RDFDataUnit.class) {
			return DataUnitType.RDF;
		}
		return null;
	}
	
	/**
	 * Execute the output annotation ie. create output {@link DataUnit} and
	 * assign it to the annotated field. If annotation is null then instantly
	 * return true. If error appear then publish event and return false.
	 * 
	 * @param annotationContainer Annotation container.
	 * @param dpuInstance
	 * @param context
	 * @return False in case of error.
	 * @param annotation
	 * @param dpuInstance
	 * @param context
	 * @return
	 */
	protected boolean annotationOutput(AnnotationContainer<OutputDataUnit> annotationContainer,
			Object dpuInstance,
			Context context) {
		if (annotationContainer == null) {
			return true;
		}
		final Field field = annotationContainer.getField();
		final OutputDataUnit annotation = annotationContainer.getAnnotation();

		// get type
		final DataUnitType type = classToDataUnitType(field.getType());
		if (type == null) {
			final String message = "Unknown type of field: " + field.getName();
			// type cannot be resolved -> publish event
			eventPublish.publishEvent(DPUEvent.createPreExecutorFailed(context,
					this, message));
			return false;
		}
		// let's create dataUnit
		ManagableDataUnit dataUnit = null;
		try {
			dataUnit = context.addOutputDataUnit(type, annotation.name());
		} catch (DataUnitCreateException e) {
			// create message
			final String message = "Failed to create DataUnit for '"
					+ field.getName() + "' exception: " + e.getMessage();
			eventPublish.publishEvent(DPUEvent.createPreExecutorFailed(context,
					this, message));
			return false;
		}
		LOG.debug("Create output DataUnit for field: {}", field.getName());
		// and set it
		return setDataUnit(field, dpuInstance, dataUnit, context);
	}
	
	
}
