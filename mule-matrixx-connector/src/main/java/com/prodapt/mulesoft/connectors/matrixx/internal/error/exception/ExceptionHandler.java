package com.prodapt.mulesoft.connectors.matrixx.internal.error.exception;

import java.io.InputStream;

import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prodapt.mulesoft.connectors.api.HttpResponseAttributes;
import com.prodapt.mulesoft.connectors.matrixx.internal.error.MatrixxErrorTypes;


public class ExceptionHandler {
	/*
	 * private static final Logger logger =
	 * LoggerFactory.getLogger(ExceptionHandler.class);
	 * 
	 * public static final void checkErrorResponse(HttpResponse httpResponse) { int
	 * status = httpResponse.getStatusCode(); if (status != 200 && status != 201) {
	 * String errorResponse = null; InputStream inputStream =
	 * httpResponse.getEntity().getContent(); if (inputStream != null) errorResponse
	 * = IOUtils.toString(inputStream); throw new AriaException(errorResponse,
	 * toResult(httpResponse, errorResponse), getError(Integer.valueOf(status))); }
	 * }
	 * 
	 * public static final AriaErrorTypes resolveError(Throwable error) { if (error
	 * instanceof java.util.concurrent.TimeoutException) return
	 * AriaErrorTypes.TIMEOUT; if
	 * (error.getMessage().equalsIgnoreCase("Remotely closed")) return
	 * AriaErrorTypes.REMOTELY_CLOSED; return AriaErrorTypes.CONNECTIVITY; }
	 * 
	 * public static final AriaErrorTypes getError(Integer status) { return
	 * AriaErrorTypes.valueOf(status.toString()); }
	 * 
	 * public static Result<String, HttpResponseAttributes> toResult(HttpResponse
	 * httpResponse, String errorPayload) { Result.Builder<String,
	 * HttpResponseAttributes> builder = Result.builder(); HttpEntity entity =
	 * httpResponse.getEntity(); builder.output(errorPayload);
	 * entity.getLength().ifPresent(length -> builder.length(length.longValue()));
	 * String contentType = (String)httpResponse.getHeaders().get("Content-Type");
	 * if (contentType != null) try {
	 * builder.mediaType(MediaType.parse(contentType)); } catch (Exception var9) {
	 * if (logger.isDebugEnabled()) logger.debug(String.
	 * format("Response Content-Type '%s' could not be parsed to a valid Media Type. Will ignore"
	 * , new Object[] { contentType }), var9); }
	 * builder.attributes(toAttributes(httpResponse)).attributesMediaType(MediaType.
	 * APPLICATION_JAVA); return builder.build(); }
	 * 
	 * private static HttpResponseAttributes toAttributes(HttpResponse response) {
	 * return new HttpResponseAttributes(response.getStatusCode(),
	 * response.getReasonPhrase(), response.getHeaders()); }
	 */
}
