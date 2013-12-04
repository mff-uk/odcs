package cz.cuni.mff.xrg.odcs.commons.app.dao;

import java.io.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
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
 * @author Jan Vojt
 */
@Aspect
@Order(1)
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
	 * Defines a join point with advice around facade methods which save new
	 * entities. These methods are candidates for running into outdated database
	 * sequences.
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable 
	 */
	@Around("execution(* cz.cuni.mff.xrg.odcs.commons.app.facade.*Facade.save(..))"
			+ " || execution(* cz.cuni.mff.xrg.odcs.commons.app.facade.*Facade.copy*(..))")
	public Object sanitizeSequence(ProceedingJoinPoint pjp) throws Throwable {
		
		Object result = null;
		try {
			result = pjp.proceed();
		} catch (TransactionException ex) {
			VirtuosoException cause = getRootCause(ex);
			if (cause != null && cause.getErrorCode() == VirtuosoException.SQLERROR) {
				// Lets assume SQLERROR implies "non-unique primary key error",
				// retry after rollback won't hurt anything.
				LOG.error("Virtuoso SQLERROR encountered. Will update sequences and retry.", ex);
				updateSequences();
				
				// TODO seperate save and copy join point
				// TODO set ID to null for DataObject in save's argument
				
				LOG.info("Retrying operation after sequence update.");
				result = pjp.proceed();
			} else {
				// Different exception -> rethrow
				throw ex;
			}
		}
		
		return result;
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

}
