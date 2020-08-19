package com.thd.mapserver.domain.geom;

import java.util.List;

public class SFALineString extends SFAGeometry {

    private final List<SFAPoint> points;

    public SFALineString(List<SFAPoint> points, int srid) {
        super(srid);
        this.points = points;
    }

    public int numPoints() {
        return points.size();
    }

    public SFAPoint pointN(int integer) {
        if (integer < numPoints()) {
            return points.get(integer);
        } else {
            return null;
        }
    }

    public SFAPoint startPoint() {
        return points.get(0);
    }

    public SFAPoint endPoint() {
        return points.get(points.size() - 1);
    }

    public boolean isClosed() {
        return isRing();
    }

    public boolean isRing() {
        return startPoint().equals(endPoint());
    }

    @Override
    public String asText() {
        return null;
    }

    @Override
    public String geometryType() {
        return null;
    }
}
