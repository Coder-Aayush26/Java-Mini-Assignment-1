package dao;

import model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public static void printAvailableCourses() {
        printAllCourses();
    }

    public static void printAllCourses() {
        try (Connection c = DBManager.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,code,title,semester,credits,timings,professor_user_id,enrollment_limit FROM courses")) {
            System.out.println("Courses:");
            while (rs.next()) {
                System.out.printf("ID:%d | %s - %s | Sem:%d | Credits:%d | ProfUser:%s | Timings:%s | Limit:%d\n",
                        rs.getInt("id"), rs.getString("code"), rs.getString("title"),
                        rs.getInt("semester"), rs.getInt("credits"),
                        rs.getObject("professor_user_id")==null?"NA":rs.getString("professor_user_id"),
                        rs.getString("timings"), rs.getInt("enrollment_limit"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<Course> getCoursesBySemester(int sem) {
        List<Course> list = new ArrayList<>();
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM courses WHERE semester=?")) {
            ps.setInt(1, sem);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Course cr = rsToCourse(rs);
                list.add(cr);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void printCoursesList(List<Course> courses) {
        for (Course c : courses) {
            System.out.println(c);
        }
    }

    public static Course getCourseById(int id) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM courses WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rsToCourse(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private static Course rsToCourse(ResultSet rs) throws SQLException {
        Course cr = new Course();
        cr.setId(rs.getInt("id"));
        cr.setCode(rs.getString("code"));
        cr.setTitle(rs.getString("title"));
        cr.setSemester(rs.getInt("semester"));
        cr.setCredits(rs.getInt("credits"));
        cr.setTimings(rs.getString("timings"));
        cr.setSyllabus(rs.getString("syllabus"));
        cr.setEnrollmentLimit(rs.getInt("enrollment_limit"));
        int prof = rs.getInt("professor_user_id");
        if (rs.wasNull()) cr.setProfessorUserId(null);
        else cr.setProfessorUserId(prof);
        return cr;
    }

    public static boolean checkPrerequisitesSatisfied(int studentUserId, int courseId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT prereq_course_id FROM prerequisites WHERE course_id=?")) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int pre = rs.getInt("prereq_course_id");
                PreparedStatement ps2 = c.prepareStatement("SELECT grade FROM grades WHERE student_user_id=? AND course_id=?");
                ps2.setInt(1, studentUserId);
                ps2.setInt(2, pre);
                ResultSet rs2 = ps2.executeQuery();
                if (!rs2.next()) {
                    return false; // prereq not completed
                }
                String g = rs2.getString("grade");
                if (g == null || g.equalsIgnoreCase("F")) return false;
            }
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean addCourse(String code, String title, int sem, int credits, String timings, int limit) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO courses(code,title,semester,credits,timings,enrollment_limit) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1, code);
            ps.setString(2, title);
            ps.setInt(3, sem);
            ps.setInt(4, credits);
            ps.setString(5, timings);
            ps.setInt(6, limit);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public static boolean deleteCourse(int id) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM courses WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static void printCoursesByProfessor(int professorUserId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,code,title,semester,credits,timings,enrollment_limit FROM courses WHERE professor_user_id=?")) {
            ps.setInt(1, professorUserId);
            ResultSet rs = ps.executeQuery();
            System.out.println("Your courses:");
            while (rs.next()) {
                System.out.printf("ID:%d | %s - %s | Sem:%d | Credits:%d | Timings:%s | Limit:%d\n",
                        rs.getInt("id"), rs.getString("code"), rs.getString("title"),
                        rs.getInt("semester"), rs.getInt("credits"),
                        rs.getString("timings"), rs.getInt("enrollment_limit"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean isProfessorAssignedToCourse(int professorUserId, int courseId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM courses WHERE id=? AND professor_user_id=?")) {
            ps.setInt(1, courseId);
            ps.setInt(2, professorUserId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static void updateSyllabus(int cid, String syllabus) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE courses SET syllabus=? WHERE id=?")) {
            ps.setString(1, syllabus);
            ps.setInt(2, cid);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateTimings(int cid, String timings) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE courses SET timings=? WHERE id=?")) {
            ps.setString(1, timings);
            ps.setInt(2, cid);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateCredits(int cid, int credits) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE courses SET credits=? WHERE id=?")) {
            ps.setInt(1, credits);
            ps.setInt(2, cid);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateLimit(int cid, int limit) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE courses SET enrollment_limit=? WHERE id=?")) {
            ps.setInt(1, limit);
            ps.setInt(2, cid);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean assignProfessorToCourse(int profUserId, int courseId) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE courses SET professor_user_id=? WHERE id=?")) {
            ps.setInt(1, profUserId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}

