package util;

import bean.Car;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EsAccess  {
    AddDocuments adddocuments = new AddDocuments();
    TransportClient client;
     List<Map<String,Object>> esData = new ArrayList<>();
    int scrollSize = 1000,i=0;
    SearchResponse response = null;
    QueryBuilders queryBuilders = null;





    public void insert(String json,String id,Object collection)
        {
            client = (TransportClient) collection;
            client.prepareIndex("car","_doc",id).setSource(json, XContentType.JSON).get();

        }
        public List<Map<String ,Object>> find(Object collection)
        {
            client = (TransportClient) collection;

            int i = 0;
            while (response == null || response.getHits().getHits().length != 0) {
                response = client.prepareSearch("car")
                        .setTypes("_doc")
                        .setSearchType(SearchType.QUERY_THEN_FETCH)
                        .setQuery(QueryBuilders.matchAllQuery())
                        .setFrom(i * scrollSize)
                        .addSort("_id",SortOrder.ASC)
                        .execute()
                        .actionGet();

                for (SearchHit hit : response.getHits()) {
                    esData.add(hit.getSourceAsMap());
                }
                i++;
            }
            return esData;
        }

        public List<Map<String, Object>> find(String [] find ,Object collection)
        {
             client = (TransportClient) collection;

            i = 0;
            response = null;
            esData = new ArrayList<>();
            while (response == null || response.getHits().getHits().length != 0) {
                response = client.prepareSearch("car")
                        .setTypes("_doc")
                        .setSearchType(SearchType.QUERY_THEN_FETCH)
                        .setQuery(QueryBuilders.termQuery(find[0], find[1]))
                        .setFrom(i * scrollSize)
                        .addSort("_id",SortOrder.ASC).setSize(101)
                        .execute()
                        .actionGet();
                for (SearchHit hit : response.getHits())
                    esData.add(hit.getSourceAsMap());
                i++;
            }
            return esData;

        }




        public void remove(Car car,Object connection)
        {
            client = (TransportClient) connection;
            BulkByScrollResponse deleteDoc = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                    .filter(QueryBuilders.matchQuery("reg_number",car.getRegistrationNumber()))
                    .source("car")
                    .get();
            long delete = deleteDoc.getDeleted();

        }




        public void update(Object collection,String json,String id)
        {
            client = (TransportClient)collection;
            client.prepareUpdate("car","_doc",id)
                    .setDoc(json.getBytes(), XContentType.JSON)
                    .get();
        }




        public SearchResponse findOneDocument(Object collection) {
            i = 0;
            QueryBuilder qb = QueryBuilders.matchAllQuery();
            client = (TransportClient)collection;

            response = client.prepareSearch("car")
                    .setTypes("_doc")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.termQuery("isfilled", false))
                    .setFrom(i * scrollSize).setSize(1)
                    .addSort("_id",SortOrder.ASC)
                    .setSize(101).execute().actionGet();
            return response;


        }
}
