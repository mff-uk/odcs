/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.rdf.impl.PlaceHolder;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author tomesj
 */
public class PlaceholdersHelper {
    
    private List<PlaceHolder> getPlaceHolders(String constructQuery) {
		
		String regex = "graph\\s+\\?[gG]_[\\w-_]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(constructQuery);
		
		boolean hasResult = matcher.find();
		
		List<PlaceHolder> placeholders = new ArrayList<>();
		
		while (hasResult) {
			
			int start = matcher.start();
			int end = matcher.end();
			
			int partIndex = constructQuery.substring(start, end).indexOf("_") + 1;
			
			start += partIndex;
			
			String DPUName = constructQuery.substring(start, end);
			
			PlaceHolder placeHolder = new PlaceHolder(DPUName);
			placeholders.add(placeHolder);
			
			hasResult = matcher.find();
		}
		
		return placeholders;
	}
	
	private void replaceAllPlaceHolders(List<RDFDataUnit> inputs,
			List<PlaceHolder> placeHolders, DPUContext context) throws DPUException {
		
		for (PlaceHolder next : placeHolders) {
			boolean isReplased = false;
			
			for (RDFDataUnit input : inputs) {
				if (input.getDataUnitName().equals(next.getDPUName())) {

					//set RIGHT data graph for DPU
					next.setGraphName(input.getDataGraph().toString());
					isReplased = true;
					break;
				}
			}
			
			if (!isReplased) {
				String DPUName = next.getDPUName();
				final String message = "Graph for DPU name " + DPUName + " was not replased";
				
				context.sendMessage(MessageType.ERROR, message);
				throw new DPUException(message);
			}
			
		}
		
	}
	
        String getContructQuery(String originalConstructQuery,
			List<RDFDataUnit> inputs, DPUContext context) throws DPUException {
		
		String result = originalConstructQuery;
		
		List<PlaceHolder> placeHolders = getPlaceHolders(originalConstructQuery);
		
		if (!placeHolders.isEmpty()) {
			replaceAllPlaceHolders(inputs, placeHolders, context);
		}
		
		for (PlaceHolder next : placeHolders) {
			
			String graphName = "<" + next.getGraphName() + ">";
			
			result = result.replaceAll("\\?[g|G]_" + next
					.getDPUName(), graphName);
		}
		
		return result;
	}
    
}
