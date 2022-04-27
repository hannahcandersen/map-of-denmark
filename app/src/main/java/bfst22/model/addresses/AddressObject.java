package bfst22.model.addresses;

public class AddressObject {
    private String street, house, floor, side, postcode, city;

    public AddressObject(String street, String house, String postcode, String city) {
        this.street = street;
        this.house = house;
        //this.floor = floor;
        //this.side = side;
        this.postcode = postcode;
        this.city = city;
    }

    public String getStreet() {return street;}
    public String getHouse() {return house;}
    public String getFloor() {return floor;}
    public String getSide() {return side;}
    public String getPostcode() {return postcode;}
    public String getCity() {return city;}

    @Override
    public String toString() {
        return street + " " + house + " " + postcode + " " + city;
    }

}
