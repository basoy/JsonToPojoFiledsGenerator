package com.json2pojofieldsgenerator.customclasses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import org.apache.commons.lang3.StringUtils;
import org.jsonschema2pojo.AbstractTypeInfoAwareAnnotator;
import org.jsonschema2pojo.GenerationConfig;

public class CustomAnnotator extends AbstractTypeInfoAwareAnnotator {
    private final Include inclusionLevel;

    public CustomAnnotator(GenerationConfig generationConfig) {
        super(generationConfig);
        switch (generationConfig.getInclusionLevel()) {
            case ALWAYS:
                this.inclusionLevel = Include.ALWAYS;
                break;
            case NON_ABSENT:
                this.inclusionLevel = Include.NON_ABSENT;
                break;
            case NON_DEFAULT:
                this.inclusionLevel = Include.NON_DEFAULT;
                break;
            case NON_EMPTY:
                this.inclusionLevel = Include.NON_EMPTY;
                break;
            case NON_NULL:
                this.inclusionLevel = Include.NON_NULL;
                break;
            case USE_DEFAULTS:
                this.inclusionLevel = Include.USE_DEFAULTS;
                break;
            default:
                this.inclusionLevel = Include.NON_NULL;
        }

    }

    public void propertyInclusion(JDefinedClass clazz, JsonNode schema) {
        clazz.annotate(JsonInclude.class).param("value", this.inclusionLevel);
    }

    protected void addJsonTypeInfoAnnotation(JDefinedClass jclass, String propertyName) {
        JAnnotationUse jsonTypeInfo = jclass.annotate(JsonTypeInfo.class);
        jsonTypeInfo.param("use", Id.CLASS);
        jsonTypeInfo.param("include", As.PROPERTY);
        if (StringUtils.isNotBlank(propertyName)) {
            jsonTypeInfo.param("property", propertyName);
        }

    }
}
