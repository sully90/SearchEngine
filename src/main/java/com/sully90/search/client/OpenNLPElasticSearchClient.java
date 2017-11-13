package com.sully90.search.client;

import com.sully90.elasticutils.persistence.elastic.client.ElasticSearchClient;
import com.sully90.models.search.Pipeline;
import com.sully90.models.search.Searchable;
import com.sully90.persistence.elastic.util.ElasticIndex;
import org.elasticsearch.client.Client;

import java.util.List;
import java.util.stream.Stream;

public class OpenNLPElasticSearchClient<T> extends ElasticSearchClient<T> {

    private Pipeline pipeline;

    public OpenNLPElasticSearchClient(ElasticIndex indexName, Class<? extends Searchable> returnClass) {
        super(indexName, (Class<T>) returnClass);
    }

    public OpenNLPElasticSearchClient(final Client client, ElasticIndex indexName, Class<? extends Searchable> returnClass) {
        super(client, indexName, (Class<T>) returnClass);
    }

    @Override
    public void index(T entity) {
        super.indexWithPipeline(entity, Pipeline.OPENNLP.getPipelineName());
    }

    @Override
    public void index(List<T> entities) {
        super.indexWithPipeline(entities, Pipeline.OPENNLP.getPipelineName());
    }

    @Override
    public void index(Stream<T> entities) {
        super.indexWithPipeline(entities, Pipeline.OPENNLP.getPipelineName());
    }

}
