package bfst22.model.addresses;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class TSTTest {
    AddressObject addressObject1;
    AddressObject addressObject2;
    AddressObject addressObject3;
    AddressNode addressNodeS;
    AddressNode addressNodeL;
    TST tstONE;
    TST tstTWO;
    TST tstALL;
    ArrayList<AddressObject> listOfAddressObjectsONE = new ArrayList<>();
    ArrayList<AddressObject> listOfAddressObjectsTWO = new ArrayList<>();
    ArrayList<AddressObject> listOfAddressObjectsALL = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        addressObject1 = new AddressObject("Smallesund", "1A", "3700", "Rønne", 0,0);
        addressObject2 = new AddressObject("Smallesund", "15", "3700", "Rønne", 0,0);
        addressObject3 = new AddressObject("Lille Madsegade", "60", "3700", "Rønne", 0,0);
        addressNodeS = new AddressNode('S');
        addressNodeL = new AddressNode('L');

        tstONE = new TST(addressObject1);
        tstONE.put("Smallesund", addressObject2);
        tstTWO = new TST(addressObject1);
        tstALL = new TST(addressObject1);
        tstALL.put("Smallesund", addressObject2);
        tstALL.put("Lille Madsegade", addressObject3);

        listOfAddressObjectsONE.add(addressObject1);
        listOfAddressObjectsONE.add(addressObject2);
        listOfAddressObjectsTWO.add(addressObject3);
        listOfAddressObjectsALL.add(addressObject3);
        listOfAddressObjectsALL.add(addressObject1);
        listOfAddressObjectsALL.add(addressObject2);
    }

    @Test
    void getRoot() {
        assertEquals(addressNodeS.getC(), tstONE.getRoot().getC());
        assertEquals(addressNodeL.getC(), tstTWO.getRoot().getC());
    }

    @Test
    void get() {
        assertEquals(listOfAddressObjectsONE, tstONE.get("Smallesund"));
    }


    @Test
    void put() {
        tstTWO.put("Lille Madsegade", addressObject3);
        assertEquals(listOfAddressObjectsTWO, tstTWO.get("Lille Madsegade"));
    }

    @Test
    void search() {
        assertEquals(listOfAddressObjectsONE, tstONE.search("Smal"));
        assertEquals(listOfAddressObjectsALL, tstALL.search("S"));
    }
}