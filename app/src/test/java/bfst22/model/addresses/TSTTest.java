package bfst22.model.addresses;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TSTTest {
    AddressObject addressObject1;
    AddressObject addressObject2;
    TST tst;
    ArrayList<AddressObject> listOfAddressObjects = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        addressObject1 = new AddressObject("Smallesund", "1A", "3700", "Rønne", 0,0);
        addressObject2 = new AddressObject("Lille Madsegade", "60", "3700", "Rønne", 0,0);

        tst = new TST(addressObject1);

        listOfAddressObjects.add(addressObject1);
        listOfAddressObjects.add(addressObject2);
    }



    @Test
    void getRoot() {
    }

    @Test
    void get() {
        assertEquals(listOfAddressObjects, tst.get("Smallesund"));
    }

    @Test
    void put() {
        assertEquals();
    }

    @Test
    void search() {
    }
}