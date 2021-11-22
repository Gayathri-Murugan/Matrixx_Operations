package com.prodapt.mulesoft.connectors.matrixx.internal.extension;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

import com.prodapt.mulesoft.connectors.matrixx.internal.configuration.MatrixxConfiguration;

@Xml(prefix = "mule-matrixx-connector")
@Extension(name = "Matrixx Connector - Mule 4")
@Configurations(MatrixxConfiguration.class)
public class MatrixxConnector {

}
