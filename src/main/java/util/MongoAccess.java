package util;


import bean.ParkingSlot;

import org.jongo.MongoCollection;

import org.jongo.MongoCursor;

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
        collection = (MongoCollection)connection;
        if(find == null)
            fin = "";
        else
            fin = "{"+find[0]+":'" + find[1] + "'}";
        MongoCursor<ParkingSlot> source =
                collection.find(fin).as(ParkingSlot.class);
        return source;

    }
}

