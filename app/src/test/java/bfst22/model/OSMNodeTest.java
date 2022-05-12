package bfst22.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OSMNodeTest {

    OSMNode osmNode = new OSMNode (22843065,8.316857f, (float) -55.169567);

    @Test
    void getId() {
        assertEquals(22843065, osmNode.getId());
    }


    @Test
    void getLat() {
        assertEquals(8.316857f, osmNode.getLat());
    }

    @Test
    void getLon() {
        assertEquals((float) -55.169567, osmNode.getLon());
    }
}