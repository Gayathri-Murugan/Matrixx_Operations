package com.prodapt.mulesoft.connectors.matrixx.internal.error;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public class MatrixxErrorTypeProvider implements ErrorTypeProvider {

	public Set<ErrorTypeDefinition> getErrorTypes() {
	    return  Stream.<MatrixxErrorTypes>of(MatrixxErrorTypes.values()).collect(Collectors.toSet());
	  }
}
