package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.util.LinkedList;
import java.util.List;

import static com.thoughtworks.xstream.XStream.PRIORITY_VERY_LOW;

/**
 * Customised version of {@link XStream} for working with objects that are
 * stored in database.
 *
 * Usage of this class require all getters and setters to respect basic
 * conventions.
 *
 * @author Škoda Petr
 */
public class JPAXStream extends XStream {

	private final List<MemberFilter> filters = new LinkedList<>();

	protected JPAXStream() {
		super();
	}
	
	@Override
	protected MapperWrapper wrapMapper(MapperWrapper next) {
		return new MapperWrapper(next) {

			@Override
			public boolean shouldSerializeMember(Class definedIn,
					String fieldName) {
				// apply filters
				for (MemberFilter filter : filters) {
					if (!filter.shouldSerializeMember(definedIn, fieldName)) {
						return false;
					}
				}
				return super.shouldSerializeMember(definedIn, fieldName);
			}

		};
	}

	public static JPAXStream createForPipeline() {
		JPAXStream stream = new JPAXStream();
		// setup
		ClassFilter classFilter = new ClassFilter();
		classFilter.add("org.eclipse.persistence");
		stream.filters.add(classFilter);
		NameFilter skipName = new NameFilter();
		skipName.add("id");
		skipName.add("_persistence_");
		skipName.add("owner");		
		stream.filters.add(skipName);
		// this will use getters and setters for ono plain objects
		// usage of get/set will invoke jpa to load the data
		stream.registerConverter(new JavaBeanConverter(stream.getMapper()),
				PRIORITY_VERY_LOW);
		return stream;
	}
	
	public static JPAXStream createForSchedule() {
		JPAXStream stream = new JPAXStream();
		// setup
		ClassFilter classFilter = new ClassFilter();
		classFilter.add("org.eclipse.persistence");
		stream.filters.add(classFilter);
		NameFilter skipName = new NameFilter();
		skipName.add("id");
		skipName.add("_persistence_");
		skipName.add("owner");
		skipName.add("pipeline");
		stream.filters.add(skipName);
		// this will use getters and setters for ono plain objects
		// usage of get/set will invoke jpa to load the data
		stream.registerConverter(new JavaBeanConverter(stream.getMapper()),
				PRIORITY_VERY_LOW);
		return stream;
	}	
	
}
