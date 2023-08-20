package com.glackfag.shorty.controllers;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.services.AssociationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.NoSuchElementException;
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
    public String showStats(HttpServletRequest request, Model model) {
        try {
            String alias = request.getParameterNames().asIterator().next();
            Optional<Association> association = associationService.findOne(alias);

            model.addAttribute("association", association.orElseThrow());
            return "stats";
        } catch (NoSuchElementException e) {
            return "redirect:/";
        }
    }
}
