package util;

// Utility mapping grade letters to points (simple)
public class GradeUtil {
    public static double gradeToPoints(String grade) {
        if (grade == null) return 0.0;
        return switch (grade.trim().toUpperCase()) {
            case "A" -> 10.0;
            case "B" -> 8.0;
            case "C" -> 6.0;
            case "D" -> 4.0;
            default -> 0.0; // F or others
        };
    }
}

