
package resource;
import bean.Car;
import bean.ParkingSlot;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdbi.*;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;
import service.ParkingService;
import util.*;

import java.util.stream.Collectors;


@Path("/")
public class ParkingSlotManager {

	Client client = ClientBuilder.newClient();
	WebTarget resource = client.target("http://localhost:8080/insertRecord");
	Invocation.Builder builder = resource.request(MediaType.APPLICATION_JSON);


	ClientJsonObject clientJsonObject=new ClientJsonObject();
	Object jsonObject;
	ArrayList<ParkingSlot> parkingSlots;
	ArrayList<ParkingSlot> filledSlots;
	List carData;
	String removed,check,parkdetails;
    Object  message;
	Response response;
	MongoUtil mongoUtil = new MongoUtil();
	RedisUtil redisUtil = new RedisUtil();
	EsUtil esUtil       = new EsUtil();
	Car car = new Car();
	List<Map<String ,Object>> elasticSearchData = new ArrayList<>();
	Object connection = ParkingService.connection;
	String Adapter = ParkingService.Adapter;
	int flag =0;
	String[] search=new String[2];
	AddDocuments addDocuments = new AddDocuments();

	public ParkingSlotManager() {
		this.parkingSlots = new ArrayList<>();
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

	@POST
	@Path("/insert")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response assignCarParkingSpot(String json) throws ParkingFullException, IOException {
		JSONObject jobj = new JSONObject(json);
		Object regObject;
		Object carObject;
		try {
			regObject = jobj.get("reg_number");
			carObject = jobj.get("color");
		}
		catch(JSONException jsone)
		{
			message= addDocuments.SendMessage(false,"Insufficient Data check for color/reg_number","");

			return Response.ok(message).build();
		}
		ObjectMapper mapper = new ObjectMapper();
		Car car = mapper.readValue(json,Car.class);
		if(regObject instanceof Integer)
		{
			message= addDocuments.SendMessage(false,"Invalid Registration","");

			return Response.ok(message).build();

		}

		else if(car.getRegistrationNumber().isEmpty() || car.getColor().isEmpty())
		{
			message= addDocuments.SendMessage(false,"Insufficient Data","");

			return Response.ok(message).build();

		}

		else {
			if (Adapter.equalsIgnoreCase("memory")) {
				ParkingSlot parkingSlot = this.getEmptyParkingSlot();
				if (parkingSlot == null) {
					throw new ParkingFullException("Parking is full");
				}
				parkingSlot.setFilled(true);
			}
			if (Adapter.equalsIgnoreCase("mongodb")) {
				carData= mongoUtil.ifCarPresent(car,connection);
				if(carData == null){
					carData = mongoUtil.assignCarSlot(car, connection);
					message = addDocuments.SendMessage(true,"",carData);
				}
				else{
				   message =  addDocuments.SendMessage(false,"Car Already Present","");
				}
			}

			if (Adapter.equalsIgnoreCase("redis")) {

				check = redisUtil.ifPresent(car,connection);
				if(check.equalsIgnoreCase("found")){
                   message =  addDocuments.SendMessage(false,"Car Already Present","");
					jsonObject = clientJsonObject.createJsonObject("insert",false,1);

				}
				else {
                     parkdetails = redisUtil.assignCarSlot(car, connection);
                   message= addDocuments.SendMessage(true,"",parkdetails);
					jsonObject = clientJsonObject.createJsonObject("insert",true,1);



				}

			}
			if (Adapter.equalsIgnoreCase("elasticsearch")) {
				elasticSearchData = esUtil.ifPresent(car,connection);
				if(elasticSearchData.size()>0) {
                   message =  addDocuments.SendMessage(false,"Car already Present","");
                }
				else {
                    esUtil.assignCarSlot(car, connection);
                   message =  addDocuments.SendMessage(true,"",elasticSearchData);
                }
			}
		}
		response= builder.post(Entity.entity(jsonObject,MediaType.APPLICATION_JSON));


		return Response.ok(message).build();
	}


	public void checkaccess(String Adapter,Object connection) {
		switch (Adapter.toLowerCase()) {
			case "mongodb":
				mongoUtil.dataDocuments(connection);
				break;
			case "redis":
				redisUtil.dataDocuments(connection);
				break;
			case "elasticsearch":
				new EsUtil().dataDocument(connection);
				break;
		}
	}

	@POST
	@Path("/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response emptyParkingSpace(String json)throws IOException {
		ArrayList<ParkingSlot> filledSlots = getFilledParkingSlots();
		JSONObject jobj = new JSONObject(json);
		Object regObject;
		Object carObject;
		try {
			regObject = jobj.get("reg_number");
			carObject = jobj.get("color");
		}
		catch(JSONException jsone)
		{
			message =  addDocuments.SendMessage(false,"Insufficient Data check for color/reg_number","");

			return Response.ok(message).build();
		}
		ObjectMapper mapper = new ObjectMapper();
		Car car = mapper.readValue(json,Car.class);
		if(regObject instanceof Integer) {
			message =  addDocuments.SendMessage(false,"Insufficient Data check for color/reg_number","");

			return Response.ok(message).build();		}

		else if(car.getRegistrationNumber().isEmpty() || car.getColor().isEmpty()) {
			message =  addDocuments.SendMessage(false,"Insufficient Data check for color/reg_number","");

			return Response.ok(message).build();		}
		else {
			if (Adapter.equalsIgnoreCase("memory")) {
				for (ParkingSlot parkingSlot : filledSlots) {
					if (parkingSlot.getRegNumber().equals(car.getRegistrationNumber())) {
						parkingSlot.setRegNumber(null);
						parkingSlot.setColor(null);
						parkingSlot.setFilled(false);
					}
				}

			}
			if (Adapter.equalsIgnoreCase("mongodb"))
				removed = mongoUtil.removeCar(car, connection);
			if (Adapter.equalsIgnoreCase("redis")) {
				removed = redisUtil.removeCar(car, connection);
			}
			if (Adapter.equalsIgnoreCase("elasticsearch"))
				removed = esUtil.removeCar(car, connection);
			if(removed.equals("null")) {
				message =  addDocuments.SendMessage(false,"No Data Found","");
				jsonObject = clientJsonObject.createJsonObject("remove",false,1);

            }
			else {
				message =  addDocuments.SendMessage(true,"","");
				jsonObject = clientJsonObject.createJsonObject("remove",true,1);
			}
		}
		builder.post(Entity.json(jsonObject));
		return Response.ok(message).build();

	}

	@Singleton
	@GET
	@Path("/searchByColor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registrationNumbersByColor( @QueryParam("color")String color) throws RegistrationNotFoundException {
		car.setColor(color);
		flag =0;
		search[0]="reg_number";
		search[1]=null;
		if(car.getColor()== null) {

			message =  addDocuments.SendMessage(false,"Invalid Data","");

			return Response.ok(message).build();
        }
		else {

			List<String> registrationNumbers = null;
			if (Adapter.equalsIgnoreCase("memory")) {
				ArrayList<ParkingSlot> filledSlots = getFilledParkingSlots();
				registrationNumbers = filledSlots.stream()
						.filter(slot -> car.getColor().equals(slot.getColor()))
						.map(p -> p.getRegNumber())
						.collect(Collectors.toList());
				if (registrationNumbers.isEmpty())
					throw new RegistrationNotFoundException("Registration not found!");
				System.out.println(registrationNumbers);
			} else if (Adapter.equalsIgnoreCase("mongodb")) {

				carData = mongoUtil.getData(car, "color", connection,search);

			} else if (Adapter.equalsIgnoreCase("elasticsearch")) {
				elasticSearchData = esUtil.getData(car, "color", connection);
				flag = 1;
			} else if (Adapter.equalsIgnoreCase("redis")) {
				carData = redisUtil.getData(car, "color", connection,search);
			}
			if(carData == null && elasticSearchData.size() ==0) {
				message =  addDocuments.SendMessage(false,"No Data Found","");
				jsonObject = clientJsonObject.createJsonObject("searchByColor",false,1);

				builder.post(Entity.json(jsonObject));

				return Response.ok(message).build();
			}
			else {

				jsonObject = clientJsonObject.createJsonObject("searchByColor",true,1);
				response= builder.post(Entity.entity(jsonObject,MediaType.APPLICATION_JSON));
				if (flag == 1) {
					message =  addDocuments.SendMessage(true,"",elasticSearchData);
					return Response.ok(message).build();
				}
				else {
					message =  addDocuments.SendMessage(true,"",carData);
					return Response.ok(message).build();
				}
			}
		}
	}

	/**

	 * @return floor and slot where the car is parked
	 * @throws RegistrationNotFoundException if the car is not registered
	 */
	@GET
	@Path("/searchByRegistration")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSlotOfCarByRegistration(@QueryParam("reg_number") String reg_number) throws RegistrationNotFoundException {
		car.setRegistrationNumber(reg_number);
		flag =0;
		search[0]="level";
		search[1]="slot";


		if(car.getRegistrationNumber() == null) {
			message =  addDocuments.SendMessage(false,"Invalid Data","");

			return Response.ok(message).build();
		}
		else {
			if (Adapter.equalsIgnoreCase("memory")) {
				ArrayList<ParkingSlot> filledSlots = getFilledParkingSlots();
				ParkingSlot result = filledSlots.stream()
						.filter(slot -> car.getRegistrationNumber().equals(slot.getRegNumber())).findAny()
						.orElse(null);
				if (result == null)
					throw new RegistrationNotFoundException("Registration not found!");
				System.out.println("Parking Level: " + result.getParkingLevel() + " " + "Parking Slot: " + result.getParkingSlot());
			}
			if (Adapter.equalsIgnoreCase("mongodb")) {
				carData = mongoUtil.getData(car, "registernumber", connection,search);

			}
			if (Adapter.equalsIgnoreCase("elasticsearch")) {
				elasticSearchData = esUtil.getData(car, "registernumber", connection);
				flag = 1;

			}
			if (Adapter.equalsIgnoreCase("redis")) {
				carData = redisUtil.getData(car, "registernumber", connection,search);
			}
			System.out.println(elasticSearchData);
			if(carData==null && elasticSearchData.size()==0) {

				message =  addDocuments.SendMessage(false,"No Data Found","");
				jsonObject = clientJsonObject.createJsonObject("searchByRegistration",false,1);

				builder.post(Entity.json(jsonObject));

				return Response.ok(message).build();
			}
			else {
				jsonObject = clientJsonObject.createJsonObject("searchByRegistration",true,1);
				response= builder.post(Entity.entity(jsonObject,MediaType.APPLICATION_JSON));

				if (flag == 1) {
					message =  addDocuments.SendMessage(true,"",elasticSearchData);

					return Response.ok(message).build();
				}
				else {
					message =  addDocuments.SendMessage(true,"",carData);

					return Response.ok(message).build();
				}
			}
		}
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
		search[0]="level";
		search[1]="slot";

		if(car.getColor() == null){
			message =  addDocuments.SendMessage(false,"Invalid Data check for color/reg_number","");

			return Response.ok(message).build();
		}

		else {

			flag = 0;
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
			if (Adapter.equalsIgnoreCase("mongodb")) {
				carData = mongoUtil.getData(car, "color", connection,search);

			}
			if (Adapter.equalsIgnoreCase("elasticsearch")) {
				elasticSearchData = esUtil.getData(car, "color", connection);
				flag = 1;

			}
			if (Adapter.equalsIgnoreCase("redis")) {
				carData = redisUtil.getData(car, "color", connection,search);
			}
			if(carData==null && elasticSearchData.size()==0) {
				message =  addDocuments.SendMessage(false,"No Data Found","");
				jsonObject = clientJsonObject.createJsonObject("searchForSlotByColor",false,1);

				builder.post(Entity.json(jsonObject));

				return Response.ok(message).build();
			}
			else {
				jsonObject = clientJsonObject.createJsonObject("searchForSlotByColor",true,1);
				response= builder.post(Entity.entity(jsonObject,MediaType.APPLICATION_JSON));

				if (flag == 1) {
					message =  addDocuments.SendMessage(true,"",elasticSearchData);

					return Response.ok(message).build();
				}
				else {
					message =  addDocuments.SendMessage(true,"",carData);

					return Response.ok(message).build();
				}
			}
		}
	}
}
