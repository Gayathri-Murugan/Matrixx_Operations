package com.prodapt.mulesoft.connectors.matrixx.api.operation.queryparam;

import org.mule.runtime.api.util.MultiMap;

public interface QueryParamFormatter {

	MultiMap<String, String> format(MultiMap<String, String> paramMultiMap);
}
