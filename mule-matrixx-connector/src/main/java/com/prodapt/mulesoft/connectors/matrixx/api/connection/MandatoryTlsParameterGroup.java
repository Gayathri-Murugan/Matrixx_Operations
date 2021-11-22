package com.prodapt.mulesoft.connectors.matrixx.api.connection;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.http.api.HttpConstants;

public class MandatoryTlsParameterGroup implements TlsParameterGroup {
	  @Parameter
	  @Optional
	  @Expression(ExpressionSupport.NOT_SUPPORTED)
	  @DisplayName("TLS Configuration")
	  @Placement(tab = "TLS", order = 1)
	  private TlsContextFactory tlsContext;
	  
	  public HttpConstants.Protocol getProtocol() {
	    return HttpConstants.Protocol.HTTPS;
	  }
	  
	  public TlsContextFactory getTlsContext() {
	    return this.tlsContext;
	  }

}
