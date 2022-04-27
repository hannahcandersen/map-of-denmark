package bfst22.model.addresses;

import java.util.List;

//Ternary Search Tree
public class TST {
    private AddressNode root;

    public List<AddressObject> get(String key){
        AddressNode node = get(root, key, 0);
        if(node == null) return null;  //returner null, hvis der ikke er tilføjet noget til Tree/Trie
        return node.listOfAddresses;
    }
        //Metoden tager enten left,mid,right (x) samt hvilket bogstav der er input,
        // d er en counter der holder styr på hvor langt i ordet vi befinder os.
        //Hvis vores det nye c er mindre end det nuværende c, returnerer den venstre character, samt d som er nuværende plads.. Tegn TST tree for at visualisere.
        //Hvis vores det nye c er større end det nuværende c, returnerer den højre character, samt d som er nuværende plads.
        //Hvis c hverken er større eller mindre end vores nuværende c, så er c lig det den nye character og returnerer derfor midten.
    private AddressNode get(AddressNode node, String key, int d) {
        if (node == null) return null;
        char c = key.charAt(d);
        if (c < node.c) return get(node.left,  key, d);
        else if (c > node.c) return get(node.right, key, d);
        else if (d < key.length() - 1) return get(node.mid, key, d+1);
        //Hvis ingen af disse er tilfældet, så må vi være på sidste character.
        else return node;
    }

    public void put(String key, List<AddressObject> listOfAddresses)
    {  root = put(root, key, listOfAddresses, 0);  }


    //Tilføjer en ny addresse til Tree/Trie
    private AddressNode put(AddressNode node, String key, AddressObject address, int d) {
        char c = key.charAt(d);                                                 //finder nuværende bogstav
        if (node == null) { node = new AddressNode(c); }                      //tjekker om nuværende AddressNode er null - laver så en ny AddressNode med den nuværende char (bogstav)
        if      (c < node.c) node.left  = put(node.left, key, address, d);      //
        else if (c > node.c) node.right = put(node.right, key, address, d);
        else if (d < key.length() - 1)
            x.mid   = put(x.mid, key, listOfAddresses, d+1);
        else x.listOfAddresses = listOfAddresses;
        return x; }

}
