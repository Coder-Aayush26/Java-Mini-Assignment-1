package dao;

import java.sql.*;

public class StudentDAO {
    public static int getSemesterByStudentId(int studentUserId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT semester FROM students WHERE user_id=?")) {
            ps.setInt(1, studentUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("semester");
        } catch (SQLException e) { e.printStackTrace(); }
        return 1;
    }
}

