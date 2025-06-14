import java.sql.*;

public class UserDAO {
    private static final String REGISTRATION_CODE = "HRSystem2025"; // 特殊注册码

    public boolean authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean register(String username, String password, String registrationCode) throws SQLException {
        // 验证注册码
        if (!REGISTRATION_CODE.equals(registrationCode)) {
            return false;
        }

        // 检查用户名是否已存在
        if (checkUsernameExists(username)) {
            return false;
        }

        // 插入新用户
        String sql = "INSERT INTO users (username, password, is_admin) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setBoolean(3, false); // 默认非管理员
            pstmt.executeUpdate();
            return true;
        }
    }

    private boolean checkUsernameExists(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}    