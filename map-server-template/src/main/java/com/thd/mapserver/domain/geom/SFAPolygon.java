package com.thd.mapserver.domain.geom;

import java.util.ArrayList;
import java.util.List;

public class SFAPolygon extends SFAGeometry {

    private static final String TYPENAME_POLYGON = "Polygon";
    private final SFALinearRing outerRing;
    private final List<SFALinearRing> innerRings;


    public SFAPolygon(SFALinearRing outerRing, List<SFALinearRing> innerRings, int srid) {
        super(srid);
        this.outerRing = outerRing;
        if(innerRings == null){
            this.innerRings = new ArrayList<>();
        } else {
            this.innerRings = innerRings;
        }
    }

    @Override
    public String asText() {
        return String.format("%s(%s)", TYPENAME_POLYGON.toUpperCase(), GeometryUtils.convertPolygonsToWkt(outerRing, innerRings));
    }

    @Override
    public String geometryType() {
        return TYPENAME_POLYGON;
    }
}
