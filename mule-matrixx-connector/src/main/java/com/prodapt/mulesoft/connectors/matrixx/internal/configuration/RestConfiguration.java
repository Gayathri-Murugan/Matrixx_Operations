package com.prodapt.mulesoft.connectors.matrixx.internal.configuration;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.extension.api.annotation.param.DefaultEncoding;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import com.prodapt.mulesoft.connectors.api.enums.StreamingType;

public abstract class RestConfiguration implements Initialisable {
	@DefaultEncoding
	  private String defaultEncoding;
	  
	  private Charset charset;
	  
	  @Parameter
	  @Optional(defaultValue = "1")
	  @Placement(tab = "Advanced")
	  @Summary("The timeout for request to the remote service.")
	  private Integer responseTimeout;
	  
	  @Parameter
	  @Optional(defaultValue = "MINUTES")
	  @Placement(tab = "Advanced")
	  @Summary("A time unit which qualifies the Response Timeout}")
	  private TimeUnit responseTimeoutUnit = TimeUnit.MINUTES;
	  
	  @Parameter
	  @Placement(tab = "Advanced")
	  @Optional(defaultValue = "AUTO")
	  @Summary("Defines if the request should be sent using streaming. Setting the value to AUTO will automatically define the best strategy based on the request content.")
	  private StreamingType streamingType;
	  
	  public void initialise() {
	    this.charset = Charset.forName(this.defaultEncoding);
	  }
	  
	  public Charset getCharset() {
	    return this.charset;
	  }
	  
	  public Integer getResponseTimeout() {
	    return this.responseTimeout;
	  }
	  
	  public TimeUnit getResponseTimeoutUnit() {
	    return this.responseTimeoutUnit;
	  }
	  
	  public StreamingType getStreamingType() {
	    return this.streamingType;
	  }

}
