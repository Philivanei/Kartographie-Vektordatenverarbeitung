package com.thd.mapserver.domain.materials;

public class Type {

    public String id;
    public String description;
    public String title;

    @Override
    public String toString() {
        return "JsonObject{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
