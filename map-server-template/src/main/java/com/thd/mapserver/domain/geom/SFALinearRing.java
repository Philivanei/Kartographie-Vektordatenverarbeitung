package com.thd.mapserver.domain.geom;

import java.security.InvalidParameterException;
import java.util.List;

public class SFALinearRing extends SFALineString {

    public SFALinearRing(List<SFAPoint> points, int srid) {
        super(points, srid);
        if(!isRing()){
            throw new InvalidParameterException();
        }
    }

}
