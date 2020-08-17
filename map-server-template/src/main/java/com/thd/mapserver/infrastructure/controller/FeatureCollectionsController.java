package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.domain.*;
import com.thd.mapserver.domain.database.PostgresqlGetData;
import com.thd.mapserver.domain.database.PostgresqlUploader;
import com.thd.mapserver.domain.exceptions.PostgresqlException;
import com.thd.mapserver.domain.geom.SFALinearRing;
import com.thd.mapserver.domain.geom.SFAPoint;
import com.thd.mapserver.domain.geom.SFAPolygon;
import com.thd.mapserver.domain.materials.TypeList;
import com.thd.mapserver.domain.ogcnorm.Collections;
import com.thd.mapserver.domain.ogcnorm.Links;
import com.thd.mapserver.domain.ogcnorm.Response;
import com.thd.mapserver.domain.parser.GeoJsonParser;
import com.thd.mapserver.domain.exceptions.PoiConnectionException;
import org.geojson.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class FeatureCollectionsController {

    private final String CONNECTION_STRING = "jdbc:postgresql://localhost/Studienarbeit?user=postgres&password=0000";

    @PostMapping(path = "/import_features")
    public HttpEntity<String> importGeoJson(@RequestBody GeoJsonObject geoJson) {
        GeoJsonParser geoJsonParser = new GeoJsonParser();
        var features = geoJsonParser.parseJson(geoJson);

        PoiRepository uploader = new PostgresqlUploader(CONNECTION_STRING);
        try {
            uploader.add(features);
        } catch (PoiConnectionException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping(path = "/import_feature_types")
    public HttpEntity<String> importFeatureTypes(@RequestBody TypeList typeList) {

        PoiRepository uploader = new PostgresqlUploader(CONNECTION_STRING);
        try {
            uploader.add(typeList);
        } catch (PoiConnectionException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping("/collections")
    public HttpEntity<Response> getLandingPage() throws PostgresqlException {
        Response responseOGC = new Response();
        PostgresqlGetData postgresqlGetData = new PostgresqlGetData(CONNECTION_STRING);

        var rs = postgresqlGetData.getCollections();

        try {
            Links linkCollections = new Links();
            linkCollections.href = "http://localhost:8080/collections/";
            linkCollections.rel = "self";
            responseOGC.links.add(linkCollections);

            while (rs.next()) {
                Collections collections = new Collections();
                Links linkCollection = new Links();
                Links linkCollectionItems = new Links();

                collections.id = rs.getString("id");
                collections.title = rs.getString("title");
                collections.description = rs.getString("description");

                linkCollectionItems.href = "http://localhost:8080/collections/" + collections.title + "/items";
                linkCollectionItems.rel = "items";
                linkCollection.href = "http://localhost:8080/collections/" + collections.title;
                linkCollection.rel = "collection";

                collections.links.add(linkCollectionItems);
                collections.links.add(linkCollection);

                responseOGC.collections.add(collections);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return new ResponseEntity<>(responseOGC, HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionid}")
    public HttpEntity<Response> getIdLandingPage(@PathVariable("collectionid") String typ) throws PostgresqlException {
        Response responseOGC = new Response();
        PostgresqlGetData postgresqlGetData = new PostgresqlGetData(CONNECTION_STRING);

        var rs = postgresqlGetData.getSelectedCollection(typ);

        try {
            Links linkCollections = new Links();
            linkCollections.href = "http://localhost:8080/collections/";
            linkCollections.rel = "self";
            responseOGC.links.add(linkCollections);

            while (rs.next()) {
                Collections collection = new Collections();
                Links linkCollection = new Links();
                Links linkCollectionItems = new Links();

                collection.id = rs.getString("id");
                collection.title = rs.getString("title");
                collection.description = rs.getString("description");

                linkCollectionItems.href = "http://localhost:8080/collections/" + collection.id + "/items";
                linkCollectionItems.rel = "items";
                linkCollection.href = "http://localhost:8080/collections/" + collection.id;
                linkCollection.rel = "self";

                collection.links.add(linkCollectionItems);
                collection.links.add(linkCollection);

                responseOGC.collections.add(collection);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return new ResponseEntity<>(responseOGC, HttpStatus.OK);
    }

    @GetMapping(value = "/collections/{collectionid}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getItemsLandingPage(@PathVariable("collectionid") String typ,
                                                      @RequestParam(required = false, name = "limit") Integer limit,
                                                      @RequestParam(required = false, name = "offset") Integer offset,
                                                      @RequestParam(required = false, name = "bbox") double[] bbox)
            throws PostgresqlException {

        if (limit == null) {
            limit = 10;
        }

        if (offset == null) {
            offset = 0;
        }

        PostgresqlGetData postgresqlGetData = new PostgresqlGetData(CONNECTION_STRING);

        String rs;

        if (bbox != null) {
            //build bbox polygon
            List<SFAPoint> bboxPoints = new ArrayList<>();

            switch (bbox.length) {
                case 4:
                    bboxPoints.add(new SFAPoint(bbox[0], bbox[1], 4326));
                    bboxPoints.add(new SFAPoint(bbox[0], bbox[3], 4326));
                    bboxPoints.add(new SFAPoint(bbox[2], bbox[3], 4326));
                    bboxPoints.add(new SFAPoint(bbox[2], bbox[1], 4326));
                    bboxPoints.add(new SFAPoint(bbox[0], bbox[1], 4326));

                    break;
                case 5:
                    //Bad request because 3D and 2D point mix would lead to information loss/information gathering that I don't have
                    return new ResponseEntity<>("Invalid bbox", HttpStatus.BAD_REQUEST);
                case 6:
                    bboxPoints.add(new SFAPoint(bbox[0], bbox[1], 4326));
                    bboxPoints.add(new SFAPoint(bbox[0], bbox[4], 4326));
                    bboxPoints.add(new SFAPoint(bbox[3], bbox[4], 4326));
                    bboxPoints.add(new SFAPoint(bbox[3], bbox[1], 4326));
                    bboxPoints.add(new SFAPoint(bbox[0], bbox[1], 4326));

                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            SFAPolygon bboxPolygon = new SFAPolygon(new SFALinearRing(bboxPoints, 4326), null, 4326);
            rs = postgresqlGetData.getSelectedFeatures(typ, bboxPolygon, limit, offset);

        } else {
            rs = postgresqlGetData.getSelectedFeatures(typ, limit, offset);
        }

        String geometries = rs;

        return new ResponseEntity<>(geometries, HttpStatus.OK);
    }

    @GetMapping(value = "/collections/{collectionid}/items/{featureid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getItemsLandingPage(@PathVariable("collectionid") String typ,
                                                      @PathVariable("featureid") String id) throws PostgresqlException {

        PostgresqlGetData postgresqlGetData = new PostgresqlGetData(CONNECTION_STRING);
        ResultSet rs = postgresqlGetData.getSelectedFeature(typ, id);
        String feature = null;
        try {
            while (rs.next()) {
                feature = rs.getString("json_build_object");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (feature != null) {
            return new ResponseEntity<>(feature, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid match (collection.id and feature.id)", HttpStatus.BAD_REQUEST);
        }
    }

}
