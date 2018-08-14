package com.qualys.meetup.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualys.meetup.exception.ServiceException;
import com.qualys.meetup.utils.ServiceConstant;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by aagarwal on 6/12/2018.
 */
@Service
public class ElasticService {

    @Autowired
    private Client client;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ID = "id";

    public LinkedList<Map<String, Object>> search(Map<String, String> queryParams, String type) {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(ServiceConstant.STUDENT_INDEX)
                .setIndicesOptions(IndicesOptions.fromOptions(true, true, true, true));

        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type);
        }

        if (queryParams.size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            queryParams.forEach((paramName, paramValue) -> {
                boolQueryBuilder.should(QueryBuilders.matchPhrasePrefixQuery(paramName, paramValue).slop(100));
            });
            boolQueryBuilder.minimumShouldMatch(String.valueOf(queryParams.size()));
            searchRequestBuilder.setQuery(boolQueryBuilder);
        } else {
            searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
        }

        SearchResponse searchResponse;
        try {
            searchResponse = searchRequestBuilder.execute().get();

        } catch (Exception e) {
            throw new ServiceException("ElasticSearch Query execution failed due to:" + e.getMessage(), e);
        }

        LinkedList<Map<String, Object>> response = new LinkedList<>();
        SearchHits searchHits = searchResponse.getHits();
        if(searchHits != null) {
            SearchHit[] searchHitArray = searchHits.getHits();
            if(searchHitArray != null) {
                LinkedList<Map<String, Object>> dataList = new LinkedList<>();
                for (SearchHit searchHit: searchHitArray) {
                    Map<String, Object> sourceMap = searchHit.getSource();
                    sourceMap.put(ID, searchHit.getId());
                    dataList.add(sourceMap);
                }
                response = dataList;
            }
        }
        return response;

    }

    public void search(Map<String, String> queryPparams) {
        search(queryPparams, null);
    }

    public void index(Object model, String indexName, String type, String primaryKey) {

        HashMap entityMap = objectMapper.convertValue(model, HashMap.class);

        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, type);

        if(primaryKey != null) {
            indexRequestBuilder.setId(primaryKey);
        }

        indexRequestBuilder.setSource(entityMap);
        indexRequestBuilder.setOpType(IndexRequest.OpType.CREATE);

        try {
            indexRequestBuilder.execute().get();
        } catch (Exception e){
            throw new ServiceException("Failed To Index",e);
        }
    }

}
