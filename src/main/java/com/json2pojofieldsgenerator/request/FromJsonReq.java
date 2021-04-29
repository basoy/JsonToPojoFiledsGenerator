package com.json2pojofieldsgenerator.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FromJsonReq {

    @NotBlank(message = "json can't be blank")
    private final String json;
    @NotBlank(message = "className can't be blank")
    private final String className;
    @NotBlank(message = "packageName can't be blank")
    private final String packageName;

    public FromJsonReq(@NotNull String json, @NotNull String className, @NotNull String packageName) {
        this.json = json;
        this.className = className;
        this.packageName = packageName;
    }

    public String getJson() {
        return json;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }
}
