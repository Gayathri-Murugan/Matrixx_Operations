package com.prodapt.mulesoft.connectors.matrixx.internal.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.JSONObject;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.CaseInsensitiveMultiMap;
import org.mule.runtime.http.api.domain.entity.ByteArrayHttpEntity;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.entity.InputStreamHttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prodapt.mulesoft.connectors.api.enums.StreamingType;
import com.prodapt.mulesoft.connectors.matrixx.api.operation.RequestParameters;
import com.prodapt.mulesoft.connectors.matrixx.api.operation.queryparam.CommaQueryParamFormatter;
import com.prodapt.mulesoft.connectors.matrixx.api.operation.queryparam.MultimapQueryParamFormatter;
import com.prodapt.mulesoft.connectors.matrixx.api.operation.queryparam.QueryParamFormatter;

public class RestRequestBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestRequestBuilder.class);
	  
	  private String uri;
	  
	  private HttpConstants.Method method;
	  
	  private MultiMap<String, String> headers = (MultiMap<String, String>)new CaseInsensitiveMultiMap(false);
	  
	  private MultiMap<String, String> queryParams = new MultiMap();
	  
	  private TypedValue<InputStream> body = null;
	  
	  private StreamingType streamingType = StreamingType.AUTO;
	  
	  private QueryParamFormat queryParamFormat = QueryParamFormat.MULTIMAP;
	  
	  public RestRequestBuilder(String baseUri, String path, HttpConstants.Method method) {
	    this(baseUri, path, method, null);
	  }
	  
	  public RestRequestBuilder(String baseUri, String path, HttpConstants.Method method, RequestParameters requestParameters) {
	    this.uri = buildRequestUri(baseUri, (path != null) ? path : "");
	    this.method = method;
	    if (requestParameters != null) {
	      this.headers.putAll(requestParameters.getCustomHeaders());
	      this.queryParams.putAll(requestParameters.getCustomQueryParams());
	    } 
	  }
	  
	  public String getUri() {
	    return this.uri;
	  }
	  
	  public String getMethod() {
	    return this.method.name();
	  }
	  
	  public RestRequestBuilder addHeader(String key, String value) {
	    if (value != null)
	      this.headers.put(key, value); 
	    return this;
	  }
	  
	  public RestRequestBuilder addHeaders(String key, List<String> values) {
	    if (values != null && !values.isEmpty())
	      this.headers.put(key, values); 
	    return this;
	  }
	  
	  public RestRequestBuilder headers(MultiMap<String, String> headers) {
	    this.headers.putAll(headers);
	    return this;
	  }
	  
	  public MultiMap<String, String> getHeaders() {
	    return this.headers.toImmutableMultiMap();
	  }
	  
	  public RestRequestBuilder addQueryParam(String key, String value) {
	    if (value != null)
	      this.queryParams.put(key, value); 
	    return this;
	  }
	  
	  public RestRequestBuilder addQueryParams(String key, List<String> values) {
	    if (values != null && !values.isEmpty())
	      this.queryParams.put(key, values); 
	    return this;
	  }
	  
	  public RestRequestBuilder queryParams(MultiMap<String, String> queryParams) {
	    this.queryParams.putAll(queryParams);
	    return this;
	  }
	  
	  public MultiMap<String, String> getQueryParams() {
	    return this.queryParams.toImmutableMultiMap();
	  }
	  
	  public enum QueryParamFormat {
	    MULTIMAP(new MultimapQueryParamFormatter()),
	    COMMA( new CommaQueryParamFormatter());
	    
	    private final QueryParamFormatter formatter;
	    
	    QueryParamFormat(QueryParamFormatter formatter) {
	      this.formatter = formatter;
	    }
	    
	    public QueryParamFormatter getFormatter() {
	      return this.formatter;
	    }
	  }
	  
	  public RestRequestBuilder setQueryParamFormat(QueryParamFormat queryParamFormat) {
	    this.queryParamFormat = queryParamFormat;
	    return this;
	  }
	  
	  public RestRequestBuilder setBody(TypedValue<InputStream> body, StreamingType streamingType) {
	    this.body = body;
	    this.streamingType = streamingType;
	    return this;
	  }
	  
	  private HttpEntity getStreamingConfiguredHttpEntity(TypedValue<InputStream> body, StreamingType streamingType) {
	    if (body != null) {
	      byte[] bytes = null;
	      if (streamingType.equals(StreamingType.ALWAYS)) {
	        this.headers.remove("Content-Length");
	        this.headers.remove("Transfer-Encoding");
	        this.headers.put("Transfer-Encoding", "chunked");
	      } else if (streamingType.equals(StreamingType.NEVER)) {
	        bytes = setNeverStreamingContentLength(body);
	      } else {
	        setAutoContentLengthHeader(body);
	      } 
	      inferContentTypeFromBody(body);
	      if (body.getValue() != null) {
	        if (bytes != null)
	          return (HttpEntity)new ByteArrayHttpEntity(bytes); 
	        return (HttpEntity)new InputStreamHttpEntity((InputStream)body.getValue());
	      } 
	    } 
	    return null;
	  }
	  
	  private void inferContentTypeFromBody(TypedValue<InputStream> body) {
	    if (!this.headers.containsKey("Content-Type")) {
	      MediaType mediaType = body.getDataType().getMediaType();
	      if (mediaType != null && !mediaType.getPrimaryType().equals("*"))
	        this.headers.put("Content-Type", mediaType.toRfcString()); 
	    } 
	  }
	  
	  private byte[] setNeverStreamingContentLength(TypedValue<InputStream> body) {
	    String customLength = (String)this.headers.get("Content-Length");
	    byte[] bytes = null;
	    if (customLength == null)
	      if (body.getByteLength().isPresent()) {
	        this.headers.put("Content-Length", String.valueOf(body.getByteLength().getAsLong()));
	      } else if (body.getValue() != null) {
	        bytes = IOUtils.toByteArray((InputStream)body.getValue());
	        this.headers.put("Content-Length", String.valueOf(bytes.length));
	      }  
	    this.headers.remove("Transfer-Encoding");
	    return bytes;
	  }
	  
	  private void setAutoContentLengthHeader(TypedValue<InputStream> body) {
	    String customLength = (String)this.headers.get("Content-Length");
	    boolean isChunked = "chunked".equals(this.headers.get("Transfer-Encoding"));
	    if (body.getByteLength().isPresent()) {
	      boolean addHeader = true;
	      String length = String.valueOf(body.getByteLength().getAsLong());
	      if (customLength != null) {
	        LOGGER.warn("Invoking URI {} with body of known length {}. However, a {} header with value {} was manually specified. Will proceed with the custom value.", new Object[] { this.uri, length, "Content-Length", customLength });
	        addHeader = false;
	      } 
	      if (isChunked) {
	        LOGGER.debug("Invoking URI {} with a manually set {}: {} header, even though body is of known length {}. Skipping automatic addition of {} header", new Object[] { this.uri, "Transfer-Encoding", "chunked", length, "Content-Length" });
	        addHeader = false;
	      } 
	      if (addHeader)
	        this.headers.put("Content-Length", length); 
	    } else if (customLength == null && !isChunked) {
	      this.headers.put("Transfer-Encoding", "chunked");
	    } 
	  }
	  
	  public HttpRequest build() {
		  try {
		  LOGGER.info("Start of the build method:" + this.uri + "and" + returnBodyString(this.body) + "::" + this.method);
		  }catch(Exception e) {e.printStackTrace();}
	    HttpEntity httpEntity = getStreamingConfiguredHttpEntity(this.body, this.streamingType);
	    HttpRequestBuilder builder = (HttpRequestBuilder)HttpRequest.builder(true).uri(this.uri).method(this.method).queryParams(this.queryParamFormat.getFormatter().format(this.queryParams)).headers(this.headers);
	    if (httpEntity != null)
	      builder.entity(httpEntity); 
	    return builder.build();
	  }
	  
	  private String buildRequestUri(String baseUri, String path) {
	    boolean pathStartsWithSlash = path.startsWith("/");
	    boolean baseEndsInSlash = baseUri.endsWith("/");
	    if (pathStartsWithSlash && baseEndsInSlash) {
	      path = path.substring(1);
	    } else if (!pathStartsWithSlash && !baseEndsInSlash) {
	      baseUri = baseUri + '/';
	    } 
	    return baseUri + path;
	  }
	  public static String returnBodyString(TypedValue<InputStream> body) {
		  try {
		  int n= body.getValue().available();
		  byte[] b = new byte[n];
		  body.getValue().read(b, 0, n);
		  return new String(b, StandardCharsets.UTF_8);
		  }
		  catch(Exception e) {e.printStackTrace(); return null;}
	  }

}
