package com.glackfag.shorty.controllers;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.services.AssociationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("/")
@Slf4j
public class MainController {
    private final AssociationService associationService;
    private final String botUrl;

    @Autowired
    public MainController(AssociationService associationService,
                          @Qualifier("botUrl") String botUrl) {
        this.associationService = associationService;
        this.botUrl = botUrl;
    }

    @GetMapping
    public String mainPage() {
        return "redirect:" + botUrl;
    }

    @GetMapping("/bot")
    public String bot() {
        return "redirect:" + botUrl;
    }

    @GetMapping("/{alias}")
    public String redirect(@PathVariable("alias") String alias) {
        Optional<Association> association = associationService.findOne(alias);

        if (association.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found");

        associationService.updateStats(alias);

        return "redirect:" + association.get().getDestination();
    }
}
