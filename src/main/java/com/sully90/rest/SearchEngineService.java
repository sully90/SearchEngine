package com.sully90.rest;

import com.sully90.core.engine.SearchEngine;
import com.sully90.core.ml.neuralnet.Net;
import com.sully90.core.ml.neuralnet.models.Topology;
import com.sully90.core.models.Movie;
import com.sully90.core.persistence.elastic.query.QueryHelper;
import com.sully90.core.persistence.elastic.utils.ElasticIndex;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

@Path("/search")
public class SearchEngineService {

    private static Topology topology = new Topology(Arrays.asList(5, 50, 2, 10, 1));
    private static Net myNet = new Net(topology);

    @GET
    @Path("/{query}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response searchMovies(@PathParam("query") String query) {
        QueryBuilder qb = QueryHelper.matchField("title", query);

        try {
            SearchEngine<Movie> searchEngine = new SearchEngine<>(myNet, ElasticIndex.MOVIES, Movie.class);

            SearchHits searchHits = searchEngine.search(qb, SearchType.DFS_QUERY_THEN_FETCH);
            List<Movie> movies = searchEngine.deserialize(searchHits);

            return Response.status(200).entity(movies.toArray()).build();
        } catch (UnknownHostException e) {
            return Response.status(500).entity(e).build();
        }
    }

}
