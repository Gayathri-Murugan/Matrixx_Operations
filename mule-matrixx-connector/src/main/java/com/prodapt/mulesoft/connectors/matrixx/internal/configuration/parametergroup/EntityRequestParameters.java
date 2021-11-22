package com.prodapt.mulesoft.connectors.matrixx.internal.configuration.parametergroup;

import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import com.prodapt.mulesoft.connectors.matrixx.api.operation.RequestParameters;

public class EntityRequestParameters implements RequestParameters {
	
	@Parameter
	@Optional
	@Content
	@DisplayName("Custom Query Parameters")
	@Placement(tab = "Advanced", order = 1)
	private MultiMap<String, String> customQueryParams = MultiMap.emptyMultiMap();
	
	@Parameter
	@Optional
	@Content
	@DisplayName("Custom Headers")
	@Placement(tab = "Advanced", order = 2)
	private MultiMap<String, String> customHeaders = MultiMap.emptyMultiMap();

	public MultiMap<String, String> getCustomQueryParams() {
		return customQueryParams;
	}

	public MultiMap<String, String> getCustomHeaders() {
		return customHeaders;
	}
	
	

}
