package model;

public class Complaint {
    private int id;
    private int studentUserId;
    private String description;
    private String status; // Pending/Resolved
    private String resolution;

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentUserId() { return studentUserId; }
    public void setStudentUserId(int studentUserId) { this.studentUserId = studentUserId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
}

