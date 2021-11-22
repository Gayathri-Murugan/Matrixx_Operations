package com.prodapt.mulesoft.connectors.matrixx.api.request;

import java.util.Objects;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public abstract class KeyValue {

	  @Parameter
	  @Expression(ExpressionSupport.NOT_SUPPORTED)
	  private String key;
	  
	  @Parameter
	  @Expression(ExpressionSupport.NOT_SUPPORTED)
	  private String value;
	  
	  public String getKey() {
	    return this.key;
	  }
	  
	  public String getValue() {
	    return this.value;
	  }
	  
	  public boolean equals(Object obj) {
	    if (getClass().isInstance(obj)) {
	      KeyValue other = (KeyValue)obj;
	      return (Objects.equals(this.key, other.key) && Objects.equals(this.value, other.value));
	    } 
	    return false;
	  }
	  
	  public int hashCode() {
	    return Objects.hash(new Object[] { this.key, this.value });
	  }
}
