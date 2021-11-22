package com.prodapt.mulesoft.connectors.api;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Stream;

import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.core.api.util.StringUtils;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.http.api.domain.CaseInsensitiveMultiMap;

public class HttpResponseAttributes implements Serializable {
	  private static final String TAB = "   ";
	  
	  private static final String DOUBLE_TAB = "      ";
	  
	  private static final long serialVersionUID = -3131769059554988414L;
	  
	  @Parameter
	  private final int statusCode;
	  
	  @Parameter
	  private MultiMap<String, String> headers;
	  
	  @Parameter
	  private final String reasonPhrase;
	  
	  public HttpResponseAttributes(int statusCode, String reasonPhrase, MultiMap<String, String> headers) {
	    this.headers = (headers != null) ? (MultiMap<String, String>)new CaseInsensitiveMultiMap(headers) : MultiMap.emptyMultiMap();
	    this.statusCode = statusCode;
	    this.reasonPhrase = reasonPhrase;
	  }
	  
	  public int getStatusCode() {
	    return this.statusCode;
	  }
	  
	  public String getReasonPhrase() {
	    return this.reasonPhrase;
	  }
	  
	  public MultiMap<String, String> getHeaders() {
	    return this.headers;
	  }
	  
	  public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append(getClass().getName()).append(System.lineSeparator()).append("{").append(System.lineSeparator())
	      .append("   ").append("Status Code=").append(this.statusCode).append(System.lineSeparator())
	      .append("   ").append("Reason Phrase=").append(this.reasonPhrase).append(System.lineSeparator());
	    buildMapToString((Map)this.headers, "Headers", this.headers.entryList().stream(), builder);
	    builder.append("}");
	    return builder.toString();
	  }
	  
	  private String formatHttpAttributesMapsToString(String name, Stream<Map.Entry<String, String>> stream) {
	    StringBuilder builder = new StringBuilder();
	    builder.append("   ").append(name).append("=[").append(System.lineSeparator());
	    stream.forEach(element -> builder.append("      ").append((String)element.getKey()).append("=").append(obfuscateValueIfNecessary(element)).append(System.lineSeparator()));
	    builder.append("   ").append("]").append(System.lineSeparator());
	    return builder.toString();
	  }
	  
	  private String obfuscateValueIfNecessary(Map.Entry<String, String> entry) {
	    String key = entry.getKey();
	    if (key.equals("password") || key.equals("pass") || key.contains("secret") || key.contains("authorization"))
	      return "****"; 
	    return entry.getValue();
	  }
	  
	  private StringBuilder buildMapToString(Map map, String name, Stream<Map.Entry<String, String>> stream, StringBuilder builder) {
	    if (map.isEmpty()) {
	      builder.append("   ").append(name).append("=[]").append(System.lineSeparator());
	      return builder;
	    } 
	    builder.append(formatHttpAttributesMapsToString(name, stream));
	    return builder;
	  }
}
