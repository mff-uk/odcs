package cz.cuni.xrg.intlib.rdf.enums;

/**
 *
 * @author Jiri Tomes
 *
 * Set of system supported RDF data types. It´s used priority for easy setting
 * one of possible RDF types choosed by user in DPU details dialog.
 */
public enum RDFFormatType {
    AUTO/**
     * RDF type is set automatically by suffix. If no suffix, RDFXML type is
     * set. *
     */
    ,
    RDFXML /**
     * RDF type using for storing data XML syntax*
     */
    ,
    N3 /**
     * RDF type using triples - subject, predicate, object.  *
     */
    ,
    TRIG /**
     * RDF type using extended turle format (TTL).
     */
    ,
    TTL /** RDF type using turle format - extension of N3 type. **/
}

