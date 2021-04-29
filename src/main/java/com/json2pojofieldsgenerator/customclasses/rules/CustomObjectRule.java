package com.json2pojofieldsgenerator.customclasses.rules;

import static org.apache.commons.lang3.StringUtils.*;
import static org.jsonschema2pojo.rules.PrimitiveTypes.*;
import static org.jsonschema2pojo.util.TypeUtil.*;

import java.util.Arrays;
import java.util.Map;

import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.exception.ClassAlreadyExistsException;
import org.jsonschema2pojo.exception.GenerationException;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.ReflectionHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class CustomObjectRule implements Rule<JPackage, JType> {

    private final RuleFactory ruleFactory;
    private final ReflectionHelper reflectionHelper;

    public CustomObjectRule(RuleFactory ruleFactory, ReflectionHelper reflectionHelper) {
        this.ruleFactory = ruleFactory;
        this.reflectionHelper = reflectionHelper;
    }

    @Override
    public JType apply(String nodeName, JsonNode node, JsonNode parent, JPackage _package, Schema schema) {

        JType superType = reflectionHelper.getSuperType(nodeName, node, _package, schema);
        if (superType.isPrimitive() || reflectionHelper.isFinal(superType)) {
            return superType;
        }

        JDefinedClass jclass;
        try {
            jclass = createClass(nodeName, node, _package);
        } catch (ClassAlreadyExistsException e) {
            return e.getExistingClass();
        }

        jclass._extends((JClass) superType);

        schema.setJavaTypeIfEmpty(jclass);

        ruleFactory.getPropertiesRule().apply(nodeName, node.get("properties"), node, jclass, schema);

        ruleFactory.getDynamicPropertiesRule().apply(nodeName, node.get("properties"), node, jclass, schema);

        addToString(jclass);

        ruleFactory.getConstructorRule().apply(nodeName, node, parent, jclass, schema);

        makeFinalFields(jclass);

        return jclass;
    }

    private JDefinedClass createClass(String nodeName, JsonNode node, JPackage _package) throws ClassAlreadyExistsException {

        JDefinedClass newType;

        Annotator annotator = ruleFactory.getAnnotator();

        try {
            if (node.has("existingJavaType")) {
                String fqn = substringBefore(node.get("existingJavaType").asText(), "<");


                if (isPrimitive(fqn, _package.owner())) {
                    throw new ClassAlreadyExistsException(primitiveType(fqn, _package.owner()));
                }

                JClass existingClass = resolveType(_package, fqn + (node.get("existingJavaType").asText().contains("<") ? "<" + substringAfter(node.get("existingJavaType").asText(), "<") : ""));
                throw new ClassAlreadyExistsException(existingClass);
            }

            boolean usePolymorphicDeserialization = annotator.isPolymorphicDeserializationSupported(node);

            if (node.has("javaType")) {
                String fqn = node.path("javaType").asText();

                if (isPrimitive(fqn, _package.owner())) {
                    throw new GenerationException("javaType cannot refer to a primitive type (" + fqn + "), did you mean to use existingJavaType?");
                }

                if (fqn.contains("<")) {
                    throw new GenerationException("javaType does not support generic args (" + fqn + "), did you mean to use existingJavaType?");
                }

                int index = fqn.lastIndexOf(".") + 1;
                if (index == 0) { //Actually not a fully qualified name
                    fqn = _package.name() + "." + fqn;
                    index = fqn.lastIndexOf(".") + 1;
                }

                if (index >= 0 && index < fqn.length()) {
                    fqn = fqn.substring(0, index) + ruleFactory.getGenerationConfig().getClassNamePrefix() + fqn.substring(index) + ruleFactory.getGenerationConfig().getClassNameSuffix();
                }

                if (usePolymorphicDeserialization) {
                    newType = _package.owner()._class(JMod.PUBLIC, fqn, ClassType.CLASS);
                } else {
                    newType = _package.owner()._class(fqn);
                }
            } else {
                if (usePolymorphicDeserialization) {
                    newType = _package._class(JMod.PUBLIC, ruleFactory.getNameHelper().getUniqueClassName(nodeName, node, _package), ClassType.CLASS);
                } else {
                    newType = _package._class(ruleFactory.getNameHelper().getUniqueClassName(nodeName, node, _package));
                }
            }
        } catch (JClassAlreadyExistsException e) {
            throw new ClassAlreadyExistsException(e.getExistingClass());
        }

        annotator.typeInfo(newType, node);
        annotator.propertyInclusion(newType, node);

        return newType;

    }

    private void makeFinalFields(JDefinedClass jclass) {
        if (jclass.fields() != null) {
            for (JFieldVar field : jclass.fields().values()) {
                field.mods().setFinal(true);
            }
        }
    }

    private void addToString(JDefinedClass jclass) {
        Map<String, JFieldVar> fields = jclass.fields();
        JMethod toString = jclass.method(JMod.PUBLIC, String.class, "toString");

        JBlock body = toString.body();

        JClass stringBuilderClass = jclass.owner().ref(StringBuilder.class);
        JVar sb = body.decl(stringBuilderClass, "sb", JExpr._new(stringBuilderClass));

        body.add(sb
                .invoke("append").arg(jclass.dotclass().invoke("getName"))
                .invoke("append").arg(JExpr.lit('[')));

        // If this has a parent class, include its toString()
        if (!jclass._extends().fullName().equals(Object.class.getName())) {
            JVar baseLength = body.decl(jclass.owner().INT, "baseLength", sb.invoke("length"));
            JVar superString = body.decl(jclass.owner().ref(String.class), "superString", JExpr._super().invoke("toString"));

            JBlock superToStringBlock = body._if(superString.ne(JExpr._null()))._then();

            JVar contentStart = superToStringBlock.decl(jclass.owner().INT, "contentStart",
                    superString.invoke("indexOf").arg(JExpr.lit('[')));
            JVar contentEnd = superToStringBlock.decl(jclass.owner().INT, "contentEnd",
                    superString.invoke("lastIndexOf").arg(JExpr.lit(']')));

            JConditional superToStringInnerConditional = superToStringBlock._if(
                    contentStart.gte(JExpr.lit(0)).cand(contentEnd.gt(contentStart)));

            superToStringInnerConditional._then().add(
                    sb.invoke("append")
                            .arg(superString)
                            .arg(contentStart.plus(JExpr.lit(1)))
                            .arg(contentEnd));

            // Otherwise, just append super.toString()
            superToStringInnerConditional._else().add(sb.invoke("append").arg(superString));

            // Append a comma if needed
            body._if(sb.invoke("length").gt(baseLength))
                    ._then().add(sb.invoke("append").arg(JExpr.lit(',')));
        }

        boolean isFirstTime = true;
        for (JFieldVar fieldVar : fields.values()) {
            if (isFirstTime) {
                body.add(sb.invoke("append").arg(fieldVar.name() + '='));
                isFirstTime = false;
            } else {
                body.add(sb.invoke("append").arg(',' + fieldVar.name() + '='));
            }

            if (fieldVar.type().isPrimitive()) {
                body.add(sb.invoke("append").arg(JExpr.ref(fieldVar.name())));
            } else if (fieldVar.type().isArray()) {
                // Only primitive arrays are supported
                if (!fieldVar.type().elementType().isPrimitive()) {
                    throw new UnsupportedOperationException("Only primitive arrays are supported");
                }

                // Leverage Arrays.toString()
                body.add(sb.invoke("append")
                        .arg(
                                jclass.owner().ref(Arrays.class).staticInvoke("toString")
                                        .arg(JExpr.ref(fieldVar.name()))
                                        .invoke("replace").arg(JExpr.lit('[')).arg(JExpr.lit('{'))
                                        .invoke("replace").arg(JExpr.lit(']')).arg(JExpr.lit('}'))
                                        .invoke("replace").arg(JExpr.lit(", ")).arg(JExpr.lit(","))));
            } else {
                body.add(sb.invoke("append")
                        .arg(JExpr.ref(fieldVar.name())));
            }

        }

        body.add(sb.invoke("append").arg(JExpr.lit(']')));

        body._return(sb.invoke("toString"));

        toString.annotate(Override.class);
    }

}
