package hok.service;

import hok.enums.Role;
import hok.model.Admin;
import hok.model.Person;
import hok.model.Player;

/**
 * Handles login/logout with role-based access control.
 * Demonstrates polymorphism: currentUser is a Person reference
 * that can hold either a Player or Admin instance.
 */
public class AuthenticationService {

    private GameDataManager dm;
    private Person currentUser;

    public AuthenticationService(GameDataManager dm) {
        this.dm = dm;
        this.currentUser = null;
    }

    /**
     * Attempts login with ID and password.
     * Searches admins first, then players.
     * Returns true if credentials match, false otherwise.
     */
    public boolean login(String id, String password) {
        // Check admins
        Admin admin = dm.findAdminById(id);
        if (admin != null && admin.getPassword().equals(password)) {
            currentUser = admin;
            return true;
        }

        // Check players
        Player player = dm.findPlayerById(id);
        if (player != null && player.getPassword().equals(password)) {
            currentUser = player;
            return true;
        }

        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public Person getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Demonstrates polymorphism: the same Person reference is checked
     * for its actual runtime type.
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    /**
     * Players can only edit their own profile.
     * Admins can edit anyone.
     */
    public boolean canEditPlayer(String playerId) {
        if (!isLoggedIn()) return false;
        if (isAdmin()) return true;
        return currentUser.getId().equals(playerId);
    }

    /**
     * Only admins can manage data.
     */
    public boolean canManageData() {
        return isAdmin();
    }
}
