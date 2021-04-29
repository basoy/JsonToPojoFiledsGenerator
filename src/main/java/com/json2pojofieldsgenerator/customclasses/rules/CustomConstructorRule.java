package com.json2pojofieldsgenerator.customclasses.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.NameHelper;
import org.jsonschema2pojo.util.ReflectionHelper;

public class CustomConstructorRule implements Rule<JDefinedClass, JDefinedClass> {
    private final RuleFactory ruleFactory;

    public CustomConstructorRule(RuleFactory ruleFactory, ReflectionHelper reflectionHelper) {
        this.ruleFactory = ruleFactory;
    }

    public JDefinedClass apply(String nodeName, JsonNode node, JsonNode parent, JDefinedClass instanceClass, Schema currentSchema) {
        this.addFieldsConstructor(instanceClass, this.getConstructorProperties(node));
        return instanceClass;
    }

    private void addFieldsConstructor(JDefinedClass instanceClass, Set<String> classProperties) {
        this.generateFieldsConstructor(instanceClass, classProperties);
    }

    private LinkedHashSet<String> getConstructorProperties(JsonNode node) {
        if (!node.has("properties")) {
            return new LinkedHashSet();
        } else {
            LinkedHashSet<String> rtn = new LinkedHashSet();
            Iterator properties;

            NameHelper nameHelper = this.ruleFactory.getNameHelper();
            properties = node.get("properties").fields();

            while (properties.hasNext()) {
                Entry<String, JsonNode> property = (Entry) properties.next();
                rtn.add(nameHelper.getPropertyName((String) property.getKey(), (JsonNode) property.getValue()));
            }
            return rtn;
        }
    }


    private JMethod generateFieldsConstructor(JDefinedClass jclass, Set<String> classProperties) {
        JMethod fieldsConstructor = jclass.constructor(JMod.PUBLIC);

        JBlock constructorBody = fieldsConstructor.body();

        Map<String, JFieldVar> fields = jclass.fields();
        Map<String, JAnnotationUse> classFieldParams = new HashMap<>();

        for (String property : classProperties) {
            JFieldVar field = fields.get(property);

            if (field == null) {
                throw new IllegalStateException("Property " + property + " hasn't been added to JDefinedClass before calling addConstructors");
            }

            JVar param = fieldsConstructor.param(field.type(), field.name());

            classFieldParams.put(property, param.annotate(JsonProperty.class).param("value", property));

            constructorBody.assign(JExpr._this()
                    .ref(field), param);

        }
        return fieldsConstructor;
    }

}