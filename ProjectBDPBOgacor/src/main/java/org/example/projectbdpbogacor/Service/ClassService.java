package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ClassService {

    public ResultSet getAllClasses() throws SQLException {
        String sql = "SELECT k.kelas_id, k.nama_kelas, k.keterangan, k.Users_user_id AS wali_id, u.nama AS wali_name, k.Semester_semester_id " +
                "FROM Kelas k JOIN Users u ON k.Users_user_id = u.user_id";
        Connection con = DBS.getConnection(); // Connection needs to be kept open for ResultSet
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.executeQuery();
    }

    public Map<String, Object> getClassDetails(int kelasId, String waliUserId) throws SQLException {
        Map<String, Object> classDetails = new HashMap<>();
        String sql = "SELECT nama_kelas, keterangan, Users_user_id, Semester_semester_id FROM Kelas WHERE kelas_id = ? AND Users_user_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, kelasId);
            stmt.setString(2, waliUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                classDetails.put("nama_kelas", rs.getString("nama_kelas"));
                classDetails.put("keterangan", rs.getString("keterangan"));
                classDetails.put("Users_user_id", rs.getString("Users_user_id"));
                classDetails.put("Semester_semester_id", rs.getInt("Semester_semester_id"));
            }
        }
        return classDetails;
    }

    public int createClass(String className, String classDescription, String waliKelasUserId, int semesterId) throws SQLException {
        String sql = "INSERT INTO Kelas (nama_kelas, keterangan, Users_user_id, Semester_semester_id) VALUES (?, ?, ?, ?) RETURNING kelas_id";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, className);
            stmt.setString(2, classDescription);
            stmt.setString(3, waliKelasUserId);
            stmt.setInt(4, semesterId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return generated kelas_id
                }
            }
            return -1; // Indicate failure
        }
    }

    public boolean updateClass(String className, String classDescription, String newWaliKelasUserId, int semesterId, int kelasIdToUpdate, String currentWaliIdForKelasPK) throws SQLException {
        String sql = "UPDATE Kelas SET nama_kelas = ?, keterangan = ?, Users_user_id = ?, Semester_semester_id = ? WHERE kelas_id = ? AND Users_user_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, className);
            stmt.setString(2, classDescription);
            stmt.setString(3, newWaliKelasUserId);
            stmt.setInt(4, semesterId);
            stmt.setInt(5, kelasIdToUpdate);
            stmt.setString(6, currentWaliIdForKelasPK);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteClass(int kelasIdToDelete, String waliUserIdToDelete) throws SQLException {
        try (Connection con = DBS.getConnection()) {
            // Delete from dependent tables first due to ON DELETE NO ACTION
            // Order matters: Student_Class_Enrollment, Jadwal, Materi, Tugas, Ujian, Detail_Pengajar
            String[] dependentTables = {
                    "Student_Class_Enrollment", "Jadwal", "Materi", "Tugas", "Ujian", "Detail_Pengajar"
            };

            for (String tableName : dependentTables) {
                try {
                    String deleteSql = "DELETE FROM " + tableName + " WHERE Kelas_kelas_id = ? AND Kelas_Users_user_id = ?";
                    PreparedStatement delStmt = con.prepareStatement(deleteSql);
                    delStmt.setInt(1, kelasIdToDelete);
                    delStmt.setString(2, waliUserIdToDelete);
                    delStmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Warning: Could not delete from " + tableName + " for class " + kelasIdToDelete + " (Wali: " + waliUserIdToDelete + "): " + e.getMessage());
                }
            }

            // Finally, delete the class itself
            String sql = "DELETE FROM Kelas WHERE kelas_id = ? AND Users_user_id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, kelasIdToDelete);
            stmt.setString(2, waliUserIdToDelete);
            return stmt.executeUpdate() > 0;
        }
    }
}