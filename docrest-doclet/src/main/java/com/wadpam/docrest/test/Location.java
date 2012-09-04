/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.test;

/**
 *
 * @author os
 */
public class Location {
    /** Latitude is South/North distance from equator in degrees, [-90.0 .. 90.0] */
    private double latitude;
    /** Latitude is West/East distance from Greenwich Meridian in degrees, [-180.0 .. 180.0] */
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    
}
