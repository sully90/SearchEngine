package com.sully90.server;

import com.sully90.models.Movie;
import com.sully90.persistence.elastic.util.ElasticIndex;
import com.sully90.search.client.OpenNLPElasticSearchClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Update {

    public static void main(String[] args) throws Exception {
        // Re-index all movies into Elasticsearch

        Iterable<Movie> iterable = Movie.finder().find();
        OpenNLPElasticSearchClient<Movie> searchClient = new OpenNLPElasticSearchClient<Movie>(ElasticIndex.MOVIES, Movie.class);

        List<Movie> movies = new ArrayList<>();
        iterable.forEach(movies::add);

        System.out.println(movies.size());

        searchClient.index(movies);

        // The Bulk Insert is asynchronous, we give ElasticSearch some time to do the insert:
        searchClient.awaitClose(1, TimeUnit.MINUTES);
    }

}
