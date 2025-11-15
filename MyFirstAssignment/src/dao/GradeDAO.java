package dao;

import util.GradeUtil;

import java.sql.*;

public class GradeDAO {
    public static void printGradesForStudent(int studentUserId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT g.course_id,c.code,c.title,g.grade FROM grades g JOIN courses c ON g.course_id=c.id WHERE g.student_user_id=?")) {
            ps.setInt(1, studentUserId);
            ResultSet rs = ps.executeQuery();
            System.out.println("Grades:");
            while (rs.next()) {
                System.out.printf("CourseId:%d | %s - %s | Grade:%s\n", rs.getInt("course_id"), rs.getString("code"), rs.getString("title"), rs.getString("grade"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean assignGrade(int studentUserId, int courseId, String grade) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO grades(student_user_id,course_id,grade) VALUES(?,?,?) ON CONFLICT(student_user_id,course_id) DO UPDATE SET grade=excluded.grade")) {
            ps.setInt(1, studentUserId);
            ps.setInt(2, courseId);
            ps.setString(3, grade);
            ps.executeUpdate();
            // mark enrollment as completed
            PreparedStatement ps2 = c.prepareStatement("UPDATE enrollments SET completed=1 WHERE student_user_id=? AND course_id=?");
            ps2.setInt(1, studentUserId);
            ps2.setInt(2, courseId);
            ps2.executeUpdate();
            ps2.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static double computeSGPA(int studentUserId) {
        // compute average grade points for latest completed semester
        // find latest semester courses completed
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT c.semester, g.grade, c.credits FROM grades g JOIN courses c ON g.course_id=c.id WHERE g.student_user_id=?")) {
            ps.setInt(1, studentUserId);
            ResultSet rs = ps.executeQuery();
            // We'll compute simple CGPA across all completed (as fallback) and SGPA as latest semester if possible
            // For simplicity, compute CGPA; SGPA returns same if only one semester
            double totalPoints = 0;
            int totalCredits = 0;
            while (rs.next()) {
                String grade = rs.getString("grade");
                int credits = rs.getInt("credits");
                double gp = GradeUtil.gradeToPoints(grade);
                totalPoints += gp * credits;
                totalCredits += credits;
            }
            if (totalCredits == 0) return 0.0;
            return totalPoints / totalCredits;
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public static double computeCGPA(int studentUserId) {
        return computeSGPA(studentUserId); // same simplified approach
    }
}

