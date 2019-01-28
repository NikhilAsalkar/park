
package util;
import bean.Car;
import bean.ParkingSlot;
import org.elasticsearch.client.transport.TransportClient;
import org.jongo.MongoCollection;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingSlotManager {

	MongoCollection client;
	ArrayList<ParkingSlot> parkingSlots;
	ArrayList<ParkingSlot> filledSlots;
	MongoUtil mongoUtil= new MongoUtil();
	RedisUtil redisUtil = new RedisUtil();
	EsUtil esUtil = new EsUtil();
	/**
	 * Here we try to allocate the parking space for the vehicles
	 */
	public ParkingSlotManager() throws UnknownHostException {
		this.parkingSlots = new ArrayList<ParkingSlot>();
		for(int i = 1; i <= 5; i++) {
			for(int j = 1; j<= 20; j++) {
				ParkingSlot parkingSlot = new ParkingSlot();
				parkingSlot.setFilled(false);
				parkingSlot.setParkingLevel(i);
				parkingSlot.setParkingSlot(j);
				this.parkingSlots.add(parkingSlot);

			}
		}
	}
	
	/**
	 * @return the value if the parking lot is occupied
	 */

	public ParkingSlot getEmptyParkingSlot() {
		for(ParkingSlot parkingSlot: this.parkingSlots) {
			if(!parkingSlot.isFilled())
				return parkingSlot;
		}
		return null;
	}
	
	/**
	 * @return Returns the list of the occupied parking slots
	 */
	public ArrayList<ParkingSlot> getFilledParkingSlots() {
		filledSlots = (ArrayList<ParkingSlot>) this.parkingSlots.stream().filter(u -> u.isFilled() == true).collect(Collectors.toList());
		return filledSlots;
	}
	
	/**
	 * @param car Accepts the details of the car for the parking space
	 * @throws ParkingFullException If the parking is full the exception is thrown
	 */
	public void assignCarParkingSpot(String Adapter, Car car,Object collection) throws ParkingFullException ,UnknownHostException{
		if (Adapter.equalsIgnoreCase("memory")) {
			ParkingSlot parkingSlot = this.getEmptyParkingSlot();
			if (parkingSlot == null) {
				throw new ParkingFullException("Parking is full");
			}
			//parkingSlot.setCar(car);
			parkingSlot.setFilled(true);
		}
		if(Adapter.equalsIgnoreCase("mongodb")){
			mongoUtil.assignCarSlot(car,collection);
		}
		if(Adapter.equalsIgnoreCase("redis")){
			redisUtil.assignCarSlot(car,collection);
		}
		if(Adapter.equalsIgnoreCase("elasticsearch"))
		{
			esUtil.assignCarSlot(car,collection);
		}
	}


	public void checkaccess(String Adapter,Object collection) throws UnknownHostException {
		switch (Adapter.toLowerCase()) {
			case "mongodb":
				mongoUtil.dataDocuments(collection);
				break;
			case "redis":
				redisUtil.dataDocuments(collection);
				break;
			case "elasticsearch":
				new EsUtil().dataDocument(collection);
				break;
		}
	}
	/**
	 * @param car accepts the details of the car to empty the space where the car was parked
	 */



	public void emptyParkingSpace(String Adapter, Car car,Object connection) {
		ArrayList<ParkingSlot> filledSlots = getFilledParkingSlots();
		if (Adapter.equalsIgnoreCase("memory")) {
			for (ParkingSlot parkingSlot : filledSlots) {
				if (parkingSlot.getRegNumber().equals(car.getRegistrationNumber())) {
					parkingSlot.setRegNumber(null);
					parkingSlot.setColor(null);
					parkingSlot.setFilled(false);
				}
			}

		}
		if(Adapter.equalsIgnoreCase("mongodb"))
			mongoUtil.removeCar(car,connection);
		if(Adapter.equalsIgnoreCase("redis"))
			redisUtil.removeCar(car,connection);
		if(Adapter.equalsIgnoreCase("elsticsearch"))
			esUtil.removeCar(car,connection);
	}




	
	/**
	 * @return the registration number of the car
	 * @throws RegistrationNotFoundException if the registration number is not found
	 */
	public List<String> registrationNumbersByColor(String Adapter, Car car,Object connection) throws RegistrationNotFoundException {
		List<String> registrationNumbers = null;
		if(Adapter.equalsIgnoreCase("memory")){
			ArrayList<ParkingSlot> filledSlots = getFilledParkingSlots();
			registrationNumbers = filledSlots.stream()
					.filter(slot -> car.getColor().equals(slot.getColor()))
					.map(p -> p.getRegNumber())
					.collect(Collectors.toList());
			if (registrationNumbers.isEmpty())
				throw new RegistrationNotFoundException("Registration not found!");
		 System.out.println(registrationNumbers);
		}
		else if(Adapter.equalsIgnoreCase("mongodb"))
		{
			mongoUtil.getData(car,"color",connection);
		}
		else if(Adapter.equalsIgnoreCase("elasticsearch"))
			esUtil.getData(car,"color",connection);
		else if(Adapter.equalsIgnoreCase("redis"))
			redisUtil.getData(car,"color",connection);
		return null;
	}
	
	/**

	 * @return floor and slot where the car is parked
	 * @throws RegistrationNotFoundException if the car is not registered
	 */
	public String getSlotOfCarByRegistration(String Adapter, Car car,Object connection) throws RegistrationNotFoundException {
		if (Adapter.equalsIgnoreCase("memory")) {
			ArrayList<ParkingSlot> filledSlots = getFilledParkingSlots();
			ParkingSlot result = filledSlots.stream()
					.filter(slot -> car.getRegistrationNumber().equals(slot.getRegNumber())).findAny()
					.orElse(null);
			if (result == null)
				throw new RegistrationNotFoundException("Registration not found!");
			System.out.println( "Parking Level: " + result.getParkingLevel() + " " + "Parking Slot: " + result.getParkingSlot());
		}
		if(Adapter.equalsIgnoreCase("mongodb"))
		{
			mongoUtil.getData(car,"registernumber",connection);
		}
		if (Adapter.equalsIgnoreCase("elasticsearch"))
			esUtil.getData(car,"registernumber",connection);
		if(Adapter.equalsIgnoreCase("redis"))
			redisUtil.getData(car,"registernumber",connection);
		return null;
	}
	
	/**

	 * @return the floor and slot of the car where it is parked
	 * @throws ColorNotFoundException if the car is not found of the specific color
	 */




	public List<String> getSlotOfCarByColor(String Adapter, Car car,Object collection) throws ColorNotFoundException {
		List<String> parkingSlots = null;
		ArrayList<ParkingSlot> filledSlots = null;
		if (Adapter.equalsIgnoreCase("memory")) {
			filledSlots = getFilledParkingSlots();
			parkingSlots = filledSlots.stream()
					.filter(slot -> car.getColor().equals(slot.getColor()))
					.map(p -> "Parking Level: " + p.getParkingLevel() + " Parking Slot: " + p.getParkingSlot())
					.collect(Collectors.toList());
			if (parkingSlots.isEmpty())
				throw new ColorNotFoundException("Color not found!");
			System.out.println(parkingSlots);
		}
		if(Adapter.equalsIgnoreCase("mongodb"))
			mongoUtil.getData(car,"color",collection);
		if (Adapter.equalsIgnoreCase("elasticsearch"))
			esUtil.getData(car,"color",collection);
		if(Adapter.equalsIgnoreCase("redis"))
			redisUtil.getData(car,"color",collection);
		return null;
	}




}
