package com.prodapt.mulesoft.connectors.api.enums;

public enum OutputMode {
	JSON("json"),
	XML("xml");
	
	@SuppressWarnings("unused")
	private String outputModes;
	OutputMode(String outputModes) {
		this.outputModes = outputModes;
	}

}
