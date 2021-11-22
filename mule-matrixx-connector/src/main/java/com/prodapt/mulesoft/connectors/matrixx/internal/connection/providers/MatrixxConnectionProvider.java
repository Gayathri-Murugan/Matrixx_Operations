package com.prodapt.mulesoft.connectors.matrixx.internal.connection.providers;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.i18n.I18nMessageFactory;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.api.tls.TlsContextFactoryBuilder;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.core.api.lifecycle.LifecycleUtils;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.mule.runtime.http.api.client.proxy.ProxyConfig;
import org.mule.runtime.http.api.tcp.TcpClientSocketProperties;
import org.mule.runtime.http.api.tcp.TcpClientSocketPropertiesBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prodapt.mulesoft.connectors.matrixx.api.connection.MandatoryTlsParameterGroup;
import com.prodapt.mulesoft.connectors.matrixx.api.connection.TlsParameterGroup;
import com.prodapt.mulesoft.connectors.matrixx.api.proxy.HttpProxyConfig;
import com.prodapt.mulesoft.connectors.matrixx.api.request.KeyValue;
import com.prodapt.mulesoft.connectors.matrixx.api.request.QueryParam;
import com.prodapt.mulesoft.connectors.matrixx.api.request.RequestHeader;
import com.prodapt.mulesoft.connectors.matrixx.internal.connection.MatrixxConnection;

public class MatrixxConnectionProvider
		implements CachedConnectionProvider<MatrixxConnection>, Initialisable, Startable, Stoppable {

	private static final Logger logger = LoggerFactory.getLogger(MatrixxConnectionProvider.class);

	@Parameter
	private String baseURL;

//	@Parameter
//	private long client_number;
//	
//	@Parameter
//	private String auth_key;

//	private int responeTimeout;

	@RefName
	private String configName;

	@Parameter
	@Optional
	@NullSafe
	@DisplayName("Query Parameters")
	@Placement(tab = "Advanced", order = 1)
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	private List<RequestHeader> defaultHeaders;

	@Parameter
	@Optional
	@NullSafe
	@Placement(tab = "Advanced", order = 2)
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	private List<QueryParam> defaultQueryParams;

	@Parameter
	@Optional(defaultValue = "30")
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	@Summary("The timeout for establishing connections to the remote service")
	@Placement(tab = "Advanced", order = 3)
	private Integer connectionTimeout;

	@Parameter
	@Optional(defaultValue = "SECONDS")
	@Placement(tab = "Advanced", order = 4)
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	@Summary("A time unit which qualifies the Connection Timeout")
	private TimeUnit connectionTimeoutUnit = TimeUnit.SECONDS;

	@Parameter
	@Optional(defaultValue = "true")
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	@Placement(tab = "Advanced", order = 5)
	@Summary("If false, each connection will be closed after the first request is completed.")
	private boolean usePersistentConnections = true;

	@Parameter
	@Optional(defaultValue = "-1")
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	@Placement(tab = "Advanced", order = 6)
	@Summary("The maximum number of outbound connections that will be kept open at the same time")
	private Integer maxConnections;

	@Parameter
	@Optional(defaultValue = "30")
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	@Placement(tab = "Advanced", order = 7)
	@Summary("A timeout for how long a connection can remain idle before it is closed")
	private Integer connectionIdleTimeout;

	@Parameter
	@Optional(defaultValue = "SECONDS")
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	@Placement(tab = "Advanced", order = 8)
	@Summary("A time unit which qualifies the connection Idle Timeout")
	private TimeUnit connectionIdleTimeoutUnit = TimeUnit.SECONDS;

	@Parameter
	@Optional(defaultValue = "false")
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	@Placement(tab = "Advanced", order = 9)
	@Summary("Whether or not received responses should be streamed")
	private boolean streamResponse = false;

	@Parameter
	@Optional(defaultValue = "-1")
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	@Placement(tab = "Advanced", order = 10)
	@Summary("The space in bytes for the buffer where the HTTP response will be stored.")
	private int responseBufferSize;

	@Parameter
	@Optional
	@Summary("Reusable configuration element for outbound connections through a proxy")
	@Placement(tab = "Proxy")
	private HttpProxyConfig proxyConfig;

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public boolean isUsePersistentConnections() {
		return usePersistentConnections;
	}

	/*
	 * public int getResponeTimeout() { return responeTimeout; }
	 * 
	 * public void setResponeTimeout(int responeTimeout) { this.responeTimeout =
	 * responeTimeout; }
	 */

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public TimeUnit getConnectionTimeoutUnit() {
		return connectionTimeoutUnit;
	}

	public void setConnectionTimeoutUnit(TimeUnit connectionTimeoutUnit) {
		this.connectionTimeoutUnit = connectionTimeoutUnit;
	}

	public Integer getConnectionIdleTimeout() {
		return connectionIdleTimeout;
	}

	public void setConnectionIdleTimeout(Integer connectionIdleTimeout) {
		this.connectionIdleTimeout = connectionIdleTimeout;
	}

	public TimeUnit getConnectionIdleTimeoutUnit() {
		return connectionIdleTimeoutUnit;
	}

	public void setConnectionIdleTimeoutUnit(TimeUnit connectionIdleTimeoutUnit) {
		this.connectionIdleTimeoutUnit = connectionIdleTimeoutUnit;
	}

	public boolean isStreamResponse() {
		return streamResponse;
	}

	public void setStreamResponse(boolean streamResponse) {
		this.streamResponse = streamResponse;
	}

	public int getResponseBufferSize() {
		return responseBufferSize;
	}

	public void setResponseBufferSize(int responseBufferSize) {
		this.responseBufferSize = responseBufferSize;
	}

	public void setUsePersistentConnections(boolean usePersistentConnections) {
		this.usePersistentConnections = usePersistentConnections;
	}

	public Integer getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(Integer maxConnections) {
		this.maxConnections = maxConnections;
	}

	@ParameterGroup(name = "tls")
	private MandatoryTlsParameterGroup tlscofig;

	private TlsContextFactoryBuilder defaultTlsContextFactoryBuilder = TlsContextFactory.builder();

	private TlsContextFactory effectiveTlsContext = null;

	private HttpClient httpClient;

	@Inject
	HttpService httpService;

	@Override
	public MatrixxConnection connect() throws ConnectionException {
		try {
			logger.info("Before creating connection from Connection class");
			return createConnection(baseURL, getConfigName(), httpClient,
					toMultiMap((Collection) this.defaultQueryParams), toMultiMap((Collection) this.defaultHeaders));
		} catch (Exception e) {
			throw new ConnectionException("Connection cannot be created", e);
		}

	}

	@Override
	public void disconnect(MatrixxConnection connection) {
		logger.info("Before disconnecting the connection from Connection class");
		try {
			connection.stop();
			logger.info("After disconnecting the connection from Connection class");
		} finally {
			this.effectiveTlsContext = null;
		}

	}

	@Override
	public ConnectionValidationResult validate(MatrixxConnection connection) {
		logger.info("In Validate method()");
		ConnectionValidationResult result = null;
		try {

			/*
			 * HttpResponse response = connection .getHttpResponse(this.baseURL,
			 * this.client_number, this.auth_key, this.responeTimeout).get(); String
			 * responseMessage = new String(response.getEntity().getBytes());
			 * logger.info("Response from test connection call" + responseMessage);
			 * JsonObject responseJSON = new Gson().fromJson(responseMessage,
			 * JsonObject.class); result = responseJSON.get("error_code").getAsInt() == 0 ?
			 * ConnectionValidationResult.success() :
			 * ConnectionValidationResult.failure(responseJSON.get("error_msg").getAsString(
			 * ), new Exception());
			 */
			result = ConnectionValidationResult.success();
			logger.info("result:" + result.getMessage());
		} catch (Exception e) {
			logger.error("Error occurred int validate() method:", e);

		}
		return result;

	}

	protected MatrixxConnection createConnection(String baseURL, String configName, HttpClient httpClient,
			MultiMap<String, String> defaultQueryParams, MultiMap<String, String> defaultHeaders) {
		return new MatrixxConnection(getBaseURL(), getConfigName(), httpClient, defaultQueryParams, defaultHeaders);
	}

	private MultiMap<String, String> toMultiMap(Collection<? extends KeyValue> keyValues) {
		MultiMap<String, String> multiMap = new MultiMap();
		if (keyValues != null) {
			keyValues.forEach(kv -> multiMap.put(kv.getKey(), kv.getValue()));
		}
		return multiMap;
	}

	public java.util.Optional<TlsParameterGroup> getTlsConfig() {
		return (java.util.Optional) java.util.Optional.ofNullable(this.tlscofig);
	}

	@Override
	public void stop() throws MuleException {
		this.httpClient.stop();

	}

	@Override
	public void start() throws MuleException {
		startHttpClient();

	}

	@Override
	public final void initialise() throws InitialisationException {
		initialiseTls();
		verifyConnectionsParameters();

	}

	/*
	 * protected java.util.Optional<TlsParameterGroup> getTlsConfig() { return
	 * java.util.Optional.empty(); }
	 */

	private void initialiseTls() throws InitialisationException {
		TlsParameterGroup tls = getTlsConfig().orElse(null);
		if (tls == null)
			return;
		HttpConstants.Protocol protocol = tls.getProtocol();
		TlsContextFactory tlsContext = tls.getTlsContext();
		if (protocol.equals(HttpConstants.Protocol.HTTP) && tlsContext != null)
			throw new InitialisationException(I18nMessageFactory.createStaticMessage(
					"TlsContext cannot be configured with protocol HTTP, when using tls:context you must set attribute protocol=\"HTTPS\""),
					this);
		if (protocol.equals(HttpConstants.Protocol.HTTPS) && tlsContext == null) {
			LifecycleUtils.initialiseIfNeeded(this.defaultTlsContextFactoryBuilder);
			tlsContext = this.defaultTlsContextFactoryBuilder.buildDefault();
		}
		if (tlsContext != null) {
			LifecycleUtils.initialiseIfNeeded(tlsContext);
			this.effectiveTlsContext = tlsContext;
		}
	}

	private void verifyConnectionsParameters() throws InitialisationException {
		if (getMaxConnections().intValue() == 0 || getMaxConnections().intValue() < -1)
			throw new InitialisationException(I18nMessageFactory.createStaticMessage(
					"The maxConnections parameter only allows positive values or -1 for unlimited concurrent connections."),
					this);
		if (isUsePersistentConnections())
			this.connectionIdleTimeout = Integer.valueOf(0);
	}

	private void startHttpClient() {
		HttpClientConfiguration.Builder configuration = (new HttpClientConfiguration.Builder())
				.setTlsContextFactory(this.effectiveTlsContext).setProxyConfig((ProxyConfig) this.proxyConfig)
				.setMaxConnections(this.maxConnections.intValue())
				.setUsePersistentConnections(this.usePersistentConnections)
				.setConnectionIdleTimeout(asMillis(this.connectionIdleTimeout, this.connectionTimeoutUnit))
				.setStreaming(this.streamResponse).setResponseBufferSize(this.responseBufferSize)
				.setName(String.format("rest.connect.%s", new Object[] { this.configName }));
		configureTcpSocket(configuration);
		configureClient(configuration);
		this.httpClient = this.httpService.getClientFactory().create(configuration.build());
		this.httpClient.start();
		logger.info("http client started");
	}

	private int asMillis(Integer value, TimeUnit unit) {
		if (value == null || value.intValue() == -1)
			return -1;
		return Long.valueOf(unit.toMillis(value.intValue())).intValue();
	}

	private void configureTcpSocket(HttpClientConfiguration.Builder configuration) {
		TcpClientSocketPropertiesBuilder socketProperties = TcpClientSocketProperties.builder();
		if (this.connectionTimeout != null)
			socketProperties
					.connectionTimeout(Integer.valueOf(asMillis(this.connectionTimeout, this.connectionTimeoutUnit)));
		configuration.setClientSocketProperties(socketProperties.build());
	}

	protected void configureClient(HttpClientConfiguration.Builder httpConfiguration) {
	}

}
