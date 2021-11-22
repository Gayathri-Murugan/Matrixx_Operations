package com.prodapt.mulesoft.connectors.matrixx.api.connection;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.streaming.StreamingHelper;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;

import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.prodapt.mulesoft.connectors.api.HttpResponseAttributes;
import com.prodapt.mulesoft.connectors.matrixx.internal.error.MatrixxErrorTypes;
import com.prodapt.mulesoft.connectors.matrixx.internal.error.exception.MatrixxException;
import com.prodapt.mulesoft.connectors.matrixx.internal.utility.MatrixxUtility;
import com.prodapt.mulesoft.connectors.matrixx.internal.utility.RestRequestBuilder;

public class DefaultRestConnection implements RestConnection {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRestConnection.class);
	  
	  private static final String REMOTELY_CLOSED = "Remotely closed";
	  
		
		 private final String baseUri;
		 
		 private final String configName;
		 
		 private final HttpClient httpClient;
		
	    
	  private final MultiMap<String, String> defaultQueryParams;
	  
	  private final MultiMap<String, String> defaultHeaders;

	  public DefaultRestConnection( String baseUri, String configName, HttpClient httpClient, 
			  MultiMap<String, String> defaultQueryParams, MultiMap<String, String> defaultHeaders) {
			this.baseUri = baseUri;
			this.configName = configName;
			this.httpClient = httpClient;
		    this.defaultQueryParams = nullSafe(defaultQueryParams);
		    this.defaultHeaders = nullSafe(defaultHeaders);
		  }
	  
	  private void merge(MultiMap<String, String> defaultValues, Predicate<String> appendPredicate, BiConsumer<String, List<String>> appender) {
		    defaultValues.keySet().forEach(k -> {
		          if (appendPredicate.test(k))
		            appender.accept(k, defaultValues.getAll(k)); 
		        });
		  }
	@Override
	public CompletableFuture<Result<InputStream, HttpResponseAttributes>> request(RestRequestBuilder requestBuilder, int responseTimeoutMillis, MediaType defaultResponseMediaType, StreamingHelper streamingHelper) {
		CompletableFuture<Result<InputStream, HttpResponseAttributes>> future = new CompletableFuture<>();
	    HttpRequest request = getHttpRequest(requestBuilder);
	    try {
	      this.httpClient.sendAsync(request, responseTimeoutMillis, true, null).whenComplete((response, t) -> {
	            if (t != null) {
	              handleRequestException(t, request, future);
	            } else {
	              handleResponse(response, defaultResponseMediaType, future, streamingHelper);
	            } 
	          });
	    } catch (Throwable t) {
	      handleRequestException(t, request, future);
	    } 
	    return future;
	}
	
	 private void handleResponse(HttpResponse response, MediaType defaultResponseMediaType, CompletableFuture<Result<InputStream, HttpResponseAttributes>> future, StreamingHelper streamingHelper) {
		    MatrixxErrorTypes error = MatrixxErrorTypes.getErrorByCode(response.getStatusCode()).orElse(null);
		    if (error != null) {
		      handleResponseError(response, defaultResponseMediaType, future, streamingHelper, error);
		    } else {
		      future.complete(toResult(response, false, defaultResponseMediaType, streamingHelper));
		    } 
		  }
	 
	 protected void handleResponseError(HttpResponse response, MediaType defaultResponseMediaType, CompletableFuture<Result<InputStream, HttpResponseAttributes>> future, StreamingHelper streamingHelper, MatrixxErrorTypes error) {
		    future.completeExceptionally((Throwable) new MatrixxException(error, toResult(response, true, defaultResponseMediaType, streamingHelper)));
		  }
		  
		  private void handleRequestException(Throwable t, HttpRequest request, CompletableFuture<Result<InputStream, HttpResponseAttributes>> future) {
		    checkIfRemotelyClosed(t, request);
		    MatrixxErrorTypes error = (t instanceof TimeoutException) ? MatrixxErrorTypes.TIMEOUT : MatrixxErrorTypes.CONNECTIVITY;
		    future.completeExceptionally((Throwable)new ModuleException(t.getMessage(), (ErrorTypeDefinition)error, t));
		  }
		  
		  private <T> Result<T, HttpResponseAttributes> toResult(HttpResponse response, boolean isError, MediaType defaultResponseMediaType, StreamingHelper streamingHelper) {
		    Result.Builder<T, HttpResponseAttributes> builder = Result.builder();
		    HttpEntity entity = response.getEntity();
		    Object content = entity.getContent();
		    if (isError)
		      content = (streamingHelper != null) ? streamingHelper.resolveCursorProvider(content) : content; 
		    builder.output((T) content);
		    entity.getLength().ifPresent(builder::length);
		    MediaType contentType = defaultResponseMediaType;
		    String responseContentType = (String)response.getHeaders().get("Content-Type");
		    if (responseContentType != null)
		      try {
		        contentType = MediaType.parse(responseContentType);
		      } catch (Exception e) {
		        if (LOGGER.isDebugEnabled())
		          LOGGER.debug(String.format("Response Content-Type '%s' could not be parsed to a valid Media Type. Will ignore", new Object[] { responseContentType }), e); 
		      }  
		    builder.mediaType(contentType);
		    builder.attributes(toAttributes(response)).attributesMediaType(MediaType.APPLICATION_JAVA);
		    return builder.build();
		  }
		  
		  protected HttpRequest buildRequest(RestRequestBuilder requestBuilder) {
		    return requestBuilder.build();
		  }
		  

	@Override
	public String getBaseUri() {
		return this.baseUri;
	}

	@Override
	public void stop() {
	    try {
	        beforeStop();
	      } catch (Throwable t) {
	        LOGGER.warn(String.format("Exception found before stopping config '%s'", new Object[] { this.configName }), t);
	      } 
	      try {
	        this.httpClient.stop();
	      } catch (Throwable t) {
	        LOGGER.warn(String.format("Exception found while stopping http client for config '%s'", new Object[] { this.configName }), t);
	      } 
	      try {
	        afterStop();
	      } catch (Throwable t) {
	        LOGGER.warn(String.format("Exception found after stopping config '%s'", new Object[] { this.configName }), t);
	      }
		
	}
	
		  
		  protected HttpResponseAttributes toAttributes(HttpResponse response) {
		    return new HttpResponseAttributes(response.getStatusCode(), response.getReasonPhrase(), response.getHeaders());
		  }
	
	  protected void beforeStop() {}
	  
	  protected void afterStop() {}
	
	private HttpRequest getHttpRequest(RestRequestBuilder requestBuilder) {
	    MultiMap<String, String> headers = requestBuilder.getHeaders();
	    MultiMap<String, String> queryParams = requestBuilder.getQueryParams();
	    merge(this.defaultHeaders, k -> !headers.containsKey(k), requestBuilder::addHeaders);
	    merge(this.defaultQueryParams, k -> !queryParams.containsKey(k), requestBuilder::addQueryParams);
	    return buildRequest(requestBuilder);
	  }
	
	  private MultiMap<String, String> nullSafe(MultiMap<String, String> multiMap) {
		    return (multiMap != null) ? multiMap : new MultiMap();
		  }
	  
	  private void checkIfRemotelyClosed(Throwable exception, HttpRequest request) {
		    if ("https".equals(request.getUri().getScheme()) && MatrixxUtility.containsIgnoreCase(exception.getMessage(), "Remotely closed"))
		      LOGGER.error("Remote host closed connection. Possible SSL/TLS handshake issue. Check protocols, cipher suites and certificate set up. Use -Djavax.net.debug=ssl for further debugging."); 
		  }
		  

}
