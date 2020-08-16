package com.thd.mapserver.domain.parser;

import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.domain.geom.*;
import org.geojson.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoJsonParser {

    public GeoJsonParser() {

    }

    public List<SFAFeature> parseJson(GeoJsonObject json) {
        if (json instanceof FeatureCollection) {
            FeatureCollection featureCollection = (FeatureCollection) json;
            return parseFeatureCollection(featureCollection);
        } else if (json instanceof Feature) {

            Feature feature = (Feature) json;
            List<SFAFeature> sfaFeatures = new ArrayList<>();
            sfaFeatures.add(parseFeature(feature));
            return sfaFeatures;
        }
        return new ArrayList<>();
    }

    private List<SFAFeature> parseFeatureCollection(FeatureCollection featureCollection) {
        List<SFAFeature> features = new ArrayList<>();
        for (var feature : featureCollection.getFeatures()) {
            features.add(parseFeature(feature));
        }
        return features;
    }

    private SFAFeature parseFeature(Feature feature) {
        return new SFAFeature(
                feature.getId(),
                parseGeometry(feature.getGeometry()),
                parseProperties(feature.getProperties())
        );
    }

    private Map<String, String> parseProperties(Map<String, Object> properties) {
        Map<String, String> stringMap = new HashMap<>();
        //wandelt jedes element in string um
        properties.forEach((key, value) -> stringMap.put(key, value.toString()));
        return stringMap;
    }

    private SFAGeometry parseGeometry(GeoJsonObject geometry) {
        if (geometry instanceof GeometryCollection) {
            return parseGeometryCollection((GeometryCollection) geometry);

        } else if (geometry instanceof Point) {
            return parsePoint((Point) geometry);

        } else if (geometry instanceof Polygon) {
            return parsePolygon((Polygon) geometry);
        }
        return null;
    }

    private SFAGeometryCollection parseGeometryCollection(GeometryCollection geometryCollection) {
        List<SFAGeometry> sfaGeometries = new ArrayList<>();
        for (var geometry : geometryCollection.getGeometries()) {
            sfaGeometries.add(parseGeometry(geometry));
        }
        //zu Collection hinzufügen => Liste
        return new SFAGeometryCollection(sfaGeometries, 0);
    }

    private SFAPolygon parsePolygon(Polygon polygon) {
        SFALinearRing outerRing;
        List<SFALinearRing> innerRings = new ArrayList<>();

        List<SFAPoint> points = new ArrayList<>();
        for (var coordinates : polygon.getExteriorRing()) {
            points.add(new SFAPoint(coordinates.getLongitude(), coordinates.getLatitude(), coordinates.getAltitude(), 4326));
        }
        outerRing = new SFALinearRing(points, 4326);

        if(!polygon.getInteriorRings().isEmpty()){
            for (var ringList : polygon.getInteriorRings()) {
                points = new ArrayList<>();
                for (var coordinates: ringList) {
                    points.add(new SFAPoint(coordinates.getLongitude(), coordinates.getLatitude(), coordinates.getAltitude(), 4326));
                }
                innerRings.add(new SFALinearRing(points, 4326));
            }
            outerRing = new SFALinearRing(points, 4326);
        }

        return new SFAPolygon(outerRing, innerRings, 4326);
    }

    private SFAPoint parsePoint(Point point) {
        parseLngLatAlt(point.getCoordinates());
        return new SFAPoint(
                point.getCoordinates().getLongitude(),
                point.getCoordinates().getLatitude(),
                point.getCoordinates().getAltitude(),
                0);
    }

    private SFAPoint parseLngLatAlt(LngLatAlt lngLatAlt) {
        return new SFAPoint(lngLatAlt.getLongitude(), lngLatAlt.getLatitude(), lngLatAlt.getAltitude(), 0);
    }

}
