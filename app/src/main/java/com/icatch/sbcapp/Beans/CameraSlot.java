package com.icatch.sbcapp.Beans;

public class CameraSlot {
	// public boolean isRegister;
	public int id;
	public boolean isOccupied;
	public boolean isWifiReady;
	public int slotPosition;
	public String cameraName;
	public byte[] cameraPhoto;

	public CameraSlot(int id,int slotPosition, boolean isOccupied, String cameraName, byte[] cameraPhoto, boolean isWifiReady) {
		this.id =id;
		this.slotPosition = slotPosition;
		this.isOccupied = isOccupied;
		this.cameraName = cameraName;
		this.cameraPhoto = cameraPhoto;
		this.isWifiReady = isWifiReady;
	}

	public CameraSlot(int slotPosition, boolean isOccupied, String cameraName, byte[] cameraPhoto) {
		this.slotPosition = slotPosition;
		this.isOccupied = isOccupied;
		this.cameraName = cameraName;
		this.cameraPhoto = cameraPhoto;
		this.isWifiReady = false;
	}
}
