package bfst22.vector;

import java.util.ArrayList;
import java.util.Comparator;

/*
    Denne klasse er en sorteret liste med Noder
        - Vi gør dette fordi, vi først indsætter al vores data (dvs vi kender størrelsen), for siden at hive det ud som vi har brug for.
        - Dette er smart, fordi så kan vi bruge en binær søgning på den sorterede liste
 */

public class NodeMap extends ArrayList<OSMNode> {
    boolean sorted;

    @Override
    //Vi har denne metode således at vi holder datastrukturen, selvom vi måske ikke overholder kontrakten
        //forstår ikke helt dette..
    public boolean add(OSMNode node) {
        sorted = false;
        return super.add(node);
    }

    public OSMNode get(long ref) {
        //Hvis listen ikke er sorteret, sorterer den listen her
        if (!sorted) {
            sort(Comparator.comparing(node -> node.id));
            sorted = true;
        }

        //Laver en binær søgning
        int lo = 0;
        int hi = size();
        /*
            Det er denne Invariant: get(lo).id <= ref < get(hi).id,
            vi sørger for at overholde, ved at sætte variablen 'hi' til at værre større end nogen værdi kan være.
         */
        while (hi - lo > 1) {
            int mi = (lo + hi) / 2;
            if (get(mi).id <= ref) {
                lo = mi;
            } else {
                hi = mi;
            }
        }
        //While-løkken kører indtil vi kun har 1 element tilbage
        //Hvis det element vi leder efter er i listen, så vil det være i 'lo' variablen (som er det sidste element tilbage).
        var node = get(lo);
        //Hvis elementet vi leder efter er lig med 'ref' (vores parameter), så returnerer vi det, ellers returnerer vi null
        return node.id == ref ? node : null;
    }
}