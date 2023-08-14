package com.glackfag.shorty.controllers;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.services.AssociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/stats")
public class StatsController {
    private final AssociationService associationService;

    @Autowired
    public StatsController(AssociationService associationService) {
        this.associationService = associationService;
    }

    @GetMapping
    public String showStats(@RequestParam String alias, Model model) {
        Optional<Association> association = associationService.findOne(alias);

        if (association.isPresent()) {
            model.addAttribute("association", association.get());
            return "successPage";
        }

        return null;
    }
}
