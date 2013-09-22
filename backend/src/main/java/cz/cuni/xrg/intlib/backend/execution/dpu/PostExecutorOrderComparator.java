package cz.cuni.xrg.intlib.backend.execution.dpu;

import java.util.Comparator;

/**
 * {@link java.util.Comparator} implementation that compare {@link PostExecutor}
 * based on {@link PostExecutor#getPostExecutorOrder()}.
 * 
 * @author Petyr
 *
 */
class PostExecutorOrderComparator implements Comparator<PostExecutor> {

	/**
	 * Shared default instance of PostExecutorOrderComparator.
	 */
	public static final PostExecutorOrderComparator INSTANCE = new PostExecutorOrderComparator();
	
	@Override
	public int compare(PostExecutor left, PostExecutor right) {
		final int leftValue = left.getPostExecutorOrder();
		final int rightValue = right.getPostExecutorOrder();

		return (leftValue < rightValue) ? -1 : (leftValue > rightValue) ? 1 : 0;
	}
	
}
