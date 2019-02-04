
package resource;
import bean.Car;
import bean.ParkingSlot;
import jdbi.EsUtil;
import jdbi.MongoUtil;
import jdbi.RedisUtil;
import org.jongo.MongoCursor;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.util.*;
import service.ParkingService;
import util.ColorNotFoundException;
import util.ParkingFullException;
import util.RegistrationNotFoundException;

import java.util.stream.Collectors;


@Path("/")
public class ParkingSlotManager {

	ArrayList<ParkingSlot> parkingSlots;
	ArrayList<ParkingSlot> filledSlots;
	MongoUtil mongoUtil= new MongoUtil();
	RedisUtil redisUtil = new RedisUtil();
	EsUtil esUtil = new EsUtil();
	Car car = new Car();
	MongoCursor mongoCursor;
	List<Map<String ,Object>> elasticSearchData = new ArrayList<>();
	List<ParkingSlot> carData;
	Object connection = ParkingService.connection;
	String Adapter = ParkingService.Adapter;
	int flag =0;



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

	@GET
	@Path("/insert")
	@Produces(MediaType.APPLICATION_JSON)
	public Response assignCarParkingSpot( @QueryParam("color") String color,@QueryParam("regNumber") String reg_number) throws ParkingFullException {
		System.out.println(color +""+reg_number);
		car.setRegistrationNumber(reg_number);
		car.setColor(color);
		String str = null;
		System.out.println(car.getColor() +""+ car.getRegistrationNumber());
		if (Adapter.equalsIgnoreCase("memory")) {
			ParkingSlot parkingSlot = this.getEmptyParkingSlot();
			if (parkingSlot == null) {
				throw new ParkingFullException("Parking is full");
			}
			parkingSlot.setFilled(true);
		}
		if(Adapter.equalsIgnoreCase("mongodb")){
			str = mongoUtil.assignCarSlot(car,connection);
		}
		if(Adapter.equalsIgnoreCase("redis")){
			str = redisUtil.assignCarSlot(car,connection);
		}
		if(Adapter.equalsIgnoreCase("elasticsearch"))
		{
			str = esUtil.assignCarSlot(car,connection);
		}
		return Response.ok().build();
	}


	public void checkaccess(String Adapter,Object collection) {
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

	@GET
	@Path("/remove")
	public Response emptyParkingSpace( @QueryParam("regNumber")String reg_number) {
		car.setRegistrationNumber(reg_number);
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
			mongoCursor = mongoUtil.removeCar(car,connection);
		if (Adapter.equalsIgnoreCase("redis")) {
			System.out.println("Redis");
			redisUtil.removeCar(car, connection);
		}
		if(Adapter.equalsIgnoreCase("elasticsearch"))
			esUtil.removeCar(car,connection);

		return Response.ok().build();
	}

	@Singleton
	@GET
	@Path("/searchByColor")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registrationNumbersByColor( @QueryParam("color")String color) throws RegistrationNotFoundException {
		car.setColor(color);
		flag =0;
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

		carData = mongoUtil.getData(car,"color",connection);

		}
		else if(Adapter.equalsIgnoreCase("elasticsearch")){
			elasticSearchData = esUtil.getData(car,"color",connection);
			flag =1;
		}
		else if(Adapter.equalsIgnoreCase("redis")) {
			carData = redisUtil.getData(car, "color", connection);
		}
		if(flag == 1)
			return Response.ok(elasticSearchData).build();
		else
			return Response.ok(carData).build();
	}
	
	/**

	 * @return floor and slot where the car is parked
	 * @throws RegistrationNotFoundException if the car is not registered
	 */
	//@Singleton
	@GET
	@Path("/searchByRegistration")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSlotOfCarByRegistration(@QueryParam("regNumber") String reg_number) throws RegistrationNotFoundException {
		car.setRegistrationNumber(reg_number);
		flag =0;
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
			carData = mongoUtil.getData(car,"registernumber",connection);

		}
		if (Adapter.equalsIgnoreCase("elasticsearch")) {
			elasticSearchData = esUtil.getData(car, "registernumber", connection);
			flag =1;

		}
		if(Adapter.equalsIgnoreCase("redis")) {
			carData = redisUtil.getData(car, "registernumber", connection);
		}
		if(flag == 1)
			return Response.ok(elasticSearchData).build();
		else
			return Response.ok(carData).build();
	}
	
	/**
	 * @return the floor and slot of the car where it is parked
	 * @throws ColorNotFoundException if the car is not found of the specific color
	 */


	@GET
	@Path("searchForSlotByColor")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSlotOfCarByColor(@QueryParam("color")String color) throws ColorNotFoundException {
		List<String> parkingSlots = null;
		car.setColor(color);
		ArrayList<ParkingSlot> filledSlots = null;
		flag =0;
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
		{
			carData = mongoUtil.getData(car,"color",connection);

		}
		if (Adapter.equalsIgnoreCase("elasticsearch")) {
			elasticSearchData = esUtil.getData(car, "color", connection);
			flag =1;

		}
		if(Adapter.equalsIgnoreCase("redis")) {
			carData = redisUtil.getData(car, "color", connection);
		}

		System.out.println(carData);
		if(flag == 1)
			return Response.ok(elasticSearchData).build();
		else
			return Response.ok(carData).build();	}
}
