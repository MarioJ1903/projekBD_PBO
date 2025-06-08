package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AssignmentService {

    public boolean addAssignment(String keterangan, LocalDateTime deadline, LocalDateTime releaseDate, String waliUserId, int mapelId, int kelasId) throws SQLException {
        String sql = "INSERT INTO Tugas (keterangan, deadline, tanggal_direlease, Kelas_Users_user_id, Matpel_mapel_id, Kelas_kelas_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, keterangan);
            stmt.setTimestamp(2, Timestamp.valueOf(deadline));
            stmt.setTimestamp(3, Timestamp.valueOf(releaseDate));
            stmt.setString(4, waliUserId);
            stmt.setInt(5, mapelId);
            stmt.setInt(6, kelasId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ResultSet getAssignmentsByTeacher(String teacherId) throws SQLException {
        String sql = "SELECT t.tugas_id, t.keterangan, t.deadline, t.tanggal_direlease, m.nama_mapel, k.nama_kelas, t.Kelas_Users_user_id, t.Kelas_kelas_id " +
                "FROM Tugas t " +
                "JOIN Matpel m ON t.Matpel_mapel_id = m.mapel_id " +
                "JOIN Kelas k ON t.Kelas_Users_user_id = k.Users_user_id AND t.Kelas_kelas_id = k.kelas_id " +
                "JOIN Detail_Pengajar dp ON dp.Matpel_mapel_id = m.mapel_id AND dp.Kelas_Users_user_id = k.Users_user_id AND dp.Kelas_kelas_id = k.kelas_id " +
                "WHERE dp.Users_user_id = ? " +
                "ORDER BY t.deadline DESC";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, teacherId);
        return stmt.executeQuery();
    }

    public ResultSet getAssignmentsByStudent(String studentId) throws SQLException {
        String sql = "SELECT t.keterangan, t.deadline, t.tanggal_direlease, m.nama_mapel, k.nama_kelas " +
                "FROM Tugas t " +
                "JOIN Matpel m ON t.Matpel_mapel_id = m.mapel_id " +
                "JOIN Kelas k ON t.Kelas_Users_user_id = k.Users_user_id AND t.Kelas_kelas_id = k.kelas_id " +
                "WHERE EXISTS (SELECT 1 FROM Student_Class_Enrollment sce WHERE sce.Users_user_id = ? AND sce.Kelas_kelas_id = k.kelas_id AND sce.Kelas_Users_user_id = k.Users_user_id)";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, studentId);
        return stmt.executeQuery();
    }

    public boolean deleteAssignment(int tugasId, String kelasWaliId, int kelasId) throws SQLException {
        String sql = "DELETE FROM Tugas WHERE tugas_id = ? AND Kelas_Users_user_id = ? AND Kelas_kelas_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, tugasId);
            stmt.setString(2, kelasWaliId);
            stmt.setInt(3, kelasId);
            return stmt.executeUpdate() > 0;
        }
    }
}