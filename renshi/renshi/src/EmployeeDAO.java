import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    public void addEmployee(Employee emp) throws SQLException {
        String sql = "INSERT INTO employee (no, name, dept, bornday, minority, address, salary, text, other) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, emp.getNo());
            pstmt.setString(2, emp.getName());
            pstmt.setString(3, emp.getDept());
            pstmt.setInt(4, emp.getBornday());
            pstmt.setString(5, emp.getMinority());
            pstmt.setString(6, emp.getAddress());
            pstmt.setInt(7, emp.getSalary());
            pstmt.setString(8, emp.getText());
            pstmt.setString(9, emp.getOther());
            pstmt.executeUpdate();
        }
    }

    public void updateEmployee(Employee emp) throws SQLException {
        String sql = "UPDATE employee SET name=?, dept=?, bornday=?, minority=?, address=?, salary=?, text=?, other=? WHERE no=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emp.getName());
            pstmt.setString(2, emp.getDept());
            pstmt.setInt(3, emp.getBornday());
            pstmt.setString(4, emp.getMinority());
            pstmt.setString(5, emp.getAddress());
            pstmt.setInt(6, emp.getSalary());
            pstmt.setString(7, emp.getText()); // 修改为setString
            pstmt.setString(8, emp.getOther()); // 修改为setString
            pstmt.setInt(9, emp.getNo());
            pstmt.executeUpdate();
        }
    }

    public void deleteEmployee(int no) throws SQLException {
        String sql = "DELETE FROM employee WHERE no = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, no);
            pstmt.executeUpdate();
        }
    }

    public Employee getEmployeeByNo(int no) throws SQLException {
        String sql = "SELECT * FROM employee WHERE no = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, no);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapEmployee(rs);
                }
                return null;
            }
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }
        }
        return employees;
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setNo(rs.getInt("no"));
        emp.setName(rs.getString("name"));
        emp.setDept(rs.getString("dept"));
        emp.setBornday(rs.getInt("bornday"));
        emp.setMinority(rs.getString("minority"));
        emp.setAddress(rs.getString("address"));
        emp.setSalary(rs.getInt("salary"));
        emp.setText(rs.getString("text")); // 修改为getString
        emp.setOther(rs.getString("other")); // 修改为getString
        return emp;
    }
}