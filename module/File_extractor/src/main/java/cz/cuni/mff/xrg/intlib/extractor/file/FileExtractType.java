package cz.cuni.mff.xrg.intlib.extractor.file;

import java.util.HashMap;
import java.util.Map;

/**
 * For making distinction for extraction data from file.
 *
 * @author Jiri Tomes
 */
public enum FileExtractType {

	/**
	 * Data extraction from uploaded file.
	 */
	UPLOAD_FILE,
	/**
	 * Data extraction from given path to file.
	 */
	PATH_TO_FILE,
	/**
	 * Data extraction from each file in given directory.
	 */
	PATH_TO_DIRECTORY,
	/**
	 * Data extraction from file stored on the web.
	 */
	HTTP_URL;

	private static Map<FileExtractType, String> map = new HashMap<>();

	static {
		addDataToMap();
	}

	private static void addDataToMap() {

		map.put(UPLOAD_FILE, "Extract uploaded file");
		map.put(PATH_TO_FILE, "Extract file based on the path to file");
		map.put(PATH_TO_DIRECTORY,
				"Extract file based on the path to the directory");
		map.put(HTTP_URL, "Extract file from the given HTTP URL");

	}

	/**
	 *
	 * @param type how to extract data from file
	 * @return string description to given one of enum as type parameter.
	 */
	public static String getDescriptionByType(FileExtractType type) {
		return map.get(type);
	}
}
