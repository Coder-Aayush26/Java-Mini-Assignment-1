package model;

import dao.*;
import util.InputUtil;

import java.util.List;

public class Student extends User {

    private int semester;

    public Student() {}

    @Override
    public boolean authenticate(String email, String password) {
        User u = UserDAO.getUserByEmailAndPassword(email, password, "student");
        if (u != null) {
            this.id = u.id;
            this.email = u.email;
            this.name = u.name;
            this.semester = StudentDAO.getSemesterByStudentId(this.id);
            return true;
        }
        return false;
    }

    @Override
    public void signUp() {
        System.out.println("--- Student SignUp ---");
        String fullName = InputUtil.readString("Full name: ");
        String email = InputUtil.readString("Email: ");
        String password = InputUtil.readString("Create password: ");
        int sem = 1; // must start at semester 1
        boolean ok = UserDAO.createStudent(fullName, email, password, sem);
        if (ok) System.out.println("Student account created. You can login now.");
        else System.out.println("Signup failed (maybe email exists).");
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. View Available Courses");
            System.out.println("2. Register for Course");
            System.out.println("3. View Schedule");
            System.out.println("4. Drop Course");
            System.out.println("5. Track Academic Progress (Grades / SGPA / CGPA)");
            System.out.println("6. Submit Complaint");
            System.out.println("7. View My Complaints");
            System.out.println("8. Logout");
            int ch = InputUtil.readInt("Choice: ");
            switch (ch) {
                case 1 -> CourseDAO.printAvailableCourses();
                case 2 -> registerCourse();
                case 3 -> EnrollmentDAO.printStudentSchedule(this.id);
                case 4 -> dropCourse();
                case 5 -> showAcademicProgress();
                case 6 -> submitComplaint();
                case 7 -> ComplaintDAO.printComplaintsByStudent(this.id);
                case 8 -> { System.out.println("Logging out."); return; }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void registerCourse() {
        System.out.println("--- Register Course ---");
        int sem = InputUtil.readInt("Select your current semester (you must start at 1): ");
        if (sem < this.semester) {
            System.out.println("You have already completed semester " + this.semester + ". Use that semester.");
        }
        // list courses of that semester
        List<model.Course> courses = CourseDAO.getCoursesBySemester(sem);
        if (courses.isEmpty()) {
            System.out.println("No courses for semester " + sem);
            return;
        }
        CourseDAO.printCoursesList(courses);
        int courseId = InputUtil.readInt("Enter course id to register: ");
        model.Course course = CourseDAO.getCourseById(courseId);
        if (course == null) {
            System.out.println("Invalid course id.");
            return;
        }
        // check semester match
        if (course.getSemester() != sem) {
            System.out.println("Course is not in selected semester.");
            return;
        }
        // check prerequisites
        if (!CourseDAO.checkPrerequisitesSatisfied(this.id, courseId)) {
            System.out.println("Prerequisites not satisfied. Cannot register.");
            return;
        }
        // check credits limit (20)
        int currentCredits = EnrollmentDAO.getCurrentCredits(this.id);
        if (currentCredits + course.getCredits() > 20) {
            System.out.println("Credit limit exceeded. Current: " + currentCredits + " Course: " + course.getCredits());
            return;
        }
        boolean ok = EnrollmentDAO.enrollStudent(this.id, courseId);
        if (ok) System.out.println("Registered successfully.");
        else System.out.println("Registration failed (maybe already enrolled).");
    }

    private void dropCourse() {
        System.out.println("--- Drop Course ---");
        EnrollmentDAO.printStudentEnrollments(this.id);
        int courseId = InputUtil.readInt("Enter course id to drop: ");
        boolean ok = EnrollmentDAO.dropCourse(this.id, courseId);
        if (ok) System.out.println("Course dropped.");
        else System.out.println("Drop failed (maybe not enrolled or semester completed).");
    }

    private void showAcademicProgress() {
        System.out.println("--- Academic Progress ---");
        GradeDAO.printGradesForStudent(this.id);
        double sgpa = GradeDAO.computeSGPA(this.id);
        double cgpa = GradeDAO.computeCGPA(this.id);
        System.out.printf("SGPA (latest completed semester): %.2f\n", sgpa);
        System.out.printf("CGPA: %.2f\n", cgpa);
    }

    private void submitComplaint() {
        System.out.println("--- Submit Complaint ---");
        String desc = InputUtil.readString("Description: ");
        boolean ok = ComplaintDAO.createComplaint(this.id, desc);
        if (ok) System.out.println("Complaint submitted (status Pending).");
        else System.out.println("Failed to submit complaint.");
    }
}

