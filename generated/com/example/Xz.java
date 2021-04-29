
package com.example;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Xz {

    private final Integer page;
    private final Integer perPage;
    private final Integer total;
    private final Integer totalPages;
    private final List<Datum> data;
    private final Support support;

    public Xz(@JsonProperty("page") Integer page, @JsonProperty("perPage") Integer perPage, @JsonProperty("total") Integer total, @JsonProperty("totalPages") Integer totalPages, @JsonProperty("data") List<Datum> data, @JsonProperty("support") Support support) {
        this.page = page;
        this.perPage = perPage;
        this.total = total;
        this.totalPages = totalPages;
        this.data = data;
        this.support = support;
    }

    public Integer page() {
        return page;
    }

    public Integer per_page() {
        return perPage;
    }

    public Integer total() {
        return total;
    }

    public Integer total_pages() {
        return totalPages;
    }

    public List<Datum> data() {
        return data;
    }

    public Support support() {
        return support;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Xz.class.getName()).append('[');
        sb.append("page=");
        sb.append(page);
        sb.append(",perPage=");
        sb.append(perPage);
        sb.append(",total=");
        sb.append(total);
        sb.append(",totalPages=");
        sb.append(totalPages);
        sb.append(",data=");
        sb.append(data);
        sb.append(",support=");
        sb.append(support);
        sb.append(']');
        return sb.toString();
    }

}
