package com.prodapt.mulesoft.connectors.matrixx.api.operation.queryparam;

import org.mule.runtime.api.util.MultiMap;

public class CommaQueryParamFormatter implements QueryParamFormatter {
	public MultiMap<String, String> format(MultiMap<String, String> queryParams) {
	    MultiMap<String, String> formatted = new MultiMap();
	    for (String queryParam : queryParams.keySet()) {
	      if (!formatted.containsKey(queryParam))
	        formatted.put(queryParam, String.join(",", queryParams.getAll(queryParam))); 
	    } 
	    return formatted;
	  }
}
