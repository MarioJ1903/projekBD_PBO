package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubjectService {

    public ResultSet getAllSubjects() throws SQLException {
        String sql = "SELECT mapel_id, nama_mapel, category FROM Matpel";
        Connection con = DBS.getConnection(); // Connection needs to be kept open for ResultSet
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.executeQuery();
    }

    public boolean addSubject(String namaMapel, String category) throws SQLException {
        String sql = "INSERT INTO Matpel (nama_mapel, category) VALUES (?, ?)";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, namaMapel);
            stmt.setString(2, category);
            return stmt.executeUpdate() > 0;
        }
    }
}