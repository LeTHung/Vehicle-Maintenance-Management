package service;

import model.dao.RoleDAO;
import model.dao.UserDAO;
import model.entity.Role;
import model.entity.User;
import session.UserSession;
import util.PasswordUtil;

import java.time.LocalDateTime;

public class AuthService {

    private static final String STATUS_LOCKED = "LOCKED";

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    public User login(String username, String rawPassword) throws AuthenticationException {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new AuthenticationException("Vui lòng nhập tên đăng nhập và mật khẩu.");
        }

        User user = userDAO.findByUsername(username.trim())
                .orElseThrow(() -> new AuthenticationException("Tên đăng nhập hoặc mật khẩu không đúng."));

        if (STATUS_LOCKED.equalsIgnoreCase(user.getAccountStatus())) {
            throw new AuthenticationException("Tài khoản đã bị khóa.");
        }

        if (!PasswordUtil.verify(rawPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        Role role = roleDAO.findById(user.getRoleId())
                .orElseThrow(() -> new AuthenticationException("Tài khoản chưa được gán vai trò hợp lệ."));

        if (!role.isActive()) {
            throw new AuthenticationException("Vai trò của tài khoản đã bị vô hiệu hóa.");
        }

        userDAO.updateLastLogin(user.getUserId(), LocalDateTime.now());
        UserSession.getInstance().login(user, role);
        return user;
    }

    public void logout() {
        UserSession.getInstance().clear();
    }
}
