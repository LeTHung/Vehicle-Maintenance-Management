package service;

import model.entity.User;

/**
 * Service xử lý nghiệp vụ đăng nhập / đăng xuất hệ thống FleetCare.
 *
 * <p>Phối hợp giữa {@code UserDAO}, {@code RoleDAO}, {@code PasswordUtil}
 * và {@code UserSession}: xác thực mật khẩu bằng BCrypt, kiểm tra trạng thái
 * tài khoản, ghi nhận thời điểm đăng nhập, và nạp {@code currentUser} +
 * {@code currentRole} vào session.</p>
 *
 * <p>Day 1: skeleton. Day 4 sẽ implement đầy đủ.</p>
 */
public class AuthService {

    /**
     * Đăng nhập bằng cặp ({@code username}, {@code rawPassword}).
     *
     * @return entity {@link User} đã đăng nhập thành công
     * @throws AuthenticationException khi sai thông tin hoặc tài khoản bị khóa
     */
    public User login(String username, String rawPassword) throws AuthenticationException {
        // TODO Day 4: gọi UserDAO.findByUsername -> PasswordUtil.verify
        //             -> kiểm tra account_status -> set UserSession -> update last_login_at
        throw new UnsupportedOperationException("TODO Day 4: AuthService.login");
    }

    /**
     * Đăng xuất phiên hiện tại: xoá thông tin trong {@code UserSession}.
     */
    public void logout() {
        // TODO Day 4: UserSession.getInstance().clear();
        throw new UnsupportedOperationException("TODO Day 4: AuthService.logout");
    }
}
