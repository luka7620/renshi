import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class TransferHistoryDAO {
    public void addTransferHistory(TransferHistory history) throws SQLException {
        String sql = "INSERT INTO transfer_history (operation, old_val, new_val, date, sno) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, history.getOperation());
            pstmt.setString(2, history.getOldVal());
            pstmt.setString(3, history.getNewVal());
            pstmt.setTimestamp(4, new Timestamp(history.getDate().getTime()));
            pstmt.setInt(5, history.getSno());
            pstmt.executeUpdate();
        }
    }


    // TransferHistoryDAO.java
    public List<TransferHistory> getTransferHistoryBySno(int sno) throws SQLException {
        List<TransferHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM transfer_history WHERE sno = ? ORDER BY date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sno);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TransferHistory history = new TransferHistory();
                    history.setNonum(rs.getInt("nonum"));
                    history.setOperation(rs.getString("operation"));
                    history.setOldVal(rs.getString("old_val"));
                    history.setNewVal(rs.getString("new_val"));
                    history.setDate(rs.getTimestamp("date"));
                    history.setSno(rs.getInt("sno"));
                    histories.add(history);
                }
            }
        }
        return histories;
    }

    public void addTransferHistory(int employeeNo, String 部门调动, String oldDept, String selectedDepartment) {
    }
}