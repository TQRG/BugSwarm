package com.microsoft.azure.management.network.implementation;

import com.microsoft.azure.CloudException;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.NetworkInterfaces;
import com.microsoft.azure.management.network.Networks;
import com.microsoft.azure.management.network.PublicIpAddresses;
import com.microsoft.azure.management.network.implementation.api.NetworkInterfaceInner;
import com.microsoft.azure.management.network.implementation.api.NetworkInterfacesInner;
import com.microsoft.azure.management.network.implementation.api.NetworkInterfaceIPConfiguration;
import com.microsoft.azure.management.network.implementation.api.NetworkInterfaceDnsSettings;
import com.microsoft.azure.management.resources.ResourceGroups;
import com.microsoft.azure.management.resources.fluentcore.arm.ResourceUtils;
import com.microsoft.azure.management.resources.fluentcore.utils.PagedListConverter;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The type representing Azure network interfaces.
 */
class NetworkInterfacesImpl implements NetworkInterfaces {
    private final NetworkInterfacesInner client;
    private final Networks networks;
    private final PublicIpAddresses publicIpAddresses;
    private final ResourceGroups resourceGroups;

    private final PagedListConverter<NetworkInterfaceInner, NetworkInterface> converter;

    NetworkInterfacesImpl(final NetworkInterfacesInner client,
                          final Networks networks,
                          final PublicIpAddresses publicIpAddresses,
                          final ResourceGroups resourceGroups) {
        this.client = client;
        this.networks = networks;
        this.publicIpAddresses = publicIpAddresses;
        this.resourceGroups = resourceGroups;
        this.converter = new PagedListConverter<NetworkInterfaceInner, NetworkInterface>() {
            @Override
            public NetworkInterface typeConvert(NetworkInterfaceInner inner) {
                return createFluentModel(inner);
            }
        };
    }

    @Override
    public PagedList<NetworkInterface> list() throws CloudException, IOException {
        ServiceResponse<PagedList<NetworkInterfaceInner>> response = client.listAll();
        return converter.convert(response.getBody());
    }

    @Override
    public PagedList<NetworkInterface> listByGroup(String groupName) throws CloudException, IOException {
        ServiceResponse<PagedList<NetworkInterfaceInner>> response = client.list(groupName);
        return converter.convert(response.getBody());
    }

    @Override
    public NetworkInterface getByGroup(String groupName, String name) throws CloudException, IOException {
        ServiceResponse<NetworkInterfaceInner> serviceResponse = this.client.get(groupName, name);
        return createFluentModel(serviceResponse.getBody());
    }

    @Override
    public void delete(String id) throws Exception {
        this.delete(ResourceUtils.groupFromResourceId(id), ResourceUtils.nameFromResourceId(id));
    }

    @Override
    public void delete(String groupName, String name) throws Exception {
        this.client.delete(groupName, name);
    }

    @Override
    public NetworkInterface.DefinitionBlank define(String name) {
        return createFluentModel(name);
    }

    private NetworkInterfaceImpl createFluentModel(String name) {
        NetworkInterfaceInner inner = new NetworkInterfaceInner();
        inner.withIpConfigurations(new ArrayList<NetworkInterfaceIPConfiguration>());
        inner.withDnsSettings(new NetworkInterfaceDnsSettings());
        return new NetworkInterfaceImpl(name,
                inner,
                this.client,
                this.networks,
                this.publicIpAddresses,
                this.resourceGroups);
    }

    private NetworkInterfaceImpl createFluentModel(NetworkInterfaceInner inner) {
        return new NetworkInterfaceImpl(inner.name(),
                inner,
                this.client,
                this.networks,
                this.publicIpAddresses,
                this.resourceGroups);
    }
}
