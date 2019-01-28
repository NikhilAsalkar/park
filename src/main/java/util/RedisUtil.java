package util;
import java.util.*;
import bean.Car;

public class RedisUtil implements CallMethods{
    RedisAccess redisAccess = new RedisAccess();

        Set<String> hash;
        String id,data;
        String level,slot;
        String color,reg_number;
        AddDocuments adddocuments = new AddDocuments();
        String[] find;
        PrefixRemove prefixRemove = new PrefixRemove();

    @Override
    public void assignCarSlot(Car car, Object collection) {

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

        }
    }

    @Override
    public void removeCar(Car car,Object connection) {


        hash= redisAccess.getkey("car:park:*",connection);


        for(String h3 :hash) {
            String reg = redisAccess.getValue(h3,"reg_number",connection);
            if (reg.equalsIgnoreCase(reg_number)) {
                id = prefixRemove.removePrefix(h3,"car:park:");
                redisAccess.addToSortedList(id,connection);
                redisAccess.deleteKey(h3,connection);
            }
        }
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


    public void getData(Car car,String finder,Object connection)
    {
        find=adddocuments.datatoget(car,finder);
        String key = "car:park:*";
        hash= redisAccess.getkey(key,connection);


        for (String h : hash) {
            data = redisAccess.getValue(h,find[0],connection);
            if (find[1].equalsIgnoreCase(data))
                System.out.println(redisAccess.getValue(h,connection));

        }

    }
}
