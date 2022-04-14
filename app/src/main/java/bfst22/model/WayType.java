package bfst22.model;

/*
    An enum is a special "class" that represents a group of constants (unchangeable variables, like final variables)
*/

// man kan tilføje flere typer f.eks. HIGHWAY

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum WayType {
    UNKNOWN, LAKE, BEACH, WOOD, SAND, FOREST, TREE_ROW, WATER, BUILDING, RESIDENTIAL, INDUSTRIAL, MEADOW, PARK,
    CEMETERY, ALLOTMENTS, SCRUB, FARMYARD, COMMERCIAL, CONSTRUCTION, RETAIL, FARMLAND, ORCHARD, VINEYARD, BASIN,
    GRASS, GREENFIELD, GREENHOUSE_HORTICULTURE, MILITARY, PLANT_NURSERY, QUARRY, RECREATION_GROUND, VILLAGE_GREEN,
    BROWNFIELD, GARAGES, LANDFILL, RAILWAY, RELIGIOUS, GRASSLAND, HEATH, PLAYGROUND, GARDEN, BAY, BARE_ROCK, COASTLINE,
    MOTORWAY, PRIMARY, TRUNK, SECONDARY, TERTIARY, UNCLASSIFIED, LIVING_STREET, SERVICE, PEDESTRIAN, TRACK, ROAD, RACEWAY,
    BUS_GUIDEWAY, FOOTWAY_ANDMORE, STEPS, BRIDLEWAY, CYCLEWAY, RESIDENTIALWAY, CONSTRUCTIONWAY, DOG_PARK, GOLF_COURSE,
    ICE_RINK, MARINA, MINIATURE_GOLF, NATURE_RESERVE, PITCH, SPORTS_CENTRE, SWIMMING_POOL, RIVER_ANDMORE, SCHOOL,
    STREAM_ANDMORE, DOCK, POND_ANDMORE, HARBOUR, PARKING;

    /*
    Her er også stroke farve, men fordi BUILDING skal have to forskellige farver, er der lavet to metoder.
    (se nedenfor)
     */
    public Paint getFillColor() {
        return switch (this) {
            case LAKE, BASIN -> Color.valueOf("B2D2DD");
            case RESIDENTIAL -> Color.valueOf("E0DFDF");
            case MEADOW -> Color.valueOf("D4EAB6");
            case CEMETERY -> Color.valueOf("B0CAB2");
            case ALLOTMENTS -> Color.valueOf("CFE0C1");
            case FARMYARD -> Color.valueOf("F1DDBE");
            case COMMERCIAL -> Color.valueOf("EEDAD9");
            case INDUSTRIAL -> Color.valueOf("E8DCE7");
            case CONSTRUCTION, BROWNFIELD -> Color.valueOf("C7C7B6");
            case RETAIL -> Color.valueOf("F8D6D1");
            case FARMLAND, GREENHOUSE_HORTICULTURE -> Color.valueOf("EEF0D8");
            case ORCHARD, VINEYARD -> Color.valueOf("B7DFA7");
            case GRASS -> Color.valueOf("D4E9B6");
            case GREENFIELD -> Color.valueOf("F1EEE9");
            case MILITARY -> Color.valueOf("F0E4DF");
            case PLANT_NURSERY -> Color.valueOf("B8DEA8");
            case QUARRY -> Color.valueOf("C3C2C2");
            case RECREATION_GROUND -> Color.valueOf("E5FBE4");
            case VILLAGE_GREEN -> Color.valueOf("D3EAB6");
            case GARAGES -> Color.valueOf("DEDDCE");
            case LANDFILL -> Color.valueOf("B6B694");
            case RAILWAY -> Color.valueOf("E9DBE8");
            case RELIGIOUS -> Color.valueOf("CDCCC9");
            case FOREST -> Color.valueOf("B4D0A3");
            case CONSTRUCTIONWAY -> Color.valueOf("AAAAAA");
            case MOTORWAY -> Color.valueOf("DD94A1");
            case TRUNK -> Color.valueOf("F2C3AF");
            case PRIMARY -> Color.valueOf("F7D8A8");
            case SECONDARY -> Color.valueOf("F8FAC1");
            case TERTIARY -> Color.valueOf("FEFEFE");
            case UNCLASSIFIED -> Color.valueOf("FEFEFE");
            case RESIDENTIALWAY -> Color.valueOf("FEFEFE");
            case LIVING_STREET -> Color.valueOf("EDEDED");
            case ROAD -> Color.valueOf("DDDDDD");
            case SERVICE -> Color.valueOf("FEFEFE");
            case PEDESTRIAN -> Color.valueOf("DDDDE8");
            case TRACK -> Color.valueOf("A6863A");
            case RACEWAY -> Color.valueOf("F5C1CA");
            case CYCLEWAY -> Color.valueOf("5656F2");
            case FOOTWAY_ANDMORE -> Color.valueOf("EA897B");
            case STEPS -> Color.valueOf("EC8574");
            case BRIDLEWAY -> Color.valueOf("64A257");
            case BUILDING -> Color.valueOf("D7D0CB");
            default -> /*BACKGROUNDCOLOR*/ Color.valueOf("F2EFEA");
        };
    }

    public Paint getStrokeColor() {
        return switch (this) {
            case BUILDING -> Color.valueOf("C3B8AE");
            default -> Color.BLACK;
        };
    }

    public int getWidth() {
        return switch (this) {
            case MOTORWAY -> 10;
            case TRUNK, PRIMARY -> 9;
            case SECONDARY -> 7;
            case TERTIARY -> 6;
            case UNCLASSIFIED, RESIDENTIALWAY, LIVING_STREET -> 5;
            case ROAD, RACEWAY -> 3;
            case CONSTRUCTIONWAY, SERVICE, PEDESTRIAN, STEPS -> 2;
            default -> 1;
        };
    }

    public int getDashes() {
        return switch (this) {
            case BRIDLEWAY -> 4;
            case TRACK, CYCLEWAY, FOOTWAY_ANDMORE, STEPS -> 3;
            default -> 1;
        };
    }
}

