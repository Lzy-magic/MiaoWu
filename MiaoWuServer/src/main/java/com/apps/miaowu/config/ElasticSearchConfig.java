package com.apps.miaowu.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author lzy
 * @create 2020/8/11 16:36
 *
 * Elasticsearch REST client. Host, port and scheme come from
 * miaowu.elasticsearch.* (defaults: localhost:9200, http).
 */
@Configuration
public class ElasticSearchConfig {

    @Value("${miaowu.elasticsearch.host:localhost}")
    private String host;

    @Value("${miaowu.elasticsearch.port:9200}")
    private int port;

    @Value("${miaowu.elasticsearch.scheme:http}")
    private String scheme;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, scheme)));
    }
}
