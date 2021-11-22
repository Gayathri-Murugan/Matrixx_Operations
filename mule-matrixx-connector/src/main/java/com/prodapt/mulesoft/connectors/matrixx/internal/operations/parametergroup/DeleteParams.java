package com.prodapt.mulesoft.connectors.matrixx.internal.operations.parametergroup;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

public class DeleteParams {

	@Parameter
	@DisplayName("ObjectID")
	@Summary("Object Id to be deleted")
	@Expression(ExpressionSupport.SUPPORTED)
	private String ObjectID;

	public String getObjectID() {
		return ObjectID;
	}

	public void setObjectID(String objectID) {
		ObjectID = objectID;
	}

	
}
