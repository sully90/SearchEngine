package com.sully90.rest;

import com.sully90.core.engine.SearchEngine;
import com.sully90.core.ml.neuralnet.Net;
import com.sully90.core.ml.neuralnet.models.Topology;
import com.sully90.core.models.Movie;
import com.sully90.core.persistence.elastic.ml.ScoreScript;
import com.sully90.core.persistence.elastic.ml.builders.ScoreScriptBuilder;
import com.sully90.core.persistence.elastic.utils.ElasticIndex;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.lucene.search.function.FiltersFunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.sully90.http.HttpResponse.ok;

@Path("/search")
public class SearchEngineService {

    private static Logger LOGGER = LoggerFactory.getLogger(SearchEngineService.class);

    private static SearchEngine<Movie> searchEngine;
    private static Map<String, Double> fieldWeights;

    public static void init() {
        Topology topology = new Topology(Arrays.asList(5, 50, 2, 10, 1));
        Net myNet = new Net(topology);

        fieldWeights = new LinkedHashMap<String, Double>() {{
            put("popularity", 0.5);
            put("averageVote", 0.5);
        }};

        try {
            searchEngine = new SearchEngine<Movie>(myNet, ElasticIndex.MOVIES, Movie.class);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/{query}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response searchMovies(@PathParam("query") String query) {
        QueryBuilder qb = buildQuery(query, fieldWeights);

        if (LOGGER.isDebugEnabled()) LOGGER.debug("SearchEngineService: searchMovies: got query: " + query);

        SearchHits searchHits = searchEngine.search(qb, SearchType.DFS_QUERY_THEN_FETCH);
        List<Movie> movies = searchEngine.deserialize(searchHits);

        return ok(movies);
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
