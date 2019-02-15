package jdbi;

import bean.*;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import util.AddDocuments;
import util.ParkingFullException;

import java.util.ArrayList;
import java.util.HashMap;
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
    boolean tru;
    EsAccess esAccess = new EsAccess();

    public void dataDocument(Object collection)
    {

        List<Map<String,Object>> esData = new EsAccess().find(collection);
        if(esData.isEmpty())
        {
            System.out.println("im here");
            arrayList = adddocuments.addDocuments();

            for(ParkingSlot pa : arrayList){
                String id = pa.getId();
                String json = new AddDocuments().ArrayToJsonEs(pa);
                new EsAccess().insert(json,id,collection);
            }
        }

    }
    public String assignCarSlot(Car car,Object connection)
    {
        tru = true;
        client = (TransportClient)connection;
        SearchResponse response = esAccess.findOneDocument(connection);
        SearchHit []result = response.getHits().getHits();
        if(response == null)
            throw new ParkingFullException("Parking is full");
        else {
            for (SearchHit hits : response.getHits()) {
                id = hits.getId();
                esData.add(hits.getSourceAsMap());

                for (Map<String, Object > map : esData) {
                    for (Map.Entry<String,Object> entry : map.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        if (key.equals("isfilled")) {
                            if(!(Boolean)value) {
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

        String json ="{"+
                "\"color\":\""+car.getColor()+"\","+
                "\"reg_number\":\""+car.getRegistrationNumber()+"\","+
                "\"isfilled\":"+tru+
                "}";
        System.out.println(id);
        esAccess.update(connection,json,id);
        return "";

    }

    public String removeCar(Car car,Object connection)
    {
        find = adddocuments.datatoget(car,"registernumber");
        SearchResponse response = esAccess.removeDocument(find,connection);
        // esAccess.remove(car,connection);
        for(SearchHit hit : response.getHits())
            id = hit.getId();
        tru = false;
        String color="null",reg="null";
        String json ="{"+
                "\"color\":\""+color+"\","+
                "\"reg_number\":\""+reg+"\","+
                "\"isfilled\":"+tru+
                "}";
        esAccess.update(connection,json,id);
        return "Removed";

    }

    public List<Map<String, Object>> getData(Car car, String finder, Object connection) {
        Map< String,Object> map =new HashMap<>();
        find = adddocuments.datatoget(car, finder);
        esData = esAccess.find(find, connection);
        if (esData == null)
            System.out.println("No such entry");

        return esData;
    }


    public List<Map<String, Object>> ifPresent(Car car , Object connection)
    {
        Map< String,Object> map =new HashMap<>();
        find = adddocuments.datatoget(car, "registernumber");
        esData = esAccess.find(find, connection);
        return esData;

    }

}
