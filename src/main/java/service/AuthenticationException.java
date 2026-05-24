package service;

/**
 * Exception ném ra khi xác thực đăng nhập thất bại.
 *
 * <p>Các trường hợp thường gặp: sai mật khẩu, không tìm thấy username,
 * tài khoản đang ở trạng thái {@code LOCKED}, ...</p>
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }
}
