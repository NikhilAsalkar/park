package util;

import bean.Car;
import bean.ParkingSlot;
import com.mongodb.BasicDBObject;
import org.jongo.Find;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import java.util.ArrayList;

public class MongoUtil implements CallMethods{
    ArrayList<ParkingSlot> arrayList;
    AddDocuments adddocuments = new AddDocuments();
    MongoAccess mongoAccess = new MongoAccess();
    MongoCollection client ;
    BasicDBObject newdocument = new BasicDBObject();
    String find[];


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

    @Override
    public void assignCarSlot(Car car,Object connection) {

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
    }

    @Override
    public void removeCar(Car car,Object connection) {

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
    }

    @Override
    public void getData(Car car, String finder,Object connection) {
        find=adddocuments.datatoget(car,finder);
        MongoCursor<ParkingSlot> source1 = mongoAccess.fetchData(find,connection);
        if(source1 == null)
            System.out.println("No record found");
        else
            for(ParkingSlot pa : source1)
                System.out.println(pa.toString());




    }


}
