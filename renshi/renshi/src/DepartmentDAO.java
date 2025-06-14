import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {
    public void addDepartment(Department dept) throws SQLException {
        String sql = "INSERT INTO department (sno, first_dept, second_dept) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dept.getSno());
            pstmt.setString(2, dept.getFirstDept());
            pstmt.setString(3, dept.getSecondDept());
            pstmt.executeUpdate();
        }
    }

    public void updateDepartment(Department dept) throws SQLException {
        String sql = "UPDATE department SET first_dept=?, second_dept=? WHERE sno=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dept.getFirstDept());
            pstmt.setString(2, dept.getSecondDept());
            pstmt.setInt(3, dept.getSno());
            pstmt.executeUpdate();
        }
    }

    public void deleteDepartment(int sno) throws SQLException {
        String sql = "DELETE FROM department WHERE sno = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sno);
            pstmt.executeUpdate();
        }
    }

    public Department getDepartmentBySno(int sno) throws SQLException {
        String sql = "SELECT * FROM department WHERE sno = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sno);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Department dept = new Department();
                    dept.setSno(rs.getInt("sno"));
                    dept.setFirstDept(rs.getString("first_dept"));
                    dept.setSecondDept(rs.getString("second_dept"));
                    return dept;
                }
                return null;
            }
        }
    }

    public List<Department> getAllDepartments() throws SQLException {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM department";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Department dept = new Department();
                dept.setSno(rs.getInt("sno"));
                dept.setFirstDept(rs.getString("first_dept"));
                dept.setSecondDept(rs.getString("second_dept"));
                departments.add(dept);
            }
        }
        return departments;
    }
}    