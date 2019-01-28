package db;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

public class JongoAdapter  {
    public static MongoCollection getConnection(){

        DB db = new MongoClient().getDB("mydb");
        Jongo jongo = new Jongo(db);
        MongoCollection collection = jongo.getCollection("car");
        return collection;
    }
}
