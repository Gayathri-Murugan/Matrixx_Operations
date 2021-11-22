package com.prodapt.mulesoft.connectors.matrixx.internal.connection;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.streaming.StreamingHelper;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.mule.runtime.http.api.client.auth.HttpAuthentication;
import org.mule.runtime.http.api.domain.entity.ByteArrayHttpEntity;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prodapt.mulesoft.connectors.api.HttpResponseAttributes;
import com.prodapt.mulesoft.connectors.matrixx.internal.error.MatrixxErrorTypes;
import com.prodapt.mulesoft.connectors.matrixx.internal.error.exception.MatrixxException;
import com.prodapt.mulesoft.connectors.matrixx.internal.utility.MatrixxUtility;
import com.prodapt.mulesoft.connectors.matrixx.internal.utility.RestRequestBuilder;

public class MatrixxConnection {
	
	private static final Logger logger = LoggerFactory.getLogger(MatrixxConnection.class);
	
	private final String baseURL;
	
	private final String configName;
	
	private final HttpClient httpClient;
	
	private static final String REMOTELY_CLOSED = "Remotely closed";
	
	private final MultiMap<String, String> defaultQueryParams;
	
	private final MultiMap<String, String> defaultHeaders;	

	public MatrixxConnection(String baseURL, String configName, HttpClient httpClient, MultiMap<String, String> defaultQueryParams, MultiMap<String, String> defaultHeaders) {
		logger.info("Start of Matrixx connection constructor");
		this.baseURL = baseURL;
		this.configName = configName;
		logger.info("Before httpClient Initialization");
		this.httpClient = httpClient;
		logger.info("After httpClient Initialization");
	    this.defaultQueryParams = nullSafe(defaultQueryParams);
	    this.defaultHeaders = nullSafe(defaultHeaders);
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	/*
	 * private HttpClient createClient(HttpService httpService) { logger.info("1");
	 * HttpClientConfiguration.Builder builder = new
	 * HttpClientConfiguration.Builder(); logger.info("2");
	 * builder.setName("Aria-Connector"); HttpClient httpClient =
	 * httpService.getClientFactory().create(builder.build()); logger.info("3");
	 * httpClient.start(); logger.info("4"); return httpClient; }
	 */
	
	/*
	 * public CompletableFuture<HttpResponse> sendRequest(String baseURL, int
	 * responseTimeout) { logger.info("Inside sendRequest operation");
	 * HttpRequestBuilder builder = HttpRequest.builder();
	 * builder.addHeader("Content-Type", "application/json"); String body =
	 * "{\"rest_call\": \"get_current_system_version\",\"output_format\": \"json\",\"client_no\":"
	 * + 0 + ",\"auth_key\":\"" + 0 + "\"}";
	 * builder.method(HttpConstants.Method.POST).uri(baseURL).entity((HttpEntity)
	 * new ByteArrayHttpEntity(body.getBytes(StandardCharsets.UTF_8)));
	 * logger.info("Body" + body); return
	 * this.httpClient.sendAsync(builder.build(),responseTimeout,false,null); }
	 */

	public void invalidate()
	{
		try {
			this.httpClient.stop();
		} catch (Exception e) {
			logger.error("Error occurred while closing the http client connection",e);
		}
	}

	public String getBaseURL() {
		return this.baseURL;
	}
	 
	/*
	 * public CompletableFuture<HttpResponse> getHttpResponse(String baseURL, int
	 * responseTimeout) { return this.sendRequest(baseURL, 30000); }
	 */
	
	public CompletableFuture<Result<InputStream, HttpResponseAttributes>> request(RestRequestBuilder requestBuilder, int responseTimeoutMillis, MediaType defaultResponseMediaType, StreamingHelper streamingHelper) {
		logger.info("Start of request method");
		CompletableFuture<Result<InputStream, HttpResponseAttributes>> future = new CompletableFuture<>();
	    HttpRequest request = getHttpRequest(requestBuilder);
	    logger.info(request.getEntity().getContent().toString());
	    try {
	    	logger.info("Before sending the request");
	      this.httpClient.sendAsync(request, responseTimeoutMillis, true, null).whenComplete((response, t) -> {
	            if (t != null) {
	            	logger.info("Before handleRequest exception - 1");
	              handleRequestException(t, request, future);
	            } else {
	            	logger.info("Before handleResponse exception");
	              handleResponse(response, defaultResponseMediaType, future, streamingHelper);
	            } 
	          });
	    } catch (Throwable t) {
	    	logger.info("Before handleRequest exception - 2");
	      handleRequestException(t, request, future);
	    } 
	    return future;
	}
	
	 private void handleResponse(HttpResponse response, MediaType defaultResponseMediaType, CompletableFuture<Result<InputStream, HttpResponseAttributes>> future, StreamingHelper streamingHelper) {
		 logger.info("Start of HandleResponse" + response.getStatusCode());
		// logger.info("Response: " + RestRequestBuilder.returnBodyString(new TypedValue<InputStream>(response.getEntity().getContent(),DataType.INPUT_STREAM)));
		 MatrixxErrorTypes error = MatrixxErrorTypes.getErrorByCode(response.getStatusCode()).orElse(null);
		    if (error != null) {
		    	logger.info("Before calling HandleResponse Error"); 
		      handleResponseError(response, defaultResponseMediaType, future, streamingHelper, error);
		    } else {
		    	logger.info("before calling future complete ");
		      future.complete(toResult(response, false, defaultResponseMediaType, streamingHelper));
		    } 
		  }
	 
	 protected void handleResponseError(HttpResponse response, MediaType defaultResponseMediaType, CompletableFuture<Result<InputStream, HttpResponseAttributes>> future, StreamingHelper streamingHelper, MatrixxErrorTypes error) {
		 logger.info("Start of handleResponseError");   
		 future.completeExceptionally((Throwable) new MatrixxException(error, toResult(response, true, defaultResponseMediaType, streamingHelper)));
		  }
		  
		  private void handleRequestException(Throwable t, HttpRequest request, CompletableFuture<Result<InputStream, HttpResponseAttributes>> future) {
			  logger.info("Start of HandleRequest Exception");
		    checkIfRemotelyClosed(t, request);
		    MatrixxErrorTypes error = (t instanceof TimeoutException) ? MatrixxErrorTypes.TIMEOUT : MatrixxErrorTypes.CONNECTIVITY;
		    logger.info(t.getMessage());
		    future.completeExceptionally((Throwable)new ModuleException(t.getMessage(), (ErrorTypeDefinition)error, t));
		  }
		  private <T> Result<T, HttpResponseAttributes> toResult(HttpResponse response, boolean isError, MediaType defaultResponseMediaType, StreamingHelper streamingHelper) {
			  logger.info("Start of toResult() method");
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
			        if (logger.isDebugEnabled())
			        	logger.debug(String.format("Response Content-Type '%s' could not be parsed to a valid Media Type. Will ignore", new Object[] { responseContentType }), e); 
			      }  
			    builder.mediaType(contentType);
			    builder.attributes(toAttributes(response)).attributesMediaType(MediaType.APPLICATION_JAVA);
			    return builder.build();
			  }
			  
			  protected HttpRequest buildRequest(RestRequestBuilder requestBuilder) {
			    return requestBuilder.build();
		}
			  
				private HttpRequest getHttpRequest(RestRequestBuilder requestBuilder) {
					logger.info("Start of getHttpRequest");
				    MultiMap<String, String> headers = requestBuilder.getHeaders();
				    MultiMap<String, String> queryParams = requestBuilder.getQueryParams();
				    merge(this.defaultHeaders, k -> !headers.containsKey(k), requestBuilder::addHeaders);
				    merge(this.defaultQueryParams, k -> !queryParams.containsKey(k), requestBuilder::addQueryParams);
				    logger.info("Before calling and returning build request");
				    return buildRequest(requestBuilder);
				  }
				
				  private MultiMap<String, String> nullSafe(MultiMap<String, String> multiMap) {
					    return (multiMap != null) ? multiMap : new MultiMap();
					  }
				  
				  private void checkIfRemotelyClosed(Throwable exception, HttpRequest request) {
					    if ("https".equals(request.getUri().getScheme()) && MatrixxUtility.containsIgnoreCase(exception.getMessage(), "Remotely closed"))
					    	logger.error("Remote host closed connection. Possible SSL/TLS handshake issue. Check protocols, cipher suites and certificate set up. Use -Djavax.net.debug=ssl for further debugging."); 
					  }
				  protected HttpResponseAttributes toAttributes(HttpResponse response) {
					    return new HttpResponseAttributes(response.getStatusCode(), response.getReasonPhrase(), response.getHeaders());
					  }
				  
				  private void merge(MultiMap<String, String> defaultValues, Predicate<String> appendPredicate, BiConsumer<String, List<String>> appender) {
					    defaultValues.keySet().forEach(k -> {
					          if (appendPredicate.test(k))
					            appender.accept(k, defaultValues.getAll(k)); 
					        });
					  }
				  public final void stop() {
					    try {
					      beforeStop();
					    } catch (Throwable t) {
					    	logger.warn(String.format("Exception found before stopping config '%s'", new Object[] { this.configName }), t);
					    } 
					    try {
					      this.httpClient.stop();
					    } catch (Throwable t) {
					    	logger.warn(String.format("Exception found while stopping http client for config '%s'", new Object[] { this.configName }), t);
					    } 
					    try {
					      afterStop();
					    } catch (Throwable t) {
					    	logger.warn(String.format("Exception found after stopping config '%s'", new Object[] { this.configName }), t);
					    } 
					  }
				  protected void beforeStop() {}
				  
				  protected void afterStop() {}
	
	/*
	 * public URLConnection createConnection(String baseURL, String client_number,
	 * String auth_key) { URLConnection httpsconn = null; //String urlProtocol =
	 * "HTTPS".equalsIgnoreCase(protocol) ? "https://" :"http://"; try { httpsconn =
	 * new URL(baseURL).openConnection();
	 * logger.info("Connection opened successfully"); } catch(Exception e) {
	 * e.printStackTrace(); } conn.addRequestProperty("User-Agent", "Mozilla");
	 * return (HttpsURLConnection) httpsconn; }
	 * 
	 * public URLConnection getConnection() {
	 * logger.info("Connection requred in getConnection method"); return this.conn;
	 * }
	 * 
	 * public void invalidate() {
	 * 
	 * if (this.conn !=null) ((HttpsURLConnection) conn).disconnect();
	 * 
	 * logger.info("connection disconnected successfully"); }
	 */

	/*
	 * @Override public void disconnect() { try { this.httpClient.stop();
	 * }catch(Exception e) {
	 * logger.error("Error Occuring while disconnecting the http client connection",
	 * e); }
	 * 
	 * }
	 */

	
	/*
	 * public void validate() { String url = this.baseURL;
	 * 
	 * HttpRequestBuilder builder = HttpRequest.builder(); Map<String,Object>
	 * bodyMap = new HashMap<>(); //bodyMap.put("rest_call",
	 * "get_current_system_version"); //bodyMap.put("output_format", "json");
	 * bodyMap.put("client_no", this.client_no); bodyMap.put("auth_key",
	 * this.auth_key); String body =
	 * "{\"rest_call\": \"get_current_system_version\",\"output_format\": \"json\",\"client_no\":"
	 * + this.client_no + ",\"auth_key\":\"" + this.auth_key + "\"}";
	 * builder.method(HttpConstants.Method.POST).uri(url).entity((HttpEntity) new
	 * ByteArrayHttpEntity(body.getBytes(StandardCharsets.UTF_8)));
	 * 
	 * }
	 */
	 
	
	/*
	 * public CompletableFuture<HttpResponse> send(HttpRequestBuilder
	 * requestBuilder, int responseTimeout, boolean followRedirects) {
	 * requestBuilder.addHeader("Content-Type", "application/json"); return
	 * this.httpClient.sendAsync(requestBuilder.build(),
	 * Optional<HttpAuthentication>);
	 * 
	 * } public HttpResponse sendSource(HttpRequestBuilder requestBuilder, int
	 * responseTimeout, boolean followRedirects) throws IOException,
	 * TimeoutException { requestBuilder.addHeader("Content-Type",
	 * "application/json"); return this.httpClient.send(requestBuilder.build(),
	 * responseTimeout, followRedirects);
	 * 
	 * }
	 */

}
