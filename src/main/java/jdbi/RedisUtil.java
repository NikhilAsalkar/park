package jdbi;
import java.util.*;
import bean.Car;
import util.AddDocuments;
import util.ParkingFullException;
import util.PrefixRemove;

public class RedisUtil {
    RedisAccess redisAccess = new RedisAccess();

        Set<String> hash;
        String id;
    String data;
        String level,slot;
        int flag =0;
        AddDocuments adddocuments = new AddDocuments();
        String[] find,search;
        PrefixRemove prefixRemove = new PrefixRemove();


    public String assignCarSlot(Car car, Object collection) {
        String parkdetails;
        hash = redisAccess.getZRange("emptyslots",0,0,collection);
        if(hash == null)
            throw new ParkingFullException("Parking is full");
        else {
            for (String h : hash)
                id = h;
            level = id.substring(0, 1);
            slot = id.substring(1, 3);

            Map map = new HashMap();

            map.put("color", car.getColor());
            map.put("reg_number", car.getRegistrationNumber());
            map.put("level", level);
            map.put("slot", slot);

            redisAccess.addHashSet("car:park:", id, map, collection);
            redisAccess.deleteHashField("emptyslots", id, collection);

             parkdetails = "["+
                    "\"parking_id\":\""+id+"\","+
                    "\"level\":\""+level+"\","+
                    "\"slot\":"+slot+
                    "]";

        }
        return parkdetails  ;
    }

    public String removeCar(Car car,Object connection) {
        flag =0;

        hash= redisAccess.getkey("car:park:*",connection);


        for(String key :hash) {
            String reg = redisAccess.getValue(key,"reg_number",connection);
            System.out.println(reg);
            if (reg.equalsIgnoreCase(car.getRegistrationNumber())) {
                id = prefixRemove.removePrefix(key,"car:park:");
                System.out.println(id);
                redisAccess.addToSortedList(id,connection);
                System.out.println(key);
                redisAccess.deleteKey(key,connection);
                flag =1;

            }
        }
        if(flag ==1)
            return "null";
        else
            return "Removed";
    }

    public void dataDocuments(Object connection)
    {
        String hashSet = "emptyslots";
            hash = redisAccess.getkey(hashSet,connection);
        if(hash.isEmpty()) {
            for (int i = 1; i <= 5; i++)
                for (int j = 1; j <= 20; j++) {
                    if (j < 10) {
                        id = i + "0" + j;
                    } else
                        id = i + "" + j;
                    redisAccess.addToSortedList(id,connection);
                }

        }
    }


    public List getData(Car car,String finder,Object connection,String[] search)
    {
        List arrayList = new ArrayList();
        find=adddocuments.datatoget(car,finder);
        String key = "car:park:*";
        hash= redisAccess.getkey(key,connection);


        for (String key1 : hash) {
            data = redisAccess.getValue(key1,find[0],connection);
            if(data.isEmpty()){
                arrayList = null;
                break;
            }
            else {
                if (find[1].equalsIgnoreCase(data))
                    arrayList.add(redisAccess.getMValues(key1, connection,search));
            }

        }
            return arrayList;
    }

    public String ifPresent(Car car,Object connection)
    {
        flag =0;
        String key = "car:park:*";
        hash = redisAccess.getkey(key,connection);
        String[] find = new String[2];
        find[0]="reg_number";
        for(String h :hash)
        {
            data = redisAccess.getValue(h,find[0],connection);
            if(car.getRegistrationNumber().equals(data)) {
                flag = 1;
                break;
            }
        }
        if(flag ==1)
            return "found";
        else
            return "notfound";

    }
}
