package com.glackfag.shorty.util.validation;

import org.springframework.validation.FieldError;

public interface ErrorConstants {
    FieldError ALIAS_IS_ALREADY_USED = new FieldError("", "alias", null,
            false, new String[]{"AlreadyUsed"}, new Object[0], "Such alias is already used.");

    FieldError ALIAS_IS_RESERVED = new FieldError("", "alias", null,
            false, new String[]{"Reserved"}, new Object[0], "Such alias is reserved path.");
}
