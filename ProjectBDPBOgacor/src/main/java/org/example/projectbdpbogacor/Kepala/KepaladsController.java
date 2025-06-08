// ProjectBDPBOgacor/src/main/java/org/example/projectbdpbogacor/Kepala/KepaladsController.java
package org.example.projectbdpbogacor.Kepala;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.projectbdpbogacor.Aset.AlertClass;
import org.example.projectbdpbogacor.DBSource.DBS; // Still needed for some direct DB operations if not fully migrated
import org.example.projectbdpbogacor.Controller.BaseController; // Import BaseController
import org.example.projectbdpbogacor.Service.UserService; // Import UserService
import org.example.projectbdpbogacor.Service.AnnouncementService; // Import AnnouncementService

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// Extend BaseController
public class KepaladsController extends BaseController {

    private final UserService userService = new UserService(); // Instantiate UserService
    private final AnnouncementService announcementService = new AnnouncementService(); // Instantiate AnnouncementService

    @FXML
    private Label welcomeUserLabel;
    @FXML
    private TabPane kepalaTabPane;

    // Announcements
    @FXML
    private TextArea announcementTextArea;

    // View All Users
    @FXML
    private ChoiceBox<String> filterRoleChoiceBox;
    @FXML
    private TextField filterNameField;
    @FXML
    private TableView<UserTableEntry> allUsersTableView;
    @FXML
    private TableColumn<UserTableEntry, String> tableUserIdColumn;
    @FXML
    private TableColumn<UserTableEntry, String> tableUsernameColumn;
    @FXML
    private TableColumn<UserTableEntry, String> tableNisNipColumn;
    @FXML
    private TableColumn<UserTableEntry, String> tableNamaColumn;
    @FXML
    private TableColumn<UserTableEntry, String> tableGenderColumn;
    @FXML
    private TableColumn<UserTableEntry, String> tableAlamatColumn;
    @FXML
    private TableColumn<UserTableEntry, String> tableEmailColumn;
    @FXML
    private TableColumn<UserTableEntry, String> tableNomerHpColumn;
    @FXML
    private TableColumn<UserTableEntry, String> tableRoleColumn;

    private Map<String, String> roleNameToIdMap = new HashMap<>();

    @FXML
    void initialize() {
        setLoggedInUserId(org.example.projectbdpbogacor.HelloApplication.getInstance().getLoggedInUserId()); // Use setter from BaseController
        loadUserName(welcomeUserLabel); // Call method from BaseController

        loadRolesForChoiceBox(); // Load roles for the filter
        filterRoleChoiceBox.getItems().addAll("All Roles");
        filterRoleChoiceBox.getItems().addAll(roleNameToIdMap.keySet());
        filterRoleChoiceBox.setValue("All Roles");
        initAllUsersTable();
        loadAllUsersToTable(filterRoleChoiceBox.getValue(), filterNameField.getText());

        filterRoleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleFilterUsers());
        filterNameField.textProperty().addListener((observable, oldValue, newValue) -> handleFilterUsers());

        kepalaTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                if (newTab.getText().equals("View All Users")) {
                    loadAllUsersToTable(filterRoleChoiceBox.getValue(), filterNameField.getText());
                }
            }
        });
    }

    private void loadRolesForChoiceBox() {
        roleNameToIdMap.clear();
        try (ResultSet rs = userService.getRoles()) {
            while (rs.next()) {
                String roleId = rs.getString("role_id");
                String roleName = rs.getString("role_name");
                roleNameToIdMap.put(roleName, roleId);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load roles", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleCreateAnnouncement() {
        String announcementContent = announcementTextArea.getText();

        if (announcementContent.isEmpty()) {
            AlertClass.WarningAlert("Input Error", "Announcement Empty", "Please enter the announcement content.");
            return;
        }

        try {
            boolean success = announcementService.createAnnouncement(announcementContent, loggedInUserId);
            if (success) {
                AlertClass.InformationAlert("Success", "Announcement Published", "Announcement has been published successfully.");
                announcementTextArea.clear();
            } else {
                AlertClass.ErrorAlert("Failed", "Announcement Not Published", "Failed to publish announcement.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to publish announcement", e.getMessage());
            e.printStackTrace();
        }
    }

    private void initAllUsersTable() {
        tableUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        tableUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableNisNipColumn.setCellValueFactory(new PropertyValueFactory<>("nisNip"));
        tableNamaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        tableGenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        tableAlamatColumn.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        tableEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableNomerHpColumn.setCellValueFactory(new PropertyValueFactory<>("nomerHp"));
        tableRoleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
    }

    private void loadAllUsersToTable(String roleFilter, String nameFilter) {
        ObservableList<UserTableEntry> userList = FXCollections.observableArrayList();
        String roleId = null;
        try {
            if (roleFilter != null && !roleFilter.equals("All Roles")) {
                roleId = userService.getRoleIdByName(roleFilter);
            }
            try (ResultSet rs = userService.getAllUsers(roleId, nameFilter)) {
                while (rs.next()) {
                    userList.add(new UserTableEntry(
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("NIS_NIP"),
                            rs.getString("nama"),
                            rs.getString("gender").equals("L") ? "Laki-laki" : "Perempuan",
                            rs.getString("alamat"),
                            rs.getString("email"),
                            rs.getString("nomer_hp"),
                            rs.getString("role_name")
                    ));
                }
            }
            allUsersTableView.setItems(userList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load all users", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleFilterUsers() {
        String selectedRole = filterRoleChoiceBox.getValue();
        String nameFilter = filterNameField.getText();
        loadAllUsersToTable(selectedRole, nameFilter);
    }

    // handleLogout() is now in BaseController and can be removed here if identical
    // @FXML
    // void handleLogout() {
    //     super.handleLogout();
    // }

    public static class UserTableEntry {
        private final StringProperty userId;
        private final StringProperty username;
        private final StringProperty nisNip;
        private final StringProperty nama;
        private final StringProperty gender;
        private final StringProperty alamat;
        private final StringProperty email;
        private final StringProperty nomerHp;
        private final StringProperty roleName;

        public UserTableEntry(String userId, String username, String nisNip, String nama, String gender, String alamat, String email, String nomerHp, String roleName) {
            this.userId = new SimpleStringProperty(userId);
            this.username = new SimpleStringProperty(username);
            this.nisNip = new SimpleStringProperty(nisNip);
            this.nama = new SimpleStringProperty(nama);
            this.gender = new SimpleStringProperty(gender);
            this.alamat = new SimpleStringProperty(alamat);
            this.email = new SimpleStringProperty(email);
            this.nomerHp = new SimpleStringProperty(nomerHp);
            this.roleName = new SimpleStringProperty(roleName);
        }

        public String getUserId() { return userId.get(); }
        public StringProperty userIdProperty() { return userId; }
        public String getUsername() { return username.get(); }
        public StringProperty usernameProperty() { return username; }
        public String getNisNip() { return nisNip.get(); }
        public StringProperty nisNipProperty() { return nisNip; }
        public String getNama() { return nama.get(); }
        public StringProperty namaProperty() { return nama; }
        public String getGender() { return gender.get(); }
        public StringProperty genderProperty() { return gender; }
        public String getAlamat() { return alamat.get(); }
        public StringProperty alamatProperty() { return alamat; }
        public String getEmail() { return email.get(); }
        public StringProperty emailProperty() { return email; }
        public String getNomerHp() { return nomerHp.get(); }
        public StringProperty nomerHpProperty() { return nomerHp; }
        public String getRoleName() { return roleName.get(); }
        public StringProperty roleNameProperty() { return roleName; }
    }
}