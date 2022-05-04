package bfst22.model.addresses;

import java.util.ArrayList;
import java.util.List;

public class AddressNode {
    private char c;                                 // character
    private AddressNode left, mid, right;           // left, middle, and right subtries
    private List<AddressObject> listOfAddresses;    // value associated with string

    public AddressNode(char c) {
        this.c = c;
        //listOfAddresses = new ArrayList<>();
    }

    public List<AddressObject> getListOfAddresses() {
        return listOfAddresses;
    }

    public void addAddress(AddressObject addressObject){
        if(listOfAddresses == null){
            listOfAddresses = new ArrayList<>();
        }
        listOfAddresses.add(addressObject);
    }


    public char getC() {
        return c;
    }

    public AddressNode getLeft() {
        return left;
    }

    public AddressNode getMid() {
        return mid;
    }

    public AddressNode getRight() {
        return right;
    }
}
