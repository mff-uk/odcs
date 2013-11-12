package cz.cuni.mff.xrg.odcs.rdf.enums;

import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;

/**
 * Possibilies how to choose handler for data extraction and how to solve finded
 * problems with no valid data.
 *
 * @author Jiri Tomes
 */
public enum HandlerExtractType {

	/**
	 * For data extraction is used statistical and error handler - see
	 * {@link StatisticalHandler}. If some data are invalid, pipeline execution
	 * fail.
	 */
	ERROR_HANDLER_FAIL_WHEN_MISTAKE,
	/**
	 * For data extraction is used statistical and error handler - see
	 * {@link StatisticalHandler}. If some data are invalid, message is shown
	 * about that after finishing extraction. Problem triples are not added to
	 * repository, pipeline execution continue to execute next DPU.
	 */
	ERROR_HANDLER_CONTINUE_WHEN_MISTAKE,
	/**
	 * For data extraction is used standard handler with information about count
	 * of extracted triples. Not valid triples are automatically skip during
	 * extraction and there is no error message about it.Pipeline execution
	 * continue to execute next DPU.
	 */
	STANDARD_HANDLER;

	public static HandlerExtractType getHandlerType(
			boolean useStatisticalErrorHandler, boolean failWhenErrors) {

		if (useStatisticalErrorHandler) {
			if (failWhenErrors) {
				return HandlerExtractType.ERROR_HANDLER_FAIL_WHEN_MISTAKE;
			} else {
				return HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE;
			}
		} else {
			return HandlerExtractType.STANDARD_HANDLER;
		}
	}
}
