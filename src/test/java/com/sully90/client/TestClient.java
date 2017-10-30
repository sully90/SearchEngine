package com.sully90.client;

import com.sully90.models.Movie;
import com.sully90.server.models.UpdateRequest;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

public class TestClient {

    @Test
    public void testGet() {
        JerseyClient jerseyClient = new JerseyClient("http://localhost", 8080, "SearchEngine");

        String path = "search/json/Avatar";

        try {
            Response response = jerseyClient.get(path, ClientResponseType.JSON);
            List<Movie> movies = response.readEntity(new GenericType<List<Movie>>(){});

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
            Response response = jerseyClient.post(path, updateRequest, ClientResponseType.JSON);
            UpdateRequest returnedUpdateRequest = response.readEntity(new GenericType<UpdateRequest>(){});

            System.out.println(returnedUpdateRequest.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
