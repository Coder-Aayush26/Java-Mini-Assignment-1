package model;

import dao.*;
import util.InputUtil;

public class Professor extends User {
    private int professorId;

    public Professor() {}

    @Override
    public boolean authenticate(String email, String password) {
        User u = UserDAO.getUserByEmailAndPassword(email, password, "professor");
        if (u != null) {
            this.id = u.id;
            this.email = u.email;
            this.name = u.name;
            this.professorId = ProfessorDAO.getProfessorIdByUserId(this.id);
            return true;
        }
        return false;
    }

    @Override
    public void signUp() {
        System.out.println("--- Professor SignUp ---");
        String fullName = InputUtil.readString("Full name: ");
        String email = InputUtil.readString("Email: ");
        String password = InputUtil.readString("Create password: ");
        boolean ok = UserDAO.createProfessor(fullName, email, password);
        if (ok) System.out.println("Professor account created. Ask admin to assign courses.");
        else System.out.println("Signup failed (maybe email exists).");
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n--- Professor Menu ---");
            System.out.println("1. View My Courses");
            System.out.println("2. Manage Course (update details)");
            System.out.println("3. View Enrolled Students for a Course");
            System.out.println("4. Logout");
            int ch = InputUtil.readInt("Choice: ");
            switch (ch) {
                case 1 -> CourseDAO.printCoursesByProfessor(this.id);
                case 2 -> manageCourse();
                case 3 -> viewEnrolledStudents();
                case 4 -> { System.out.println("Logging out."); return; }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void manageCourse() {
        System.out.println("--- Manage Course ---");
        CourseDAO.printCoursesByProfessor(this.id);
        int cid = InputUtil.readInt("Enter course id to update: ");
        if (!CourseDAO.isProfessorAssignedToCourse(this.id, cid)) {
            System.out.println("You are not assigned to this course.");
            return;
        }
        System.out.println("1. Update syllabus (text)");
        System.out.println("2. Update timings");
        System.out.println("3. Update credits");
        System.out.println("4. Update enrollment limit");
        int ch = InputUtil.readInt("Choice: ");
        switch (ch) {
            case 1 -> {
                String s = InputUtil.readString("Enter new syllabus: ");
                CourseDAO.updateSyllabus(cid, s);
                System.out.println("Syllabus updated.");
            }
            case 2 -> {
                String t = InputUtil.readString("Enter new timings: ");
                CourseDAO.updateTimings(cid, t);
                System.out.println("Timings updated.");
            }
            case 3 -> {
                int c = InputUtil.readInt("Enter credits (2 or 4): ");
                CourseDAO.updateCredits(cid, c);
                System.out.println("Credits updated.");
            }
            case 4 -> {
                int lim = InputUtil.readInt("Enter new enrollment limit: ");
                CourseDAO.updateLimit(cid, lim);
                System.out.println("Limit updated.");
            }
            default -> System.out.println("Invalid choice");
        }
    }

    private void viewEnrolledStudents() {
        System.out.println("--- Enrolled Students ---");
        CourseDAO.printCoursesByProfessor(this.id);
        int cid = InputUtil.readInt("Enter course id: ");
        EnrollmentDAO.printStudentsInCourse(cid);
    }
}

