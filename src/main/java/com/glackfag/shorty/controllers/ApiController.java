package com.glackfag.shorty.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glackfag.shorty.AliasGenerator;
import com.glackfag.shorty.dto.AssociationDTO;
import com.glackfag.shorty.dto.ReportDTO;
import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.models.Report;
import com.glackfag.shorty.services.AssociationService;
import com.glackfag.shorty.services.CreatorService;
import com.glackfag.shorty.services.ReportService;
import com.glackfag.shorty.util.validation.AssociationValidator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
@Slf4j
public class ApiController {
    @Value("${shorty.auth}")
    private String authToken;
    private final ObjectMapper objectMapper;
    private final AssociationValidator associationValidator;
    private final AliasGenerator aliasGenerator;
    private final AssociationService associationService;
    private final CreatorService creatorService;
    private final ReportService reportService;

    @Autowired
    public ApiController(ObjectMapper objectMapper, AssociationValidator associationValidator, AssociationService associationService, AliasGenerator aliasGenerator, CreatorService creatorService, ReportService reportService) {
        this.objectMapper = objectMapper;
        this.associationValidator = associationValidator;
        this.associationService = associationService;
        this.aliasGenerator = aliasGenerator;
        this.creatorService = creatorService;
        this.reportService = reportService;
    }

    @PostMapping("/create/custom")
    public ResponseEntity<HttpStatus> createWithCustomAlias(@RequestHeader("Authorization") String auth,
                                                            @RequestBody @Valid AssociationDTO associationDTO, BindingResult bindingResult) {
        if (!isValidAuthToken(auth))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Association association = objectMapper.convertValue(associationDTO, Association.class);

        associationValidator.validate(association, bindingResult);

        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);

        association.setUsages(0);
        association.setLastUsage(LocalDate.now());
        associationService.save(association);

        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    /**
     * Saves Association, generates alias automatically
     *
     * @param auth - Authorization token
     * @param associationDTO - Association instance with not empty 'destination' field (Association to save)
     * @return Present Association entity with auto-generated alias
     */
    @PostMapping("/create/auto")
    public ResponseEntity<AssociationDTO> create(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                                                 @RequestBody AssociationDTO associationDTO) {
        if (!isValidAuthToken(auth))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Association association = objectMapper.convertValue(associationDTO, Association.class);

        association.setUsages(0);
        association.setLastUsage(LocalDate.now());
        association.setAlias(aliasGenerator.generate(association));
        association.setReports(0);

        associationService.save(association);

        associationDTO = objectMapper.convertValue(association, AssociationDTO.class);
        return ResponseEntity.ok(associationDTO);
    }

    /**
     * Creates report and returns banned Association or null(if wasn't banned)
     *
     * @param auth - Authorization token
     * @param dto  - Report entity
     * @return Banned Association or null(if wasn't banned)
     */
    @PostMapping("/report")
    public ResponseEntity<AssociationDTO> report(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                                                 @RequestBody ReportDTO dto) {
        if (!isValidAuthToken(auth))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Report report = objectMapper.convertValue(dto, Report.class);

        if (!associationService.existsByAlias(report.getAlias()))
            return ResponseEntity.notFound().build();
        if (reportService.alreadyReported(report))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        boolean isBanned = reportService.save(report);

        if (isBanned) {
            Association association = associationService.findOne(report.getAlias()).get();
            return ResponseEntity.ok().body(objectMapper.convertValue(association, AssociationDTO.class));
        }

        return ResponseEntity.ok(null);
    }

    /**
     * @param auth      - Authorization token
     * @param data      - Requested data (ApiController.Data)
     * @param creatorId - CreatorId, required if data is 'field' or 'entities'
     * @param field - Required if data is 'field'
     * @param alias - Required if data is 'entity'
     * @return Required data
     */
    @GetMapping("/read")
    public ResponseEntity<?> readData(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestParam(name = "data") String data,
            @RequestParam(name = "cid", required = false) Long creatorId,
            @RequestParam(name = "field", required = false) String field,
            @RequestParam(name = "alias", required = false) String alias) {
        if (!isValidAuthToken(auth))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        switch (data) {
            case Data.FIELD -> {
                if (creatorId == null || field == null)
                    return ResponseEntity.badRequest().body("Missing parameters");
                if (Arrays.stream(Field.values()).map(Field::name).noneMatch(x -> x.equalsIgnoreCase(field)))
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

                return ResponseEntity.ok(result.toArray(new AssociationDTO[0]));
            }
        }

        return ResponseEntity.badRequest().body("Invalid data requested");
    }

    @ExceptionHandler(Throwable.class)
    protected void handle(Throwable e) {
        log.warn(e.getMessage());
    }

    private boolean isValidAuthToken(String token) {
        return token.equals(authToken);
    }

    private interface Data {
        String FIELD = "field"; // field of association
        String ENTITY = "entity"; // association
        String ENTITIES = "entities"; // associations by creatorId
    }

    public enum Field {
        DESTINATION,
        ALIAS;
    }

}
