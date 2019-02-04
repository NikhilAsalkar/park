package bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author zoro
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingSlot {
	@JsonProperty("_id")
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

	public ParkingSlot(){

	}

	public ParkingSlot(String id, String color, String regNumber, int parkingLevel, int parkingSlot, boolean isFilled) {
		this.id = id;
		this.color = color;
		this.regNumber = regNumber;
		this.parkingLevel = parkingLevel;
		this.parkingSlot = parkingSlot;
		this.isFilled = isFilled;
	}

	public float getParkingLevel() {
		return parkingLevel;
	}

	public float getParkingSlot() {
		return parkingSlot;
	}

	public void setParkingLevel(int parkingLevel) {
		this.parkingLevel = parkingLevel;
	}



	public void setParkingSlot(int parkingSlot) {
		this.parkingSlot = parkingSlot;
	}

	public String  getId() {
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
