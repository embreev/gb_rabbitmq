package com.geekbrains.decembermarket.soap;

import com.geekbrains.decembermarket.soap.catalog.GetProductsListRequest;
import com.geekbrains.decembermarket.soap.catalog.GetProductsListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CatalogEndpoint {
    private static final String CATALOG_NAMESPACE_URI = "http://www.decembermarket.geekbrains.com/soap/catalog";

    private SoapCatalogService soapCatalogService;

    @Autowired
    public void setSoapCatalogService(SoapCatalogService soapCatalogService) {
        this.soapCatalogService = soapCatalogService;
    }

    @PayloadRoot(namespace = CATALOG_NAMESPACE_URI, localPart = "getProductsListRequest")
    @ResponsePayload
    public GetProductsListResponse getProductsListResponse(@RequestPayload GetProductsListRequest request) {
        GetProductsListResponse response = new GetProductsListResponse();
        response.setClientName("Hello, " + request.getName());
        response.setProductsList(soapCatalogService.getAllProductsList());
        return response;
    }
}
