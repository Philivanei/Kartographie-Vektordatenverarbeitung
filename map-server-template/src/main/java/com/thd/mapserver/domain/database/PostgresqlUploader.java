package com.thd.mapserver.domain.database;

import com.thd.mapserver.domain.PoiRepository;
import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.domain.exceptions.PoiConnectionException;
import com.thd.mapserver.domain.exceptions.PostgresqlException;
import com.thd.mapserver.domain.materials.Type;
import com.thd.mapserver.domain.materials.TypeList;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;


public class PostgresqlUploader implements PoiRepository {

    private final String connectionString;

    public PostgresqlUploader(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public void add(SFAFeature poi) throws PoiConnectionException {

        final var geometry = poi.getGeometry();
        final var properties = poi.getProperties();

        final var wktGeometry = geometry.asText();
        final var sridGeometry = geometry.srid();
        final var typ = properties.get("type");
        final var description = properties.get("description");

        try (final var connection = DriverManager.getConnection(connectionString)) {
            final var insertString = "INSERT INTO features VALUES (uuid_generate_v4(), ST_GeomFromText(?, ?), ?, ?);";
            final var insertStatement = connection.prepareStatement(insertString);


            insertStatement.setString(1, wktGeometry);
            insertStatement.setInt(2, sridGeometry);
            insertStatement.setString(3, typ);
            insertStatement.setString(4, description);

            insertStatement.executeUpdate();

        } catch (
                final SQLException e) {
            throw new PostgresqlException("Could not save poi feature with geometry: " + wktGeometry, e);
        }

    }

    @Override
    public void add(List<SFAFeature> pois) throws PoiConnectionException {
        for (var poi : pois) {
            add(poi);
        }
    }

    @Override
    public void add(Type type) throws PoiConnectionException {
        final var id = type.id;
        final var description = type.description;
        final var title = type.title;

        try (final var connection = DriverManager.getConnection(connectionString)) {
            final var insertString = "INSERT INTO collections VALUES (?, ?, ?);";
            final var insertStatement = connection.prepareStatement(insertString);

            insertStatement.setString(1, id);
            insertStatement.setString(2, description);
            insertStatement.setString(3, title);

            insertStatement.executeUpdate();

        } catch (
                final SQLException e) {
            throw new PostgresqlException("Could not save collection with id: " + id, e);
        }
    }

    @Override
    public void add(TypeList typeList) throws PoiConnectionException {
        for (var material : typeList.collections) {
            add(material);
        }
    }
}
