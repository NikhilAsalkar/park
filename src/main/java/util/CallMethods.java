package util;
import bean.Car;

public interface CallMethods
{
    void dataDocuments(Object connection);
    void assignCarSlot(Car car,Object connection);
    void removeCar(Car car,Object connection);
    void getData(Car car,String finder,Object connection);


}
