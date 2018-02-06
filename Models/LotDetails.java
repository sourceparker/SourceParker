package com.siat.diayan.sourceparker.Models;

import com.firebase.geofire.GeoFire;

/**
 * Created by diayan on 2/2/2018.
 */

public class LotDetails {
    private String capacity;
    private String lotName;
    private String lot_ID;
    private String OwnerName;
    private String phoneNumb;
    private GeoFire latitude;
    private GeoFire longitude;

    public LotDetails(String capacity, String lotName, String lot_ID, String ownerName, String phoneNumb, GeoFire latitude, GeoFire longitude) {
        this.capacity = capacity;
        this.lotName = lotName;
        this.lot_ID = lot_ID;
        OwnerName = ownerName;
        this.phoneNumb = phoneNumb;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getLotName() {
        return lotName;
    }

    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    public String getLot_ID() {
        return lot_ID;
    }

    public void setLot_ID(String lot_ID) {
        this.lot_ID = lot_ID;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getPhoneNumb() {
        return phoneNumb;
    }

    public void setPhoneNumb(String phoneNumb) {
        this.phoneNumb = phoneNumb;
    }

    public GeoFire getLatitude() {
        return latitude;
    }

    public void setLatitude(GeoFire latitude) {
        this.latitude = latitude;
    }

    public GeoFire getLongitude() {
        return longitude;
    }

    public void setLongitude(GeoFire longitude) {
        this.longitude = longitude;
    }
}
