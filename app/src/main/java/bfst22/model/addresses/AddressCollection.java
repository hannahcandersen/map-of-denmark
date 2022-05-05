package bfst22.model.addresses;

import java.util.List;

public class AddressCollection {
    //public static final long serialVersionUID = 768   - skal vi bruge dette?
    private List<AddressObject> addressList;

    public AddressCollection(List<AddressObject> addressList) {
        //this.addressList

        //sorter liste på bogstav= new .

        //For(næste index)
        //for(AddressObject ao : addressList) {
          //  TST.put(ao.getStreet(), ao);

        //}

    }

    public List<AddressObject> getAddressList() {
        return addressList;
    }
}
