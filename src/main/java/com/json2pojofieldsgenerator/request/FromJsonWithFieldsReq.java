package com.json2pojofieldsgenerator.request;

import javax.validation.constraints.NotBlank;

public class FromJsonWithFieldsReq {
    @NotBlank(message = "json can't be blank")
    private final String json;
    @NotBlank(message = "className can't be blank")
    private final String className;
    @NotBlank(message = "packageName can't be blank")
    private final String packageName;
    @NotBlank(message = "fields can't be blank")
    private final String fields;

    public FromJsonWithFieldsReq(String json, String className, String packageName, String fields) {
        this.json = json;
        this.className = className;
        this.packageName = packageName;
        this.fields = fields;
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

    public String getFields() {
        return fields;
    }
}
