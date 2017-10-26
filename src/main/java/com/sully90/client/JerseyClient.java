package com.sully90.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sully90.http.HttpStatusCode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class JerseyClient {

    private Client client;

    private String hostName;
    private int port;
    private String applicationName;

    public JerseyClient(String hostName, int port, String applicationName) {
        this.hostName = hostName;
        this.port = port;
        this.applicationName = applicationName;

        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);

        this.client = Client.create(cc);
    }

    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ClientResponse get(String path, ClientResponseType responseType) throws IOException {
        WebResource webResource = this.getWebResource(path);

        ClientResponse clientResponse = webResource.accept(responseType.getType())
                .get(ClientResponse.class);

        if (clientResponse.getStatus() != HttpStatusCode.OK.getCode()) {
            throw new RuntimeException("Failed: Http code: " + clientResponse.getStatus());
        }

        return clientResponse;
    }

    public ClientResponse post(String path, String input, ClientResponseType responseType) throws IOException {
        WebResource webResource = this.getWebResource(path);

        ClientResponse clientResponse = webResource.type(responseType.getType())
                .post(ClientResponse.class, input);

        if (clientResponse.getStatus() != HttpStatusCode.CREATED.getCode()) {
            throw new RuntimeException("Failed: Http code: " + clientResponse.getStatus());
        }

        return clientResponse;
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
