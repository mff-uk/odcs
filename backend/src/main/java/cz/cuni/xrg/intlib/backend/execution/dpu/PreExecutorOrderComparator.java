package cz.cuni.xrg.intlib.backend.execution.dpu;

import java.util.Comparator;

/**
 * {@link java.util.Comparator} implementation that compare {@link PreExecutor}
 * based on {@link PreExecutor#getPreExecutorOrder()}.
 * 
 * @author Petyr
 *
 */
public class PreExecutorOrderComparator implements Comparator<PreExecutor> {

	/**
	 * Shared default instance of PreExecutorOrderComparator.
	 */
	public static final PreExecutorOrderComparator INSTANCE = new PreExecutorOrderComparator();	
	
	@Override
	public int compare(PreExecutor left, PreExecutor right) {
		final int leftValue = left.getPreExecutorOrder();
		final int rightValue = right.getPreExecutorOrder();

		return (leftValue < rightValue) ? -1 : (leftValue > rightValue) ? 1 : 0;
	}
	
}
