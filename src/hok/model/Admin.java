package hok.model;

import hok.enums.Role;

/**
 * Represents a system administrator.
 * Extends Person with admin-specific attributes.
 * Demonstrates inheritance: shares id/name/role from Person,
 * adds permissionLevel and password.
 */
public class Admin extends Person {
    private int permissionLevel;
    private String password;

    public Admin(String id, String name, String password) {
        super(id, name, Role.ADMIN);
        this.password = password;
        this.permissionLevel = 1;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public String getPassword() {
        return password;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
