package com.thd.mapserver.domain.geom;

import java.util.List;

public class SFAGeometryCollection extends SFAGeometry {
    private static final String TYPENAME_GEOMETRY_COLLECTION = "GeometryCollection";
    List<SFAGeometry> sfaGeometries;

    public SFAGeometryCollection(List<SFAGeometry> sfaGeometries, int srid) {
        super(srid);
        this.sfaGeometries = sfaGeometries;
    }

    @Override
    public String asText() {
        int count = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (var geometry : sfaGeometries) {
            stringBuilder.append(geometry.asText());

            if ((sfaGeometries.size() - 1) != count) {
                stringBuilder.append(",");
            }
            count++;
        }
        return String.format("%s(%s)", TYPENAME_GEOMETRY_COLLECTION.toUpperCase(), stringBuilder.toString());
    }

    @Override
    public String geometryType() {
        return TYPENAME_GEOMETRY_COLLECTION;
    }
}
