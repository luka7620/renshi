import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        // 创建数据库表（如果不存在）
        createTablesIfNotExists();
        
        // 启动登录窗口
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame();
            }
        });
    }

    private static void createTablesIfNotExists() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 创建用户表
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(50) PRIMARY KEY," +
                    "password VARCHAR(50) NOT NULL," +
                    "is_admin BOOLEAN DEFAULT false" +
                    ")");
            
            // 创建员工表
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS employee (" +
                    "no INT PRIMARY KEY," +
                    "name VARCHAR(50)," +
                    "dept VARCHAR(50)," +
                    "bornday INT," +
                    "minority VARCHAR(50)," +  // 修改为VARCHAR类型
                    "address VARCHAR(50)," +
                    "salary INT," +
                    "text INT," +
                    "other INT" +
                    ")");
            
            // 创建部门表
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS department (" +
                    "sno INT PRIMARY KEY," +
                    "first_dept VARCHAR(50)," +
                    "second_dept VARCHAR(50)" +
                    ")");
            
            // 创建调动历史表
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS transfer_history (" +
                    "Nonum INT AUTO_INCREMENT PRIMARY KEY," +
                    "operation VARCHAR(50)," +
                    "old_val VARCHAR(50)," +
                    "new_val VARCHAR(50)," +
                    "date DATETIME," +
                    "sno INT" +
                    ")");
            
            // 创建考核历史表
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS assessment_history (" +
                    "Nonum INT AUTO_INCREMENT PRIMARY KEY," +
                    "operation VARCHAR(50)," +
                    "old_val VARCHAR(50)," +
                    "new_val VARCHAR(50)," +
                    "date DATETIME," +
                    "sno INT" +
                    ")");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}    