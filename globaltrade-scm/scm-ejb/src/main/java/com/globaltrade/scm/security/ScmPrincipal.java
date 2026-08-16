package com.globaltrade.scm.security;

import java.security.Principal;
import java.util.Objects;

public class ScmPrincipal implements Principal {

    private final String name;

    public ScmPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScmPrincipal)) return false;
        return name.equals(((ScmPrincipal) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}