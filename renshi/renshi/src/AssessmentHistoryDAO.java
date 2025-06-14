import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssessmentHistoryDAO {
    public void addAssessmentHistory(AssessmentHistory history) throws SQLException {
        String sql = "INSERT INTO assessment_history (operation, old_val, new_val, date, sno) VALUES (?, ?, ?, ?, ?)";
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

    public List<AssessmentHistory> getAssessmentHistoryBySno(int sno) throws SQLException {
        List<AssessmentHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM assessment_history WHERE sno = ? ORDER BY date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sno);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AssessmentHistory history = new AssessmentHistory();
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
}    