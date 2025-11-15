package dao;

import java.sql.*;

public class DBManager {
    private static Connection conn = null;

    // Adjust these according to your MySQL setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db?useSSL=false";
    private static final String DB_USER = "root"; // your MySQL username
    private static final String DB_PASSWORD = "pass123"; // your MySQL password

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return conn;
    }

    public static void init() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found! Add mysql-connector-j.jar to classpath.");
            return;
        }

        Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement st = c.createStatement();

        // Create tables if they don't exist
        st.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100),
                email VARCHAR(100) UNIQUE,
                password VARCHAR(100),
                role ENUM('student','professor') NOT NULL
            );
        """);

        st.execute("""
            CREATE TABLE IF NOT EXISTS students (
                user_id INT PRIMARY KEY,
                semester INT DEFAULT 1,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            );
        """);

        st.execute("""
            CREATE TABLE IF NOT EXISTS professors (
                user_id INT PRIMARY KEY,
                expertise VARCHAR(100),
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            );
        """);

        st.execute("""
            CREATE TABLE IF NOT EXISTS courses (
                id INT AUTO_INCREMENT PRIMARY KEY,
                code VARCHAR(50) UNIQUE,
                title VARCHAR(100),
                semester INT,
                credits INT,
                timings VARCHAR(100),
                syllabus TEXT,
                enrollment_limit INT,
                professor_user_id INT,
                FOREIGN KEY(professor_user_id) REFERENCES users(id)
            );
        """);

        st.execute("""
            CREATE TABLE IF NOT EXISTS prerequisites (
                course_id INT,
                prereq_course_id INT,
                PRIMARY KEY(course_id, prereq_course_id),
                FOREIGN KEY(course_id) REFERENCES courses(id),
                FOREIGN KEY(prereq_course_id) REFERENCES courses(id)
            );
        """);

        st.execute("""
            CREATE TABLE IF NOT EXISTS enrollments (
                student_user_id INT,
                course_id INT,
                completed BOOLEAN DEFAULT FALSE,
                PRIMARY KEY(student_user_id, course_id),
                FOREIGN KEY(student_user_id) REFERENCES users(id),
                FOREIGN KEY(course_id) REFERENCES courses(id)
            );
        """);

        st.execute("""
            CREATE TABLE IF NOT EXISTS grades (
                student_user_id INT,
                course_id INT,
                grade CHAR(2),
                PRIMARY KEY(student_user_id, course_id),
                FOREIGN KEY(student_user_id) REFERENCES users(id),
                FOREIGN KEY(course_id) REFERENCES courses(id)
            );
        """);

        st.execute("""
            CREATE TABLE IF NOT EXISTS complaints (
                id INT AUTO_INCREMENT PRIMARY KEY,
                student_user_id INT,
                description TEXT,
                status VARCHAR(20),
                resolution TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(student_user_id) REFERENCES users(id)
            );
        """);

        // Insert sample data if empty
        ResultSet rs = st.executeQuery("SELECT COUNT(*) AS cnt FROM users;");
        rs.next();
        if (rs.getInt("cnt") == 0) {
            st.execute("INSERT INTO users(name,email,password,role) VALUES('Prof Alice','alice@uni.edu','pass1','professor');");
            st.execute("INSERT INTO users(name,email,password,role) VALUES('Prof Bob','bob@uni.edu','pass2','professor');");
            st.execute("INSERT INTO users(name,email,password,role) VALUES('Student One','s1@uni.edu','s1pass','student');");
            st.execute("INSERT INTO users(name,email,password,role) VALUES('Student Two','s2@uni.edu','s2pass','student');");
            st.execute("INSERT INTO users(name,email,password,role) VALUES('Student Three','s3@uni.edu','s3pass','student');");
            st.execute("INSERT INTO students(user_id,semester) VALUES(3,1),(4,1),(5,1);");
            st.execute("INSERT INTO professors(user_id,expertise) VALUES(1,'Databases'),(2,'Algorithms');");
            st.execute("INSERT INTO courses(code,title,semester,credits,timings,syllabus,enrollment_limit,professor_user_id) VALUES" +
                    "('CS101','Intro to CS',1,4,'Mon 9-11','Basics',50,1)," +
                    "('CS102','Data Structures',2,4,'Tue 9-11','DS',50,2)," +
                    "('CS103','Discrete Maths',1,4,'Wed 9-11','Discrete',50,NULL)," +
                    "('CS201','Algorithms',3,4,'Thu 9-11','Algo',40,2)," +
                    "('CS202','Databases',3,4,'Fri 9-11','DBMS',40,1);");
            st.execute("INSERT INTO prerequisites(course_id,prereq_course_id) VALUES(4,2);");
        }

        st.close();
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
