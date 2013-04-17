package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.Type;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import cz.cuni.xrg.intlib.commons.app.util.IntlibEntityManagerFactory;

/**
 * Facade for working with DPUs.
 * @author Jan Vojt <jan@vojt.net>
 *
 */
public class DpuFacade {

	private static boolean initialized = false;
	/**
	 * Entity manager for accessing database with persisted objects.
	 * @todo autowire through Spring and remove setter and constructor
	 */
	private EntityManager em;

	/**
	 * Constructs facade and its dependencies.
	 */
	public DpuFacade() {
		this(IntlibEntityManagerFactory.getImem());
	}

	/**
	 * Construct with given Entity Manager
	 * @param em
	 */
	public DpuFacade(EntityManager em) {
		this.em = em;

		if(!initialized) {
			this.prefillDPUs();
			initialized = true;
		}
	}

	/**
	 * Creates DPU without persisting it.
	 * @return
	 */
	public DPU createDpu() {
		DPU dpu = new DPU();
		return dpu;
	}

	/**
	 * Returns list of all DPUs currently persisted in database.
	 * @return DPU list
	 */
	public List<DPU> getAllDpus() {

		@SuppressWarnings("unchecked")
		List<DPU> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM DPU e").getResultList(),
				DPU.class
		);

		return resultList;
	}

	/**
	 * Find DPU in database by ID and return it.
	 * @param id
	 * @return
	 */
	public DPU getDpu(int id) {
		return em.find(DPU.class, id);
	}

	/**
	 * Saves any modifications made to the DPU into the database.
	 * @param dpu
	 */
	public void save(DPU dpu) {

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		if (dpu.getId() == 0) {
			em.persist(dpu);
		} else {
			em.merge(dpu);
		}

		tx.commit();
	}

	/**
	 * Deletes DPU from the database.
	 * @param dpu
	 */
	public void delete(DPU dpu) {

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.remove(dpu);

		tx.commit();
	}

    private void prefillDPUs() {

		DPU basicEx = new DPU("RDF Extractor", Type.EXTRACTOR);
		basicEx.setJarPath("RDF_extractor/target/RDF_extractor-0.0.1.jar");
		this.save(basicEx);

		DPU sparqlEx = new DPU("File Extractor", Type.EXTRACTOR);
		sparqlEx.setJarPath("File_extractor/target/File_extractor-0.0.1.jar");
		this.save(sparqlEx);

		DPU genericTr = new DPU("SPARQL Transformer", Type.TRANSFORMER);
		genericTr.setJarPath("SPARQL_transformer/target/SPARQL_transformer-0.0.1.jar");
		save(genericTr);

		DPU rdfLo = new DPU("RDF Loader", Type.LOADER);
		rdfLo.setJarPath("RDF_loader/target/RDF_loader-0.0.1.jar");
		save(rdfLo);

		DPU sparqlLo = new DPU("File Loader", Type.LOADER);
		sparqlLo.setJarPath("File_loader/target/File_loader-0.0.1.jar");
		save(sparqlLo);

		DPU module = new DPU("TEST MODULE", Type.LOADER);
		module.setJarPath("module/target/module-0.0.1.jar");
		save(module);
    }

}
