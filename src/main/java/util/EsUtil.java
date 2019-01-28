package util;

import bean.*;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EsUtil {

    AddDocuments adddocuments = new AddDocuments();
    TransportClient client;
     List<Map<String,Object>> esData = new ArrayList<>();
    ArrayList<ParkingSlot> arrayList;
    String[] find;
    String id;
    int j;
    EsAccess esAccess = new EsAccess();

    public void dataDocument(Object collection)
    {

        List<Map<String,Object>> esData = new EsAccess().find(collection);
        if(esData == null)
        {
            arrayList = adddocuments.addDocuments();

            for(ParkingSlot pa : arrayList){
                String id = pa.getId();
                String json = new AddDocuments().ArrayToJsonEs(pa);
                new EsAccess().insert(json,id,collection);
            }
        }

    }
    public void assignCarSlot(Car car,Object connection)
    {
        boolean tru = true;
        client = (TransportClient)connection;
         SearchResponse response = esAccess.findOneDocument(connection);
        SearchHit []result = response.getHits().getHits();
         if(response == null)
             throw new ParkingFullException("Parking is full");
         else {
             for (SearchHit hits : response.getHits()) {
                 id = hits.getId();
                 esData.add(hits.getSourceAsMap());
                 System.out.println(id);

                 for (Map<String, Object > map : esData) {
                     for (Map.Entry<String,Object> entry : map.entrySet()) {
                         String key = entry.getKey();
                         Object value = entry.getValue();
                         if (key == "isfilled") {
                                if((Boolean)value == false) {
                                    j = 1;
                                    break;
                                }
                         }
                     }
                     if (j == 1)
                         break;
                 }
                 if (j == 1)
                     break;
             }
             j = 0;
         }

        System.out.println(car.getColor()+" : "+car.getRegistrationNumber());
       String json ="{"+
               "\"color\":\""+car.getColor()+"\","+
               "\"reg_number\":\""+car.getRegistrationNumber()+"\","+
               "\"isfilled\":"+tru+
               "}";
        esAccess.update(connection,json,id);

    }

    public void removeCar(Car car,Object connection)
    {
            esAccess.remove(car,connection);

    }

    public void getData(Car car,String finder,Object collection) {
        find = adddocuments.datatoget(car, finder);
        esData = esAccess.find(find, collection);
        if (esData == null)
            System.out.println("No such entry");
        else {
            for (Map<String, Object> map : esData) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    System.out.print(key + " : " + value + " , ");
                }
                System.out.println();
            }
        }
    }

}
