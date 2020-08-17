package com.thd.mapserver.infrastructure.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.thd.mapserver.domain.database.PostgresqlGetData;
import com.thd.mapserver.domain.exceptions.PostgresqlException;
import com.thd.mapserver.domain.ogcnorm.Collections;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {

    private final String CONNECTION_STRING = "jdbc:postgresql://localhost/Studienarbeit?user=postgres&password=0000";

    @GetMapping("/")
    public String test(Model model) throws PostgresqlException {
        PostgresqlGetData postgresqlGetData = new PostgresqlGetData(CONNECTION_STRING);

        List<FeatureCollection> featureCollections = new ArrayList<>();
        var rs = postgresqlGetData.getCollections();

            try {
                while(rs.next()){
                    var name = rs.getString("title");
                    var description = rs.getString("description");
                    FeatureCollection featureCollection = new FeatureCollection(name, description);
                    featureCollections.add(featureCollection);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        model.addAttribute("collections", featureCollections); // A Attribute can be accessed via the ${attributeName} syntax
        // in the html template
        return "index"; // name of the template page located under resources/templates
    }

    @GetMapping("/conformance")
    public String getLandingPage() {
        return "conformance";
    }

    public static class FeatureCollection {
        private final String name;
        private final String description;

        public FeatureCollection(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }
    }

}
