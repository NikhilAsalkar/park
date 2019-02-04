package jdbi;


import bean.ParkingSlot;

import org.jongo.MongoCollection;

import org.jongo.MongoCursor;

import java.util.ArrayList;

public class MongoAccess {

    MongoCollection collection;
    String fin;

    public void update(Object document, String id){
        collection.update("{\"_id\":\""+ id +"\"}").with(document);
    }


    public ParkingSlot fetchOneDocument(Object connection)
    {
        collection = (MongoCollection)connection;
        return collection.findOne("{isfilled:false}").as(ParkingSlot.class);
    }


    public MongoCursor<ParkingSlot> fetchData(String[] find,Object connection)
    {
        MongoCursor<ParkingSlot> source;
        collection = (MongoCollection)connection;
        if(find == null)
            fin = "";
        else
            fin = "{"+find[0]+":'" + find[1] + "'}";
           source = collection.find(fin).as(ParkingSlot.class);


        return source;

    }
}

