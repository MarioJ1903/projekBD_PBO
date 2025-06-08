package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SemesterService {

    public ResultSet getAllSemesters() throws SQLException {
        String sql = "SELECT semester_id, tahun_ajaran, semester FROM Semester";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.executeQuery();
    }
}