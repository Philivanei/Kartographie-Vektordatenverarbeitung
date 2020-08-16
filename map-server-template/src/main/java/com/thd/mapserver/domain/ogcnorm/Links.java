package com.thd.mapserver.domain.ogcnorm;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

public class Links {

    @NotNull
    public String href;

    //optional
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String rel;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String hreflang;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer length;


}
