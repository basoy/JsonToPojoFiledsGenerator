package com.json2pojofieldsgenerator;

import com.json2pojofieldsgenerator.request.FromJsonReq;
import com.json2pojofieldsgenerator.request.FromJsonWithFieldsReq;
import com.json2pojofieldsgenerator.request.FromUriReq;
import com.json2pojofieldsgenerator.request.FromUriWithFieldsReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;;
import java.io.IOException;
import java.util.regex.Pattern;

@Service
public class Json2PojoService {

    public boolean fromUriWithFields(@Valid FromUriWithFieldsReq uriWithFieldsReq) throws IOException {
        if (isBlankArgument(uriWithFieldsReq) && isCorrectUri(uriWithFieldsReq.getUri())) {
            Json2PojoFromFieldsGenerator.generatePojoFromUri(
                    uriWithFieldsReq.getUri(),
                    uriWithFieldsReq.getClassName(),
                    uriWithFieldsReq.getPackageName(),
                    uriWithFieldsReq.getFields());
            return true;
        } else return false;
    }

    public boolean fromJsonWithFields(@Valid FromJsonWithFieldsReq jsonWithFieldsReq) throws IOException {
        if (isBlankArgument(jsonWithFieldsReq)) {
            Json2PojoFromFieldsGenerator.generatePojoFromJson(
                    jsonWithFieldsReq.getJson(),
                    jsonWithFieldsReq.getClassName(),
                    jsonWithFieldsReq.getPackageName(),
                    jsonWithFieldsReq.getFields());
            return true;
        } else return false;
    }

    public boolean fromUri(@Valid FromUriReq uriReq) throws IOException {
        if (isBlankArgument(uriReq) && isCorrectUri(uriReq.getUri())) {
            Json2PojoFromFieldsGenerator.generatePojoFromUri(
                    uriReq.getUri(),
                    uriReq.getClassName(),
                    uriReq.getPackageName(),
                    null);
            return true;
        } else return false;
    }

    public boolean fromJson(@Valid FromJsonReq jsonReq) throws IOException {
        if (isBlankArgument(jsonReq)) {
            Json2PojoFromFieldsGenerator.generatePojoFromJson(
                    jsonReq.getJson(),
                    jsonReq.getClassName(),
                    jsonReq.getPackageName(),
                    null);
            return true;
        } else return false;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Json2PojoService.class);

    private static boolean isBlankArgument(Object object) {
        boolean result = true;
        for (ConstraintViolation<Object> violation : Validation.buildDefaultValidatorFactory().getValidator().validate(object)) {
            LOGGER.error(violation.getMessage());
            result = false;
        }
        return result;
    }

    private static final String URL_REGEX =
            "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})";

    private static boolean isCorrectUri(String uri) {
        if (!Pattern.compile(URL_REGEX).matcher(uri).matches()) {
            LOGGER.error("Incorrect uri");
        }
        return Pattern.compile(URL_REGEX).matcher(uri).matches();
    }

}
