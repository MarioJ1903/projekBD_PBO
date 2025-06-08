package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ExamService {

    public boolean addExam(String jenisUjian, LocalDateTime tanggalUjian, String waliUserId, int mapelId, int kelasId) throws SQLException {
        String sql = "INSERT INTO Ujian (jenis_ujian, tanggal, Kelas_Users_user_id, Matpel_mapel_id, Kelas_kelas_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, jenisUjian);
            stmt.setTimestamp(2, Timestamp.valueOf(tanggalUjian));
            stmt.setString(3, waliUserId);
            stmt.setInt(4, mapelId);
            stmt.setInt(5, kelasId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ResultSet getExamsByTeacher(String teacherId) throws SQLException {
        String sql = "SELECT u.ujian_id, u.jenis_ujian, u.tanggal, m.nama_mapel, k.nama_kelas, u.Kelas_Users_user_id, u.Kelas_kelas_id " +
                "FROM Ujian u " +
                "JOIN Matpel m ON u.Matpel_mapel_id = m.mapel_id " +
                "JOIN Kelas k ON u.Kelas_Users_user_id = k.Users_user_id AND u.Kelas_kelas_id = k.kelas_id " +
                "JOIN Detail_Pengajar dp ON dp.Matpel_mapel_id = m.mapel_id AND dp.Kelas_Users_user_id = k.Users_user_id AND dp.Kelas_kelas_id = k.kelas_id " +
                "WHERE dp.Users_user_id = ? " +
                "ORDER BY u.tanggal DESC";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, teacherId);
        return stmt.executeQuery();
    }

    public ResultSet getExamsByStudent(String studentId) throws SQLException {
        String sql = "SELECT u.jenis_ujian, u.tanggal, m.nama_mapel, k.nama_kelas " +
                "FROM Ujian u " +
                "JOIN Matpel m ON u.Matpel_mapel_id = m.mapel_id " +
                "JOIN Kelas k ON u.Kelas_Users_user_id = k.Users_user_id AND u.Kelas_kelas_id = k.kelas_id " +
                "WHERE EXISTS (SELECT 1 FROM Student_Class_Enrollment sce WHERE sce.Users_user_id = ? AND sce.Kelas_kelas_id = k.kelas_id AND sce.Kelas_Users_user_id = k.Users_user_id)";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, studentId);
        return stmt.executeQuery();
    }

    public boolean deleteExam(int ujianId, String kelasWaliId, int kelasId) throws SQLException {
        String sql = "DELETE FROM Ujian WHERE ujian_id = ? AND Kelas_Users_user_id = ? AND Kelas_kelas_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, ujianId);
            stmt.setString(2, kelasWaliId);
            stmt.setInt(3, kelasId);
            return stmt.executeUpdate() > 0;
        }
    }
}