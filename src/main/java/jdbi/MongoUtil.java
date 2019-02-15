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
    List list;
    AddDocuments adddocuments = new AddDocuments();
    MongoAccess mongoAccess = new MongoAccess();
    MongoCollection client ;
    BasicDBObject newdocument = new BasicDBObject();
    String find[];
    HashMap<String,String > hash = new HashMap<>();
    int flag =0;

    public void dataDocuments(Object connection){
        client = (MongoCollection) connection;
        String[] fin =null;
        String[] search =null;
        MongoCursor<ParkingSlot> find = mongoAccess.fetchData(fin,connection,search);
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


    public List assignCarSlot(Car car,Object connection) {

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

                    list.add(parkingSlot.getId());
                    list.add(parkingSlot.getParkingLevel());
                    list.add(parkingSlot.getParkingSlot());

                }

                return list;
    }

    public String removeCar(Car car, Object connection) {
        flag=0;
        find=adddocuments.datatoget(car,"registernumber");
        String[] search=null;
        MongoCursor<ParkingSlot> source = mongoAccess.fetchData(find,connection,search);
        if(source == null)
            System.out.println("No record found");
        else {
            for (ParkingSlot pa : source) {
                newdocument.put("color", "null");
                newdocument.put("reg_number", "null");
                newdocument.put("isfilled", false);
                mongoAccess.update(newdocument, pa.getId());
                flag =1;
            }
        }
        if(flag == 0)
            return "null";
        else
            return "Removed";
    }


    public List<ParkingSlot> getData(Car car, String finder, Object connection,String[] search) {
        find=adddocuments.datatoget(car,finder);
        List<ParkingSlot> arrayList = new ArrayList();
        MongoCursor<ParkingSlot> source1 = mongoAccess.fetchData(find,connection,search);

      /*  else
            for(ParkingSlot pa : source1)
                System.out.println(pa.toString());
        */


           for (ParkingSlot pa : source1) {
               arrayList.add(pa);
           }

       return arrayList;
    }
    public List<ParkingSlot> ifCarPresent(Car car, Object connection)
    {
        String find = "{reg_number:'"+car.getRegistrationNumber()+"'}";
        List<ParkingSlot> arrayList = new ArrayList();

        MongoCursor<ParkingSlot> source1 = mongoAccess.findCar(connection,find);
        if(source1 == null)
            return arrayList= null;
        else
        {
            for(ParkingSlot pa:source1)
                arrayList.add(pa);
            return arrayList;
        }

    }

}
