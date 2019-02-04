package jdbi;

import bean.Car;
import bean.ParkingSlot;
import com.mongodb.BasicDBObject;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import util.AddDocuments;
import util.ParkingFullException;

import java.util.*;

public class MongoUtil {
    ArrayList<ParkingSlot> arrayList;
    AddDocuments adddocuments = new AddDocuments();
    MongoAccess mongoAccess = new MongoAccess();
    MongoCollection client ;
    BasicDBObject newdocument = new BasicDBObject();
    String find[];
    HashMap<String,String > hash = new HashMap<>();


    public void dataDocuments(Object connection){
        client = (MongoCollection) connection;
        String[] fin =null;
        MongoCursor<ParkingSlot> find = mongoAccess.fetchData(fin,connection);
        int count = find.count();
        if(count == 0)
        {
            arrayList = adddocuments.addDocuments();

            for(ParkingSlot pa : arrayList){

                String Json = new AddDocuments().ArrayListToJson(pa);
                client.insert(Json);

            }
        }
    }


    public String assignCarSlot(Car car,Object connection) {

        ParkingSlot parkingSlot = mongoAccess.fetchOneDocument(connection);

                if(parkingSlot == null)
                    throw new ParkingFullException("Parking is Full");
                else
                {
                    newdocument.put("color",car.getColor());
                    newdocument.put("reg_number",car.getRegistrationNumber());
                    newdocument.put("isfilled","true");
                    String id = parkingSlot.getId();
                    mongoAccess.update(newdocument,id);
                }
                return "";
    }

    public MongoCursor removeCar(Car car, Object connection) {

        find=adddocuments.datatoget(car,"registernumber");
        MongoCursor<ParkingSlot> source = mongoAccess.fetchData(find,connection);
        if(source == null)
            System.out.println("No record found");
        else {
            for (ParkingSlot pa : source) {
                newdocument.put("color", "null");
                newdocument.put("reg_number", "null");
                newdocument.put("isfilled", false);
                mongoAccess.update(newdocument, pa.getId());
            }
        }
        return null;
    }


    public List<ParkingSlot> getData(Car car, String finder, Object connection) {
        find=adddocuments.datatoget(car,finder);
        List<ParkingSlot> arrayList = new ArrayList();
        MongoCursor<ParkingSlot> source1 = mongoAccess.fetchData(find,connection);

       /* if(source1 == null)
            System.out.println("No record found");
        else
            for(ParkingSlot pa : source1)
                System.out.println(pa.toString());
        */


       for ( ParkingSlot pa: source1) {
           arrayList.add(pa);
       }
       return arrayList;
    }


}
