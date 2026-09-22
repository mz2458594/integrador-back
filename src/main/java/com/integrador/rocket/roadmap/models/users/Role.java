package com.integrador.rocket.roadmap.models.users;

import java.util.List;

public enum Role {
    ADMINISTRADOR(List.of("read", "write", "delete", "update")), ESTUDIANTE(List.of("read", "write", "update"));

    private final List<String> permissions;

    private Role(List<String> permissions){
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public static Role obtenerValores(String idioma){
        for (Role r: Role.values()){
            if (r.name().equalsIgnoreCase(idioma)){
                return r;
            }
        }
        throw new IllegalArgumentException("No se encontro ningún rol con el valor de " + idioma);
    }

}
