/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.xrg.odcs.rdf.help;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

/**
 *
 * @author tomasknap
 */
public interface OrderTupleQueryResult extends TupleQueryResult {
    
    @Override
    public boolean hasNext() throws QueryEvaluationException;
    
    @Override
    public BindingSet next() throws QueryEvaluationException;
}
