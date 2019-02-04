package service;

import db.ConnectionHandling;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import util.GetPropertyValues;
import resource.ParkingSlotManager;

import java.net.UnknownHostException;


public class ParkingService extends Application<Configuration> {
    public static String Adapter;
    public static Object connection;


    public static void main(String[] args) throws Exception {
        new ParkingService().run(args);
    }


    public void init(Configuration configuration) throws UnknownHostException
    {    ParkingSlotManager parkingSlotManager = new ParkingSlotManager();

        GetPropertyValues properties = new GetPropertyValues();
        Adapter = properties.getPropValues();
        connection = new ConnectionHandling().getConnection(Adapter);
        parkingSlotManager.checkaccess(Adapter,connection);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        init(configuration);
        ParkingSlotManager parkingSlotManager = new ParkingSlotManager();
        environment.jersey().register(parkingSlotManager);

    }
}
