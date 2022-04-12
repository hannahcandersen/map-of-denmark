package bfst22.model;

/*
    An enum is a special "class" that represents a group of constants (unchangeable variables, like final variables)
*/

// man kan tilføje flere typer f.eks. HIGHWAY

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.awt.*;

public enum WayType {
    UNKNOWN, LAKE, BEACH, WOOD, SAND, FOREST, TREE_ROW, WATER, BUILDING, RESIDENTIAL, INDUSTRIAL, MEADOW, PARK,
    WETLAND, CEMETERY, ALLOTMENTS, SCRUB, FARMYARD, COMMERCIAL, CONSTRUCTION, RETAIL, FARMLAND, ORCHARD, VINEYARD, BASIN,
    GRASS, GREENFIELD, GREENHOUSE_HORTICULTURE, MILITARY, PLANT_NURSERY, QUARRY, RECREATION_GROUND, VILLAGE_GREEN, SALT_POND,
    BROWNFIELD, GARAGES, LANDFILL, RAILWAY, RELIGIOUS, GRASSLAND, HEATH, PLAYGROUND, GARDEN, BAY, BARE_ROCK, COASTLINE,
    MOTORWAY, PRIMARY, TRUNK, SECONDARY, TERTIARY, UNCLASSIFIED, LIVING_STREET, SERVICE, PEDESTRIAN, TRACK, ROAD, RACEWAY,
    BUS_GUIDEWAY, FOOTWAY_ANDMORE, STEPS, BRIDLEWAY, CYCLEWAY, RESIDENTIALWAY, CONSTRUCTIONWAY, DOG_PARK, GOLF_COURSE,
    ICE_RINK, MARINA, MINIATURE_GOLF, NATURE_RESERVE, PITCH, SPORTS_CENTRE, SWIMMING_POOL, RIVER_ANDMORE, SCHOOL,
    STREAM_ANDMORE, DOCK, POND_ANDMORE, HARBOUR, PARKING;

    public Paint getColorValue() {
        return switch (this) {
            case LAKE, BASIN, SALT_POND -> Color.valueOf("B2D2DD");
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
            default -> /*BACKGROUNDCOLOR*/ Color.valueOf("F2EFEA");
        };
    }




}
