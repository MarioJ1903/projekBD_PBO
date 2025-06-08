package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScheduleService {

    public boolean addSchedule(String hari, String jamMulai, String jamSelesai, String waliUserId, int mapelId, int kelasId) throws SQLException {
        String sql = "INSERT INTO Jadwal (hari, jam_mulai, jam_selsai, Kelas_Users_user_id, Matpel_mapel_id, Kelas_kelas_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, hari);
            stmt.setString(2, jamMulai);
            stmt.setString(3, jamSelesai);
            stmt.setString(4, waliUserId);
            stmt.setInt(5, mapelId);
            stmt.setInt(6, kelasId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ResultSet getSchedulesByTeacherAndClass(String teacherId, int kelasId, String waliUserId, int mapelId) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT j.jadwal_id, j.hari, j.jam_mulai, j.jam_selsai, m.nama_mapel, k.nama_kelas " +
                        "FROM Jadwal j " +
                        "JOIN Matpel m ON j.Matpel_mapel_id = m.mapel_id " +
                        "JOIN Kelas k ON j.Kelas_Users_user_id = k.Users_user_id AND j.Kelas_kelas_id = k.kelas_id " +
                        "JOIN Detail_Pengajar dp ON dp.Matpel_mapel_id = m.mapel_id AND dp.Kelas_Users_user_id = k.Users_user_id AND dp.Kelas_kelas_id = k.kelas_id " +
                        "WHERE dp.Users_user_id = ? "
        );

        // Add filters based on provided parameters
        if (kelasId > 0 && waliUserId != null && !waliUserId.isEmpty()) {
            sqlBuilder.append(" AND k.kelas_id = ? AND k.Users_user_id = ?");
        }
        if (mapelId > 0) {
            sqlBuilder.append(" AND m.mapel_id = ?");
        }

        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sqlBuilder.toString());
        int paramIndex = 1;
        stmt.setString(paramIndex++, teacherId);

        if (kelasId > 0 && waliUserId != null && !waliUserId.isEmpty()) {
            stmt.setInt(paramIndex++, kelasId);
            stmt.setString(paramIndex++, waliUserId);
        }
        if (mapelId > 0) {
            stmt.setInt(paramIndex++, mapelId);
        }
        return stmt.executeQuery();
    }

    public ResultSet getSchedulesByStudent(String studentId) throws SQLException {
        String sql = "SELECT j.hari, j.jam_mulai, j.jam_selsai, m.nama_mapel, k.nama_kelas, u.nama AS nama_pengajar " +
                "FROM Jadwal j " +
                "JOIN Matpel m ON j.Matpel_mapel_id = m.mapel_id " +
                "JOIN Kelas k ON j.Kelas_Users_user_id = k.Users_user_id AND j.Kelas_kelas_id = k.kelas_id " +
                "JOIN Users u ON k.Users_user_id = u.user_id " +
                "WHERE EXISTS (SELECT 1 FROM Student_Class_Enrollment sce WHERE sce.Users_user_id = ? AND sce.Kelas_kelas_id = k.kelas_id AND sce.Kelas_Users_user_id = k.Users_user_id)";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, studentId);
        return stmt.executeQuery();
    }

    public int getJadwalId(int kelasId, String waliUserId, int mapelId, String hari) throws SQLException {
        String sql = "SELECT jadwal_id FROM Jadwal WHERE Kelas_kelas_id = ? AND Kelas_Users_user_id = ? AND Matpel_mapel_id = ? AND hari = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, kelasId);
            stmt.setString(2, waliUserId);
            stmt.setInt(3, mapelId);
            stmt.setString(4, hari);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("jadwal_id");
            }
            return -1; // Not found
        }
    }
}