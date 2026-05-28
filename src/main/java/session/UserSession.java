package session;

import model.entity.Role;
import model.entity.User;

public final class UserSession {

    private static final UserSession INSTANCE = new UserSession();

    private User currentUser;
    private Role currentRole;

    private UserSession() {
    }

    public static UserSession getInstance() {
        return INSTANCE;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Role getCurrentRole() {
        return currentRole;
    }

    public void login(User user, Role role) {
        this.currentUser = user;
        this.currentRole = role;
    }

    public void clear() {
        this.currentUser = null;
        this.currentRole = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
