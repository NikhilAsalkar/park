package bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.util.SystemPropertiesPropertySource;
import org.bson.types.ObjectId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

/**
 * @author zoro
 *
 */
public class ParkingSlot {
	@JsonProperty("_id")
	@MongoObjectId
	private String id;
	@JsonProperty("color")
	public String color;
	@JsonProperty("reg_number")
	public String regNumber;
	@JsonProperty("level")
	public int parkingLevel;
	@JsonProperty("slot")
	public int parkingSlot;
	@JsonProperty("isfilled")
	public boolean isFilled;

	public int getParkingLevel() {
		return parkingLevel;
	}

	public void setParkingLevel(int parkingLevel) {
		this.parkingLevel = parkingLevel;
	}

	public int getParkingSlot() {
		return parkingSlot;
	}

	public void setParkingSlot(int parkingSlot) {
		this.parkingSlot = parkingSlot;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isFilled() {
		return isFilled;
	}
	public void setFilled(boolean isFilled) {
		this.isFilled = isFilled;
	}



	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getRegNumber() {
		return regNumber;
	}

	public void setRegNumber(String regNumber) {
		this.regNumber = regNumber;
	}


	@Override
	public String toString() {
		return "ParkingSlot{" +
				"id='" + id + '\'' +
				", parkingLevel=" + parkingLevel +
				", parkingSlot=" + parkingSlot +
				", isFilled=" + isFilled +
				", color='" + color + '\'' +
				", regNumber='" + regNumber + '\'' +
				'}';
	}


}
