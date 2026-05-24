package session;

import model.entity.Role;
import model.entity.User;

/**
 * Lớp Singleton lưu thông tin phiên đăng nhập hiện tại.
 *
 * <p>Sau khi {@code AuthService.login} thành công, {@link User} và {@link Role}
 * tương ứng sẽ được nạp vào đây. Toàn bộ Controller có thể đọc
 * {@code UserSession.getInstance()} để biết người dùng đang đăng nhập là ai,
 * vai trò gì, từ đó áp phân quyền (RBAC) ở tầng UI.</p>
 *
 * <p>Khi đăng xuất, gọi {@link #clear()} để xoá thông tin.</p>
 */
public class UserSession {

    private static UserSession instance;

    private User currentUser;
    private Role currentRole;

    private UserSession() {
    }

    /**
     * Lấy thể hiện duy nhất của {@code UserSession}.
     * Đồng bộ để an toàn khi gọi từ nhiều thread (ví dụ JavaFX Application Thread
     * và thread phụ trợ load dữ liệu).
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Role getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(Role currentRole) {
        this.currentRole = currentRole;
    }

    /**
     * Kiểm tra đã đăng nhập hay chưa.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Xoá thông tin phiên: gọi khi người dùng đăng xuất hoặc khi cần reset.
     */
    public void clear() {
        this.currentUser = null;
        this.currentRole = null;
    }
}
