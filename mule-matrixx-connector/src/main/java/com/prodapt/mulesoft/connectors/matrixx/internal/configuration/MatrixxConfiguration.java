package com.prodapt.mulesoft.connectors.matrixx.internal.configuration;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

import com.prodapt.mulesoft.connectors.matrixx.internal.connection.providers.MatrixxConnectionProvider;
import com.prodapt.mulesoft.connectors.matrixx.internal.operations.AddUserToSubscription;
import com.prodapt.mulesoft.connectors.matrixx.internal.operations.CreateDevice;
import com.prodapt.mulesoft.connectors.matrixx.internal.operations.CreateSubscription;
import com.prodapt.mulesoft.connectors.matrixx.internal.operations.CreateUser;
import com.prodapt.mulesoft.connectors.matrixx.internal.operations.DeleteDevice;
import com.prodapt.mulesoft.connectors.matrixx.internal.operations.DeleteUser;

@Operations({ CreateUser.class,CreateSubscription.class,AddUserToSubscription.class,CreateDevice.class,DeleteDevice.class,DeleteUser.class})

@ConnectionProviders(MatrixxConnectionProvider.class)
public class MatrixxConfiguration extends RestConfiguration {
}