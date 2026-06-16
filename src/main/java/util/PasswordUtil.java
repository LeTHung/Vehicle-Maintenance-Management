package util;

/**
 * Tiện ích băm và xác thực mật khẩu.
 *
 * <p>Dự định dùng thư viện jBCrypt ({@code org.mindrot.jbcrypt.BCrypt}) ở
 * Day 2. Mọi mật khẩu thô KHÔNG được phép ghi xuống DB trực tiếp — phải đi
 * qua {@link #hash(String)} trước.</p>
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /**
     * Băm mật khẩu thô thành chuỗi hash an toàn để lưu DB.
     */
    public static String hash(String rawPassword) {
        // TODO Day 2: return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
        throw new UnsupportedOperationException("TODO Day 2: PasswordUtil.hash");
    }

    /**
     * So khớp mật khẩu thô với chuỗi hash đã lưu.
     */
    public static boolean verify(String rawPassword, String hash) {
        // TODO Day 2: return BCrypt.checkpw(rawPassword, hash);
        throw new UnsupportedOperationException("TODO Day 2: PasswordUtil.verify");
    }
}
