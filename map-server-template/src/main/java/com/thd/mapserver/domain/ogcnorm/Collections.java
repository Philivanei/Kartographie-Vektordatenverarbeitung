package com.thd.mapserver.domain.ogcnorm;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Collections {

    @NotNull
    public String id;
    @NotNull
    public List<Links> links = new ArrayList<>();

    //optional
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String itemType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String[] crs;

}
