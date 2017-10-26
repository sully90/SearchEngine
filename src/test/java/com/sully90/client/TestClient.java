package com.sully90.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sully90.core.models.Movie;
import com.sully90.server.models.RestResponse;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestClient {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() {
        JerseyClient jerseyClient = new JerseyClient("http://localhost", 8080, "SearchEngine");

        String path = "search/James%20Bond";

        try {
            ClientResponse response = jerseyClient.get(path, ClientResponseType.JSON);
            RestResponse<Movie> restResponse = response.getEntity(new GenericType<RestResponse<Movie>>() {});

            List<Movie> movies = restResponse.getEntities();

            System.out.println("Got output. Status: " + restResponse.getStatus());
            System.out.println(movies.get(0).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
