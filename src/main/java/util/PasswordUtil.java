package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Tiện ích băm và xác thực mật khẩu.
 *
 * <p>Mọi mật khẩu thô KHÔNG được phép ghi xuống DB trực tiếp — phải đi qua
 * {@link #hash(String)} trước.</p>
 */
public final class PasswordUtil {

    private static final int LOG_ROUNDS = 12;

    private PasswordUtil() {
    }

    /**
     * Băm mật khẩu thô thành chuỗi hash an toàn để lưu DB.
     */
    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }

        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * So khớp mật khẩu thô với chuỗi hash đã lưu.
     */
    public static boolean verify(String rawPassword, String hash) {
        if (rawPassword == null || rawPassword.isBlank() || hash == null || hash.isBlank()) {
            return false;
        }

        try {
            return BCrypt.checkpw(rawPassword, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
