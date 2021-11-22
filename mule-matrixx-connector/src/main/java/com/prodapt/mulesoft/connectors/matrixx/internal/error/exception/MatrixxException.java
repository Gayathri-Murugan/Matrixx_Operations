package com.prodapt.mulesoft.connectors.matrixx.internal.error.exception;

import org.mule.runtime.api.exception.ErrorMessageAwareException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;

import com.prodapt.mulesoft.connectors.api.HttpResponseAttributes;
import com.prodapt.mulesoft.connectors.matrixx.internal.error.MatrixxErrorTypes;

public class MatrixxException extends ModuleException implements ErrorMessageAwareException {
	
	private static final long serialVersionUID = -3131769059554988414L;
	
	private final Message message;
	  
	  public MatrixxException(MatrixxErrorTypes error, Result<Object, HttpResponseAttributes> response) {
	    super("Request returned status code " + ((HttpResponseAttributes)response.getAttributes().get()).getStatusCode(), error);
	    Message.Builder builder = Message.builder().payload(getPayloadTypedValue(response)).mediaType(getMediaType(response));
	    setAttributes(response, builder);
	    this.message = builder.build();
	  }
	  
	  private TypedValue<Object> getPayloadTypedValue(Result<Object, HttpResponseAttributes> response) {
	    DataType dataType = DataType.builder().type(response.getOutput().getClass()).mediaType(getMediaType(response)).build();
	    return new TypedValue(response.getOutput(), dataType, response.getByteLength());
	  }
	  
	  private void setAttributes(Result<Object, HttpResponseAttributes> response, Message.Builder builder) {
	    response.getAttributes().ifPresent(attributes -> {
	          DataType dataType = DataType.builder().type(attributes.getClass()).mediaType(response.getAttributesMediaType().orElse(MediaType.APPLICATION_JAVA)).build();
	          builder.attributes(new TypedValue(attributes, dataType));
	        });
	  }
	  
	  private MediaType getMediaType(Result<Object, HttpResponseAttributes> response) {
	    return response.getMediaType().orElse(null);
	  }
	  
	  public Message getErrorMessage() {
	    return this.message;
	  }
}
