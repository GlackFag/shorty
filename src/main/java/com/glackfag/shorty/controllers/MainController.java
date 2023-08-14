package com.glackfag.shorty.controllers;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.services.AssociationService;
import com.glackfag.shorty.util.validation.AssociationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class MainController {
    private final AssociationService associationService;
    private final AssociationValidator associationValidator;

    @Autowired
    public MainController(AssociationService associationService, AssociationValidator associationValidator) {
        this.associationService = associationService;
        this.associationValidator = associationValidator;
    }

    @GetMapping
    public String mainPage(Model model) {
        model.addAttribute("newAssociation", new Association());

        return "index";
    }

    @PostMapping
    public String create(@ModelAttribute("newAssociation") @Valid Association association,
                         BindingResult bindingResult, Model model) {
        associationValidator.validate(association, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            return "index";
        }

        associationService.save(association);

        return "successPage";
    }

    @GetMapping("/{alias}")
    public String redirect(@PathVariable("alias") String alias) {
        Optional<Association> association = associationService.findOne(alias);

        if(association.isEmpty())
            return "index";

        associationService.updateStats(alias);

        return "redirect:" + association.get().getDestination();
    }
}
