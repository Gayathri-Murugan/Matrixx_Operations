package com.prodapt.mulesoft.connectors.matrixx.internal.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prodapt.mulesoft.connectors.api.HttpResponseAttributes;

public class MatrixxUtility {
	private static final Logger logger = LoggerFactory.getLogger(MatrixxUtility.class);

	public static String getBodyAsString(Map<String, Object> bodyMap) {
		StringBuilder body = new StringBuilder();
		Iterator<Map.Entry<String, Object>> iterator = bodyMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			body = body.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		logger.info(body.toString());
		return StringUtils.removeEnd(body.toString(), "&");
	}

	public static InputStream getInputStreamFromResponse(HttpResponse response) {
		return response.getEntity().getContent();
	}

	public static HttpResponseAttributes setResponseAttributes(HttpResponse httpResponse) {
		return new HttpResponseAttributes(httpResponse.getStatusCode(), httpResponse.getReasonPhrase(),
				httpResponse.getHeaders());
	}

	public static boolean containsIgnoreCase(String value, String predicate) {
		if (value == null || predicate == null)
			return false;
		return value.toLowerCase().contains(predicate.toLowerCase());
	}

	public static JSONObject getJSONPayload(TypedValue<InputStream> payload) {
		JSONObject json = null;
		StringBuilder StringPayload = new StringBuilder();
		BufferedReader brStream = new BufferedReader(new InputStreamReader(payload.getValue()));
		String line = "";
		try {
			while ((line = brStream.readLine()) != null) {
				StringPayload.append(line);
			}
			payload.getValue().close();
			json = new JSONObject(StringPayload.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static <T> BiConsumer<Result<T, HttpResponseAttributes>, Throwable> handleResponse(
			CompletionCallback<T, HttpResponseAttributes> callback) {
		return (r, e) -> {
			if (e != null) {
				callback.error(e);
			} else {
				callback.success(r);
			}
		};
	}
}
