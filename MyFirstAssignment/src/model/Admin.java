package model;

import dao.*;
import util.InputUtil;

public class Admin extends User {
    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "admin123"; // fixed admin password

    public Admin() {
        this.name = "Administrator";
    }

    @Override
    public boolean authenticate(String email, String password) {
        if (email == null || password == null) return false;
        return email.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD);
    }

    @Override
    public void signUp() {
        System.out.println("Admin sign up not allowed.");
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Manage Course Catalog (add/delete/view)");
            System.out.println("2. Manage Student Records (view/update grades)");
            System.out.println("3. Assign Professors to Courses");
            System.out.println("4. Handle Complaints");
            System.out.println("5. Logout");
            int ch = InputUtil.readInt("Choice: ");
            switch (ch) {
                case 1 -> manageCatalog();
                case 2 -> manageStudents();
                case 3 -> assignProfessors();
                case 4 -> handleComplaints();
                case 5 -> { System.out.println("Logging out."); return; }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void manageCatalog() {
        System.out.println("--- Manage Catalog ---");
        System.out.println("1. View Courses");
        System.out.println("2. Add Course");
        System.out.println("3. Delete Course");
        int ch = InputUtil.readInt("Choice: ");
        switch (ch) {
            case 1 -> CourseDAO.printAllCourses();
            case 2 -> {
                String title = InputUtil.readString("Title: ");
                String code = InputUtil.readString("Course code: ");
                int sem = InputUtil.readInt("Semester: ");
                int credits = InputUtil.readInt("Credits (2/4): ");
                String timings = InputUtil.readString("Timings: ");
                int limit = InputUtil.readInt("Enrollment limit: ");
                boolean ok = CourseDAO.addCourse(code, title, sem, credits, timings, limit);
                System.out.println(ok ? "Course added." : "Add failed (maybe code exists).");
            }
            case 3 -> {
                int cid = InputUtil.readInt("Enter course id to delete: ");
                boolean ok = CourseDAO.deleteCourse(cid);
                System.out.println(ok ? "Deleted." : "Delete failed (maybe assigned or doesn't exist).");
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void manageStudents() {
        System.out.println("--- Manage Students ---");
        UserDAO.printAllStudents();
        int sid = InputUtil.readInt("Enter student user id to view details (or 0 to cancel): ");
        if (sid == 0) return;
        GradeDAO.printGradesForStudent(sid);
        if (InputUtil.readString("Do you want to assign/update grade for a course? (y/n): ").equalsIgnoreCase("y")) {
            int cid = InputUtil.readInt("Enter course id: ");
            String grade = InputUtil.readString("Enter grade (A, B, C, D, F): ");
            boolean ok = GradeDAO.assignGrade(sid, cid, grade);
            System.out.println(ok ? "Grade assigned/updated." : "Failed to assign grade.");
        }
    }

    private void assignProfessors() {
        System.out.println("--- Assign Professors ---");
        UserDAO.printAllProfessors();
        int pid = InputUtil.readInt("Enter professor user id: ");
        CourseDAO.printAllCourses();
        int cid = InputUtil.readInt("Enter course id to assign: ");
        boolean ok = CourseDAO.assignProfessorToCourse(pid, cid);
        System.out.println(ok ? "Assigned." : "Assign failed.");
    }

    private void handleComplaints() {
        System.out.println("--- Handle Complaints ---");
        ComplaintDAO.printAllComplaints();
        int compId = InputUtil.readInt("Enter complaint id to update status (0 to cancel): ");
        if (compId == 0) return;
        System.out.println("1. Mark Resolved");
        System.out.println("2. Keep Pending");
        int ch = InputUtil.readInt("Choice: ");
        if (ch == 1) {
            String res = InputUtil.readString("Enter resolution notes: ");
            boolean ok = ComplaintDAO.updateComplaintStatus(compId, "Resolved", res);
            System.out.println(ok ? "Updated." : "Update failed.");
        } else {
            System.out.println("No changes made.");
        }
    }
}
