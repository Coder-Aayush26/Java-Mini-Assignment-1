package model;

public class Course {
    private int id;
    private String code;
    private String title;
    private int semester;
    private int credits;
    private String timings;
    private String syllabus;
    private int enrollmentLimit;
    private Integer professorUserId; // optional

    // getters/setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public String getTimings() { return timings; }
    public void setTimings(String timings) { this.timings = timings; }
    public String getSyllabus() { return syllabus; }
    public void setSyllabus(String syllabus) { this.syllabus = syllabus; }
    public int getEnrollmentLimit() { return enrollmentLimit; }
    public void setEnrollmentLimit(int enrollmentLimit) { this.enrollmentLimit = enrollmentLimit; }
    public Integer getProfessorUserId() { return professorUserId; }
    public void setProfessorUserId(Integer professorUserId) { this.professorUserId = professorUserId; }

    @Override
    public String toString() {
        return String.format("ID:%d | %s - %s | Sem:%d | Credits:%d | ProfUserId:%s | Timings:%s | Limit:%d",
                id, code, title, semester, credits, professorUserId==null?"NA":professorUserId.toString(), timings, enrollmentLimit);
    }
}

