// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.sully90.core.persistence.elastic.client.bulk.configuration;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.Client;
import com.sully90.core.persistence.elastic.client.bulk.listener.LoggingBulkProcessorListener;
import com.sully90.core.persistence.elastic.client.bulk.options.BulkProcessingOptions;
import com.sully90.core.persistence.elastic.client.bulk.options.BulkProcessingOptionsBuilder;

public class BulkProcessorConfiguration {

    private BulkProcessingOptions options = new BulkProcessingOptionsBuilder().build();
    private BulkProcessor.Listener listener = new LoggingBulkProcessorListener();

    public BulkProcessorConfiguration(BulkProcessingOptions options)
    {
        this(options, new LoggingBulkProcessorListener());
    }

    public BulkProcessorConfiguration(BulkProcessingOptions options, BulkProcessor.Listener listener) {
        this.options = options;
        this.listener = listener;
    }

    public BulkProcessingOptions getBulkProcessingOptions() {
        return options;
    }

    public BulkProcessor.Listener getBulkProcessorListener() {
        return listener;
    }

    public BulkProcessor build(final Client client) {
        return BulkProcessor.builder(client, listener)
//                .setName(options.getName())
                .setConcurrentRequests(options.getConcurrentRequests())
                .setBulkActions(options.getBulkActions())
                .setBulkSize(options.getBulkSize())
                .setFlushInterval(options.getFlushInterval())
                .setBackoffPolicy(options.getBackoffPolicy())
                .build();
    }
}