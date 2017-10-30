package com.sully90.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sully90.http.HttpStatusCode;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

        this.client = ClientBuilder.newClient(new ClientConfig().register(JacksonJsonProvider.class));
    }

    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response get(String path, ClientResponseType responseType) throws IOException {
        WebTarget webTarget = this.getWebResource(path);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        if (response.getStatus() != HttpStatusCode.OK.getCode()) {
            throw new RuntimeException("JerseyClient: Got non 200 status code: " + response.getStatus());
        }

        return response;
    }

    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response post(String path, Object input, ClientResponseType responseType) throws IOException {
        WebTarget webTarget = this.getWebResource(path);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.post(Entity.entity(input, MediaType.APPLICATION_JSON));

        if (response.getStatus() != HttpStatusCode.CREATED.getCode()) {
            throw new RuntimeException("JerseyClient: Got non 201 status code: " + response.getStatus());
        }

        return response;
    }

    private WebTarget getWebResource(String path) {
        return client.target(this.getURL(path));

    }

    private String getURL(String path) {
        StringBuilder builder = new StringBuilder(this.hostName + ":" + this.port + "/");
        builder
                .append(this.applicationName)
//                .append("/rest/")
                .append("/")
                .append(path);
        String url = builder.toString();
        System.out.println(url);
        return url;
    }

}
