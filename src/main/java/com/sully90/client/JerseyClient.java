package com.sully90.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sully90.http.HttpStatusCode;
import com.sully90.server.models.RestResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class JerseyClient {

    private Client client;

    private String hostName;
    private int port;
    private String applicationName;

    private static ObjectMapper objectMapper = new ObjectMapper();

    public JerseyClient(String hostName, int port, String applicationName) {
        this.hostName = hostName;
        this.port = port;
        this.applicationName = applicationName;

        this.client = Client.create();
    }

    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public RestResponse get(String path, ClientResponseType responseType) throws IOException {
        WebResource webResource = this.getWebResource(path);

        ClientResponse clientResponse = webResource.accept(responseType.getType())
                .get(ClientResponse.class);

        if (clientResponse.getStatus() != HttpStatusCode.OK.getCode()) {
            throw new RuntimeException("Failed: Http code: " + clientResponse.getStatus());
        }

        String output = clientResponse.getEntity(String.class);

        return objectMapper.readValue(output, RestResponse.class);
    }

    public RestResponse post(String path, String input, ClientResponseType responseType) throws IOException {
        WebResource webResource = this.getWebResource(path);

        ClientResponse clientResponse = webResource.type(responseType.getType())
                .post(ClientResponse.class, input);

        if (clientResponse.getStatus() != HttpStatusCode.CREATED.getCode()) {
            throw new RuntimeException("Failed: Http code: " + clientResponse.getStatus());
        }

        String output = clientResponse.getEntity(String.class);

        return objectMapper.readValue(output, RestResponse.class);
    }

    private WebResource getWebResource(String path) {
        return client.resource(this.getURL(path));

    }

    private String getURL(String path) {
        StringBuilder builder = new StringBuilder(this.hostName + ":" + this.port + "/");
        builder
                .append(this.applicationName)
                .append("/rest/")
                .append(path);
        String url = builder.toString();
        System.out.println(url);
        return url;
    }

}
