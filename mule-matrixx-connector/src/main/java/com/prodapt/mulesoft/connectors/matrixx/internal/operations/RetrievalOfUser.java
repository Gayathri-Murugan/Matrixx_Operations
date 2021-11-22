package com.prodapt.mulesoft.connectors.matrixx.internal.operations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.extension.api.runtime.streaming.StreamingHelper;
import org.mule.runtime.http.api.HttpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prodapt.mulesoft.connectors.matrixx.api.HttpResponseAttributes;
import com.prodapt.mulesoft.connectors.matrixx.api.operation.RequestParameters;
import com.prodapt.mulesoft.connectors.matrixx.internal.configuration.RestConfiguration;
import com.prodapt.mulesoft.connectors.matrixx.internal.configuration.parametergroup.ConfigurationOverrides;
import com.prodapt.mulesoft.connectors.matrixx.internal.configuration.parametergroup.EntityRequestParameters;
import com.prodapt.mulesoft.connectors.matrixx.internal.connection.MatrixxConnection;
import com.prodapt.mulesoft.connectors.matrixx.internal.utility.MatrixxUtility;
import com.prodapt.mulesoft.connectors.matrixx.internal.utility.RestRequestBuilder;
import com.prodapt.mulesoft.connectors.matrixx.internal.operations.parametergroup.DeleteParams;

public class RetrievalOfUser {

	private static final Logger logger = LoggerFactory.getLogger(CreateUser.class);
	public static final RestRequestBuilder.QueryParamFormat QUERY_PARAM_FORMAT = RestRequestBuilder.QueryParamFormat.MULTIMAP;

	@DisplayName("Retrieval of User")
	@MediaType(value = "application/json")
	@Summary(" This service implements the retrieval of a user using the Object ID for the user")
	public void retrievalofUser(@Config RestConfiguration rc, @Connection MatrixxConnection connection,
			@ParameterGroup(name = "Input Parameter") DeleteParams deleteParams,
			@ParameterGroup(name = "Request Parameters") EntityRequestParameters parameters,
			@ParameterGroup(name = "Connector Overrides") ConfigurationOverrides overrides,
			@Content(primary = true) TypedValue<InputStream> payload, StreamingHelper streamingHelper,
			CompletionCallback<InputStream, HttpResponseAttributes> callback) {

		String resourcePath = "/data/json/user/" + deleteParams.getObjectID();

		logger.info("URL :: {}", (connection.getBaseURL() + resourcePath));

		RestRequestBuilder builder = new RestRequestBuilder(connection.getBaseURL(), resourcePath,
				HttpConstants.Method.GET, (RequestParameters) parameters).setQueryParamFormat(QUERY_PARAM_FORMAT)
						.addHeader("Content-Type", "application/json").addHeader("accept", "application/json");
					
		try {
			connection.request(builder, 0, null, streamingHelper).whenComplete(MatrixxUtility.handleResponse(callback));
		} catch (Throwable t) {
			t.printStackTrace();
			callback.error(t);
		}
	}
}