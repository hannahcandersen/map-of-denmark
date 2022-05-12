package bfst22.model.addresses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


//Ternary Search Tree/Trie
public class TST implements Serializable {

    public static final long serialVersionUID = 134123;
    //rooten er det første bogstav i det første ord, som bliver tilføjet til Tree/Trie
    private AddressNode root;   // root of trie


    public TST(AddressObject addressObject) {put(addressObject.getStreet(), addressObject);}

    public AddressNode getRoot(){
        return root;
    }

    //Vi kan bruge denne metode til at finde, om der eksisterer en adresse i vores TST
    //Vi skal kun give den navnet på gaden (Starter med stort Bogstav)
    public List<AddressObject> get(String key){
        AddressNode node = get(root, key, 0);    //Den laver et kald til den anden get metode
        if(node == null) return null;               //returner null, hvis der ikke er tilføjet noget til Tree/Trie
        return node.getListOfAddresses();           //returnerer alle adresserne med samme navn
    }

/*
    Dette er en hjælpe metode til den anden get metode
        node er den node vi befinder os på (det kan være node, node.left, node.right eller node.mid)
        key er navnet på gaden
        d er en counter der holder styr på hvilke bogstav vi befinder os på
        Metoden sammen ligner bogstavet som er i TST og det fra gadenavnet
        Hvis de er det samme bogstav, så kalder den rekursivt node.mid
        Hvis gadenavnet's bogstav kommer før i alfabetet end det der står i TST, så kalder den rekursivt node.left
        Hvis gadenavnet's bogstav kommer efter i alfabetet end det der står i TST, så kalder den rekursivt node.right
        Hvis ingen af disse if-statements er true, så er vi på sidste bogstav i gadenavnet og returnerer derfor den node
 */
    private AddressNode get(AddressNode node, String key, int d) {
        if (node == null) return null;
        char c = key.charAt(d);
        if (c < node.c) return get(node.left, key, d);
        else if (c > node.c) return get(node.right, key, d);
        else if (d < key.length() - 1) return get(node.mid, key, d+1);
        else return node;  //returnerer den sidste AddressNode, som holder på listen med adresseobjekter
    }

    //Tilføjer en ny addresse til Tree/Trie
    public void put(String key, AddressObject address)
    {  root = put(root, key, address, 0); }

    //Hjælpe metode til put metoden
    //Metoden tjekker om der er indsat noget gadenavn i forvejen med samme navn, og ellers tilføjer den hvis ikke
        //Hvis den nuværende node er null, så opretter den en node med den nuværende bogstav
        //Ellers går den til venstre, højre eller midt og tjekker om gadenavnet eksisterer i forvejen
        //Hvis ingen af disse if-statements er true, så er vi på sidste bogstav i gadenavnet, og tilføjer derfor adresseobjektet til listen i sidste node
    private AddressNode put(AddressNode node, String key, AddressObject address, int d) {
        char c = key.charAt(d);
        if (node == null) { node = new AddressNode(c); }
        if      (c < node.c) node.left  = put(node.left, key, address, d);
        else if (c > node.c) node.right = put(node.right, key, address, d);
        else if (d < key.length() - 1)
            node.mid = put(node.mid, key, address, d+1);
        //Hvis ingen af disse er tilfældet, så må vi være på sidste character.
        else node.addAddress(address);
        return node;
    }

    //Hjælpe metode til search metode
    private AddressNode getEndNode(String input){
        AddressNode node = get(root, input, 0);
        if(node == null) return null;
        return node;
    }

    //Hjælpe metode til search metoden
    //Denne metoder returnerer alle adresserne, som starter med det samme
        //fx. når der er blevet tastet 4 bogstaver ind i søgefeltet, så er det denne metode som står for hvilke adresser bliver vist
    private List<AddressObject> getAllEndOfAddressName(AddressNode currentRootNode){
        List<AddressObject> searchedList = new ArrayList<>();
        //System.out.println("currrentNode c: " + currentRootNode.c);

        if (currentRootNode == null) return null;

        //Vi skal have alle børnene (left, mid, right) for alle noderne efter endNode (sidste bogstav i ordet)
            //vi kalder endNode for currentRootNode i dette tilfælde, da den vil være root for det subtree med alle adresserne vi søger
        if(currentRootNode.getListOfAddresses() != null)              //tjekker om der er en liste i currentNode
            searchedList.addAll(currentRootNode.getListOfAddresses());

        if(currentRootNode.left != null) {                            //tjekker om currentRootNode har venstre barn
            //System.out.println("Venstre barn");
            searchedList.addAll(getAllEndOfAddressName(currentRootNode.left));
        }
        if(currentRootNode.right != null) {                           //tjekker om currentRootNode har højre barn
            //System.out.println("Højre barn");
            searchedList.addAll(getAllEndOfAddressName(currentRootNode.right));
        }
        if(currentRootNode.mid != null) {                             //tjekker om currentRootNode har mid barn
            //System.out.println("Mid barn");
            searchedList.addAll(getAllEndOfAddressName(currentRootNode.mid));
        }
        return searchedList;
    }

    //Hvis søgte gadenavn ikke matcher nogle adresser i TST, så returnerer den en tom liste ellers returnerer den liste med adresser
    public List<AddressObject> search(String input) {
        AddressNode endNode = getEndNode(input);
        if(endNode == null) {
            List<AddressObject> noMatches = new ArrayList<>();
            noMatches.add(new AddressObject("", "", "", "", 0, 0));
            return noMatches;
        }
        else return getAllEndOfAddressName(endNode);
    }

}
