
package com.example;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Datum {

    private final Integer id;
    private final String name;
    private final Integer year;
    private final String color;
    private final String pantoneValue;

    public Datum(@JsonProperty("id") Integer id, @JsonProperty("name") String name, @JsonProperty("year") Integer year, @JsonProperty("color") String color, @JsonProperty("pantoneValue") String pantoneValue) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.color = color;
        this.pantoneValue = pantoneValue;
    }

    public Integer id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Integer year() {
        return year;
    }

    public String color() {
        return color;
    }

    public String pantone_value() {
        return pantoneValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Datum.class.getName()).append('[');
        sb.append("id=");
        sb.append(id);
        sb.append(",name=");
        sb.append(name);
        sb.append(",year=");
        sb.append(year);
        sb.append(",color=");
        sb.append(color);
        sb.append(",pantoneValue=");
        sb.append(pantoneValue);
        sb.append(']');
        return sb.toString();
    }

}
