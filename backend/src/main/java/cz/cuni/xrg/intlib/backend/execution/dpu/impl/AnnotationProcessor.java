package cz.cuni.xrg.intlib.backend.execution.dpu.impl;

import java.lang.reflect.Field;
import java.util.LinkedList;
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
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.ManagableDataUnit;
import cz.cuni.xrg.intlib.commons.dpu.annotation.InputDataUnit;
import cz.cuni.xrg.intlib.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;

/**
 * Examine the given DPU instance for annotations. If there is
 * {@link InputDataUnit} or {@link OutputDataUnit} annotation on field then
 * create or assign suitable DataUnit. If there is no {@link DataUnit} suitable
 * then publish event and return false.
 * 
 * @author Petyr
 * 
 */
@Component
public class AnnotationProcessor implements PreExecutor {
	
	/**
	 * Pre-executor order. Will be executed after ContextPreparator.
	 */
	public static final int ORDER = ContextPreparator.ORDER + 20;	
	
	private static final Logger LOG = LoggerFactory
			.getLogger(AnnotationProcessor.class);
	
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
			LOG.error("Failed to set value for DataUnit", e);
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
	 * Filter given list of {@link ManagableDataUnit}s by using {@link Class}.
	 * 
	 * @param candidates
	 * @param type
	 * @return List with {@link ManagableDataUnit} can be empty, can not be
	 *         null.
	 */
	protected LinkedList<ManagableDataUnit> filter(List<ManagableDataUnit> candidates,
			Class<?> type) {
		LinkedList<ManagableDataUnit> result = new LinkedList<ManagableDataUnit>();
		for (ManagableDataUnit item : candidates) {
			if (type.isInstance(item)) {
				result.add(item);
			}
		}
		return result;
	}

	/**
	 * Filter given list of {@link ManagableDataUnit}s according to their URI.
	 * Is case insensitive.
	 * 
	 * @param candidates
	 * @param type
	 * @return List with {@link ManagableDataUnit} can be empty, can not be
	 *         null.
	 */
	protected LinkedList<ManagableDataUnit> filter(List<ManagableDataUnit> candidates,
			String name) {
		LinkedList<ManagableDataUnit> result = new LinkedList<ManagableDataUnit>();
		for (ManagableDataUnit item : candidates) {
			if (item.getURI().compareToIgnoreCase(name) == 0) {
				result.add(item);
			}
		}
		return result;
	}

	/**
	 * Execute the input annotation ie. get input {@link DataUnit} from
	 * {@link Context} and assign it to annotated field. If annotationContainer
	 * is null then instantly return true. If error appear then publish event
	 * and return false.
	 * 
	 * @param annotationContainer Annotation container.
	 * @param dpuInstance
	 * @param context
	 * @return False in case of error.
	 */
	protected boolean annotationInput(AnnotationContainer<InputDataUnit> annotationContainer,
			Object dpuInstance,
			Context context) {
		if (annotationContainer == null) {
			return true;
		}
		final Field field = annotationContainer.getField();
		final InputDataUnit annotation = annotationContainer.getAnnotation();

		LinkedList<ManagableDataUnit> typeMatch = filter(context.getInputs(),
				field.getType());
		if (typeMatch.isEmpty()) {
			// check if not optional
			if (annotation.optional()) {
				return true;
			}
			LOG.error("No input of required type  for field '{}'.",
					field.getName());
			final String message = "No input for field: " + field.getName()
					+ " All inputs have different type.";
			eventPublish.publishEvent(DPUEvent.createPreExecutorFailed(context,
					this, message));
			return false;
		}
		// now we filter by name
		LinkedList<ManagableDataUnit> nameMatch = filter(typeMatch,
				annotation.name());
		if (nameMatch.isEmpty()) {
			if (annotation.relaxed()) {
				LOG.debug("Assign DataUnit names: {} for field: {}",
						annotation.name(), field.getName());
				// just use first with good type
				return setDataUnit(field, dpuInstance, typeMatch.getFirst(),
						context);
			} else {
				// check if not optional
				if (annotation.optional()) {
					return true;
				}
				LOG.error("Missing required DataUnit for field: '{}'",
						field.getName());
				// error
				final String message = "Can't find DataUnit with required name for field:"
						+ field.getName();
				eventPublish.publishEvent(DPUEvent.createPreExecutorFailed(
						context, this, message));
				return false;
			}
		} else {
			LOG.debug("Assign DataUnit URI: {} for field: {}", nameMatch
					.getFirst().getURI(), field.getName());
			// use first with required name
			return setDataUnit(field, dpuInstance, nameMatch.getFirst(),
					context);
		}
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
			LOG.error("Failed to create DataUnit for: '{}'", field.getName(), e);
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

	@Override
	public boolean preAction(Node node,
			Map<Node, Context> contexts,
			Object dpuInstance,
			PipelineExecution execution) {
		// get current context and DPUInstanceRecord
		Context context = contexts.get(node);

		// InputDataUnit annotation
		List<AnnotationContainer<InputDataUnit>> inputAnnotations = AnnotationGetter
				.getAnnotations(dpuInstance, InputDataUnit.class);
		for (AnnotationContainer<InputDataUnit> item : inputAnnotations) {
			if (annotationInput(item, dpuInstance, context)) {
				// ok
			} else {
				return false;
			}
		}
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

	@Override
	public int getOrder() {
		// execute after ContextPreparator
		return ORDER;
	}

}
