package util;

public class ParkingFullException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParkingFullException(String message) {
		super(message);
	}
}
