package bfst22.model.addresses;

import java.util.List;

public class AddressCollection {
    //public static final long serialVersionUID = 768   - skal vi bruge dette?
    private List<AddressObject> addressList;

    public AddressCollection(List<AddressObject> addressList) {
        //this.addressList = new ...;

        //sorter liste
    }

    public List<AddressObject> getAddressList() {
        return addressList;
    }
}
