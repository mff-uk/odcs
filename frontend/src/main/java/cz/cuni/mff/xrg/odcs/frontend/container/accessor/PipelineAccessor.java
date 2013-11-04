package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.frontend.container.ClassAccessor;

public class PipelineAccessor implements ClassAccessor<Pipeline> {

	private List<String> all = Arrays.asList("id", "name", "description");

	private List<String> sortable = Arrays.asList("name", "description");

	private List<String> filtrable = Arrays.asList("name", "description");
	
	@Override
	public List<String> all() {
		return all;
	}

	@Override
	public List<String> sortable() {
		return sortable;
	}
	
	@Override
	public List<String> filtrable() {
		return filtrable;
	}

	@Override
	public Class<Pipeline> getEntityClass() {
		return Pipeline.class;
	}

	@Override
	public String getColumnName(String id) {
		switch (id) {
		case "id":
			return "id";
		case "name":
			return "name";
		case "description":
			return "description";
		default:
			return null;
		}
	}
	
	@Override
	public Object getValue(Pipeline object, String id) {
		switch (id) {
		case "id":
			return object.getId();
		case "name":
			return object.getName();
		case "description":
			return object.getDescription();
		default:
			return null;
		}
	}

	@Override
	public Class<?> getType(String id) {
		switch (id) {
		case "id":
			return Integer.class;
		case "name":
		case "description":
			return String.class;
		default:
			return null;
		}
	}

}
