package com.thd.mapserver.domain;

import com.thd.mapserver.domain.exceptions.PoiConnectionException;
import com.thd.mapserver.domain.materials.Type;
import com.thd.mapserver.domain.materials.TypeList;

import java.util.List;

public interface PoiRepository {
    void add(SFAFeature poi) throws PoiConnectionException;

    void add(List<SFAFeature> pois) throws PoiConnectionException;

    void add(TypeList jsonObjectList) throws PoiConnectionException;

    void add(Type jsonObject) throws PoiConnectionException;
}

