package com.thd.mapserver.domain.database;

import com.thd.mapserver.domain.exceptions.PostgresqlException;
import com.thd.mapserver.domain.geom.SFAPolygon;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresqlGetData {

    private final String connectionString;

    public PostgresqlGetData(String connectionString) {
        this.connectionString = connectionString;
    }

    public ResultSet getCollections() throws PostgresqlException {
        try (final var connection = DriverManager.getConnection(connectionString)) {
            final var getString = "SELECT * FROM public.collections";
            final var getStatement = connection.createStatement();

            return getStatement.executeQuery(getString);

        } catch (
                final SQLException e) {
            throw new PostgresqlException("Could not load data out of database: ", e);
        }
    }

    public ResultSet getSelectedCollection(String id) throws PostgresqlException {
        try (final var connection = DriverManager.getConnection(connectionString)) {
            final var getString = "SELECT * FROM public.collections WHERE id=?";
            final var getStatement = connection.prepareStatement(getString);

            getStatement.setString(1, id);

            return getStatement.executeQuery();

        } catch (
                final SQLException e) {
            throw new PostgresqlException("Could not load data out of database: ", e);
        }

    }


    public String getSelectedFeatures(String typ, int limit, int offset) throws PostgresqlException {
        try (final var connection = DriverManager.getConnection(connectionString)) {
            final var getString = "SELECT description, typ, ST_AsGeoJSON(geometries) as geoJson FROM features " +
                    "WHERE typ=? LIMIT ? OFFSET ?;";
            final var getStatement = connection.prepareStatement(getString);

            getStatement.setObject(1, typ);
            getStatement.setObject(2, limit);
            getStatement.setObject(3, offset);

            return getJsonString(getStatement);

        } catch (
                final SQLException e) {
            throw new PostgresqlException("Could not load data out of database: ", e);
        }
    }

    private String getJsonString(PreparedStatement getStatement) throws SQLException {
        var res = getStatement.executeQuery();

        StringBuilder stringBuilder = new StringBuilder("[");
        while (res.next()) {
            stringBuilder.append("{");
            stringBuilder.append(
                    String.format("\"type\": \"Feature\"," +
                                    "\"properties\": { \"description\": \"%s\", \"typ\": \"%s\" }," +
                                    "\"geometry\": %s",
                            res.getString("description"), res.getString("typ"), res.getString("geoJson"))
            );
            stringBuilder.append("}");
            if (!res.isLast()) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");

        return String.format("{" +
                "\"type\": \"FeatureCollection\"," +
                "\t\"features\": %s\n" +
                "\t}", stringBuilder.toString());
    }

    public String getSelectedFeatures(String typ, SFAPolygon bbox, int limit, int offset) throws PostgresqlException {
        try (final var connection = DriverManager.getConnection(connectionString)) {
            final var getString = "SELECT description, typ, ST_AsGeoJSON(geometries) as geoJson FROM features " +
                    "WHERE typ=? AND ST_Intersects(geometries, ST_GeomFromText(?)) LIMIT ? OFFSET ?;";
            final var getStatement = connection.prepareStatement(getString);

            getStatement.setObject(1, typ);
            getStatement.setObject(2, bbox.asText());
            getStatement.setObject(3, limit);
            getStatement.setObject(4, offset);

            return getJsonString(getStatement);

        } catch (
                final SQLException e) {
            throw new PostgresqlException("Could not load data out of database: ", e);
        }
    }

    public ResultSet getSelectedFeature(String typ, String id) throws PostgresqlException {
        try (final var connection = DriverManager.getConnection(connectionString)) {
            final var getString = "SELECT json_build_object(\n" +
                    "'type', 'Feature',\n" +
                    "\t'properties', json_build_object(\n" +
                    "\t\t'description', description,\n" +
                    "\t\t'typ', typ\n" +
                    "\t\t),\n" +
                    "\t\t'geometry', ST_AsGeoJSON(geometries)::json\n" +
                    "\t)\n" +
                    "FROM features WHERE typ=? AND id=?;";
            final var getStatement = connection.prepareStatement(getString);

            getStatement.setObject(1, typ);
            getStatement.setObject(2, java.util.UUID.fromString(id));

            return getStatement.executeQuery();

        } catch (
                final SQLException e) {
            throw new PostgresqlException("Could not load data out of database: ", e);
        }
    }
}
