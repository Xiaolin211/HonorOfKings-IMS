package hok.model;

import hok.enums.Role;

/**
 * Abstract base class for all persons in the system.
 * Provides common fields (id, name, role) shared by Player and Admin.
 * Cannot be instantiated directly — demonstrates abstraction.
 */
public abstract class Person {
    private final String id;
    private String name;
    private Role role;

    public Person(String id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + role + ")";
    }
}
