package com.honestmc.laryngoscopeapp.Beans;

import org.litepal.crud.DataSupport;

/**
 * Created by b.jiang on 2017/3/31.
 */

public class CameraTable extends DataSupport {
    private int id;
    public boolean isOccupied;
    public int slotPosition;
    public String cameraName;
    public byte[] cameraPhoto;

    public CameraTable(boolean isOccupied, int slotPosition, String cameraName, byte[] cameraPhoto) {
        this.isOccupied = isOccupied;
        this.slotPosition = slotPosition;
        this.cameraName = cameraName;
        this.cameraPhoto = cameraPhoto;
    }

    public CameraTable() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public int getSlotPosition() {
        return slotPosition;
    }

    public void setSlotPosition(int slotPosition) {
        this.slotPosition = slotPosition;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public byte[] getCameraPhoto() {
        return cameraPhoto;
    }

    public void setCameraPhoto(byte[] cameraPhoto) {
        this.cameraPhoto = cameraPhoto;
    }
}
