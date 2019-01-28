import db.ConnectionHandling;
import util.GetPropertyValues;
import util.MenuCall;
import util.ParkingSlotManager;
import java.io.IOException;


public class Main {
    public static String Adapter = null;
    public static void main(String[] args) throws IOException {

        ParkingSlotManager parkingSlotManager = new ParkingSlotManager();



        GetPropertyValues properties = new GetPropertyValues();
        Adapter = properties.getPropValues();
        Object connection = new ConnectionHandling().getConnection(Adapter);
        MenuCall menuCall = new MenuCall();


        parkingSlotManager.checkaccess(Adapter,connection);


        menuCall.menuDisplay(Adapter,connection);

    }

}
