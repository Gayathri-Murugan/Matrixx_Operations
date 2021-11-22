package com.prodapt.mulesoft.connectors.matrixx.api.connection;

import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.http.api.HttpConstants;

public interface TlsParameterGroup {
	HttpConstants.Protocol getProtocol();
	  
	  TlsContextFactory getTlsContext();
}
