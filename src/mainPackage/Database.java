/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mainPackage;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;

/**
 *
 * @author User
 */
public class Database {
    
    private static final String DB_URL = "jdbc:sqlite:database/data.db";
    
    // connect
    public Connection connect() {
        Connection conn = null;
        try {       
            // Start connection
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.out.println("SQLite connection failed: " + e.getMessage());
        } 
        return conn;  
    }
    
    
    public List<String> getSectionCodes() {
        List<String> sections = new ArrayList<>();
        String sql = "SELECT section_name FROM sections";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sections.add(rs.getString("section_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sections;
    }
    
    public String login(String username, String password) {
    String sql = "SELECT pass, role FROM userData WHERE user = ?";

    try (Connection conn = connect();
         PreparedStatement state = conn.prepareStatement(sql)) {

        state.setString(1, username);
        ResultSet rs = state.executeQuery();

        if (rs.next()) {
            String hashedPassword = rs.getString("pass");
            String role = rs.getString("role");

            if (BCrypt.checkpw(password, hashedPassword)) {
                return role;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null; // Login failed
}
    // ========================================================================================
    
    public void insertToTable(long sNum, String name, int age, String email, String birthday, String gender, String subject, String section) {
        String sql = "insert into students values(?,?,?,?,?,?,?,?)";
            
        try(Connection conn = connect();
            PreparedStatement state = conn.prepareStatement(sql)) {
                
            state.setLong(1, sNum);
            state.setString(2, name);
            state.setInt(3, age);
            state.setString(4, email);
            state.setString(5, birthday);
            state.setString(6, gender);
            state.setString(7, subject);
            state.setString(8, section);
            
            int rows = state.executeUpdate();
            
        if (rows > 0) {
            JOptionPane.showMessageDialog(null, "Data added successfully!");
            
            createAccount(sNum, name, "student");
            
        } else {
            JOptionPane.showMessageDialog(null, "Cannot insert data!");
        }
            
        } catch(SQLException e) {
           System.out.print(e);
           JOptionPane.showMessageDialog(null, "Cannot insert data!");
    }
    }
    
    
    public void addSection(String section_name, int total_students, String schedule) {
        String sql = "insert into sections(section_name,total_students,schedule) values(?,?,?)";
            
        try(Connection conn = connect();
            PreparedStatement state = conn.prepareStatement(sql)) {
                
            
            state.setString(1, section_name);
            state.setInt(2, total_students);
            state.setString(3, schedule);
            
            int rows = state.executeUpdate();
            
        if (rows > 0) {
            JOptionPane.showMessageDialog(null, "Data added successfully!");
            
        } else {
            JOptionPane.showMessageDialog(null, "Cannot insert data!");
        }
            
        } catch(SQLException e) {
           System.out.print(e);
           JOptionPane.showMessageDialog(null, "Cannot insert data!");
    }
    }

    // ========================================================================================
    
    public DefaultTableModel getAllStudents() {
        DefaultTableModel model = new DefaultTableModel(
        new String[]{"ID", "Names", "Age", "Email", "Birthday", "Gender", "Subject", "Section"}, 0
        );
        
        String sql = "SELECT studentID, name, age, email, birthday, gender, subject, section FROM students";
        
        try (Connection conn = connect();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

         while(rs.next()) {
             long id = rs.getLong("studentID");
             String name = rs.getString("name");
             int age = rs.getInt("age");
             String email = rs.getString("email");
             String birthday = rs.getString("birthday");
             String gender = rs.getString("gender");
             String course = rs.getString("subject");
             String section = rs.getString("section");
             
             model.addRow(new Object[]{id, name, age, email, birthday, gender, course, section});
         }

        } catch (SQLException e) {
        e.printStackTrace();
        }
        return model;
    }
    
    // ========================================================================================
    
    public DefaultTableModel getAllSection() {
        DefaultTableModel model = new DefaultTableModel(
        new String[]{"ID", "Section", "Total Students", "Schedule"}, 0
        );
        
        String sql = "SELECT ID, section_name, total_students, schedule FROM sections";
        
        try (Connection conn = connect();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

         while(rs.next()) {
             long id = rs.getLong("ID");
             String section_name = rs.getString("section_name");
             int total_students = rs.getInt("total_students");
             String sched = rs.getString("schedule");
             
             model.addRow(new Object[]{id, section_name, total_students, sched});
         }

        } catch (SQLException e) {
        e.printStackTrace();
        }
        return model;
    }
    
    public void createAccount(long user, String fullName, String role) {
    String sql = "INSERT INTO userData(user, pass, role) VALUES (?, ?, ?)";

    // Extract initial from first name
    String initial = "";
    if (fullName != null && !fullName.isEmpty()) {
        initial = fullName.trim().substring(0, 1).toUpperCase();
    }

    // Auto format password: FirstNameInitial + ID
    String autoPass = initial + user;

    // Hash password
    String hashed = BCrypt.hashpw(autoPass, BCrypt.gensalt());

    try (Connection conn = connect();
         PreparedStatement state = conn.prepareStatement(sql)) {

        state.setString(1, String.valueOf(user));
        state.setString(2, hashed);
        state.setString(3, role);

        int rows = state.executeUpdate();

        if (rows > 0) {
            
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
                "Username already exists!");
    }
}
    
    // ========================================================================================
    
    
    public boolean deleteToTable(String id) {
        String sql = "DELETE from students WHERE studentID = ?";
        
        try(Connection conn = connect();
                PreparedStatement state = conn.prepareStatement(sql)) {
            
                state.setString(1, id);
                
                int rows = state.executeUpdate();
                
                return rows > 0;
                
        } catch(SQLException e) {
                System.out.print(e);
                JOptionPane.showMessageDialog(null, "ERROR!");
                return false;
        }
    } 
    
    // ==========================================================================================
    // Add student to the subject
    
    public void insertToIPT(String id, String name) {
        String sql = "insert into IPT values(?,?,?,?,?,?,?,?,?,?)";
            
        try(Connection conn = connect();
            PreparedStatement state = conn.prepareStatement(sql)) {
                
            state.setString(1, id);
            state.setString(2, name);
            state.setString(3, "");
            state.setString(4, "");
            state.setString(5, "");
            state.setString(6, "");
            state.setString(7, "");
            state.setString(8, "");
            state.setString(9, "");
            state.setString(10, "");
            
            int rows = state.executeUpdate();
            
        if (rows > 0) {
            
        } else {
            
        }
            
        } catch(SQLException e) {
           System.out.print(e);
           
    }
    }
    
    // ==============================================================================
    
    public void saveQuizData(JTable table, String subject) {
        
        String sql = "UPDATE " + subject + " SET quiz = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement state = conn.prepareStatement(sql)) {

            for (int i = 0; i < table.getRowCount(); i++) {
                // Get ID (must be available or retrievable)
                String id = getStudentIdByName(conn, table.getValueAt(i, 0).toString(), subject);
                if (id == null) continue;

                // Get quiz scores
                String q1 = (table.getValueAt(i, 1) == null) ? "":
                table.getValueAt(i, 1).toString();
                String q2 = (table.getValueAt(i, 2) == null) ? "":
                table.getValueAt(i, 2).toString();
                String q3 = (table.getValueAt(i, 3) == null) ? "":
                table.getValueAt(i, 3).toString();
                String q4 = (table.getValueAt(i, 4) == null) ? "":
                table.getValueAt(i, 4).toString();
                String q5 = (table.getValueAt(i, 5) == null) ? "":
                table.getValueAt(i, 5).toString();

                // Combine as array string
                String quizArray = "[" + q1 + "," + q2 + "," + q3 + "," + q4 + "," + q5 + "]";

                // Set parameters
                state.setString(1, quizArray);
                state.setString(2, id);
                updateFinalGrade(subject, id);                
                state.executeUpdate();
            }

            System.out.println("Quiz data saved successfully!");

        } catch (SQLException e) {
            System.out.println("Error saving quiz data: " + e.getMessage());
        }
    }
    
    public void saveAttendanceData(JTable table, String subject) {
        
        String sql = "UPDATE " + subject + " SET attendance = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement state = conn.prepareStatement(sql)) {

            for (int i = 0; i < table.getRowCount(); i++) {
                // Get ID (must be available or retrievable)
                String id = getStudentIdByName(conn, table.getValueAt(i, 0).toString(), subject);
                if (id == null) continue;

                // Get quiz scores
                String q1 = (table.getValueAt(i, 1) == null) ? "":
                table.getValueAt(i, 1).toString();
                String q2 = (table.getValueAt(i, 2) == null) ? "":
                table.getValueAt(i, 2).toString();
                String q3 = (table.getValueAt(i, 3) == null) ? "":
                table.getValueAt(i, 3).toString();
                String q4 = (table.getValueAt(i, 4) == null) ? "":
                table.getValueAt(i, 4).toString();
                String q5 = (table.getValueAt(i, 5) == null) ? "":
                table.getValueAt(i, 5).toString();

                // Combine as array string
                String attendanceArray = "[" + q1 + "," + q2 + "," + q3 + "," + q4 + "," + q5 + "]";

                // Set parameters
                state.setString(1, attendanceArray);
                state.setString(2, id);
                updateFinalGrade(subject, id); 
                state.executeUpdate();
            }

            System.out.println("Attendance data saved successfully!");

        } catch (SQLException e) {
            System.out.println("Error saving attendance data: " + e.getMessage());
        }
    }
    
    public void saveExamData(JTable table, String subject) {
    String sql = "UPDATE " + subject + " SET prelim_exam = ?, midterm_exam = ?, final_exam = ? WHERE id = ?";

    try (Connection conn = connect();
         PreparedStatement state = conn.prepareStatement(sql)) {

        for (int i = 0; i < table.getRowCount(); i++) {
            String id = getStudentIdByName(conn, table.getValueAt(i, 0).toString(), subject);
            if (id == null) continue;

            String prelim = (table.getValueAt(i, 1) == null) ? "" : table.getValueAt(i, 1).toString();
            String midterm = (table.getValueAt(i, 2) == null) ? "" : table.getValueAt(i, 2).toString();
            String fin = (table.getValueAt(i, 3) == null) ? "" : table.getValueAt(i, 3).toString();

            state.setString(1, prelim);
            state.setString(2, midterm);
            state.setString(3, fin);
            state.setString(4, id);
            updateFinalGrade(subject, id); 
            state.executeUpdate();
        }

        System.out.println("Exam data saved successfully!");

    } catch (SQLException e) {
        System.out.println("Error saving exam data: " + e.getMessage());
    }
}
    
    
    public void saveData(DefaultTableModel model, String subject) {
    String sql = "UPDATE " + subject + " SET assignment = ?, activities = ? WHERE student_name = ?";

    try (Connection conn = connect();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        for (int i = 0; i < model.getRowCount(); i++) {
            String student = (String) model.getValueAt(i, 0);
            String id = getStudentIdByName(conn, model.getValueAt(i, 0).toString(), subject);

            // Get data from your JTable columns
            String[] activities = new String[5];
            for (int j = 1; j <= 5; j++) {
                Object val = model.getValueAt(i, j);
                activities[j - 1] = (val != null) ? val.toString() : "";
            }

            String[] assignments = new String[2];
            for (int j = 6; j <= 7; j++) {
                Object val = model.getValueAt(i, j);
                assignments[j - 6] = (val != null) ? val.toString() : "";
            }

            // Convert arrays to a single string (like [85,90,...])
            String activitiesStr = "[" + String.join(",", activities) + "]";
            String assignmentsStr = "[" + String.join(",", assignments) + "]";

            // Set parameters in query
            ps.setString(1, assignmentsStr);
            ps.setString(2, activitiesStr);
            ps.setString(3, student);
            updateFinalGrade(subject, id); 
            ps.addBatch();
        }

        ps.executeBatch();
        JOptionPane.showMessageDialog(null, "Data saved successfully!");

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saving data: " + e.getMessage());
    }
}
    
    // Subject for student dashboard
    
    public DefaultTableModel getAllSubjects(long studentId) {
    DefaultTableModel model = new DefaultTableModel(
        new String[]{"Subject"}, 0
    );

    String sql = "SELECT subject FROM students WHERE studentID=?";

    try (Connection conn = connect();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setLong(1, studentId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String subjectsStr = rs.getString("subject");
            if (subjectsStr != null && !subjectsStr.isEmpty()) {

                // Split the comma separated subjects
                String[] subjects = subjectsStr.split(",");

                // Add each subject to the table
                for (String sub : subjects) {
                    model.addRow(new Object[]{sub.trim()});
                }
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return model;
}
    
    public DefaultTableModel getAllGrades(long studentId) {
    DefaultTableModel model = new DefaultTableModel(
        new String[]{"Subject", "Final Grade"}, 0
    );

    String sqlSubjects = "SELECT subject FROM students WHERE studentID=?";

    try (Connection conn = connect();
         PreparedStatement ps = conn.prepareStatement(sqlSubjects)) {

        ps.setLong(1, studentId);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) return model;

        String subjectsStr = rs.getString("subject");
        if (subjectsStr == null || subjectsStr.isEmpty()) return model;

        String[] subjects = subjectsStr.split(",");

        for (String subject : subjects) {
            subject = subject.trim();
            if (subject.isEmpty()) continue;

            String sqlGrade = "SELECT final_grade FROM " + subject + " WHERE id=?";

            String grade = "INC"; // default if no data

            try (PreparedStatement ps2 = conn.prepareStatement(sqlGrade)) {
                ps2.setLong(1, studentId);
                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {
                    String value = rs2.getString("final_grade");
                    if (value != null && !value.trim().isEmpty()) {
                        grade = value;
                    }
                }
            } catch (SQLException ignored) {
                // Subject table exists but student not found OR other minor issues
            }

            model.addRow(new Object[]{subject, grade});
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return model;
}
    
    public void getAllInfo(long studentId, StudentDashboard callback) {
    String sql = "SELECT name, section, email FROM students WHERE studentID=?";

    try (Connection conn = connect();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setLong(1, studentId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String name = rs.getString("name");
            String section = rs.getString("section");
            String email = rs.getString("email");

            callback.setCurrentProfile(name, section, email);
        }

        rs.close();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}



    private String getStudentIdByName(Connection conn, String name, String subject) {
        String id = null;
        String sql = "SELECT id FROM " + subject + " WHERE student_name = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getString("id");
            }
        } catch (SQLException e) {
            System.out.println("Error finding ID: " + e.getMessage());
        }
        return id;
    }
    
    // Get all subject info
    
    public DefaultTableModel getAllQuiz(String subject) {
    DefaultTableModel model = new DefaultTableModel(
        new String[]{"Student", "Quiz 1", "Quiz 2", "Quiz 3", "Quiz 4", "Quiz 5"}, 
        0
    );

    String sql = "SELECT student_name, quiz FROM " + subject;

    try (Connection conn = connect();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String student_name = rs.getString("student_name");
            String quizData = rs.getString("quiz");

            // Default empty quizzes
            String q1 = "", q2 = "", q3 = "", q4 = "", q5 = "";

            if (quizData != null && !quizData.trim().isEmpty()) {
                // Remove brackets [] and split by comma
                quizData = quizData.replace("[", "").replace("]", "").trim();
                String[] parts = quizData.split(",");

                // Fill available quiz values
                if (parts.length > 0) q1 = parts[0].trim();
                if (parts.length > 1) q2 = parts[1].trim();
                if (parts.length > 2) q3 = parts[2].trim();
                if (parts.length > 3) q4 = parts[3].trim();
                if (parts.length > 4) q5 = parts[4].trim();
            }

            // Add each quiz score to the table model
            model.addRow(new Object[]{student_name, q1, q2, q3, q4, q5});
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return model;
    }

    public DefaultTableModel getAllAttendance(String subject) {
    DefaultTableModel model = new DefaultTableModel(
        new String[]{"Student", "WEEK 1", "WEEK 2", "WEEK 3", "WEEK 4", "WEEK 5"}, 
        0
    );

    String sql = "SELECT student_name, attendance FROM " + subject;

    try (Connection conn = connect();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String student_name = rs.getString("student_name");
            String attendanceData = rs.getString("attendance");

            // Default empty attendance
            String q1 = "", q2 = "", q3 = "", q4 = "", q5 = "";

            if (attendanceData != null && !attendanceData.trim().isEmpty()) {
                // Remove brackets [] and split by comma
                attendanceData = attendanceData.replace("[", "").replace("]", "").trim();
                String[] parts = attendanceData.split(",");

                // Fill available attendance values
                if (parts.length > 0) q1 = parts[0].trim();
                if (parts.length > 1) q2 = parts[1].trim();
                if (parts.length > 2) q3 = parts[2].trim();
                if (parts.length > 3) q4 = parts[3].trim();
                if (parts.length > 4) q5 = parts[4].trim();
            }

            // Add each quiz score to the table model
            model.addRow(new Object[]{student_name, q1, q2, q3, q4, q5});
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return model;
}
    
    public DefaultTableModel getAllExam(String subject) {
        DefaultTableModel model = new DefaultTableModel(
        new String[]{"Student", "Prelim_Exam", "Midterm_Exam", "Final_Exam"}, 0
        );
        
        String sql = "SELECT student_name, prelim_exam, midterm_exam, final_exam FROM " + subject;
        
        try (Connection conn = connect();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

         while(rs.next()) {
             String student_name = rs.getString("student_name");
             String prelim = rs.getString("prelim_exam");
             String midterm = rs.getString("midterm_exam");
             String Final = rs.getString("final_exam");
             
             model.addRow(new Object[]{student_name, prelim, midterm, Final});
         }

        } catch (SQLException e) {
        e.printStackTrace();
        }
        return model;
    }
    
    public DefaultTableModel getAllAssignment(String subject) {
    DefaultTableModel model = new DefaultTableModel(
        new String[]{"Student", "Act. 1", "Act. 2", "Act. 3", "Act. 4", "Act. 5", "Assignment 1", "Assignment 2"}, 
        0
    );

    String sql = "SELECT student_name, activities, assignment FROM " + subject;

    try (Connection conn = connect();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String studentName = rs.getString("student_name");
            String activities = rs.getString("activities");
            String assignment = rs.getString("assignment");

            // Split arrays safely
            String[] acts = (activities != null && !activities.isEmpty())
                    ? activities.replace("[", "").replace("]", "").split(",")
                    : new String[5];

            String[] assigns = (assignment != null && !assignment.isEmpty())
                    ? assignment.replace("[", "").replace("]", "").split(",")
                    : new String[2];

            // Prepare row (8 columns)
            String[] row = new String[8];
            row[0] = studentName;

            for (int i = 0; i < 5; i++) {
                String value = (i < acts.length && acts[i] != null) ? acts[i].trim() : "";
                row[i + 1] = value;
            }

            for (int i = 0; i < 2; i++) {
                String value = (i < assigns.length && assigns[i] != null) ? assigns[i].trim() : "";
                row[i + 6] = value;
            }

            model.addRow(row);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return model;
}
    
    
    public void insertToITE(String id, String name) {
        String sql = "insert into ITE values(?,?,?,?,?,?,?,?,?,?)";
            
        try(Connection conn = connect();
            PreparedStatement state = conn.prepareStatement(sql)) {
                
            state.setString(1, id);
            state.setString(2, name);
            state.setString(3, "");
            state.setString(4, "");
            state.setString(5, "");
            state.setString(6, "");
            state.setString(7, "");
            state.setString(8, "");
            state.setString(9, "");
            state.setString(10, "");
            
            int rows = state.executeUpdate();
            
        if (rows > 0) {
            
        } else {
            
        }
            
        } catch(SQLException e) {
           System.out.print(e);
           
    }
    }
    
    public void deleteToIPT(String id) {
    String sql = "DELETE FROM IPT WHERE id = ?";
    
    try (Connection conn = connect();
         PreparedStatement state = conn.prepareStatement(sql)) {
        
        state.setString(1, id);
        int rows = state.executeUpdate();
        
        if (rows > 0) {
            JOptionPane.showMessageDialog(null, "Record deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(null, "No record found with the given ID.");
        }
        
    } catch (SQLException e) {
        System.out.print(e);
        JOptionPane.showMessageDialog(null, "ERROR while deleting record!");
    }
}
    
    public void deleteToITE(String id) {
    String sql = "DELETE FROM ITE WHERE id = ?";
    
    try (Connection conn = connect();
         PreparedStatement state = conn.prepareStatement(sql)) {
        
        state.setString(1, id);
        int rows = state.executeUpdate();
        
        if (rows > 0) {
            JOptionPane.showMessageDialog(null, "Record deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(null, "No record found with the given ID.");
        }
        
    } catch (SQLException e) {
        System.out.print(e);
        JOptionPane.showMessageDialog(null, "ERROR while deleting record!");
    }
}
    
    public void deleteToDataStruct(String id) {
    String sql = "DELETE FROM Data_Structure WHERE id = ?";
    
    try (Connection conn = connect();
         PreparedStatement state = conn.prepareStatement(sql)) {
        
        state.setString(1, id);
        int rows = state.executeUpdate();
        
        if (rows > 0) {
            JOptionPane.showMessageDialog(null, "Record deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(null, "No record found with the given ID.");
        }
        
    } catch (SQLException e) {
        System.out.print(e);
        JOptionPane.showMessageDialog(null, "ERROR while deleting record!");
    }
}
    
    public void deleteToHCI(String id) {
    String sql = "DELETE FROM HCI WHERE id = ?";
    
    try (Connection conn = connect();
         PreparedStatement state = conn.prepareStatement(sql)) {
        
        state.setString(1, id);
        int rows = state.executeUpdate();
        
        if (rows > 0) {
            JOptionPane.showMessageDialog(null, "Record deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(null, "No record found with the given ID.");
        }
        
    } catch (SQLException e) {
        System.out.print(e);
        JOptionPane.showMessageDialog(null, "ERROR while deleting record!");
    }
}

    
    public void insertToDataStruct(String id, String name) {
        String sql = "insert into Data_Structure values(?,?,?,?,?,?,?,?,?,?)";
            
        try(Connection conn = connect();
            PreparedStatement state = conn.prepareStatement(sql)) {
                
            state.setString(1, id);
            state.setString(2, name);
            state.setString(3, "");
            state.setString(4, "");
            state.setString(5, "");
            state.setString(6, "");
            state.setString(7, "");
            state.setString(8, "");
            state.setString(9, "");
            state.setString(10, "");
            
            int rows = state.executeUpdate();
            
        if (rows > 0) {
            
        } else {
            
        }
            
        } catch(SQLException e) {
           System.out.print(e);
           
    }
    }
    
   
    public void insertToDataHCI(String id, String name) {
        String sql = "insert into HCI values(?,?,?,?,?,?,?,?,?,?)";
            
        try(Connection conn = connect();
            PreparedStatement state = conn.prepareStatement(sql)) {
                
            state.setString(1, id);
            state.setString(2, name);
            state.setString(3, "");
            state.setString(4, "");
            state.setString(5, "");
            state.setString(6, "");
            state.setString(7, "");
            state.setString(8, "");
            state.setString(9, "");
            state.setString(10, "");
            
            int rows = state.executeUpdate();
            
        if (rows > 0) {
            
        } else {
            
        }
            
        } catch(SQLException e) {
           System.out.print(e);
           
    }
    }
    
    
    
    // ====================================================
    // FINAL GRADES
    
    public void updateFinalGrade(String tableName, String id) {
    String sql = "SELECT attendance, quiz, assignment, activities, prelim_exam, midterm_exam, final_exam "
                + "FROM " + tableName + " WHERE id=?";

    try (Connection conn = connect();
         PreparedStatement pst = conn.prepareStatement(sql)) {

        pst.setString(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {

            String attStr = rs.getString("attendance");
            String quizStr = rs.getString("quiz");
            String assignStr = rs.getString("assignment");
            String actStr = rs.getString("activities");
            String prelimStr = rs.getString("prelim_exam");
            String midtermStr = rs.getString("midterm_exam");
            String finalStr = rs.getString("final_exam");

            boolean noData =
                (attStr == null || attStr.isEmpty()) &&
                (quizStr == null || quizStr.length() < 3) &&
                (assignStr == null || assignStr.length() < 3) &&
                (actStr == null || actStr.length() < 3) &&
                (prelimStr == null || prelimStr.isEmpty()) &&
                (midtermStr == null || midtermStr.isEmpty()) &&
                (finalStr == null || finalStr.isEmpty());

            if (noData) {
                saveFinalGradeText(conn, tableName, id, "INC");
                return;
            }

            double attendance = parseAttendance(attStr);
            double[] quizzes = parseArray(quizStr);
            double[] assignments = parseArray(assignStr);
            double[] activities = parseArray(actStr);

            double prelim = parseDoubleOrZero(prelimStr);
            double midterm = parseDoubleOrZero(midtermStr);
            double finals = parseDoubleOrZero(finalStr);

            double finalScore = computeFinalScore(attendance, quizzes, assignments,
                                                 activities, prelim, midterm, finals);

            double grade = convertToGrade(finalScore);
            saveFinalGradeText(conn, tableName, id, String.format("%.2f", grade));
        }

    } catch (SQLException e) {
        System.out.println("Error updating final grade: " + e.getMessage());
    }
}
    
    private double parseDoubleOrZero(String value) {
    try {
        return Double.parseDouble(value);
    } catch (Exception e) {
        return 0;
    }
}

private double[] parseArray(String text) {
    if (text == null || text.length() < 3) return new double[]{0};
    text = text.replace("[", "").replace("]", "");
    String[] parts = text.split(",");
    double[] result = new double[parts.length];
    for (int i = 0; i < parts.length; i++) {
        result[i] = Double.parseDouble(parts[i].trim());
    }
    return result;
}

private double parseAttendance(String text) {
    if (text == null) return 0;
    long present = text.chars().filter(ch -> ch == 'P').count();
    long total = text.chars().filter(ch -> ch == 'P' || ch == 'A').count();
    return total > 0 ? (present * 100.0) / total : 0;
}

private double average(double[] arr) {
    double sum = 0;
    for (double val : arr) sum += val;
    return arr.length > 0 ? sum / arr.length : 0;
}

private double computeFinalGrade(double attendance, double[] quizzes,
                                 double[] assignments, double[] activities,
                                 double prelim, double midterm, double finals) {

    double finalScore = (attendance * 0.10)
                      + (average(quizzes) * 0.20)
                      + (average(assignments) * 0.20)
                      + (average(activities) * 0.20)
                      + (((prelim + midterm + finals) / 3.0) * 0.30);

    return convertToGrade(finalScore);
}

private double convertToGrade(double score) {
    if (score >= 96) return 1.00;
    if (score >= 90) return 1.25;
    if (score >= 85) return 1.50;
    if (score >= 80) return 1.75;
    if (score >= 75) return 2.00;
    if (score >= 70) return 2.25;
    if (score >= 65) return 2.50;
    if (score >= 60) return 2.75;
    if (score >= 55) return 3.00;
    return 5.00;
}

private void saveFinalGrade(Connection conn, String tableName, String id, double grade) throws SQLException {
    String updateSql = "UPDATE " + tableName + " SET final_grade=? WHERE id=?";
    PreparedStatement st = conn.prepareStatement(updateSql);
    st.setString(1, String.format("%.2f", grade));
    st.setString(2, id);
    st.executeUpdate();
    st.close();
}

private void saveFinalGradeText(Connection conn, String tableName, String id, String grade) throws SQLException {
    String updateSql = "UPDATE " + tableName + " SET final_grade=? WHERE id=?";
    PreparedStatement st = conn.prepareStatement(updateSql);
    st.setString(1, grade);
    st.setString(2, id);
    st.executeUpdate();
    st.close();
}

private double computeFinalScore(double attendance, double[] quizzes,
                                double[] assignments, double[] activities,
                                double prelim, double midterm, double finals) {

    return (attendance * 0.10)
         + (average(quizzes) * 0.20)
         + (average(assignments) * 0.20)
         + (average(activities) * 0.20)
         + (((prelim + midterm + finals) / 3.0) * 0.30);
}
    
}
