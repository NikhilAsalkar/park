package util;

import bean.ParkingSlot;

import java.util.ArrayList;
import bean.*;
import org.bson.types.ObjectId;

public class AddDocuments {

    ArrayList<ParkingSlot>arrayList;
    Car car;
    String string[];
    String id;
   public  ArrayList<ParkingSlot> addDocuments()
    {
        this.arrayList = new ArrayList<ParkingSlot>();

        for(int i = 1; i <= 5; i++) {
            for(int j = 1; j<= 20; j++) {

                if(j<10)
                     id = i +"0"+j;
                else
                    id = i+""+j;
                ParkingSlot parkingSlot = new ParkingSlot();
                parkingSlot.setId(id);
                parkingSlot.setFilled(false);
                parkingSlot.setParkingLevel(i);
                parkingSlot.setParkingSlot(j);
                this.arrayList.add(parkingSlot);
            }
        }
        return arrayList;
    }

    public  String[] datatoget(Car car,String finder)
    {
        if(finder.equalsIgnoreCase("color"))
            string = new String[]{"color",car.getColor()};
        if(finder.equalsIgnoreCase("registernumber"))
            string = new String[]{"reg_number",car.getRegistrationNumber()};
        return string;
    }

    public String ArrayListToJson(ParkingSlot arrayList)
    {

        String json = "{"+
                "\"_id\":\""+arrayList.getId()+"\","+
                "\"color\":\""+arrayList.getColor()+"\","+
                "\"reg_number\":\""+arrayList.getRegNumber()+"\","+
                "\"level\":"+arrayList.getParkingLevel()+","+
                "\"slot\":"+ arrayList.getParkingSlot() +","+
                "\"isfilled\":"+arrayList.isFilled()+
                "}";
        return json;
    }
    public String ArrayToJsonEs(ParkingSlot arrayList)
    {
        String json = "{"+
                "\"color\":\""+arrayList.getColor()+"\","+
                "\"reg_number\":\""+arrayList.getRegNumber()+"\","+
                "\"level\":"+arrayList.getParkingLevel()+","+
                "\"slot\":"+ arrayList.getParkingSlot() +","+
                "\"isfilled\":"+arrayList.isFilled()+
                "}";
        return json;
    }
}
