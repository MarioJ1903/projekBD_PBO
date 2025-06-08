package org.example.projectbdpbogacor.Service;

import org.example.projectbdpbogacor.Aset.HashGenerator;
import org.example.projectbdpbogacor.DBSource.DBS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    public boolean addUser(String userId, String username, String password, String nisNip, String nama,
                           String gender, String alamat, String email, String nomerHp, String roleId) throws SQLException {
        try (Connection con = DBS.getConnection()) {
            // Check if user_id already exists
            String checkIdSql = "SELECT COUNT(*) FROM Users WHERE user_id = ?";
            PreparedStatement checkIdStmt = con.prepareStatement(checkIdSql);
            checkIdStmt.setString(1, userId);
            ResultSet rs = checkIdStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // User ID already exists
            }

            String hashedPassword = HashGenerator.hash(password);
            String sql = "INSERT INTO Users (user_id, username, password, NIS_NIP, nama, gender, alamat, email, nomer_hp, Role_role_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, nisNip);
            stmt.setString(5, nama);
            stmt.setString(6, gender);
            stmt.setString(7, alamat);
            stmt.setString(8, email);
            stmt.setString(9, nomerHp);
            stmt.setString(10, roleId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Map<String, String> getUserDetails(String userId) throws SQLException {
        Map<String, String> userDetails = new HashMap<>();
        String sql = "SELECT user_id, username, NIS_NIP, nama, gender, alamat, email, nomer_hp, Role_role_id FROM Users WHERE user_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userDetails.put("user_id", rs.getString("user_id"));
                userDetails.put("username", rs.getString("username"));
                userDetails.put("NIS_NIP", rs.getString("NIS_NIP"));
                userDetails.put("nama", rs.getString("nama"));
                userDetails.put("gender", rs.getString("gender"));
                userDetails.put("alamat", rs.getString("alamat"));
                userDetails.put("email", rs.getString("email"));
                userDetails.put("nomer_hp", rs.getString("nomer_hp"));
                userDetails.put("Role_role_id", rs.getString("Role_role_id"));
            }
        }
        return userDetails;
    }

    public boolean updateUser(String userId, String username, String password, String nisNip, String nama,
                              String gender, String alamat, String email, String nomerHp, String roleId) throws SQLException {
        try (Connection con = DBS.getConnection()) {
            String sql;
            PreparedStatement stmt;

            if (password != null && !password.isEmpty()) {
                String hashedPassword = HashGenerator.hash(password);
                sql = "UPDATE Users SET username = ?, password = ?, NIS_NIP = ?, nama = ?, gender = ?, alamat = ?, email = ?, nomer_hp = ?, Role_role_id = ? WHERE user_id = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, nisNip);
                stmt.setString(4, nama);
                stmt.setString(5, gender);
                stmt.setString(6, alamat);
                stmt.setString(7, email);
                stmt.setString(8, nomerHp);
                stmt.setString(9, roleId);
                stmt.setString(10, userId);
            } else {
                sql = "UPDATE Users SET username = ?, NIS_NIP = ?, nama = ?, gender = ?, alamat = ?, email = ?, nomer_hp = ?, Role_role_id = ? WHERE user_id = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, nisNip);
                stmt.setString(3, nama);
                stmt.setString(4, gender);
                stmt.setString(5, alamat);
                stmt.setString(6, email);
                stmt.setString(7, nomerHp);
                stmt.setString(8, roleId);
                stmt.setString(9, userId);
            }
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(String userId) throws SQLException {
        try (Connection con = DBS.getConnection()) {
            // Delete related records in dependent tables first due to ON DELETE NO ACTION (if applicable)
            // This logic might need to be more sophisticated depending on FK constraints
            // For now, let's assume cascade or ignore, and only delete the user directly
            // In a real app, you'd manage these dependencies properly (e.g., in a transaction)
            String deleteEnrollmentSql = "DELETE FROM Student_Class_Enrollment WHERE Users_user_id = ?";
            try (PreparedStatement delEnrollStmt = con.prepareStatement(deleteEnrollmentSql)) {
                delEnrollStmt.setString(1, userId);
                delEnrollStmt.executeUpdate();
            }

            // Other dependent tables that might need manual deletion if not ON DELETE CASCADE
            // "Feedback", "Pengumuman", "Rapor", "Absensi", "Detail_Pengajar", "Kelas" (if user is Wali Kelas)
            // You'd add similar prepared statements for these if they don't cascade on delete for Users_user_id

            String sql = "DELETE FROM Users WHERE user_id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ResultSet getAllUsers(String roleId, String nameFilter) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT u.user_id, u.username, u.NIS_NIP, u.nama, u.gender, u.alamat, u.email, u.nomer_hp, r.role_name " +
                "FROM Users u JOIN Role r ON u.Role_role_id = r.role_id WHERE 1=1 ");

        if (roleId != null && !roleId.equals("All Roles")) { // Assuming "All Roles" is handled by the controller
            sqlBuilder.append("AND u.Role_role_id = ? ");
        }
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            sqlBuilder.append("AND (u.nama ILIKE ? OR u.username ILIKE ? OR u.NIS_NIP ILIKE ?) ");
        }
        sqlBuilder.append("ORDER BY u.nama");

        Connection con = DBS.getConnection(); // Connection needs to be kept open until ResultSet is processed
        PreparedStatement stmt = con.prepareStatement(sqlBuilder.toString());
        int paramIndex = 1;
        if (roleId != null && !roleId.equals("All Roles")) {
            stmt.setString(paramIndex++, roleId);
        }
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            stmt.setString(paramIndex++, "%" + nameFilter.trim() + "%");
            stmt.setString(paramIndex++, "%" + nameFilter.trim() + "%");
            stmt.setString(paramIndex++, "%" + nameFilter.trim() + "%");
        }
        return stmt.executeQuery();
    }

    public ResultSet getRoles() throws SQLException {
        String sql = "SELECT role_id, role_name FROM Role";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.executeQuery();
    }

    public String getRoleIdByName(String roleName) throws SQLException {
        String roleId = null;
        String sql = "SELECT role_id FROM Role WHERE role_name = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, roleName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                roleId = rs.getString("role_id");
            }
        }
        return roleId;
    }

    public String getRoleNameById(String roleId) throws SQLException {
        String roleName = null;
        String sql = "SELECT role_name FROM Role WHERE role_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, roleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                roleName = rs.getString("role_name");
            }
        }
        return roleName;
    }

    public ResultSet getStudents() throws SQLException {
        String sql = "SELECT user_id, nama, NIS_NIP FROM Users WHERE Role_role_id = 'S'";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.executeQuery();
    }

    public ResultSet getWaliKelas() throws SQLException {
        String sql = "SELECT user_id, nama FROM Users WHERE Role_role_id = 'W'";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.executeQuery();
    }

    public ResultSet getGuru() throws SQLException {
        String sql = "SELECT user_id, nama FROM Users WHERE Role_role_id = 'G'";
        Connection con = DBS.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.executeQuery();
    }

    public Map<String, String> getUserDetailsForLogin(String username, String roleName) throws SQLException {
        Map<String, String> userDetails = new HashMap<>();
        String roleId = getRoleIdByName(roleName);
        if (roleId == null) {
            return null; // Role not found
        }
        String sql = "SELECT user_id, password FROM Users WHERE Username = ? AND Role_role_id = ?";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, roleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userDetails.put("user_id", rs.getString("user_id"));
                userDetails.put("password", rs.getString("password"));
            }
        }
        return userDetails;
    }

}

