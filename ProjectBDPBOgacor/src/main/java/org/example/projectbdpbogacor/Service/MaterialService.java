package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MaterialService {

    public boolean addMaterial(String namaMateri, String waliUserId, int mapelId, int kelasId) throws SQLException {
        String sql = "INSERT INTO Materi (nama_materi, Kelas_Users_user_id, Matpel_mapel_id, Kelas_kelas_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, namaMateri);
            stmt.setString(2, waliUserId);
            stmt.setInt(3, mapelId);
            stmt.setInt(4, kelasId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ResultSet getMaterialsByTeacher(String teacherId) throws SQLException {
        String sql = "SELECT mt.materi_id, mt.nama_materi, m.nama_mapel, k.nama_kelas, mt.Kelas_Users_user_id, mt.Kelas_kelas_id " +
                "FROM Materi mt " +
                "JOIN Matpel m ON mt.Matpel_mapel_id = m.mapel_id " +
                "JOIN Kelas k ON mt.Kelas_Users_user_id = k.Users_user_id AND mt.Kelas_kelas_id = k.kelas_id " +
                "JOIN Detail_Pengajar dp ON dp.Matpel_mapel_id = m.mapel_id AND dp.Kelas_Users_user_id = k.Users_user_id AND dp.Kelas_kelas_id = k.kelas_id " +
                "WHERE dp.Users_user_id = ? " +
                "ORDER BY m.nama_mapel, k.nama_kelas, mt.nama_materi";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, teacherId);
        return stmt.executeQuery();
    }

    public ResultSet getMaterialsByStudent(String studentId) throws SQLException {
        String sql = "SELECT mt.nama_materi, m.nama_mapel, k.nama_kelas " +
                "FROM Materi mt " +
                "JOIN Matpel m ON mt.Matpel_mapel_id = m.mapel_id " +
                "JOIN Kelas k ON mt.Kelas_Users_user_id = k.Users_user_id AND mt.Kelas_kelas_id = k.kelas_id " +
                "WHERE EXISTS (SELECT 1 FROM Student_Class_Enrollment sce WHERE sce.Users_user_id = ? AND sce.Kelas_kelas_id = k.kelas_id AND sce.Kelas_Users_user_id = k.Users_user_id)";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, studentId);
        return stmt.executeQuery();
    }

    public boolean deleteMaterial(int materiId, String kelasWaliId, int kelasId) throws SQLException {
        String sql = "DELETE FROM Materi WHERE materi_id = ? AND Kelas_Users_user_id = ? AND Kelas_kelas_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, materiId);
            stmt.setString(2, kelasWaliId);
            stmt.setInt(3, kelasId);
            return stmt.executeUpdate() > 0;
        }
    }
}