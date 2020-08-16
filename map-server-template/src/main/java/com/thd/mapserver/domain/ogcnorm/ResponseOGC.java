package com.thd.mapserver.domain.ogcnorm;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ResponseOGC {

    @NotNull
    public List<Collections> collections = new ArrayList<>();
    @NotNull
    public List<Links> links = new ArrayList<>();


}
