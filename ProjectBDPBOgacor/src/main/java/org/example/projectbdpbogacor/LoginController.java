package org.example.projectbdpbogacor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.example.projectbdpbogacor.Aset.AlertClass;
import org.example.projectbdpbogacor.Aset.HashGenerator;
import org.example.projectbdpbogacor.DBSource.DBS; // Still needed for checkConnection
import org.example.projectbdpbogacor.Service.UserService; // Import UserService

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class LoginController {

    private final UserService userService = new UserService(); // Instantiate UserService

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ChoiceBox<String> selectRole;

    @FXML
    private ToggleButton darkModeToggle;

    private String userId;

    @FXML
    void initialize() {
        selectRole.getItems().addAll("Admin", "Kepala Sekolah", "Guru", "Wali Kelas", "Siswa");
        selectRole.setValue("Choice Role");

        HelloApplication app = HelloApplication.getInstance();
        if (app != null) {
            darkModeToggle.setSelected(app.isDarkMode());
            darkModeToggle.setText(app.isDarkMode() ? "Light Mode" : "Dark Mode");
        }

        DBS.checkConnection(); // Retain for initial connection check
    }

    @FXML
    void handleModeToggle(ActionEvent event) {
        HelloApplication app = HelloApplication.getInstance();
        if (app != null) {
            app.toggleMode();
            darkModeToggle.setText(app.isDarkMode() ? "Light Mode" : "Dark Mode");
        }
    }

    boolean verifyCredentials(String username, String password, String roleName) throws SQLException {
        String roleId = userService.getRoleIdByName(roleName); // Use UserService to get role ID
        if (roleId == null) {
            AlertClass.ErrorAlert("Login Error", "Invalid Role", "The selected role is not recognized.");
            return false;
        }

        // Use UserService to verify credentials
        Map<String, String> userDetails = userService.getUserDetails(username); // Assuming UserService has getUserDetails(username, roleId)
        if (userDetails != null && !userDetails.isEmpty()) {
            String dbPasswordHash = userDetails.get("password"); // Assuming password hash is returned
            if (HashGenerator.verify(password, dbPasswordHash)) { // HashGenerator remains
                this.userId = userDetails.get("user_id"); // Assuming user_id is returned
                return true;
            }
        }
        return false;
    }

    @FXML
    void onLoginClick(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = selectRole.getValue();

        if (username.isEmpty()) {
            AlertClass.WarningAlert("Input Error", "Username Empty", "Please enter your username.");
            return;
        }
        if (password.isEmpty()) {
            AlertClass.WarningAlert("Input Error", "Password Empty", "Please enter your password.");
            return;
        }
        if (role == null || role.equals("Choice Role")) {
            AlertClass.WarningAlert("Input Error", "Role Not Selected", "Please select a role.");
            return;
        }
        if (password.length() != 8) { // Password length validation remains
            AlertClass.WarningAlert("Input Error", "Password Length Error", "Password must be exactly 8 characters long.");
            return;
        }

        try {
            // Need a specific getUserDetails method in UserService for login with username and role ID
            // Let's add a new method to UserService for login
            Map<String, String> loginUserDetails = userService.getUserDetailsForLogin(username, role); // New method call
            if (loginUserDetails != null && !loginUserDetails.isEmpty()) {
                String dbPasswordHash = loginUserDetails.get("password");
                if (org.example.projectbdpbogacor.Aset.HashGenerator.verify(password, dbPasswordHash)) {
                    this.userId = loginUserDetails.get("user_id");
                    HelloApplication app = HelloApplication.getInstance();
                    app.setLoggedInUserId(this.userId);
                    app.setLoggedInUserRole(role);

                    String fxmlFile = "";
                    String title = "";
                    int sceneWidth = 1300;
                    int sceneHeight = 700;

                    switch (role) {
                        case "Admin":
                            fxmlFile = "/org/example/projectbdpbogacor/admin-dashboard-view.fxml";
                            title = "Admin Dashboard";
                            break;
                        case "Kepala Sekolah":
                            fxmlFile = "/org/example/projectbdpbogacor/kepala-dashboard-view.fxml";
                            title = "Kepala Sekolah Dashboard";
                            break;
                        case "Guru":
                            fxmlFile = "/org/example/projectbdpbogacor/guru-dashboard-view.fxml";
                            title = "Guru Dashboard";
                            break;
                        case "Wali Kelas":
                            fxmlFile = "/org/example/projectbdpbogacor/wali-dashboard-view.fxml";
                            title = "Wali Kelas Dashboard";
                            break;
                        case "Siswa":
                            fxmlFile = "/org/example/projectbdpbogacor/siswa-dashboard-view.fxml";
                            title = "Siswa Dashboard";
                            break;
                        default:
                            AlertClass.ErrorAlert("Login Error", "Invalid Role", "The selected role does not have a defined dashboard.");
                            return;
                    }
                    app.changeScene(fxmlFile, title, sceneWidth, sceneHeight);
                } else {
                    AlertClass.ErrorAlert("Login Failed", "Invalid Credentials", "Please check your username and password.");
                }
            } else {
                AlertClass.ErrorAlert("Login Failed", "Invalid Credentials", "Please check your username and password.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Login Failed", "An error occurred during login: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            AlertClass.ErrorAlert("Error Loading View", "Could not load dashboard", "An error occurred while loading the dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}