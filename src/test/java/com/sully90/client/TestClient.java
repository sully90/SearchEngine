package com.sully90.client;

import com.sully90.models.Movie;
import com.sully90.server.models.UpdateRequest;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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

        List<Movie> movies = new ArrayList<>();
        Movie.finder().find().forEach(movies::add);

        LinkedList<String> ids = new LinkedList<>();
        LinkedList<Integer> originalRanks = new LinkedList<>();
        LinkedList<Integer> judgements = new LinkedList<>();

        for (int i = 0; i < movies.size(); i++) {
            ids.add(movies.get(i).getMongoId());
            judgements.add(Integer.valueOf(movies.size() - i));
            originalRanks.add(Integer.valueOf(i));
        }

        UpdateRequest updateRequest = new UpdateRequest(ids, originalRanks, judgements);

        try {
            Response response = jerseyClient.post(path, updateRequest, ClientResponseType.JSON);

            if (response.getStatus() != 201) {
                System.out.println(response);
            } else {

                UpdateRequest returnedUpdateRequest = response.readEntity(new GenericType<UpdateRequest>() {
                });

                System.out.println(returnedUpdateRequest.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
