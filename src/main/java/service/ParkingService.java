package service;

import db.ConnectionHandling;
import db.YmlConfiguration;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import util.GetPropertyValues;
import resource.ParkingSlotManager;

import java.net.UnknownHostException;


public class ParkingService extends Application<YmlConfiguration> {
    public static String Adapter;
    public static Object connection;


    public static void main(String[] args) throws Exception {
        new ParkingService().run(args);
    }


    public void init(YmlConfiguration configuration) throws UnknownHostException
    {
        GetPropertyValues properties = new GetPropertyValues();
        Adapter = properties.getPropValues();
        connection = new ConnectionHandling().getConnection(Adapter);
    }

    @Override
    public void run(YmlConfiguration configuration, Environment environment) throws Exception {
        init(configuration);

        ParkingSlotManager parkingSlotManager = new ParkingSlotManager();
        parkingSlotManager.checkaccess(Adapter,connection);
        environment.jersey().register(parkingSlotManager);
    }
}
