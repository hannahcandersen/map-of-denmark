package bfst22.model.addresses;

import bfst22.model.OSMNode;

public class AddressObject {
    private String street, house, postcode, city;
    private float lat, lon;

    public AddressObject(String street, String house, String postcode, String city, float lat, float lon) {
        this.street = street;
        this.house = house;
        this.postcode = postcode;
        this.city = city;
        this.lat = lat;
        this.lon = lon;
    }

    public String getStreet() {return street;}
    public String getHouse() {return house;}
    public String getPostcode() {return postcode;}
    public String getCity() {return city;}
    public float getLat() {return lat;}
    public float getLon() {return lon;}

    @Override
    public String toString() {
        return street + " " + house + " " + postcode + " " + city;
    }



}
