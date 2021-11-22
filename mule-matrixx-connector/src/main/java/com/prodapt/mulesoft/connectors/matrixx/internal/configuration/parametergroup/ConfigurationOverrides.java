package com.prodapt.mulesoft.connectors.matrixx.internal.configuration.parametergroup;

import java.util.concurrent.TimeUnit;

import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import com.prodapt.mulesoft.connectors.api.enums.StreamingType;

public class ConfigurationOverrides {
	
	@Parameter
	@ConfigOverride
	@Optional
	@Placement(tab = "Advanced")
	@Summary("The timeout for request to the remote service. 0 means inifinte")
	private Integer responseTimeout;
	
	@Parameter
	@ConfigOverride
	@Optional
	@Placement(tab = "Advanced")
	@Summary("A time unit which qualifies for the Response Timeout")
	private TimeUnit responseTimeoutUnit;
	
	@Parameter
	@ConfigOverride
	@Optional
	@Placement(tab = "Advanced")
	@Summary("Defines if the request should be sent using streaming. Setting the value to AUTO will automatically define the best strategy based on the request content.")
	private StreamingType streamingType;

	public Integer getResponseTimeout() {
		return responseTimeout;
	}

	public TimeUnit getResponseTimeoutUnit() {
		return responseTimeoutUnit;
	}

	public StreamingType getStreamingType() {
		return streamingType;
	}
	
	public int getResponseTimeoutAsMillis() {
		return Long.valueOf(this.getResponseTimeoutUnit().toMillis(this.responseTimeout.intValue())).intValue();
	}

}
