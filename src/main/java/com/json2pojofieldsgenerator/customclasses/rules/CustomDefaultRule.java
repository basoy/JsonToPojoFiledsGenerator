package com.json2pojofieldsgenerator.customclasses.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;

public class CustomDefaultRule implements Rule<JFieldVar, JFieldVar> {

    public JFieldVar apply(String nodeName, JsonNode node, JsonNode parent, JFieldVar field, Schema currentSchema) {
        boolean defaultPresent = node != null && StringUtils.isNotEmpty(node.asText());
        String fieldType = field.type().fullName();
        if (defaultPresent && !field.type().isPrimitive() && node.isNull()) {
            field.init(JExpr._null());
        } else if (fieldType.startsWith(String.class.getName()) && node != null) {
            field.init(getDefaultValue(field.type(), node));
        } else if (defaultPresent) {
            field.init(getDefaultValue(field.type(), node));
        }
        return field;
    }

    static JExpression getDefaultValue(JType fieldType, JsonNode node) {
        return getDefaultValue(fieldType, node.asText());
    }

    static JExpression getDefaultValue(JType fieldType, String value) {
        fieldType = fieldType.unboxify();
        if (fieldType.fullName().equals(String.class.getName())) {
            return JExpr.lit(value);
        } else if (fieldType.fullName().equals(Integer.TYPE.getName())) {
            return JExpr.lit(Integer.parseInt(value));
        } else if (fieldType.fullName().equals(BigInteger.class.getName())) {
            return JExpr._new(fieldType).arg(JExpr.lit(value));
        } else if (fieldType.fullName().equals(Double.TYPE.getName())) {
            return JExpr.lit(Double.parseDouble(value));
        } else if (fieldType.fullName().equals(BigDecimal.class.getName())) {
            return JExpr._new(fieldType).arg(JExpr.lit(value));
        } else if (fieldType.fullName().equals(Boolean.TYPE.getName())) {
            return JExpr.lit(Boolean.parseBoolean(value));
        } else {
            JInvocation invokeCreate;
            if (!fieldType.fullName().equals(LocalDate.class.getName()) && !fieldType.fullName().equals(LocalTime.class.getName())) {
                if (fieldType.fullName().equals(Long.TYPE.getName())) {
                    return JExpr.lit(Long.parseLong(value));
                } else if (fieldType.fullName().equals(Float.TYPE.getName())) {
                    return JExpr.lit(Float.parseFloat(value));
                } else if (fieldType.fullName().equals(URI.class.getName())) {
                    invokeCreate = fieldType.owner().ref(URI.class).staticInvoke("create");
                    return invokeCreate.arg(JExpr.lit(value));
                }
            }
            invokeCreate = JExpr._new(fieldType);
            invokeCreate.arg(JExpr.lit(value));
            return invokeCreate;

        }
    }
}
