package db;

import java.net.UnknownHostException;

public class ConnectionHandling {
    Object connection;

    public Object getConnection(String Adapter) throws UnknownHostException
    {
        if(Adapter.equalsIgnoreCase("mongodb")) {
            connection =  JongoAdapter.getConnection();
        }
        if(Adapter.equalsIgnoreCase("redis"))
        {
            connection = new RedisAdapter().getConnection();
        }
        if(Adapter.equalsIgnoreCase("elasticsearch")){
            connection = EsAdapter.getConnected();
        }
        return connection;

    }



}
