package com.glackfag.shorty.util.validation;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.services.AssociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class AssociationValidator implements Validator {
    private final AssociationService associationService;
    private final List<String> reservedPaths;

    @Autowired
    public AssociationValidator(AssociationService associationService,
                                @Qualifier("reservedPaths") List<String> reservedPaths) {
        this.associationService = associationService;
        this.reservedPaths = reservedPaths;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == Association.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Association association = (Association) target;

        if(associationService.existsByAliasAndDestinationNot(association.getAlias(), association.getDestination()))
            ErrorTranslator.translateError(errors, ErrorConstants.ALIAS_IS_ALREADY_USED);
        if(reservedPaths.contains(association.getAlias()))
            ErrorTranslator.translateError(errors, ErrorConstants.ALIAS_IS_RESERVED);
        if(association.getAlias().matches("^[a-zA-Z0-9._]+$"))
            ErrorTranslator.translateError(errors, ErrorConstants.ALIAS_CONTAINS_RESERVED_CHAR);
    }
}
