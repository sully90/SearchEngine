package com.sully90.server;

import com.sully90.elasticutils.persistence.elastic.ml.ScoreScript;
import com.sully90.elasticutils.persistence.elastic.ml.builders.ScoreScriptBuilder;
import com.sully90.models.Movie;
import com.sully90.nlp.opennlp.OpenNLPService;
import com.sully90.persistence.elastic.util.ElasticIndex;
import com.sully90.search.client.OpenNLPElasticSearchClient;
import com.sully90.server.models.UpdateRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
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
import java.util.*;

import static com.sully90.http.HttpResponse.created;
import static com.sully90.http.HttpResponse.ok;

@Path("/search")
public class SearchEngineService {

    private static Logger LOGGER = LoggerFactory.getLogger(SearchEngineService.class);

    private static OpenNLPElasticSearchClient<Movie> searchEngine;
    private static Map<String, Double> fieldWeights;

    private static OpenNLPService openNLPService;

    public static void init() {
        fieldWeights = new LinkedHashMap<String, Double>() {{
//            put("popularity", 0.25);
            put("averageVote", 1.0d);
        }};

        searchEngine = new OpenNLPElasticSearchClient<>(ElasticIndex.MOVIES, Movie.class);
        openNLPService = new OpenNLPService();
    }

    @GET
    @Path("/index")
    @Produces({ MediaType.TEXT_HTML })
    public Viewable index() {
        return new Viewable("/index", null);
    }

    @GET
    @Path("json/{query}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response searchMovies(@PathParam("query") String query) {
        Map<String, Set<String>> namedEntities = openNLPService.getNamedEntities(query);

        System.out.println(namedEntities);

        QueryBuilder qb = buildQuery(query, namedEntities);

        if (LOGGER.isDebugEnabled()) LOGGER.debug("SearchEngineService: searchMovies: got query: " + query);

        SearchHits searchHits = searchEngine.search(qb, SearchType.DFS_QUERY_THEN_FETCH);
        List<Movie> movies = searchEngine.deserialize(searchHits);

        return ok(movies);
    }

    @GET
    @Path("/index/result/{mongoId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response result(@PathParam("mongoId") String mongoId) {
//        ObjectId id = new ObjectId(mongoId);
//        Movie movie = Movie.finder().findOne(id);
//        return ok(movie);
        QueryBuilder qb = QueryBuilders.matchQuery("mongoId", mongoId);
        List<Movie> hits = searchEngine.searchAndDeserialize(qb);
        return ok(hits);
    }

    @POST
    @Path("/json/update/post")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response update(UpdateRequest updateRequest) {
        return created(updateRequest);
    }


    private static QueryBuilder buildQuery(String queryText, Map<String, Set<String>> namedEntities) {
        QueryBuilder titleMatch = QueryBuilders.matchQuery("title", queryText).boost(5.0f);

        QueryBuilder overviewQuery = QueryBuilders.matchQuery("overview", queryText).boost(1.0f).lenient(true);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(titleMatch).should(overviewQuery);

        for (String key : namedEntities.keySet()) {
            Set<String> values = namedEntities.get(key);
            Iterator<String> itValues = values.iterator();
            while(itValues.hasNext()) {
                String value = itValues.next();

                // Query against entities.X field and boost
                QueryBuilder matchQuery = QueryBuilders.matchQuery("entities." + key, value)
                        .fuzzyTranspositions(false).operator(Operator.AND).boost(10.0f);
//                qb.should(matchQuery);
                boolQueryBuilder.should(matchQuery);

            }
        }

        ScoreScript<Movie> scoreScript = new ScoreScript<>(Movie.class);

        for (String key : fieldWeights.keySet()) {
            scoreScript.builder().add(key, fieldWeights.get(key), ScoreScriptBuilder.ScriptOperator.MULTIPLY, ScoreScriptBuilder.ScriptOperator.MULTIPLY);
        }

        ScriptScoreFunctionBuilder scriptScoreFunctionBuilder = scoreScript.getScript();
        QueryBuilder qb = QueryBuilders.functionScoreQuery(boolQueryBuilder, scriptScoreFunctionBuilder);

//        System.out.println(qb.toString());
        return qb;
    }


    @Deprecated
    private static QueryBuilder overviewQuery(String queryText, Map<String, Double> fieldWeights) {
        QueryBuilder match = QueryBuilders.matchQuery(
                "overview", queryText).boost(1.0f);

        ScoreScript<Movie> scoreScript = new ScoreScript<>(Movie.class);

        for (String key : fieldWeights.keySet()) {
            scoreScript.builder().add(key, fieldWeights.get(key), ScoreScriptBuilder.ScriptOperator.MULTIPLY, ScoreScriptBuilder.ScriptOperator.MULTIPLY);
        }

        ScriptScoreFunctionBuilder scriptScoreFunctionBuilder = scoreScript.getScript();
        QueryBuilder qb = QueryBuilders.functionScoreQuery(match, scriptScoreFunctionBuilder);

        return qb;
    }

}
