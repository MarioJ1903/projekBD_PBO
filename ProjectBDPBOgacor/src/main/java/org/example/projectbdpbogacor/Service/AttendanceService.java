package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AttendanceService {

    public boolean recordAttendance(String studentUserId, int jadwalId, String status, LocalDateTime attendanceTime) throws SQLException {
        try (Connection con = DBS.getConnection()) {
            // Check if attendance already exists for this student, date, and jadwal
            String checkAbsensiSql = "SELECT absensi_id FROM Absensi WHERE Users_user_id = ? AND tanggal::date = ? AND Jadwal_jadwal_id = ?";
            PreparedStatement checkAbsensiStmt = con.prepareStatement(checkAbsensiSql);
            checkAbsensiStmt.setString(1, studentUserId);
            checkAbsensiStmt.setDate(2, Date.valueOf(attendanceTime.toLocalDate()));
            checkAbsensiStmt.setInt(3, jadwalId);
            ResultSet rsCheck = checkAbsensiStmt.executeQuery();

            if (rsCheck.next()) {
                // Update existing attendance
                int absensiIdToUpdate = rsCheck.getInt("absensi_id");
                String updateSql = "UPDATE Absensi SET status = ?, tanggal = ? WHERE absensi_id = ?";
                PreparedStatement updateStmt = con.prepareStatement(updateSql);
                updateStmt.setString(1, status);
                updateStmt.setTimestamp(2, Timestamp.valueOf(attendanceTime));
                updateStmt.setInt(3, absensiIdToUpdate);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert new attendance
                String insertSql = "INSERT INTO Absensi (tanggal, status, Users_user_id, Jadwal_jadwal_id) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = con.prepareStatement(insertSql);
                insertStmt.setTimestamp(1, Timestamp.valueOf(attendanceTime));
                insertStmt.setString(2, status);
                insertStmt.setString(3, studentUserId);
                insertStmt.setInt(4, jadwalId);
                return insertStmt.executeUpdate() > 0;
            }
        }
    }

    public ResultSet getAttendanceByFilters(String teacherId, int kelasId, String waliUserId, int mapelId, String studentId, Date selectedDate) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT a.tanggal, a.status, m.nama_mapel, k.nama_kelas, j.jam_mulai, j.jam_selsai " +
                        "FROM Absensi a " +
                        "JOIN Users u ON a.Users_user_id = u.user_id " +
                        "JOIN Jadwal j ON a.Jadwal_jadwal_id = j.jadwal_id " +
                        "JOIN Matpel m ON j.Matpel_mapel_id = m.mapel_id " +
                        "JOIN Kelas k ON j.Kelas_Users_user_id = k.Users_user_id AND j.Kelas_kelas_id = k.kelas_id " +
                        "JOIN Detail_Pengajar dp ON dp.Matpel_mapel_id = m.mapel_id AND dp.Kelas_Users_user_id = k.Users_user_id AND dp.Kelas_kelas_id = k.kelas_id " +
                        "WHERE dp.Users_user_id = ? "
        );

        int paramIndex = 1;
        // The guru's ID is always the first parameter
        // (Parameters will be added dynamically based on filters)

        if (kelasId > 0 && waliUserId != null && !waliUserId.isEmpty()) {
            sqlBuilder.append(" AND k.kelas_id = ? AND k.Users_user_id = ?");
        }
        if (mapelId > 0) {
            sqlBuilder.append(" AND m.mapel_id = ?");
        }
        if (studentId != null && !studentId.isEmpty()) {
            sqlBuilder.append(" AND u.user_id = ?");
        }
        if (selectedDate != null) {
            sqlBuilder.append(" AND a.tanggal::date = ?");
        }

        sqlBuilder.append(" ORDER BY a.tanggal DESC");

        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sqlBuilder.toString());

        stmt.setString(paramIndex++, teacherId); // Bind guru's ID

        if (kelasId > 0 && waliUserId != null && !waliUserId.isEmpty()) {
            stmt.setInt(paramIndex++, kelasId);
            stmt.setString(paramIndex++, waliUserId);
        }
        if (mapelId > 0) {
            stmt.setInt(paramIndex++, mapelId);
        }
        if (studentId != null && !studentId.isEmpty()) {
            stmt.setString(paramIndex++, studentId);
        }
        if (selectedDate != null) {
            stmt.setDate(paramIndex++, selectedDate);
        }
        return stmt.executeQuery();
    }

    public ResultSet getAttendanceByStudent(String studentId) throws SQLException {
        String sql = "SELECT a.tanggal, a.status, m.nama_mapel, k.nama_kelas, j.jam_mulai, j.jam_selsai " +
                "FROM Absensi a " +
                "JOIN Users u ON a.Users_user_id = u.user_id " +
                "JOIN Jadwal j ON a.Jadwal_jadwal_id = j.jadwal_id " +
                "JOIN Matpel m ON j.Matpel_mapel_id = m.mapel_id " +
                "JOIN Kelas k ON j.Kelas_Users_user_id = k.Users_user_id AND j.Kelas_kelas_id = k.kelas_id " +
                "WHERE u.user_id = ?";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, studentId);
        return stmt.executeQuery();
    }

    public Map<String, Integer> getAttendanceSummary(String studentUserId, LocalDateTime semesterStart, LocalDateTime semesterEnd) throws SQLException {
        Map<String, Integer> summary = new HashMap<>();
        summary.put("Hadir", 0);
        summary.put("Alpha", 0);
        summary.put("Ijin", 0);

        String sql = "SELECT a.status, COUNT(a.status) AS count " +
                "FROM Absensi a " +
                "WHERE a.Users_user_id = ? " +
                "AND a.tanggal >= ? AND a.tanggal <= ? " +
                "GROUP BY a.status";

        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, studentUserId);
            stmt.setTimestamp(2, Timestamp.valueOf(semesterStart));
            stmt.setTimestamp(3, Timestamp.valueOf(semesterEnd));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                summary.put(rs.getString("status"), rs.getInt("count"));
            }
        }
        return summary;
    }
}