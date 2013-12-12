package cz.cuni.mff.xrg.odcs.commons.app.dao;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclarePrecedence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import virtuoso.jdbc4.VirtuosoException;

/**
 * Aspect around methods which insert data into database, whose job is to fix
 * corrupted sequences in database (after crash).
 * 
 * <p>
 * Virtuoso caches sequence values in memory outside transaction context. Upon
 * serious crash (e.g. SEGFAULT, SIGKILL, ...), it is unable to recover sequence
 * values. This causes inconsistency in database, which results in failure upon
 * inserting a row with primary key generated from corrupted sequence.
 * 
 * <p>
 * To work around this problem, this aspect catches SQL errors in relevant
 * business processes. If caught, sequences are reset to correct values
 * according to current state of database.
 * 
 * <p>
 * This aspect needs to run around Spring's transactional aspect, so we can
 * catch errors that show up when committing transaction. The declared
 * precedence accomplishes this.
 *
 * @author Jan Vojt
 */
@Aspect
@DeclarePrecedence("VirtuosoSequenceSanitizerAspect,AnnotationTransactionAspect")
public class VirtuosoSequenceSanitizerAspect {
	
	private static final Logger LOG = LoggerFactory.getLogger(VirtuosoSequenceSanitizerAspect.class);
	
	/**
	 * Path to SQL script for fixing corrupted sequences (within classpath).
	 */
	private static final String SQL_SCRIPT_PATH = "/sql/sequences.sql";

	/**
	 * We cannot used shared {@link EntityManager}, because we need to manage
	 * transactions inside {@link #updateSequences()} method.
	 * The {@link Transactional} advice does not work on private methods.
	 * This is actually because we are using Spring's AOP proxies. If we used
	 * AspectJ load-time or compile-time weaving, we could annotate
	 * {@link #updateSequences()} directly with {@link Transactional}.
	 */
	@Autowired
	protected EntityManagerFactory emf;
	
	/**
	 * Boolean value deciding whether we need to remember all sequence
	 * assignments on this thread.
	 * 
	 * @see #rememberAssignSequence(org.aspectj.lang.JoinPoint) 
	 */
	private final ThreadLocal<Boolean> REMEMBER_SEQ_ASSIGN = new ThreadLocal<>();
	
	/**
	 * Map of remembered original values before they were modified by JPA.
	 * 
	 * @see #rememberAssignSequence(org.aspectj.lang.JoinPoint) 
	 */
	private final ThreadLocal<Map<DataObject, Long>> SEQ_ASSIGNMENTS = new ThreadLocal<>();
	
	/**
	 * Defines a join point with advice around facade methods which save new
	 * entities. These methods are candidates for running into outdated database
	 * sequences.
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable 
	 */
	@Around("execution(* cz.cuni.mff.xrg.odcs.commons.app.facade.*Facade.save(..))")
	public Object sanitizeSequenceOnSave(ProceedingJoinPoint pjp) throws Throwable {
		
		JoinPoint.StaticPart staticPart = pjp.getStaticPart();
		String signature = staticPart.getSignature().toShortString();
		if (signature.contains("save")) {
			remember();
		}

		try {
			return pjp.proceed();
		} catch (VirtuosoException | TransactionException ex) {
			LOG.error("Encountered exception when persisting new objects, will try to recover.", ex);
			return handleError(pjp, ex);
		} finally {
			forget();
		}
	}
	
	/**
	 * Defines a join point with before advice weaved into JDBC driver, so we
	 * can track primary keys that were assigned to {@link DataObject}s. If an
	 * error occurs we can reconstruct objects back to their original state,
	 * fix the database, and retry transaction.
	 * 
	 * @param jp join point
	 */
	@Before("execution(* org.eclipse.persistence.internal.descriptors.ObjectBuilder.assignSequenceNumber(java.lang.Object, org.eclipse.persistence.internal.sessions.AbstractSession))")
	public void rememberAssignSequence(JoinPoint jp) {
		if (REMEMBER_SEQ_ASSIGN.get() != null && REMEMBER_SEQ_ASSIGN.get()) {
			Object[] args = jp.getArgs();
			DataObject obj = (DataObject) args[0];
			SEQ_ASSIGNMENTS.get().put(obj, obj.getId());
		}
	}
	
	/**
	 * Logic for handling database errors caused by corrupted sequences.
	 * 
	 * @param pjp join point to proceed with
	 * @param ex exception thrown by underlying database / JPA provider
	 * @return
	 * @throws Throwable 
	 */
	private Object handleError(ProceedingJoinPoint pjp, Exception ex) throws Throwable {
			VirtuosoException cause = getRootCause(ex);
			if (REMEMBER_SEQ_ASSIGN.get()
					&& cause != null
					&& cause.getErrorCode() == VirtuosoException.SQLERROR) {
				
				// Lets assume SQLERROR implies "non-unique primary key error",
				// retry after rollback won't hurt anything.
				LOG.error("Virtuoso SQLERROR encountered. Will update sequences and retry.", ex);
				updateSequences();
				forget();
				resetArgumentState();
				
				LOG.info("Retrying operation after sequence update.");
				return pjp.proceed();
			} else {
				// Different exception -> rethrow
				LOG.error("Unexpected error type, giving up on recovery.");
				throw ex;
			}
	}
	
	/**
	 * Rollback restores previous state of database, however Java objects may
	 * have been altered, so they need to be fixed to previous state.
	 */
	private void resetArgumentState() {
		Map<DataObject, Long> toRepair = SEQ_ASSIGNMENTS.get();
		for (Map.Entry<DataObject, Long> entry : toRepair.entrySet()) {
			DataObject obj = entry.getKey();
			try {
				Field idField = obj.getClass().getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(obj, entry.getValue());
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
				LOG.error("Failed to set DataObject id to {}.", entry.getValue(), ex);
			}
		}
	}
	
	/**
	 * Reads SQL update script from classpath and executes queries to update
	 * database sequences.
	 */
	private void updateSequences() {
		
		// all update queries must be in transactional context
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		
		try (InputStream in = this.getClass().getResourceAsStream(SQL_SCRIPT_PATH)) {
			
			if (in == null) {
				throw new FileNotFoundException(SQL_SCRIPT_PATH);
			}
			
			BufferedReader bin = new BufferedReader(new InputStreamReader(in));

			String line;
			tx.begin();
			while ((line = bin.readLine()) != null) {
				line = StringUtils.trim(line);
				if (StringUtils.isNotEmpty(line)) {
					// we need to remove the terminating column
					String sql = line.substring(0, line.length()-1);
					em.createNativeQuery(sql).executeUpdate();
				}
			}
			tx.commit();
			
		} catch (IOException ex) {
			LOG.error("Cannot read SQL script with queries for updating sequences from path '{}' (within classpath).", SQL_SCRIPT_PATH, ex);
			tx.rollback();
		}
	}
	
	/**
	 * Finds nested {@link VirtuosoException}.
	 * 
	 * @param th thrown exception
	 * @return nested Virtuoso exception or null
	 */
	private VirtuosoException getRootCause(Throwable th) {
		while (!(th == null || th instanceof VirtuosoException)) {
			th = th.getCause();
		}
		return (VirtuosoException) th;
	}
	
	private void remember() {
		LOG.debug("Enabling cache for changes in primary keys.");
		REMEMBER_SEQ_ASSIGN.set(Boolean.TRUE);
		SEQ_ASSIGNMENTS.set(new HashMap<DataObject, Long>());
	}
	
	private void forget() {
		LOG.debug("Disabling and purging cache for changes in primary keys.");
		REMEMBER_SEQ_ASSIGN.set(Boolean.FALSE);
		SEQ_ASSIGNMENTS.set(null);
	}

}
