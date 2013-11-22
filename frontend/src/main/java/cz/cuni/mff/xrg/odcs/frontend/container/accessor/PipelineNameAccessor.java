package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.ClassAccessorBase;

/**
 * Accessor used for containers that just list the pipelines.
 *
 * @author Petyr
 */
public class PipelineNameAccessor extends ClassAccessorBase<Pipeline> {

	public PipelineNameAccessor() {
		super(Pipeline.class);

		addNon(Long.class, "id", new ColumnGetter<Long>() {
			@Override
			public Long get(Pipeline object) {
				return object.getId();
			}
		});
		
		addNon(String.class, "name", new ColumnGetter<String>() {
			@Override
			public String get(Pipeline object) {
				return object.getName();
			}
		});
		
		addNon(String.class, "description", new ColumnGetter<String>() {
			@Override
			public String get(Pipeline object) {
				return object.getDescription();
			}
		});
		
	}

}
