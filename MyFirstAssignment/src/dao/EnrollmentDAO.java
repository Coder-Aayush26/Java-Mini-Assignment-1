package dao;

import java.sql.*;

public class EnrollmentDAO {

    public static int getCurrentCredits(int studentUserId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT SUM(c.credits) as tot FROM enrollments e JOIN courses c ON e.course_id=c.id WHERE e.student_user_id=? AND e.completed=0")) {
            ps.setInt(1, studentUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("tot");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static boolean enrollStudent(int studentUserId, int courseId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement check = c.prepareStatement("SELECT 1 FROM enrollments WHERE student_user_id=? AND course_id=?")) {
            check.setInt(1, studentUserId);
            check.setInt(2, courseId);
            ResultSet rs = check.executeQuery();
            if (rs.next()) return false; // already enrolled
            PreparedStatement ps = c.prepareStatement("INSERT INTO enrollments(student_user_id,course_id,completed) VALUES(?,?,0)");
            ps.setInt(1, studentUserId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static void printStudentSchedule(int studentUserId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT c.id,c.code,c.title,c.timings,u.name as professor FROM enrollments e JOIN courses c ON e.course_id=c.id LEFT JOIN users u ON c.professor_user_id=u.id WHERE e.student_user_id=?")) {
            ps.setInt(1, studentUserId);
            ResultSet rs = ps.executeQuery();
            System.out.println("Weekly Schedule:");
            while (rs.next()) {
                System.out.printf("CourseID:%d | %s - %s | Timings:%s | Professor:%s\n",
                        rs.getInt("id"), rs.getString("code"), rs.getString("title"),
                        rs.getString("timings"), rs.getString("professor"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void printStudentEnrollments(int studentUserId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT c.id,c.code,c.title FROM enrollments e JOIN courses c ON e.course_id=c.id WHERE e.student_user_id=?")) {
            ps.setInt(1, studentUserId);
            ResultSet rs = ps.executeQuery();
            System.out.println("Your enrollments:");
            while (rs.next()) {
                System.out.printf("CourseId:%d | %s - %s\n", rs.getInt("id"), rs.getString("code"), rs.getString("title"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean dropCourse(int studentUserId, int courseId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM enrollments WHERE student_user_id=? AND course_id=? AND completed=0")) {
            ps.setInt(1, studentUserId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static void printStudentsInCourse(int courseId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT u.id,u.name,u.email FROM enrollments e JOIN users u ON e.student_user_id=u.id WHERE e.course_id=?")) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            System.out.println("Enrolled Students:");
            while (rs.next()) {
                System.out.printf("UserId:%d | %s | %s\n", rs.getInt("id"), rs.getString("name"), rs.getString("email"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}

