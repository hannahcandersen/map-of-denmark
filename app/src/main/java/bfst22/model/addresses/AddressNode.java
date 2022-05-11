package bfst22.model.addresses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddressNode implements Serializable {

    public static final long serialVersionUID = 134123;
    char c;                                 // character
    AddressNode left, mid, right;           // left, middle, and right subtries
    private List<AddressObject> listOfAddresses;    // value associated with string

    public AddressNode(char c) {
        this.c = c;
        //listOfAddresses = new ArrayList<>();
    }

    public char getC(){
        return c;
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
}
