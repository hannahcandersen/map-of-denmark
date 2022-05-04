package bfst22.model.addresses;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;

//Ternary Search Tree/Trie
public class TST {
    //rooten er det første bogstav i det første ord, som bliver tilføjet til Tree/Trie
    private static AddressNode root;   // root of trie
    private ObservableList<AddressObject> listOfSearchResult = FXCollections.observableArrayList();




    public TST(AddressObject addressObject) {
        put(addressObject.getStreet(), addressObject);
    }

    //key er navnet på vejen
    //vores value er listen med addresserne med samme navn
    public static List<AddressObject> get(String key){
        AddressNode node = get(root, key, 0);
        if(node == null) return null;  //returner null, hvis der ikke er tilføjet noget til Tree/Trie
        return node.getListOfAddresses();
    }
        //Metoden tager enten left,mid,right (x) samt hvilket bogstav der er input,
        // d er en counter der holder styr på hvor langt i ordet vi befinder os.
        //Hvis vores det nye c er mindre end det nuværende c, returnerer den venstre character, samt d som er nuværende plads.. Tegn TST tree for at visualisere.
        //Hvis vores det nye c er større end det nuværende c, returnerer den højre character, samt d som er nuværende plads.
        //Hvis c hverken er større eller mindre enford vores nuværende c, så er c lig det den nye character og returnerer derfor midten.
    private static AddressNode get(AddressNode node, String key, int d) {
        if (node == null) return null;
        char c = key.charAt(d);
        if (c < node.getC()) return get(node.getLeft(), key, d);
        else if (c > node.getC()) return get(node.getRight(), key, d);
        else if (d < key.length() - 1) return get(node.getMid(), key, d+1);
        //Hvis ingen af disse er tilfældet, så må vi være på sidste character.
        else return node;
    }

    public void put(String key, AddressObject address)
    {  root = put(root, key, address, 0); }


    //Tilføjer en ny addresse til Tree/Trie
    private AddressNode put(AddressNode node, String key, AddressObject address, int d) {
        char c = key.charAt(d);                                                 //finder nuværende bogstav
        if (node == null) { node = new AddressNode(c); }                      //tjekker om nuværende AddressNode er null - laver så en ny AddressNode med den nuværende char (bogstav)
        if      (c < node.getC()) node.getLeft()  = put(node.getLeft(), key, address, d);      //
        else if (c > node.getC()) node.right = put(node.getLeft(), key, address, d);
        else if (d < key.length() - 1)
            node.mid = put(node.mid, key, address, d+1);
        else node.addAddress(address);
        //Hvis ingen af disse er tilfældet, så må vi være på sidste character.
        return node;
    }

    public AddressNode getEndNode(String input){
        return get(root, input, 0);


        /*char lastCharOfInput = input.charAt(input.length()-1);
        char c = key.charAt(d);

        if(node == null) return null;
        if(c == lastCharOfInput) return node;
        else if(c < node.c) return getEndNode(node.left, key, d, input);
        else if (c > node.c) return getEndNode(node.right, key, d, input);
        else if (d < key.length() - 1) return getEndNode(node.mid, key, d+1, input);

        return null;*/
    }

    public List<AddressObject> getAllEndOfAddressName(AddressNode currentRootNode){
        List<AddressObject> searchedList = new ArrayList<>();

        if (currentRootNode == null) return null;
        //vi skal have alle børnene for alle noderne efter currentRootNode
        while(currentRootNode.getLeft() != null || currentRootNode.getRight() != null || currentRootNode.getMid() != null) {
            if(!currentRootNode.getListOfAddresses().isEmpty())              //tjekker om der er en liste i currentNode
                searchedList.addAll(currentRootNode.getListOfAddresses());

            System.out.println("currrentNode c: " + currentRootNode.getC());
            getAllEndOfAddressName(currentRootNode.getLeft());
            getAllEndOfAddressName(currentRootNode.getRight());
            getAllEndOfAddressName(currentRootNode.getMid());
        }


        /*if(currentRootNode.left != null) {                  //tjekker om currentRootNode har venstre barn
            if(!currentRootNode.listOfAddresses.isEmpty())              //tjekker om der er en liste i currentNode
                searchedList.addAll(currentRootNode.listOfAddresses);   //tilføjer listen af adresser til searchResult

            return getAllEndOfAddressName(currentRootNode.left);
        } else if(currentRootNode.right != null) {          //tjekker om currentRootNode har højre barn
            if(!currentRootNode.listOfAddresses.isEmpty())
                searchedList.addAll(currentRootNode.listOfAddresses);

            return getAllEndOfAddressName(currentRootNode.right);
        } else if(currentRootNode.mid != null) {            //tjekker om currentRootNode har midt barn
                if(!currentRootNode.listOfAddresses.isEmpty())
                    searchedList.addAll(currentRootNode.listOfAddresses);

            return getAllEndOfAddressName(currentRootNode.mid);
        }*/


        return currentRootNode.listOfAddresses; //?

    }



}
