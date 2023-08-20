package com.glackfag.shorty;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.services.AssociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Random;

@Component
public class AliasGenerator {
    private final AssociationService associationService;

    @Autowired
    public AliasGenerator(AssociationService associationService) {
        this.associationService = associationService;
    }

    public String generate(Association association) {
        String alias = "";
        Random random = new Random(association.getCreatorId());

        do {
            int rand1 = random.nextInt();
            int rand2 = random.nextInt();

            alias = Integer.toHexString(Objects.hash(rand1, rand2, association.getDestination(), alias));
        } while (associationService.existsByAliasAndCreatorNot(alias, association.getCreatorId()) ||
                associationService.isAliasAssociatedWithDifferentDestination(alias, association.getDestination()));

        return alias;
    }
}
