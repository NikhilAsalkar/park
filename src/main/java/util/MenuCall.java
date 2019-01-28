package util;

import bean.Car;

import java.net.UnknownHostException;
import java.util.Scanner;

public class MenuCall {
    public void menuDisplay(String Adapter,Object connection)throws UnknownHostException
    {

        String color,reg_number=null;
        ParkingSlotManager parkingSlotManager = new ParkingSlotManager();
        int accept_choice;
        Car car=new Car();
        Scanner scanner= new Scanner(System.in);

        while(true)
        {
            System.out.println("1.Add Vehicle");
            System.out.println("2.Search Registration Number of Vehicle by color");
            System.out.println("3.Search for Vehicle slots with same color");
            System.out.println("4.Search for slot number of Vehicle by Registration Number");
            System.out.println("5.Remove a car");
            System.out.println("6.Exit");
            accept_choice = scanner.nextInt();
            switch(accept_choice)
            {
                case 1:

                    System.out.println("Enter the Color of car");
                    color = scanner.next();
                    System.out.println("Enter the registration number");
                    reg_number = scanner.next();
                    car.setColor(color);
                    car.setRegistrationNumber(reg_number);

                    try {
                        parkingSlotManager.assignCarParkingSpot(Adapter,car,connection);
                    } catch (ParkingFullException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;
                case 2:

                    System.out.println("Searching registration numbers of cars by color..");
                    color=scanner.next();
                    car.setColor(color);

                    try {
                        parkingSlotManager.registrationNumbersByColor(Adapter,car,connection);

                    } catch (RegistrationNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    break;
                case 3:
                    System.out.println("Enter the Color of bean.Car to search");
                    color = scanner.next();
                    car.setColor(color);
                    try {
                        parkingSlotManager.getSlotOfCarByColor(Adapter,car,connection);

                    } catch (ColorNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("Enter Registration number of bean.Car to find slot number");
                    reg_number = scanner.next();
                    car.setRegistrationNumber(reg_number);
                    try {
                        parkingSlotManager.getSlotOfCarByRegistration(Adapter,car,connection);

                    } catch (RegistrationNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;
                case 5:
                    System.out.println("Enter Registration Number");
                    reg_number = scanner.next();
                    car.setRegistrationNumber(reg_number);
                    parkingSlotManager.emptyParkingSpace(Adapter,car,connection);

                    break;
                case 6:

                    System.exit(0);
            }
        }

    }
}
