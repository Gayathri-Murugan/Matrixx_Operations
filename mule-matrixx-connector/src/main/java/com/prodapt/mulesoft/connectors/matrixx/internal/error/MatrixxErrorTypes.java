package com.prodapt.mulesoft.connectors.matrixx.internal.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.mule.runtime.extension.api.error.MuleErrors;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;

public enum MatrixxErrorTypes implements ErrorTypeDefinition<MatrixxErrorTypes> {
	CONNECTIVITY((ErrorTypeDefinition<?>)MuleErrors.CONNECTIVITY),
	  CLIENT_ERROR,
	  TIMEOUT(CLIENT_ERROR),
	  UNAUTHORIZED(CLIENT_ERROR),
	  NOT_FOUND(CLIENT_ERROR),
	  TOO_MANY_REQUESTS(CLIENT_ERROR),
	  BAD_REQUEST(CLIENT_ERROR),
	  NOT_ACCEPTABLE,
	  SERVER_ERROR,
	  INTERNAL_SERVER_ERROR(SERVER_ERROR),
	  SERVICE_UNAVAILABLE(SERVER_ERROR),
	  UNSUPPORTED_MEDIA_TYPE(SERVER_ERROR);
	  
	/*
	 * static { UNSUPPORTED_MEDIA_TYPE = new
	 * AriaErrorTypes("UNSUPPORTED_MEDIA_TYPE", 7, CLIENT_ERROR, request ->
	 * "media type " + request.getHeaderValue("Content-Type") + " not supported"); }
	 */
	  
	  private ErrorTypeDefinition<?> parentErrorType;
	  
	  private final Function<HttpRequest, String> errorMessageFunction;
	  
	  MatrixxErrorTypes() {
	    String message = name().replace("_", " ").toLowerCase();
	    this.errorMessageFunction = (httpRequest -> message);
	  }
	  
	  MatrixxErrorTypes(ErrorTypeDefinition<?> parentErrorType) {
		  this.parentErrorType = parentErrorType;
		  this.errorMessageFunction = null;
	  }
	  
	  MatrixxErrorTypes(ErrorTypeDefinition<?> parentErrorType, Function<HttpRequest, String> errorMessageFunction) {
	    this.parentErrorType = parentErrorType;
	    this.errorMessageFunction = errorMessageFunction;
	  }
	  
	  public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
	    return (Optional)Optional.ofNullable(this.parentErrorType);
	  }
	  
	  public static Optional<MatrixxErrorTypes> getErrorByCode(int statusCode) {
		  MatrixxErrorTypes error = null;
	    HttpConstants.HttpStatus status = HttpConstants.HttpStatus.getStatusByCode(statusCode);
	    if (status != null)
	      try {
	        error = valueOf(status.name());
	      } catch (IllegalArgumentException illegalArgumentException) {} 
	    if (error == null)
	      if (statusCode >= 400 && statusCode < 500) {
	        error = CLIENT_ERROR;
	      } else if (statusCode >= 500) {
	        error = SERVER_ERROR;
	      }  
	    return Optional.ofNullable(error);
	  }
	  
	  public static Optional<HttpConstants.HttpStatus> getHttpStatus(MatrixxErrorTypes error) {
	    HttpConstants.HttpStatus result = null;
	    for (HttpConstants.HttpStatus status : HttpConstants.HttpStatus.values()) {
	      if (error.name().equals(status.name()))
	        result = status; 
	    } 
	    return Optional.ofNullable(result);
	  }
	  
	  public String getErrorMessage(HttpRequest request) {
	    return this.errorMessageFunction.apply(request);
	  }
	}
