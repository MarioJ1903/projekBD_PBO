package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AnnouncementService {

    public boolean createAnnouncement(String content, String userId) throws SQLException {
        String sql = "INSERT INTO Pengumuman (pengumuman, Users_user_id, waktu) VALUES (?, ?, NOW())";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, content);
            stmt.setString(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ResultSet getAllAnnouncements() throws SQLException {
        String sql = "SELECT pengumuman_id, pengumuman, waktu FROM Pengumuman ORDER BY waktu DESC";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.executeQuery();
    }

    public ResultSet getAnnouncementsByUserId(String userId) throws SQLException {
        String sql = "SELECT pengumuman_id, pengumuman, waktu FROM Pengumuman WHERE Users_user_id = ? ORDER BY waktu DESC";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, userId);
        return stmt.executeQuery();
    }

    public boolean updateAnnouncement(int announcementId, String content, String userId) throws SQLException {
        String sql = "UPDATE Pengumuman SET pengumuman = ?, waktu = NOW() WHERE pengumuman_id = ? AND Users_user_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, content);
            stmt.setInt(2, announcementId);
            stmt.setString(3, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteAnnouncement(int announcementId, String userId) throws SQLException {
        String sql = "DELETE FROM Pengumuman WHERE pengumuman_id = ? AND Users_user_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, announcementId);
            stmt.setString(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteAnnouncement(int announcementId) throws SQLException {
        // Overload for admin to delete without user_id filter
        String sql = "DELETE FROM Pengumuman WHERE pengumuman_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, announcementId);
            return stmt.executeUpdate() > 0;
        }
    }
}