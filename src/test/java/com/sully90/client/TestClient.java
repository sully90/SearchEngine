package com.sully90.client;

import com.sully90.models.Movie;
import com.sully90.server.models.UpdateRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestClient {

    @Test
    public void testGet() {
        JerseyClient jerseyClient = new JerseyClient("http://localhost", 8080, "SearchEngine");

        String path = "search/Avatar";

        try {
            ClientResponse response = jerseyClient.get(path, ClientResponseType.JSON);
            List<Movie> movies = response.getEntity(new GenericType<List<Movie>>(){});

            System.out.println(movies.get(0).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPost() {
        JerseyClient jerseyClient = new JerseyClient("http://localhost", 8080, "SearchEngine");

        String path = "search/json/update/post";

        Movie movie = Movie.finder().findOne();
        UpdateRequest updateRequest = new UpdateRequest(movie.getObjectId().toString(), 1, 3);

        try {
            ClientResponse response = jerseyClient.post(path, updateRequest, ClientResponseType.JSON);

            UpdateRequest returnedUpdateRequest = response.getEntity(new GenericType<UpdateRequest>() {});

            System.out.println(returnedUpdateRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
