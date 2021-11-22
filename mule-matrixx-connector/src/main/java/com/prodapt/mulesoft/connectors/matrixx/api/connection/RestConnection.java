package com.prodapt.mulesoft.connectors.matrixx.api.connection;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.streaming.StreamingHelper;


import com.prodapt.mulesoft.connectors.api.HttpResponseAttributes;
import com.prodapt.mulesoft.connectors.matrixx.internal.utility.RestRequestBuilder;

public interface RestConnection {
	
	CompletableFuture<Result<InputStream, HttpResponseAttributes>> request(RestRequestBuilder paramRestRequestBuilder, int paramInt, MediaType paramMediaType, StreamingHelper paramStreamingHelper);
	
	String getBaseUri();
	  
	void stop();

}
