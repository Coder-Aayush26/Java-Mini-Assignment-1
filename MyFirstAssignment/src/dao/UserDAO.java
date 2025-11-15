package dao;

import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static User getUserByEmailAndPassword(String email, String password, String role) {
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,name,email,role FROM users WHERE email=? AND password=? AND role=?")) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u;
                if (role.equals("student")) u = new model.Student();
                else u = new model.Professor();
                u.id = rs.getInt("id");
                u.name = rs.getString("name");
                u.email = rs.getString("email");
                return u;
            }
        } catch (SQLException e)
        {
            System.out.println("Hello");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean createStudent(String name, String email, String password, int semester) {
        String q = "INSERT INTO users(name,email,password,role) VALUES(?,?,?, 'student')";
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
            ResultSet gk = ps.getGeneratedKeys();
            if (gk.next()) {
                int uid = gk.getInt(1);
                PreparedStatement ps2 = c.prepareStatement("INSERT INTO students(user_id,semester) VALUES(?,?)");
                ps2.setInt(1, uid);
                ps2.setInt(2, semester);
                ps2.executeUpdate();
                ps2.close();
            }
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            return false;
        }
    }

    public static boolean createProfessor(String name, String email, String password) {
        String q = "INSERT INTO users(name,email,password,role) VALUES(?,?,?, 'professor')";
        try (Connection c = DBManager.getConnection();
             PreparedStatement ps = c.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
            ResultSet gk = ps.getGeneratedKeys();
            if (gk.next()) {
                int uid = gk.getInt(1);
                PreparedStatement ps2 = c.prepareStatement("INSERT INTO professors(user_id,expertise) VALUES(?,?)");
                ps2.setInt(1, uid);
                ps2.setString(2, "");
                ps2.executeUpdate();
                ps2.close();
            }
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            return false;
        }
    }

    public static void printAllStudents() {
        try (Connection c = DBManager.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT u.id,u.name,u.email,s.semester FROM users u JOIN students s ON u.id=s.user_id")) {
            System.out.println("Users (students):");
            while (rs.next()) {
                System.out.printf("UserId:%d | %s | %s | Semester:%d\n", rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getInt("semester"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void printAllProfessors() {
        try (Connection c = DBManager.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT u.id,u.name,u.email,p.expertise FROM users u JOIN professors p ON u.id=p.user_id")) {
            System.out.println("Professors:");
            while (rs.next()) {
                System.out.printf("UserId:%d | %s | %s | Expertise:%s\n", rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("expertise"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
