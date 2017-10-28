package com.sully90.search.client;

import com.sully90.elasticutils.persistence.elastic.client.ElasticSearchClient;
import com.sully90.models.search.Pipeline;
import com.sully90.models.search.Searchable;
import com.sully90.search.util.ElasticIndex;

import java.util.List;
import java.util.stream.Stream;

public class OpenNLPElasticSearchClient<T> extends ElasticSearchClient<T> {

    private Pipeline pipeline;

    public OpenNLPElasticSearchClient(ElasticIndex indexName, Class<? extends Searchable> returnClass) {
        super(indexName.getIndexName(), (Class<T>) returnClass);
    }

    @Override
    public void index(T entity) {
        indexWithPipeline(entity, Pipeline.OPENNLP.getPipelineName());
    }

    @Override
    public void index(List<T> entities) {
        indexWithPipeline(entities, Pipeline.OPENNLP.getPipelineName());
    }

    @Override
    public void index(Stream<T> entities) {
        indexWithPipeline(entities, Pipeline.OPENNLP.getPipelineName());
    }

}
