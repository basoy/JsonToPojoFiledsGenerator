package com.json2pojofieldsgenerator.request;

import javax.validation.constraints.NotBlank;

public class FromUriReq {
    @NotBlank(message = "uri can't be blank")
    private final String uri;
    @NotBlank(message = "className can't be blank")
    private final String className;
    @NotBlank(message = "packageName can't be blank")
    private final String packageName;

    public FromUriReq(String uri, String className, String packageName) {
        this.uri = uri;
        this.className = className;
        this.packageName = packageName;
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
}
