
package com.example;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Support {

    private final String url;
    private final String text;

    public Support(@JsonProperty("url") String url, @JsonProperty("text") String text) {
        this.url = url;
        this.text = text;
    }

    public String url() {
        return url;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Support.class.getName()).append('[');
        sb.append("url=");
        sb.append(url);
        sb.append(",text=");
        sb.append(text);
        sb.append(']');
        return sb.toString();
    }

}
