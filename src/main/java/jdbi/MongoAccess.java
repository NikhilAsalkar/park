package jdbi;


import bean.ParkingSlot;

import org.jongo.MongoCollection;

import org.jongo.MongoCursor;

import java.util.ArrayList;

public class MongoAccess {

    MongoCollection collection;
    String fin;
    String searchjson;
    MongoCursor<ParkingSlot> source;

    public void update(Object document, String id){
        collection.update("{\"_id\":\""+ id +"\"}").with(document);
    }


    public ParkingSlot fetchOneDocument(Object connection)
    {

            collection = (MongoCollection) connection;
            return collection.findOne("{isfilled:false}").as(ParkingSlot.class);

    }


    public MongoCursor<ParkingSlot> fetchData(String[] find,Object connection,String[] search)
    {
        collection = (MongoCollection)connection;
        if(find == null)
            fin = "";
        else
            fin = "{"+find[0]+":'" + find[1] + "'}";
        if(search == null)
            searchjson ="";
        else if(search[1] == null)
            searchjson = "{"+search[0]+":1}";
        else
            searchjson="{"+search[0]+":1,"+search[1]+":1}";

           source = collection.find(fin).projection(searchjson).as(ParkingSlot.class);


        return source;

    }

    public MongoCursor<ParkingSlot> findCar(Object connection,String query)
    {
        collection = (MongoCollection)connection;
        source = collection.find(query).as(ParkingSlot.class);
        System.out.println(source);
        return source;
    }
}

