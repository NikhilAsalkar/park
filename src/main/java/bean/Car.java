package bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Car {
	@JsonProperty("color")
	public String color;


	@JsonProperty("reg_number")
	public String registrationNumber;


	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getRegistrationNumber() {

		return registrationNumber;
	}
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	@Override
	public String toString() {
		return "Car [color=" + color + ", registrationNumber=" + registrationNumber + "]";
	}
}
