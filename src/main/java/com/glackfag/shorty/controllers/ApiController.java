package com.glackfag.shorty.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glackfag.shorty.AliasGenerator;
import com.glackfag.shorty.dto.AssociationDTO;
import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.services.AssociationService;
import com.glackfag.shorty.services.CreatorService;
import com.glackfag.shorty.util.validation.AssociationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    private final ObjectMapper objectMapper;
    private final AssociationValidator associationValidator;
    private final AliasGenerator aliasGenerator;
    private final AssociationService associationService;
    private final CreatorService creatorService;

    @Autowired
    public ApiController(ObjectMapper objectMapper, AssociationValidator associationValidator, AssociationService associationService, AliasGenerator aliasGenerator, CreatorService creatorService) {
        this.objectMapper = objectMapper;
        this.associationValidator = associationValidator;
        this.associationService = associationService;
        this.aliasGenerator = aliasGenerator;
        this.creatorService = creatorService;
    }

    @PostMapping("/create/custom")
    public HttpEntity<HttpStatus> createWithCustomAlias(@RequestBody @Valid AssociationDTO associationDTO,
                                                        BindingResult bindingResult) {
        Association association = objectMapper.convertValue(associationDTO, Association.class);

        associationValidator.validate(association, bindingResult);

        if (bindingResult.hasErrors())
            return new HttpEntity<>(HttpStatus.BAD_REQUEST);

        association.setUsages(0);
        association.setLastUsage(LocalDate.now());
        associationService.save(association);

        return new HttpEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/create/auto")
    public HttpEntity<AssociationDTO> create(@RequestBody AssociationDTO associationDTO) {
        Association association = objectMapper.convertValue(associationDTO, Association.class);

        association.setUsages(0);
        association.setLastUsage(LocalDate.now());
        association.setAlias(aliasGenerator.generate(association));

        associationService.save(association);

        associationDTO = objectMapper.convertValue(association, AssociationDTO.class);
        return new HttpEntity<>(associationDTO);
    }

    @GetMapping("/read")
    public ResponseEntity<?> readData(
            @RequestParam(name = "data") String data,
            @RequestParam(name = "cid", required = false) Long creatorId,
            @RequestParam(name = "field", required = false) String field,
            @RequestParam(name = "alias", required = false) String alias) {
        switch (data) {
            case Data.FIELD -> {
                if (creatorId == null || field == null)
                    return ResponseEntity.badRequest().body("Missing parameters");
                if(Arrays.stream(Field.values()).map(Field::name).noneMatch(x -> x.equalsIgnoreCase(field)))
                    return ResponseEntity.badRequest().body("Invalid field parameter");

                Field requiredField = Field.valueOf(field);
                List<String> resultList = creatorService.findFieldsByCreatorId(creatorId, requiredField);
                return ResponseEntity.ok(resultList.toArray(new String[0]));
            }
            case Data.ENTITY -> {
                if (alias == null)
                    return ResponseEntity.badRequest().body("Missing parameters");

                Optional<Association> associationOptional = associationService.findOne(alias);

                return associationOptional.map(association ->
                        ResponseEntity.ok(objectMapper.convertValue(association, AssociationDTO.class))).
                        orElseGet(() -> ResponseEntity.ok(new AssociationDTO()));
            }
            case Data.ENTITIES -> {
                if (creatorId == null)
                    return ResponseEntity.badRequest().body("Missing parameters");

                List<AssociationDTO> result = associationService.findAllByCreatorId(creatorId)
                        .stream().map(x -> objectMapper.convertValue(x, AssociationDTO.class)).toList();

                return ResponseEntity.ok(result);
            }
        }

        return ResponseEntity.badRequest().body("Invalid data requested");
    }


    private interface Data {
        String FIELD = "field";
        String ENTITY = "entity";
        String ENTITIES = "entities";
    }

    public enum Field {
        DESTINATION,
        ALIAS;
    }

}
