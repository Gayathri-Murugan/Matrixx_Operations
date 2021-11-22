package com.prodapt.mulesoft.connectors.matrixx.api.operation.queryparam;

import org.mule.runtime.api.util.MultiMap;

public class MultimapQueryParamFormatter implements QueryParamFormatter {
	public MultiMap<String, String> format(MultiMap<String, String> queryParams) {
	    return queryParams;
	  }
}
