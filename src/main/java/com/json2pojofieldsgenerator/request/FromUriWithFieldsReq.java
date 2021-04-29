package com.json2pojofieldsgenerator.request;

import javax.validation.constraints.NotBlank;

public class FromUriWithFieldsReq {
    @NotBlank(message = "uri can't be blank")
    private final String uri;
    @NotBlank(message = "className can't be blank")
    private final String className;
    @NotBlank(message = "packageName can't be blank")
    private final String packageName;
    @NotBlank(message = "fields can't be blank")
    private final String fields;

    public FromUriWithFieldsReq(String uri, String className, String packageName, String fields) {
        this.uri = uri;
        this.className = className;
        this.packageName = packageName;
        this.fields = fields;
    }

    public String getUri() {
        return uri;
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
