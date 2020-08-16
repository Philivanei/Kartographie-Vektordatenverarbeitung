package com.thd.mapserver.domain.materials;

import java.util.Arrays;

public class TypeList {

    public Type[] collections;

    @Override
    public String toString() {
        return "JsonObjectList{" +
                "collections=" + Arrays.toString(collections) +
                '}';
    }
}
