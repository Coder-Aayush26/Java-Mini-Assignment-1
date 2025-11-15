package dao;

import java.sql.*;

public class ComplaintDAO {

    public static boolean createComplaint(int studentUserId, String desc) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO complaints(student_user_id,description,status) VALUES(?,?, 'Pending')")) {
            ps.setInt(1, studentUserId);
            ps.setString(2, desc);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static void printComplaintsByStudent(int studentUserId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,description,status,resolution,created_at FROM complaints WHERE student_user_id=?")) {
            ps.setInt(1, studentUserId);
            ResultSet rs = ps.executeQuery();
            System.out.println("Your complaints:");
            while (rs.next()) {
                System.out.printf("Id:%d | %s | Status:%s | Resolution:%s | CreatedAt:%s\n", rs.getInt("id"), rs.getString("description"), rs.getString("status"), rs.getString("resolution"), rs.getString("created_at"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void printAllComplaints() {
        try (Connection c = DBManager.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,student_user_id,description,status,resolution,created_at FROM complaints")) {
            System.out.println("All complaints:");
            while (rs.next()) {
                System.out.printf("Id:%d | StudentId:%d | %s | Status:%s | Resolution:%s | CreatedAt:%s\n",
                        rs.getInt("id"), rs.getInt("student_user_id"), rs.getString("description"),
                        rs.getString("status"), rs.getString("resolution"), rs.getString("created_at"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean updateComplaintStatus(int id, String status, String resolution) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE complaints SET status=?, resolution=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setString(2, resolution);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}

