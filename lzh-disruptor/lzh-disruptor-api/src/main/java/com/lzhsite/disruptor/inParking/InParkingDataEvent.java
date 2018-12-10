package com.lzhsite.disruptor.inParking;

public class InParkingDataEvent {
	
	private String carLicense = "";

	public void setCarLicense(String carLicense) {
		this.carLicense = carLicense;
	}

	public String getCarLicense() {
		return carLicense;
	}
}
