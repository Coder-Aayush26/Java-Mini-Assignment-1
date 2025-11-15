package main;

import factory.UserFactory;
import model.User;
import dao.DBManager;
import util.InputUtil;

public class Main {
    public static void main(String[] args) {
        try {
            DBManager.init(); // create DB / tables & sample data if not present
            runApp();
        } catch (Exception e) {
            System.err.println("Fatal error initializing application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBManager.closeConnection();
        }
    }

    private static void runApp() {
        while (true) {
            System.out.println("\n=== Welcome to University Course Registration System ===");
            System.out.println("1. Login as Student");
            System.out.println("2. Login as Professor");
            System.out.println("3. Login as Administrator");
            System.out.println("4. Exit");
            int choice = InputUtil.readInt("Enter choice: ");
            switch (choice) {
                case 1 -> login("student");
                case 2 -> login("professor");
                case 3 -> login("admin");
                case 4 -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private static void login(String role) {
        System.out.println("\n--- " + role.toUpperCase() + " LOGIN ---");
        String email;
        String password;
        if (role.equals("admin")) {
            email = InputUtil.readString("Enter admin username (default 'admin'): ");
            password = InputUtil.readString("Enter admin password: ");
        } else {
            email = InputUtil.readString("Email: ");
            password = InputUtil.readString("Password: ");
        }

        User user = UserFactory.getUser(role);
        if (user.authenticate(email, password)) {
            System.out.println("Login successful. Hello, " + user.getDisplayName());
            user.showMenu();
        } else {
            System.out.println("Login failed. Check credentials or sign up if Student/Professor.");
            if (!role.equals("admin")) {
                String s = InputUtil.readString("Do you want to sign up as " + role + " now? (y/n): ");
                if (s.equalsIgnoreCase("y")) {
                    user.signUp();
                    System.out.println("Sign up complete. Try logging in.");
                }
            }
        }
    }
}

