package com.weeding.time.app.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationUserRole {

    ADMIN("Admin"),          // Admin
    GUEST("Gość"),           // Gość
    GROOM("Pan młody"),      // Pan młody
    BRIDE("Panna młoda"),    // Panna młoda
    WITNESS("Świadek");      // Świadek

    private final String displayName;

    // Metoda statyczna do mapowania Stringa na enum
    public static ApplicationUserRole fromDisplayName(String displayName) {
        for (ApplicationUserRole role : ApplicationUserRole.values()) {
            if (role.getDisplayName().equalsIgnoreCase(displayName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + displayName);
    }
}
