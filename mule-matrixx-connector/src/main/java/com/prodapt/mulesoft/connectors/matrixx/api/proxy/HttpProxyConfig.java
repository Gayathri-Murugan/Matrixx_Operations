package com.prodapt.mulesoft.connectors.matrixx.api.proxy;

import java.util.Objects;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.http.api.client.proxy.ProxyConfig;

@Alias("proxy")
@TypeDsl(allowTopLevelDefinition = true)
public class HttpProxyConfig  implements ProxyConfig {
	
	@Parameter
	  @Expression(ExpressionSupport.NOT_SUPPORTED)
	  private String host;
	  
	  @Parameter
	  @Expression(ExpressionSupport.NOT_SUPPORTED)
	  private int port = Integer.MAX_VALUE;
	  
	  @Parameter
	  @Optional
	  @Expression(ExpressionSupport.NOT_SUPPORTED)
	  private String username;
	  
	  @Parameter
	  @Optional
	  @Password
	  @Expression(ExpressionSupport.NOT_SUPPORTED)
	  private String password;
	  
	  @Parameter
	  @Optional
	  @Expression(ExpressionSupport.NOT_SUPPORTED)
	  private String nonProxyHosts;
	  
	  public String getHost() {
	    return this.host;
	  }
	  
	  public int getPort() {
	    return this.port;
	  }
	  
	  public String getUsername() {
	    return this.username;
	  }
	  
	  public String getPassword() {
	    return this.password;
	  }
	  
	  public String getNonProxyHosts() {
	    return this.nonProxyHosts;
	  }
	  
	  public boolean equals(Object o) {
	    if (this == o)
	      return true; 
	    if (o == null || getClass() != o.getClass())
	      return false; 
	    HttpProxyConfig that = (HttpProxyConfig)o;
	    return (this.port == that.port && 
	      Objects.equals(this.host, that.host) && 
	      Objects.equals(this.username, that.username) && 
	      Objects.equals(this.password, that.password) && 
	      Objects.equals(this.nonProxyHosts, that.nonProxyHosts));
	  }
	  
	  public int hashCode() {
	    return Objects.hash(new Object[] { this.host, Integer.valueOf(this.port), this.username, this.password, this.nonProxyHosts });
	  }

}
