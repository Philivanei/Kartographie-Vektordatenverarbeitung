package com.thd.mapserver.domain.geom;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class GeometryUtils {

    private GeometryUtils() {
    }

    public static String convertCoordinatesToWkt(SFAPoint point) {
        return Double.isNaN(point.getZ()) ? String.format(Locale.US, "%.7f %.7f", point.getX(), point.getY())
                : String.format(Locale.US, "%.7f %.7f %.7f", point.getX(), point.getY(), point.getZ());
    }

    public static String convertPolygonsToWkt(SFALinearRing outerRing, List<SFALinearRing> innerRings) {
        StringBuilder stringBuilder = new StringBuilder();

        List<SFALinearRing> allRings = new ArrayList<>();
        allRings.add(outerRing);
        if (!innerRings.isEmpty()) {
            allRings.addAll(innerRings);
        }

        var lastRing = allRings.get(allRings.size() - 1);

        for (SFALinearRing ring : allRings) {
            stringBuilder.append("(");
            for (int i = 0; i < ring.numPoints(); i++) {
                stringBuilder.append(convertCoordinatesToWkt(ring.pointN(i)));
                if (i != ring.numPoints() - 1) {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append(")");

            if (ring != lastRing) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

}
