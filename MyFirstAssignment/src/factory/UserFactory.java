package factory;

import model.User;
import model.Student;
import model.Professor;
import model.Admin;

public class UserFactory {
    // Factory design pattern to create different user objects based on role
    public static User getUser(String role) {
        return switch (role.toLowerCase()) {
            case "student" -> new Student();
            case "professor" -> new Professor();
            case "admin" -> new Admin();
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };
    }
}

