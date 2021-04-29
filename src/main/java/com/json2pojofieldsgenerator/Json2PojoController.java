package com.json2pojofieldsgenerator;

import com.json2pojofieldsgenerator.request.FromJsonReq;
import com.json2pojofieldsgenerator.request.FromJsonWithFieldsReq;
import com.json2pojofieldsgenerator.request.FromUriReq;
import com.json2pojofieldsgenerator.request.FromUriWithFieldsReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.io.IOException;

@Component
@Controller
public class Json2PojoController {
    @Autowired
    private final Json2PojoService json2PojoService;

    public Json2PojoController(Json2PojoService json2PojoService) {
        this.json2PojoService = json2PojoService;
    }

    @PostMapping("/fromUriWithFields")
    public ResponseEntity fromUriWithFields(@Valid @RequestBody FromUriWithFieldsReq fromUriWithFieldsReq) throws IOException {
        if (json2PojoService.fromUriWithFields(fromUriWithFieldsReq)) {
            return ResponseEntity.ok(HttpStatus.OK);
        }
        return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/fromJsonWithFields")
    public ResponseEntity fromJsonWithFields(@Valid @RequestBody FromJsonWithFieldsReq fromJsonWithFieldsReq) throws IOException {
        if (json2PojoService.fromJsonWithFields(fromJsonWithFieldsReq)) {
            return ResponseEntity.ok(HttpStatus.OK);
        }
        return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/fromUri")
    public ResponseEntity fromUri(@Valid @RequestBody FromUriReq fromUriReq) throws IOException {
        if (json2PojoService.fromUri(fromUriReq)) {
            return ResponseEntity.ok(HttpStatus.OK);
        }
        return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/fromJson")
    public ResponseEntity fromJson(@Valid @RequestBody FromJsonReq fromJsonReq) throws IOException {
        if (json2PojoService.fromJson(fromJsonReq)) {
            return ResponseEntity.ok(HttpStatus.OK);
        }
        return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
    }
}