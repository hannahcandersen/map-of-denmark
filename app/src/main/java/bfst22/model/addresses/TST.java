package bfst22.model.addresses;

import java.util.List;

//Ternary Search Tree
public class TST {
    private AddressNode root;

    public List<AddressObject> get(String key){
        AddressNode x = get(root, key,0);
        if(x == null) return null;
        return x.listOfAddresses;
    }  // same as for tries (See page 737).


    private AddressNode get(AddressNode x, String key, int d) {
        if (x == null) return null;
        char c = key.charAt(d);
        if      (c < x.c) return get(x.left,  key, d);
        else if (c > x.c) return get(x.right, key, d);
        else if (d < key.length() - 1) return get(x.mid, key, d+1);
        else return x;
    }

    public void put(String key, List<AddressObject> listOfAddresses)
    {  root = put(root, key, listOfAddresses, 0);  }


    private AddressNode put(AddressNode x, String key, List<AddressObject> listOfAddresses, int d)
    {
        char c = key.charAt(d);
        if (x == null) { x = new AddressNode(); x.c = c; }
        if      (c < x.c) x.left  = put(x.left,  key, listOfAddresses, d);
        else if (c > x.c) x.right = put(x.right, key, listOfAddresses, d);
        else if (d < key.length() - 1)
            x.mid   = put(x.mid, key, listOfAddresses, d+1);
        else x.listOfAddresses = listOfAddresses;
        return x; }

}
