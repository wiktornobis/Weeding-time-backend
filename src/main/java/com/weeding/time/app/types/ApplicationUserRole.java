package com.weeding.time.app.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationUserRole {

    ADMIN("Admin"),          // Admin
    BRIDE("Panna młoda"),    // Panna młoda
    GROOM("Pan młody"),      // Pan młody
    GUEST("Gość"),           // Gość
    WITNESS("Świadek");      // Świadek

    private final String displayRoleName;

    // Metoda statyczna do mapowania Stringa na enum
    public static ApplicationUserRole displayRoleName(String displayName) {
        for (ApplicationUserRole role : ApplicationUserRole.values()) {
            if (role.getDisplayRoleName().equalsIgnoreCase(displayName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + displayName);
    }
}
