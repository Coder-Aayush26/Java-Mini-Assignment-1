package model;

public abstract class User {
    public int id;
    public String email;
    public String name;

    public String getDisplayName() {
        return name != null ? name : email;
    }

    public abstract boolean authenticate(String email, String password);
    public abstract void showMenu();
    public abstract void signUp(); // for student/professor flows (admin ignored)
}

