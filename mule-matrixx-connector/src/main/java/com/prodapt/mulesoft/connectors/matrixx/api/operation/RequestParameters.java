package com.prodapt.mulesoft.connectors.matrixx.api.operation;

import org.mule.runtime.api.util.MultiMap;

public interface RequestParameters {
	
	MultiMap<String, String> getCustomQueryParams();
	  
	  MultiMap<String, String> getCustomHeaders();

}
