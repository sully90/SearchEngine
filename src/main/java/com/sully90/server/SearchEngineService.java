package com.sully90.server;

import com.sully90.elasticutils.persistence.elastic.ml.ScoreScript;
import com.sully90.elasticutils.persistence.elastic.ml.builders.ScoreScriptBuilder;
import com.sully90.models.Movie;
import com.sully90.persistence.elastic.util.ElasticIndex;
import com.sully90.search.client.OpenNLPElasticSearchClient;
import com.sully90.server.models.UpdateRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.lucene.search.function.FiltersFunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.search.SearchHits;
import org.glassfish.jersey.server.mvc.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.sully90.http.HttpResponse.created;
import static com.sully90.http.HttpResponse.ok;

@Path("/search")
public class SearchEngineService {

    private static Logger LOGGER = LoggerFactory.getLogger(SearchEngineService.class);

    private static OpenNLPElasticSearchClient<Movie> searchEngine;
    private static Map<String, Double> fieldWeights;

    static {
        init();
    }

    public static void init() {
        fieldWeights = new LinkedHashMap<String, Double>() {{
            put("popularity", 0.25);
            put("averageVote", 0.75);
        }};

        searchEngine = new OpenNLPElasticSearchClient<>(ElasticIndex.MOVIES, Movie.class);
    }

    @GET
    @Path("/index")
    @Produces({ MediaType.TEXT_HTML })
    public Viewable index() {
        Map<String, String> model = new HashMap<>();
        model.put("hello", "Hello");
        model.put("world", "World");
        return new Viewable("/index", model);
    }

    @GET
    @Path("json/{query}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response searchMovies(@PathParam("query") String query) {
        QueryBuilder qb = buildQuery(query, fieldWeights);

        if (LOGGER.isDebugEnabled()) LOGGER.debug("SearchEngineService: searchMovies: got query: " + query);

        SearchHits searchHits = searchEngine.search(qb, SearchType.DFS_QUERY_THEN_FETCH);
        List<Movie> movies = searchEngine.deserialize(searchHits);

        return ok(movies);
    }


    @POST
    @Path("/json/update/post")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response update(UpdateRequest updateRequest) {
        return created(updateRequest);
    }


    private static QueryBuilder buildQuery(String queryText, Map<String, Double> fieldWeights) {
        QueryBuilder match = QueryBuilders.multiMatchQuery(
                queryText, "title", "overview", "tagLine"
        );

        ScoreScript<Movie> scoreScript = new ScoreScript<>(Movie.class);

        for (String key : fieldWeights.keySet()) {
            scoreScript.builder().add(key, fieldWeights.get(key), ScoreScriptBuilder.ScriptOperator.MULTIPLY, ScoreScriptBuilder.ScriptOperator.MULTIPLY);
        }

        ScriptScoreFunctionBuilder scriptScoreFunctionBuilder = scoreScript.getScript();
        QueryBuilder qb = QueryBuilders.functionScoreQuery(match, scriptScoreFunctionBuilder)
                .scoreMode(FiltersFunctionScoreQuery.ScoreMode.AVG);

        return qb;
    }

}
