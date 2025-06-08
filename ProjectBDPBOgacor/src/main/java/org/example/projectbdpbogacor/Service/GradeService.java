package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeService {

    public int getOrCreateRaporId(String studentUserId, int semesterId) throws SQLException {
        String raporIdSql = "SELECT rapor_id FROM Rapor WHERE Users_user_id = ? AND Semester_semester_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement raporStmt = con.prepareStatement(raporIdSql)) {
            raporStmt.setString(1, studentUserId);
            raporStmt.setInt(2, semesterId);
            ResultSet rs = raporStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("rapor_id");
            } else {
                String insertRaporSql = "INSERT INTO Rapor (Users_user_id, Semester_semester_id) VALUES (?, ?) RETURNING rapor_id";
                try (PreparedStatement insertRaporStmt = con.prepareStatement(insertRaporSql)) {
                    insertRaporStmt.setString(1, studentUserId);
                    insertRaporStmt.setInt(2, semesterId);
                    ResultSet newRaporRs = insertRaporStmt.executeQuery();
                    if (newRaporRs.next()) {
                        return newRaporRs.getInt("rapor_id");
                    }
                }
            }
        }
        return -1; // Indicate failure
    }

    public boolean addGrade(int score, String jenisNilai, int mapelId, int raporId) throws SQLException {
        String insertNilaiSql = "INSERT INTO Nilai (nilai, jenis_nilai, Matpel_mapel_id, Rapor_rapor_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(insertNilaiSql)) {
            stmt.setInt(1, score);
            stmt.setString(2, jenisNilai);
            stmt.setInt(3, mapelId);
            stmt.setInt(4, raporId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ResultSet getGradesByStudentAndSemesterAndSubject(String studentUserId, int semesterId, int mapelId) throws SQLException {
        String sql = "SELECT n.jenis_nilai, m.nama_mapel, n.nilai " +
                "FROM Nilai n " +
                "JOIN Matpel m ON n.Matpel_mapel_id = m.mapel_id " +
                "JOIN Rapor r ON n.Rapor_rapor_id = r.rapor_id " +
                "WHERE r.Users_user_id = ? AND r.Semester_semester_id = ? AND n.Matpel_mapel_id = ?";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, studentUserId);
        stmt.setInt(2, semesterId);
        stmt.setInt(3, mapelId);
        return stmt.executeQuery();
    }

    public ResultSet getGradesByStudent(String studentId) throws SQLException {
        String sql = "SELECT n.jenis_nilai, m.nama_mapel, n.nilai " +
                "FROM Nilai n " +
                "JOIN Matpel m ON n.Matpel_mapel_id = m.mapel_id " +
                "JOIN Rapor r ON n.Rapor_rapor_id = r.rapor_id " +
                "WHERE r.Users_user_id = ?";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, studentId);
        return stmt.executeQuery();
    }

    public ResultSet getGradesByStudentAndSemester(String studentUserId, int semesterId) throws SQLException {
        String sql = "SELECT n.jenis_nilai, m.nama_mapel, n.nilai " +
                "FROM Nilai n " +
                "JOIN Matpel m ON n.Matpel_mapel_id = m.mapel_id " +
                "JOIN Rapor r ON n.Rapor_rapor_id = r.rapor_id " +
                "WHERE r.Users_user_id = ? AND r.Semester_semester_id = ?";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, studentUserId);
        stmt.setInt(2, semesterId);
        return stmt.executeQuery();
    }
}