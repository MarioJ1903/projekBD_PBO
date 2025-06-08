package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnrollmentService {

    public boolean checkTeacherAssignment(String teacherId, int mapelId, String waliKelasId, int kelasId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Detail_Pengajar WHERE Users_user_id = ? AND Matpel_mapel_id = ? AND Kelas_Users_user_id = ? AND Kelas_kelas_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, teacherId);
            stmt.setInt(2, mapelId);
            stmt.setString(3, waliKelasId);
            stmt.setInt(4, kelasId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean assignTeacherToSubjectInClass(String guruUserId, int mapelId, String waliKelasId, int kelasId) throws SQLException {
        String sql = "INSERT INTO Detail_Pengajar (Users_user_id, Matpel_mapel_id, Kelas_Users_user_id, Kelas_kelas_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, guruUserId);
            stmt.setInt(2, mapelId);
            stmt.setString(3, waliKelasId);
            stmt.setInt(4, kelasId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ResultSet getAllSubjectAssignments() throws SQLException {
        String sql = "SELECT m.nama_mapel, k.nama_kelas, u_guru.nama AS guru_name, dp.Users_user_id AS guru_id, dp.Matpel_mapel_id, dp.Kelas_Users_user_id AS kelas_wali_id, dp.Kelas_kelas_id " +
                "FROM Detail_Pengajar dp " +
                "JOIN Matpel m ON dp.Matpel_mapel_id = m.mapel_id " +
                "JOIN Kelas k ON dp.Kelas_kelas_id = k.kelas_id AND dp.Kelas_Users_user_id = k.Users_user_id " +
                "JOIN Users u_guru ON dp.Users_user_id = u_guru.user_id " +
                "ORDER BY m.nama_mapel, k.nama_kelas, u_guru.nama";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.executeQuery();
    }

    public boolean deleteSubjectAssignment(String teacherId, int subjectId, String kelasWaliId, int kelasId) throws SQLException {
        String sql = "DELETE FROM Detail_Pengajar WHERE Users_user_id = ? AND Matpel_mapel_id = ? AND Kelas_Users_user_id = ? AND Kelas_kelas_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, teacherId);
            stmt.setInt(2, subjectId);
            stmt.setString(3, kelasWaliId);
            stmt.setInt(4, kelasId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean checkStudentEnrollment(String studentUserId, int kelasId, String waliUserId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Student_Class_Enrollment WHERE Users_user_id = ? AND Kelas_kelas_id = ? AND Kelas_Users_user_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement checkStmt = con.prepareStatement(sql)) {
            checkStmt.setString(1, studentUserId);
            checkStmt.setInt(2, kelasId);
            checkStmt.setString(3, waliUserId);
            ResultSet rs = checkStmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean enrollStudentToClass(String studentUserId, int kelasId, String waliUserId) throws SQLException {
        String sql = "INSERT INTO Student_Class_Enrollment (Users_user_id, Kelas_kelas_id, Kelas_Users_user_id) VALUES (?, ?, ?)";
        try (Connection con = DBS.getConnection();
             PreparedStatement insertStmt = con.prepareStatement(sql)) {
            insertStmt.setString(1, studentUserId);
            insertStmt.setInt(2, kelasId);
            insertStmt.setString(3, waliUserId);
            return insertStmt.executeUpdate() > 0;
        }
    }

    public ResultSet getStudentsInClass(int kelasId, String waliUserId) throws SQLException {
        String sql = "SELECT u.user_id, u.nama, u.NIS_NIP FROM Users u " +
                "JOIN Student_Class_Enrollment sce ON u.user_id = sce.Users_user_id " +
                "WHERE sce.Kelas_kelas_id = ? AND sce.Kelas_Users_user_id = ? AND u.Role_role_id = 'S'";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, kelasId);
        stmt.setString(2, waliUserId);
        return stmt.executeQuery();
    }

    public boolean removeStudentFromClass(String studentUserId, int kelasId, String waliUserId) throws SQLException {
        String sql = "DELETE FROM Student_Class_Enrollment WHERE Users_user_id = ? AND Kelas_kelas_id = ? AND Kelas_Users_user_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, studentUserId);
            stmt.setInt(2, kelasId);
            stmt.setString(3, waliUserId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ResultSet getClassesByWaliKelas(String waliId) throws SQLException {
        String sql = "SELECT DISTINCT k.kelas_id, k.nama_kelas, k.Users_user_id AS wali_id, u_wali.nama AS wali_name " +
                "FROM Kelas k " +
                "JOIN Detail_Pengajar dp ON k.kelas_id = dp.Kelas_kelas_id AND k.Users_user_id = dp.Kelas_Users_user_id " +
                "JOIN Users u_wali ON k.Users_user_id = u_wali.user_id " +
                "WHERE dp.Users_user_id = ?";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, waliId);
        return stmt.executeQuery();
    }

    public ResultSet getClassesTaughtByGuru(String guruId) throws SQLException {
        String sql = "SELECT DISTINCT k.kelas_id, k.nama_kelas, k.Users_user_id AS wali_id, u_wali.nama AS wali_name " +
                "FROM Kelas k " +
                "JOIN Detail_Pengajar dp ON k.kelas_id = dp.Kelas_kelas_id AND k.Users_user_id = dp.Kelas_Users_user_id " +
                "JOIN Users u_wali ON k.Users_user_id = u_wali.user_id " +
                "WHERE dp.Users_user_id = ?";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, guruId);
        return stmt.executeQuery();
    }

    public ResultSet getSubjectsTaughtByGuru(String guruId) throws SQLException {
        String sql = "SELECT DISTINCT m.mapel_id, m.nama_mapel FROM Matpel m " +
                "JOIN Detail_Pengajar dp ON m.mapel_id = dp.Matpel_mapel_id " +
                "WHERE dp.Users_user_id = ?";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, guruId);
        return stmt.executeQuery();
    }

    public ResultSet getSubjectsForAbsensi(String teacherId, int kelasId, String waliUserId) throws SQLException {
        String sql = "SELECT DISTINCT m.mapel_id, m.nama_mapel FROM Matpel m " +
                "JOIN Detail_Pengajar dp ON m.mapel_id = dp.Matpel_mapel_id " +
                "WHERE dp.Users_user_id = ? AND dp.Kelas_kelas_id = ? AND dp.Kelas_Users_user_id = ?";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, teacherId);
        stmt.setInt(2, kelasId);
        stmt.setString(3, waliUserId);
        return stmt.executeQuery();
    }

    public ResultSet getStudentsForAbsensi(int kelasId, String waliUserId, int mapelId) throws SQLException {
        String sql = "SELECT u.user_id, u.nama, u.NIS_NIP FROM Users u " +
                "JOIN Student_Class_Enrollment sce ON u.user_id = sce.Users_user_id " +
                "WHERE sce.Kelas_kelas_id = ? AND sce.Kelas_Users_user_id = ? AND u.Role_role_id = 'S' " +
                "AND EXISTS (SELECT 1 FROM Jadwal j WHERE j.Kelas_kelas_id = sce.Kelas_kelas_id AND j.Kelas_Users_user_id = sce.Kelas_Users_user_id AND j.Matpel_mapel_id = ?)";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, kelasId);
        stmt.setString(2, waliUserId);
        stmt.setInt(3, mapelId);
        return stmt.executeQuery();
    }
}