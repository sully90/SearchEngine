package com.sully90.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sully90.core.models.Movie;
import com.sully90.server.models.RestResponse;
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
            RestResponse<Movie> restResponse = jerseyClient.get(path, ClientResponseType.JSON);

            List<Movie> movies = restResponse.getEntities();

            System.out.println("Got output. Status: " + restResponse.getStatus().getCode());
            System.out.println(movies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
