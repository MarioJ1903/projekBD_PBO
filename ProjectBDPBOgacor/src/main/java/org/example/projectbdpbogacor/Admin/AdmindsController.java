package org.example.projectbdpbogacor.Admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.projectbdpbogacor.Aset.AlertClass;
import org.example.projectbdpbogacor.DBSource.DBS; // Still needed for some direct connections if not fully migrated
import org.example.projectbdpbogacor.Controller.BaseController;
import org.example.projectbdpbogacor.Service.UserService;
import org.example.projectbdpbogacor.Service.ClassService; // Import ClassService
import org.example.projectbdpbogacor.Service.SubjectService; // Import SubjectService
import org.example.projectbdpbogacor.model.PengumumanEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdmindsController extends BaseController {

    private final UserService userService = new UserService();
    private final ClassService classService = new ClassService(); // Instantiate ClassService
    private final SubjectService subjectService = new SubjectService(); // Instantiate SubjectService

    @FXML
    private Label welcomeUserLabel;
    @FXML
    private TabPane adminTabPane;

    // Manage Users (Add User)
    @FXML
    private TextField newUserIdField;
    @FXML
    private TextField newUsernameField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private TextField newNisNipField;
    @FXML
    private TextField newNameField;
    @FXML
    private ChoiceBox<String> newGenderChoiceBox;
    @FXML
    private TextField newAddressField;
    @FXML
    private TextField newEmailField;
    @FXML
    private TextField newPhoneNumberField;
    @FXML
    private ChoiceBox<String> newRoleChoiceBox;

    // Manage Users (Edit/Delete)
    @FXML
    private ChoiceBox<String> editUserChoiceBox;
    @FXML
    private TextField editUserIdField;
    @FXML
    private TextField editUsernameField;
    @FXML
    private PasswordField editPasswordField;
    @FXML
    private TextField editNisNipField;
    @FXML
    private TextField editNameField;
    @FXML
    private ChoiceBox<String> editGenderChoiceBox;
    @FXML
    private TextField editAddressField;
    @FXML
    private TextField editEmailField;
    @FXML
    private TextField editNomerHpField;
    @FXML
    private ChoiceBox<String> editRoleChoiceBox;
    @FXML
    private Button deleteUserButton;
    @FXML
    private Button updateUserButton;

    // Manage Schedules & Subjects (This section will now be for schedules only, subjects moved to a new tab)
    @FXML
    private ChoiceBox<String> scheduleDayChoiceBox;
    @FXML
    private TextField scheduleStartTimeField;
    @FXML
    private TextField scheduleEndTimeField;
    @FXML
    private ChoiceBox<String> scheduleSubjectChoiceBox;
    @FXML
    private ChoiceBox<String> scheduleClassChoiceBox;

    // Assign Students to Classes (now integrated into Manage Students in Class)
    @FXML
    private ChoiceBox<String> assignStudentToClassChoiceBox;
    @FXML
    private Button assignStudentToClassButton;

    // Manage Students in Class
    @FXML
    private ChoiceBox<String> studentClassFilterChoiceBox;
    @FXML
    private TableView<StudentEntry> studentInClassTableView;
    @FXML
    private TableColumn<StudentEntry, String> studentNameInClassColumn;
    @FXML
    private TableColumn<StudentEntry, String> nisNipInClassColumn;
    @FXML
    private TableColumn<StudentEntry, String> studentUserIdInClassColumn;
    @FXML
    private Button deleteStudentFromClassButton;
    @FXML
    private Button editStudentInClassButton;

    // Announcements
    @FXML
    private TextArea announcementTextArea;
    @FXML
    private TableView<PengumumanEntry> announcementTable;
    @FXML
    private TableColumn<PengumumanEntry, String> announcementWaktuColumn;
    @FXML
    private TableColumn<PengumumanEntry, String> announcementContentColumn;

    // Manage Class
    @FXML
    private TextField newClassNameField;
    @FXML
    private TextField newClassDescriptionField;
    @FXML
    private ChoiceBox<String> newClassWaliKelasChoiceBox;
    @FXML
    private ChoiceBox<String> newClassSemesterChoiceBox;
    @FXML
    private ChoiceBox<String> editClassChoiceBox;
    @FXML
    private Button updateClassButton;
    @FXML
    private Button deleteClassButton;

    // View All Users Table
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
    @FXML
    private ChoiceBox<String> filterRoleChoiceBox;
    @FXML
    private TextField filterNameField;

    // NEW FXML elements for "Manage Subjects" Tab
    @FXML
    private TextField newSubjectNameField;
    @FXML
    private ChoiceBox newCategoryChoiceBox;
    @FXML
    private ChoiceBox<String> assignTeacherSubjectChoiceBox;
    @FXML
    private ChoiceBox<String> assignTeacherClassChoiceBox;
    @FXML
    private ChoiceBox<String> assignTeacherGuruChoiceBox;
    @FXML
    private TableView<SubjectAssignmentEntry> subjectAssignmentTable;
    @FXML
    private TableColumn<SubjectAssignmentEntry, String> assignmentSubjectColumn;
    @FXML
    private TableColumn<SubjectAssignmentEntry, String> assignmentClassColumn;
    @FXML
    private TableColumn<SubjectAssignmentEntry, String> assignmentTeacherColumn;
    @FXML
    private Button deleteAssignmentButton;

    // Helper data structures for ChoiceBoxes
    private ObservableList<Pair<String, String>> classData = FXCollections.observableArrayList();
    private ObservableList<Pair<String, Integer>> subjectData = FXCollections.observableArrayList();
    private ObservableList<Pair<String, String>> studentData = FXCollections.observableArrayList();
    private ObservableList<Pair<String, String>> allUsersData = FXCollections.observableArrayList();
    private ObservableList<Pair<String, String>> waliKelasData = FXCollections.observableArrayList();
    private ObservableList<Pair<String, Integer>> semesterData = FXCollections.observableArrayList();
    private ObservableList<Pair<String, String>> guruData = FXCollections.observableArrayList();
    private Map<String, String> roleNameToIdMap = new HashMap<>();
    private Map<String, String> roleIdToNameMap = new HashMap<>();
    private Map<String, String> editableClassesMap = new HashMap<>();


    @FXML
    void initialize() {
        setLoggedInUserId(org.example.projectbdpbogacor.HelloApplication.getInstance().getLoggedInUserId());
        loadUserName(welcomeUserLabel);

        newGenderChoiceBox.getItems().addAll("L", "P");
        newGenderChoiceBox.setValue("L");

        newCategoryChoiceBox.getItems().addAll("General", "Science");
        newCategoryChoiceBox.setValue("General");

        loadRolesForChoiceBox();
        newRoleChoiceBox.setValue("Siswa");

        newRoleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newUserIdField.setText(generateUserIdPrefix(newValue));
            }
        });

        scheduleDayChoiceBox.getItems().addAll("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu");
        scheduleDayChoiceBox.setValue("Senin");
        loadSubjectsForChoiceBox();
        loadClassesForChoiceBox();
        loadStudentsForChoiceBox();

        editGenderChoiceBox.getItems().addAll("L", "P");
        loadUsersForEditDelete();
        editRoleChoiceBox.getItems().addAll(roleNameToIdMap.keySet());
        editUserChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleUserSelectionForEditDelete();
            } else {
                clearEditUserFields();
            }
        });

        initStudentInClassTable();
        loadClassesForStudentFilter();

        loadWaliKelasForChoiceBox();
        loadSemestersForChoiceBox();
        loadClassesForEdit();
        editClassChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleClassSelectionForEdit();
            } else {
                clearClassEditFields();
            }
        });

        initAllUsersTable();
        filterRoleChoiceBox.getItems().addAll("All Roles");
        filterRoleChoiceBox.getItems().addAll(roleNameToIdMap.keySet());
        filterRoleChoiceBox.setValue("All Roles");

        initSubjectAssignmentTable();
        loadGuruForChoiceBox();

        initAnnouncementTable();
        announcementTable.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                announcementTextArea.setText(newSelection.getPengumuman());
            } else {
                announcementTextArea.clear();
            }
        });

        adminTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                switch (newTab.getText()) {
                    case "Manage Schedules":
                        loadSubjectsForChoiceBox();
                        loadClassesForChoiceBox();
                        break;
                    case "Manage Users":
                        loadUsersForEditDelete();
                        editUserChoiceBox.getSelectionModel().clearSelection();
                        clearEditUserFields();
                        break;
                    case "Manage Students in Class":
                        loadStudentsForChoiceBox();
                        loadClassesForStudentFilter();
                        studentInClassTableView.getItems().clear();
                        break;
                    case "Manage Classes":
                        loadWaliKelasForChoiceBox();
                        loadSemestersForChoiceBox();
                        loadClassesForEdit();
                        clearClassEditFields();
                        break;
                    case "View All Users":
                        loadAllUsersToTable(filterRoleChoiceBox.getValue(), filterNameField.getText());
                        break;
                    case "Manage Subjects":
                        loadSubjectsForChoiceBox();
                        loadClassesForChoiceBox();
                        loadGuruForChoiceBox();
                        loadSubjectAssignments();
                        break;
                    case "Announcements":
                        loadAnnouncements();
                        break;
                }
            }
        });

        filterRoleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleFilterUsers());
        filterNameField.textProperty().addListener((observable, oldValue, newValue) -> handleFilterUsers());

        loadAllUsersToTable(filterRoleChoiceBox.getValue(), filterNameField.getText());
        loadAnnouncements();
    }

    private void loadRolesForChoiceBox() {
        roleNameToIdMap.clear();
        roleIdToNameMap.clear();
        newRoleChoiceBox.getItems().clear();

        try (ResultSet rs = userService.getRoles()) {
            while (rs.next()) {
                String roleId = rs.getString("role_id");
                String roleName = rs.getString("role_name");
                roleNameToIdMap.put(roleName, roleId);
                roleIdToNameMap.put(roleId, roleName);
                newRoleChoiceBox.getItems().add(roleName);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load roles", e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure ResultSet and Connection are closed if they were managed by the controller
            // In UserService.getRoles(), the Connection is kept open for the ResultSet
            // so we rely on the caller to close it. This pattern needs careful management.
            // A better pattern for services is to return ObservableList or custom DTOs
            // and close the ResultSet/Connection within the service method.
            // For now, let's add a closing mechanism here for the ResultSet.
            // The connection is implicitly closed when try-with-resources block exits in DBS.getConnection() for direct calls,
            // but for service methods returning ResultSet, the connection needs to be managed carefully.
            // I'll assume for now that direct connections are handled by the service layer's return type.
        }
    }

    private String generateUserIdPrefix(String roleName) {
        String prefix = "";
        switch (roleName) {
            case "Admin": prefix = "A"; break;
            case "Kepala Sekolah": prefix = "K"; break;
            case "Guru": prefix = "G"; break;
            case "Wali Kelas": prefix = "W"; break;
            case "Siswa": prefix = "S"; break;
            default: prefix = "";
        }
        return prefix;
    }

    @FXML
    void handleAddUser() {
        String userId = newUserIdField.getText();
        String username = newUsernameField.getText();
        String password = newPasswordField.getText();
        String nisNip = newNisNipField.getText();
        String nama = newNameField.getText();
        String gender = newGenderChoiceBox.getValue();
        String alamat = newAddressField.getText();
        String email = newEmailField.getText();
        String nomerHp = newPhoneNumberField.getText();
        String selectedRoleName = newRoleChoiceBox.getValue();

        if (userId.isEmpty() || username.isEmpty() || password.isEmpty() || nisNip.isEmpty() || nama.isEmpty() ||
                gender == null || alamat.isEmpty() || email.isEmpty() || nomerHp.isEmpty() || selectedRoleName == null) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please fill in all user details and select a role.");
            return;
        }
        if (password.length() != 8) {
            AlertClass.WarningAlert("Input Error", "Password Length Error", "Password must be exactly 8 characters long.");
            return;
        }

        String roleId = null;
        try {
            roleId = userService.getRoleIdByName(selectedRoleName);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to retrieve role ID", e.getMessage());
            e.printStackTrace();
            return;
        }

        if (roleId == null) {
            AlertClass.ErrorAlert("Role Error", "Invalid Role", "Selected role is not recognized.");
            return;
        }

        try {
            boolean success = userService.addUser(userId, username, password, nisNip, nama, gender, alamat, email, nomerHp, roleId);
            if (success) {
                AlertClass.InformationAlert("Success", "User Added", "New user '" + nama + "' has been added with User ID: " + userId + " and Role: " + selectedRoleName);
                newUserIdField.clear();
                newUsernameField.clear();
                newPasswordField.clear();
                newNisNipField.clear();
                newNameField.clear();
                newAddressField.clear();
                newEmailField.clear();
                newPhoneNumberField.clear();
                newRoleChoiceBox.setValue("Siswa");
                loadUsersForEditDelete();
                loadStudentsForChoiceBox();
                loadWaliKelasForChoiceBox();
                loadGuruForChoiceBox();
                loadAllUsersToTable(filterRoleChoiceBox.getValue(), filterNameField.getText());
            } else {
                AlertClass.WarningAlert("Input Error", "Duplicate User ID", "The entered User ID already exists. Please use a unique ID.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to add user", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadUsersForEditDelete() {
        allUsersData.clear();
        editUserChoiceBox.getItems().clear();

        // Need to ensure ResultSet is closed here as it's returned by the service
        try (ResultSet rs = userService.getAllUsers(null, null)) { // Pass nulls for no filter initially
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String nama = rs.getString("nama");
                String display = nama + " (" + userId + ")";
                allUsersData.add(new Pair<>(display, userId));
                editUserChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load users for edit/delete", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleUserSelectionForEditDelete() {
        String selectedUserDisplay = editUserChoiceBox.getValue();
        if (selectedUserDisplay == null || selectedUserDisplay.isEmpty()) {
            clearEditUserFields();
            return;
        }

        String userIdToEdit = allUsersData.stream()
                .filter(p -> p.getKey().equals(selectedUserDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        if (userIdToEdit == null) {
            clearEditUserFields();
            return;
        }

        try {
            Map<String, String> userDetails = userService.getUserDetails(userIdToEdit);
            if (!userDetails.isEmpty()) {
                editUserIdField.setText(userDetails.get("user_id"));
                editUsernameField.setText(userDetails.get("username"));
                editPasswordField.setText("");
                editNisNipField.setText(userDetails.get("NIS_NIP"));
                editNameField.setText(userDetails.get("nama"));
                editGenderChoiceBox.setValue(userDetails.get("gender"));
                editAddressField.setText(userDetails.get("alamat"));
                editEmailField.setText(userDetails.get("email"));
                editNomerHpField.setText(userDetails.get("nomer_hp"));
                String roleId = userDetails.get("Role_role_id");
                editRoleChoiceBox.setValue(userService.getRoleNameById(roleId));
            } else {
                clearEditUserFields();
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load user details", e.getMessage());
            e.printStackTrace();
            clearEditUserFields();
        }
    }

    private void clearEditUserFields() {
        editUserIdField.clear();
        editUsernameField.clear();
        editPasswordField.clear();
        editNisNipField.clear();
        editNameField.clear();
        editGenderChoiceBox.getSelectionModel().clearSelection();
        editAddressField.clear();
        editEmailField.clear();
        editNomerHpField.clear();
        editRoleChoiceBox.getSelectionModel().clearSelection();
    }

    @FXML
    void handleUpdateUser() {
        String userId = editUserIdField.getText();
        String username = editUsernameField.getText();
        String password = editPasswordField.getText();
        String nisNip = editNisNipField.getText();
        String nama = editNameField.getText();
        String gender = editGenderChoiceBox.getValue();
        String alamat = editAddressField.getText();
        String email = editEmailField.getText();
        String nomerHp = editNomerHpField.getText();
        String selectedRoleName = editRoleChoiceBox.getValue();

        if (userId.isEmpty() || username.isEmpty() || nisNip.isEmpty() || nama.isEmpty() ||
                gender == null || alamat.isEmpty() || email.isEmpty() || nomerHp.isEmpty() || selectedRoleName == null) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please fill in all user details for update.");
            return;
        }

        String roleId = null;
        try {
            roleId = userService.getRoleIdByName(selectedRoleName);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to retrieve role ID", e.getMessage());
            e.printStackTrace();
            return;
        }

        if (roleId == null) {
            AlertClass.ErrorAlert("Role Error", "Invalid Role", "Selected role is not recognized.");
            return;
        }

        if (!password.isEmpty() && password.length() != 8) {
            AlertClass.WarningAlert("Input Error", "Password Length Error", "Password must be exactly 8 characters long if changing.");
            return;
        }

        try {
            boolean success = userService.updateUser(userId, username, password.isEmpty() ? null : password, nisNip, nama, gender, alamat, email, nomerHp, roleId);
            if (success) {
                AlertClass.InformationAlert("Success", "User Updated", "User '" + nama + "' has been updated successfully.");
                clearEditUserFields();
                loadUsersForEditDelete();
                loadStudentsForChoiceBox();
                loadWaliKelasForChoiceBox();
                loadGuruForChoiceBox();
                loadClassesForChoiceBox();
                loadAllUsersToTable(filterRoleChoiceBox.getValue(), filterNameField.getText());
                loadSubjectAssignments();
            } else {
                AlertClass.ErrorAlert("Failed", "User Not Updated", "Failed to update user. User ID might not exist.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to update user", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteUser() {
        String selectedUserDisplay = editUserChoiceBox.getValue();
        if (selectedUserDisplay == null || selectedUserDisplay.isEmpty()) {
            AlertClass.WarningAlert("Selection Error", "No User Selected", "Please select a user to delete.");
            return;
        }

        String userIdToDelete = allUsersData.stream()
                .filter(p -> p.getKey().equals(selectedUserDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        if (userIdToDelete == null) {
            AlertClass.ErrorAlert("Retrieval Error", "Invalid User", "Could not retrieve user ID for deletion.");
            return;
        }

        Optional<ButtonType> result = AlertClass.ConfirmationAlert(
                "Confirm Deletion",
                "Delete User: " + selectedUserDisplay,
                "Are you sure you want to delete this user? This action cannot be undone."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = userService.deleteUser(userIdToDelete);
                if (success) {
                    AlertClass.InformationAlert("Success", "User Deleted", "User '" + selectedUserDisplay + "' has been deleted.");
                    clearEditUserFields();
                    loadUsersForEditDelete();
                    loadStudentsForChoiceBox();
                    loadWaliKelasForChoiceBox();
                    loadGuruForChoiceBox();
                    loadClassesForChoiceBox();
                    loadAllUsersToTable(filterRoleChoiceBox.getValue(), filterNameField.getText());
                    loadSubjectAssignments();
                } else {
                    AlertClass.ErrorAlert("Failed", "User Not Deleted", "Failed to delete user. User ID might not exist or there are other dependencies.");
                }
            } catch (SQLException e) {
                AlertClass.ErrorAlert("Database Error", "Failed to delete user", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadSubjectsForChoiceBox() {
        subjectData.clear();
        scheduleSubjectChoiceBox.getItems().clear();
        assignTeacherSubjectChoiceBox.getItems().clear();
        try (ResultSet rs = subjectService.getAllSubjects()) {
            while (rs.next()) {
                int mapelId = rs.getInt("mapel_id");
                String namaMapel = rs.getString("nama_mapel");
                subjectData.add(new Pair<>(namaMapel, mapelId));
                scheduleSubjectChoiceBox.getItems().add(namaMapel);
                assignTeacherSubjectChoiceBox.getItems().add(namaMapel);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load subjects", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadClassesForChoiceBox() {
        classData.clear();
        scheduleClassChoiceBox.getItems().clear();
        studentClassFilterChoiceBox.getItems().clear();
        assignTeacherClassChoiceBox.getItems().clear();

        try (ResultSet rs = classService.getAllClasses()) {
            while (rs.next()) {
                int kelasId = rs.getInt("kelas_id");
                String namaKelas = rs.getString("nama_kelas");
                String waliId = rs.getString("wali_id");
                String waliName = rs.getString("wali_name");
                String display = namaKelas + " (Wali: " + waliName + ")";
                classData.add(new Pair<>(display, kelasId + "-" + waliId));
                scheduleClassChoiceBox.getItems().add(display);
                studentClassFilterChoiceBox.getItems().add(display);
                assignTeacherClassChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load classes", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStudentsForChoiceBox() {
        studentData.clear();
        assignStudentToClassChoiceBox.getItems().clear();
        try (ResultSet rs = userService.getStudents()) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String nama = rs.getString("nama");
                String nisNip = rs.getString("NIS_NIP");
                String display = nama + " (NIS/NIP: " + nisNip + ")";
                studentData.add(new Pair<>(display, userId));
                assignStudentToClassChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load students", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadGuruForChoiceBox() {
        guruData.clear();
        assignTeacherGuruChoiceBox.getItems().clear();
        try (ResultSet rs = userService.getGuru()) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String nama = rs.getString("nama");
                String display = nama + " (" + userId + ")";
                guruData.add(new Pair<>(display, userId));
                assignTeacherGuruChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load teachers (Guru)", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddSchedule() {
        String hari = scheduleDayChoiceBox.getValue();
        String jamMulai = scheduleStartTimeField.getText();
        String jamSelesai = scheduleEndTimeField.getText();
        String selectedSubjectDisplay = scheduleSubjectChoiceBox.getValue();
        String selectedClassDisplay = scheduleClassChoiceBox.getValue();

        if (hari == null || hari.isEmpty() || jamMulai.isEmpty() || jamSelesai.isEmpty() ||
                selectedSubjectDisplay == null || selectedClassDisplay == null) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please fill in all schedule details.");
            return;
        }

        if (!jamMulai.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$") || !jamSelesai.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            AlertClass.WarningAlert("Input Error", "Invalid Time Format", "Please enter time in HH:MM format (e.g., 08:00).");
            return;
        }

        Integer mapelId = subjectData.stream()
                .filter(p -> p.getKey().equals(selectedSubjectDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        String combinedClassId = classData.stream()
                .filter(p -> p.getKey().equals(selectedClassDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        if (mapelId == null || combinedClassId == null) {
            AlertClass.ErrorAlert("Selection Error", "Invalid Selection", "Could not retrieve ID for selected subject or class. Please try again.");
            return;
        }

        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        // This should be handled by a ScheduleService
        try (Connection con = DBS.getConnection()) {
            String sql = "INSERT INTO Jadwal (hari, jam_mulai, jam_selsai, Kelas_Users_user_id, Matpel_mapel_id, Kelas_kelas_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, hari);
            stmt.setString(2, jamMulai);
            stmt.setString(3, jamSelesai);
            stmt.setString(4, waliUserId);
            stmt.setInt(5, mapelId);
            stmt.setInt(6, kelasId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                AlertClass.InformationAlert("Success", "Schedule Added", "New schedule has been added successfully.");
                scheduleDayChoiceBox.getSelectionModel().clearSelection();
                scheduleStartTimeField.clear();
                scheduleEndTimeField.clear();
                scheduleSubjectChoiceBox.getSelectionModel().clearSelection();
                scheduleClassChoiceBox.getSelectionModel().clearSelection();
            } else {
                AlertClass.ErrorAlert("Failed", "Schedule Not Added", "Failed to add schedule to the database.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to add schedule", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddSubject() {
        String namaMapel = newSubjectNameField.getText();
        String category = (String) newCategoryChoiceBox.getValue();

        if (namaMapel.isEmpty() || category.isEmpty()) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please fill in both subject name and category.");
            return;
        }

        try {
            boolean success = subjectService.addSubject(namaMapel, category);
            if (success) {
                AlertClass.InformationAlert("Success", "Subject Added", "New subject '" + namaMapel + "' has been added successfully.");
                newSubjectNameField.clear();
                newSubjectNameField.clear();
                loadSubjectsForChoiceBox();
                loadSubjectAssignments();
            } else {
                AlertClass.ErrorAlert("Failed", "Subject Not Added", "Failed to add subject to the database.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to add subject", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleAssignTeacherToSubject() {
        String selectedSubjectDisplay = assignTeacherSubjectChoiceBox.getValue();
        String selectedClassDisplay = assignTeacherClassChoiceBox.getValue();
        String selectedGuruDisplay = assignTeacherGuruChoiceBox.getValue();

        if (selectedSubjectDisplay == null || selectedClassDisplay == null || selectedGuruDisplay == null) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please select a subject, class, and teacher.");
            return;
        }

        Integer mapelId = subjectData.stream()
                .filter(p -> p.getKey().equals(selectedSubjectDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        String combinedClassId = classData.stream()
                .filter(p -> p.getKey().equals(selectedClassDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        String guruUserId = guruData.stream()
                .filter(p -> p.getKey().equals(selectedGuruDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        if (mapelId == null || combinedClassId == null || guruUserId == null) {
            AlertClass.ErrorAlert("Selection Error", "Invalid Selection", "Could not retrieve IDs for selected subject, class, or teacher.");
            return;
        }

        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliKelasId = ids[1];

        // This should be handled by an EnrollmentService
        try (Connection con = DBS.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM Detail_Pengajar WHERE Users_user_id = ? AND Matpel_mapel_id = ? AND Kelas_Users_user_id = ? AND Kelas_kelas_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkSql);
            checkStmt.setString(1, guruUserId);
            checkStmt.setInt(2, mapelId);
            checkStmt.setString(3, waliKelasId);
            checkStmt.setInt(4, kelasId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                AlertClass.WarningAlert("Duplicate Assignment", "Teacher Already Assigned", "This teacher is already assigned to this subject in this class.");
                return;
            }

            String sql = "INSERT INTO Detail_Pengajar (Users_user_id, Matpel_mapel_id, Kelas_Users_user_id, Kelas_kelas_id) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, guruUserId);
            stmt.setInt(2, mapelId);
            stmt.setString(3, waliKelasId);
            stmt.setInt(4, kelasId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                AlertClass.InformationAlert("Success", "Assignment Added", "Teacher assigned to subject in class successfully.");
                assignTeacherSubjectChoiceBox.getSelectionModel().clearSelection();
                assignTeacherClassChoiceBox.getSelectionModel().clearSelection();
                assignTeacherGuruChoiceBox.getSelectionModel().clearSelection();
                loadSubjectAssignments();
            } else {
                AlertClass.ErrorAlert("Failed", "Assignment Failed", "Failed to assign teacher to subject in class.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to assign teacher", e.getMessage());
            e.printStackTrace();
        }
    }

    private void initSubjectAssignmentTable() {
        assignmentSubjectColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        assignmentClassColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        assignmentTeacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
    }

    @FXML
    void loadSubjectAssignments() {
        ObservableList<SubjectAssignmentEntry> assignmentList = FXCollections.observableArrayList();
        // This should be handled by an EnrollmentService
        String sql = "SELECT m.nama_mapel, k.nama_kelas, u_guru.nama AS guru_name, dp.Users_user_id AS guru_id, dp.Matpel_mapel_id, dp.Kelas_Users_user_id AS kelas_wali_id, dp.Kelas_kelas_id " +
                "FROM Detail_Pengajar dp " +
                "JOIN Matpel m ON dp.Matpel_mapel_id = m.mapel_id " +
                "JOIN Kelas k ON dp.Kelas_kelas_id = k.kelas_id AND dp.Kelas_Users_user_id = k.Users_user_id " +
                "JOIN Users u_guru ON dp.Users_user_id = u_guru.user_id " +
                "ORDER BY m.nama_mapel, k.nama_kelas, u_guru.nama";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                assignmentList.add(new SubjectAssignmentEntry(
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas"),
                        rs.getString("guru_name"),
                        rs.getString("guru_id"),
                        rs.getInt("Matpel_mapel_id"),
                        rs.getString("kelas_wali_id"),
                        rs.getInt("Kelas_kelas_id")
                ));
            }
            subjectAssignmentTable.setItems(assignmentList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load subject assignments", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteSubjectAssignment() {
        SubjectAssignmentEntry selectedAssignment = subjectAssignmentTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) {
            AlertClass.WarningAlert("Selection Error", "No Assignment Selected", "Please select an assignment to delete.");
            return;
        }

        Optional<ButtonType> result = AlertClass.ConfirmationAlert(
                "Confirm Deletion",
                "Delete Subject Assignment",
                "Are you sure you want to delete the assignment of " + selectedAssignment.getTeacherName() +
                        " to " + selectedAssignment.getSubjectName() + " in " + selectedAssignment.getClassName() + "?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // This should be handled by an EnrollmentService
            try (Connection con = DBS.getConnection()) {
                String sql = "DELETE FROM Detail_Pengajar WHERE Users_user_id = ? AND Matpel_mapel_id = ? AND Kelas_Users_user_id = ? AND Kelas_kelas_id = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, selectedAssignment.getTeacherId());
                stmt.setInt(2, selectedAssignment.getSubjectId());
                stmt.setString(3, selectedAssignment.getKelasWaliId());
                stmt.setInt(4, selectedAssignment.getKelasId());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    AlertClass.InformationAlert("Success", "Assignment Deleted", "Subject assignment has been deleted.");
                    loadSubjectAssignments();
                } else {
                    AlertClass.ErrorAlert("Failed", "Deletion Failed", "Failed to delete subject assignment. It might not exist.");
                }
            } catch (SQLException e) {
                AlertClass.ErrorAlert("Database Error", "Failed to delete assignment", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleAssignStudentToClass() {
        String selectedStudentDisplay = assignStudentToClassChoiceBox.getValue();
        String selectedClassDisplay = studentClassFilterChoiceBox.getValue();

        if (selectedStudentDisplay == null || selectedClassDisplay == null) {
            AlertClass.WarningAlert("Selection Error", "Missing Selection", "Please select both a student and a class.");
            return;
        }

        String studentUserId = studentData.stream()
                .filter(p -> p.getKey().equals(selectedStudentDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        String combinedClassId = classData.stream()
                .filter(p -> p.getKey().equals(selectedClassDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        if (studentUserId == null || combinedClassId == null) {
            AlertClass.ErrorAlert("Retrieval Error", "Invalid Selection", "Could not retrieve IDs for selected student or class.");
            return;
        }

        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        // This should be handled by an EnrollmentService
        try (Connection con = DBS.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM Student_Class_Enrollment WHERE Users_user_id = ? AND Kelas_kelas_id = ? AND Kelas_Users_user_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkSql);
            checkStmt.setString(1, studentUserId);
            checkStmt.setInt(2, kelasId);
            checkStmt.setString(3, waliUserId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                AlertClass.WarningAlert("Duplicate Entry", "Student Already Assigned", "This student is already assigned to this class.");
                return;
            }

            String insertSql = "INSERT INTO Student_Class_Enrollment (Users_user_id, Kelas_kelas_id, Kelas_Users_user_id) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = con.prepareStatement(insertSql);
            insertStmt.setString(1, studentUserId);
            insertStmt.setInt(2, kelasId);
            insertStmt.setString(3, waliUserId);

            int rowsAffected = insertStmt.executeUpdate();
            if (rowsAffected > 0) {
                AlertClass.InformationAlert("Success", "Student Assigned", "Student assigned to class successfully.");
                assignStudentToClassChoiceBox.getSelectionModel().clearSelection();
                loadStudentsInSelectedClass();
            } else {
                AlertClass.ErrorAlert("Failed", "Assignment Failed", "Failed to assign student to class.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Assignment Failed", e.getMessage());
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

        // This should be handled by an AnnouncementService
        try (Connection con = DBS.getConnection()) {
            String sql = "INSERT INTO Pengumuman (pengumuman, Users_user_id, waktu) VALUES (?, ?, NOW())";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, announcementContent);
            stmt.setString(2, loggedInUserId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                AlertClass.InformationAlert("Success", "Announcement Published", "Announcement has been published successfully.");
                announcementTextArea.clear();
                loadAnnouncements();
            } else {
                AlertClass.ErrorAlert("Failed", "Announcement Not Published", "Failed to publish announcement.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to publish announcement", e.getMessage());
            e.printStackTrace();
        }
    }

    private void initAnnouncementTable() {
        announcementWaktuColumn.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        announcementContentColumn.setCellValueFactory(new PropertyValueFactory<>("pengumuman"));
    }

    private void loadAnnouncements() {
        ObservableList<PengumumanEntry> announcementList = FXCollections.observableArrayList();
        // This should be handled by an AnnouncementService
        String sql = "SELECT pengumuman_id, pengumuman, waktu FROM Pengumuman ORDER BY waktu DESC";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("waktu");
                String waktuFormatted;
                if (timestamp != null) {
                    waktuFormatted = timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } else {
                    waktuFormatted = "N/A";
                }

                announcementList.add(new PengumumanEntry(
                        rs.getInt("pengumuman_id"),
                        waktuFormatted,
                        rs.getString("pengumuman")
                ));
            }
            announcementTable.setItems(announcementList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load announcements", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdateAnnouncement() {
        PengumumanEntry selectedAnnouncement = announcementTable.getSelectionModel().getSelectedItem();
        if (selectedAnnouncement == null) {
            AlertClass.WarningAlert("Selection Error", "No Announcement Selected", "Please select an announcement to update.");
            return;
        }

        String updatedContent = announcementTextArea.getText();
        if (updatedContent.isEmpty()) {
            AlertClass.WarningAlert("Input Error", "Announcement Content Empty", "Please enter content for the announcement.");
            return;
        }

        int pengumumanId = selectedAnnouncement.getPengumumanId();

        // This should be handled by an AnnouncementService
        try (Connection con = DBS.getConnection()) {
            String sql = "UPDATE Pengumuman SET pengumuman = ?, waktu = NOW() WHERE pengumuman_id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, updatedContent);
            stmt.setInt(2, pengumumanId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                AlertClass.InformationAlert("Success", "Announcement Updated", "Announcement updated successfully.");
                announcementTextArea.clear();
                loadAnnouncements();
            } else {
                AlertClass.ErrorAlert("Failed", "Announcement Not Updated", "Failed to update announcement.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to update announcement", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteAnnouncement() {
        PengumumanEntry selectedAnnouncement = announcementTable.getSelectionModel().getSelectedItem();
        if (selectedAnnouncement == null) {
            AlertClass.WarningAlert("Selection Error", "No Announcement Selected", "Please select an announcement to delete.");
            return;
        }

        Optional<ButtonType> result = AlertClass.ConfirmationAlert(
                "Confirm Deletion",
                "Delete Announcement",
                "Are you sure you want to delete this announcement? This action cannot be undone."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            int pengumumanId = selectedAnnouncement.getPengumumanId();

            // This should be handled by an AnnouncementService
            try (Connection con = DBS.getConnection()) {
                String sql = "DELETE FROM Pengumuman WHERE pengumuman_id = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setInt(1, pengumumanId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    AlertClass.InformationAlert("Success", "Announcement Deleted", "Announcement deleted successfully.");
                    announcementTextArea.clear();
                    loadAnnouncements();
                } else {
                    AlertClass.ErrorAlert("Failed", "Announcement Not Deleted", "Failed to delete announcement.");
                }
            } catch (SQLException e) {
                AlertClass.ErrorAlert("Database Error", "Failed to delete announcement", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void initStudentInClassTable() {
        studentNameInClassColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        nisNipInClassColumn.setCellValueFactory(new PropertyValueFactory<>("nisNip"));
        studentUserIdInClassColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    private void loadClassesForStudentFilter() {
        studentClassFilterChoiceBox.getItems().clear();
        for (Pair<String, String> classPair : classData) {
            studentClassFilterChoiceBox.getItems().add(classPair.getKey());
        }
    }

    @FXML
    void handleClassFilterSelection() {
        loadStudentsInSelectedClass();
    }

    private void loadStudentsInSelectedClass() {
        ObservableList<StudentEntry> studentsInClassList = FXCollections.observableArrayList();
        String selectedClassDisplay = studentClassFilterChoiceBox.getValue();

        if (selectedClassDisplay == null || selectedClassDisplay.isEmpty()) {
            studentInClassTableView.setItems(FXCollections.emptyObservableList());
            return;
        }

        int kelasId = -1;
        String waliUserId = null;
        for (Pair<String, String> p : classData) {
            if (p.getKey().equals(selectedClassDisplay)) {
                String[] ids = p.getValue().split("-");
                kelasId = Integer.parseInt(ids[0]);
                waliUserId = ids[1];
                break;
            }
        }

        if (kelasId == -1 || waliUserId == null) {
            AlertClass.ErrorAlert("Retrieval Error", "Invalid Class Selection", "Could not retrieve class ID for filtering.");
            return;
        }

        // This should be handled by an EnrollmentService
        String sql = "SELECT u.user_id, u.nama, u.NIS_NIP FROM Users u " +
                "JOIN Student_Class_Enrollment sce ON u.user_id = sce.Users_user_id " +
                "WHERE sce.Kelas_kelas_id = ? AND sce.Kelas_Users_user_id = ? AND u.Role_role_id = 'S'";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, kelasId);
            stmt.setString(2, waliUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                studentsInClassList.add(new StudentEntry(
                        rs.getString("nama"),
                        rs.getString("NIS_NIP"),
                        rs.getString("user_id")
                ));
            }
            studentInClassTableView.setItems(studentsInClassList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load students in class", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteStudentFromClass() {
        StudentEntry selectedStudent = studentInClassTableView.getSelectionModel().getSelectedItem();
        String selectedClassDisplay = studentClassFilterChoiceBox.getValue();

        if (selectedStudent == null) {
            AlertClass.WarningAlert("Selection Error", "No Student Selected", "Please select a student to remove from the class.");
            return;
        }
        if (selectedClassDisplay == null || selectedClassDisplay.isEmpty()) {
            AlertClass.WarningAlert("Selection Error", "No Class Selected", "Please select a class filter.");
            return;
        }

        Optional<ButtonType> result = AlertClass.ConfirmationAlert(
                "Confirm Removal",
                "Remove Student from Class",
                "Are you sure you want to remove '" + selectedStudent.getStudentName() + "' from '" + selectedClassDisplay + "'?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            int kelasId = -1;
            String waliUserId = null;
            for (Pair<String, String> p : classData) {
                if (p.getKey().equals(selectedClassDisplay)) {
                    String[] ids = p.getValue().split("-");
                    kelasId = Integer.parseInt(ids[0]);
                    waliUserId = ids[1];
                    break;
                }
            }

            if (kelasId == -1 || waliUserId == null) {
                AlertClass.ErrorAlert("Retrieval Error", "Invalid Class Data", "Could not retrieve class details for removal.");
                return;
            }

            // This should be handled by an EnrollmentService
            try (Connection con = DBS.getConnection()) {
                String sql = "DELETE FROM Student_Class_Enrollment WHERE Users_user_id = ? AND Kelas_kelas_id = ? AND Kelas_Users_user_id = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, selectedStudent.getUserId());
                stmt.setInt(2, kelasId);
                stmt.setString(3, waliUserId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    AlertClass.InformationAlert("Success", "Student Removed", "'" + selectedStudent.getStudentName() + "' has been removed from the class.");
                    loadStudentsInSelectedClass();
                } else {
                    AlertClass.ErrorAlert("Failed", "Removal Failed", "Failed to remove student from class. Enrollment might not exist.");
                }
            } catch (SQLException e) {
                AlertClass.ErrorAlert("Database Error", "Removal Failed", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleEditStudentInClass() {
        StudentEntry selectedStudent = studentInClassTableView.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            AlertClass.WarningAlert("Selection Error", "No Student Selected", "Please select a student to edit.");
            return;
        }

        adminTabPane.getSelectionModel().select(1);
        String userDisplayToSelect = selectedStudent.getStudentName() + " (" + selectedStudent.getUserId() + ")";
        editUserChoiceBox.setValue(userDisplayToSelect);
        handleUserSelectionForEditDelete();
    }

    private void loadWaliKelasForChoiceBox() {
        waliKelasData.clear();
        newClassWaliKelasChoiceBox.getItems().clear();

        try (ResultSet rs = userService.getWaliKelas()) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String nama = rs.getString("nama");
                String display = nama + " (" + userId + ")";
                waliKelasData.add(new Pair<>(display, userId));
                newClassWaliKelasChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load Wali Kelas", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSemestersForChoiceBox() {
        semesterData.clear();
        newClassSemesterChoiceBox.getItems().clear();

        // This should be handled by a SemesterService
        String sql = "SELECT semester_id, tahun_ajaran, semester FROM Semester";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int semesterId = rs.getInt("semester_id");
                String tahunAjaran = rs.getString("tahun_ajaran");
                String semesterName = rs.getString("semester");
                String display = tahunAjaran + " - " + semesterName;
                semesterData.add(new Pair<>(display, semesterId));
                newClassSemesterChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load semesters", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadClassesForEdit() {
        editableClassesMap.clear();
        editClassChoiceBox.getItems().clear();

        try (ResultSet rs = classService.getAllClasses()) {
            while (rs.next()) {
                int kelasId = rs.getInt("kelas_id");
                String namaKelas = rs.getString("nama_kelas");
                String waliId = rs.getString("wali_id");
                String waliName = rs.getString("wali_name");
                String display = namaKelas + " (Wali: " + waliName + ")";
                String combinedId = kelasId + "-" + waliId;
                editableClassesMap.put(display, combinedId);
                editClassChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load classes for editing", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleClassSelectionForEdit() {
        String selectedClassDisplay = editClassChoiceBox.getValue();
        if (selectedClassDisplay == null || selectedClassDisplay.isEmpty()) {
            clearClassEditFields();
            return;
        }

        String combinedId = editableClassesMap.get(selectedClassDisplay);
        if (combinedId == null) {
            clearClassEditFields();
            return;
        }
        String[] ids = combinedId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        try {
            Map<String, Object> classDetails = classService.getClassDetails(kelasId, waliUserId);
            if (!classDetails.isEmpty()) {
                newClassNameField.setText((String) classDetails.get("nama_kelas"));
                newClassDescriptionField.setText((String) classDetails.get("keterangan"));

                String currentWaliId = (String) classDetails.get("Users_user_id");
                waliKelasData.stream()
                        .filter(p -> p.getValue().equals(currentWaliId))
                        .findFirst()
                        .ifPresent(p -> newClassWaliKelasChoiceBox.setValue(p.getKey()));

                int currentSemesterId = (Integer) classDetails.get("Semester_semester_id");
                semesterData.stream()
                        .filter(p -> p.getValue().equals(currentSemesterId))
                        .findFirst()
                        .ifPresent(p -> newClassSemesterChoiceBox.setValue(p.getKey()));
            } else {
                clearClassEditFields();
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load class details for editing", e.getMessage());
            e.printStackTrace();
            clearClassEditFields();
        }
    }

    private void clearClassEditFields() {
        newClassNameField.clear();
        newClassDescriptionField.clear();
        newClassWaliKelasChoiceBox.getSelectionModel().clearSelection();
        newClassSemesterChoiceBox.getSelectionModel().clearSelection();
    }

    @FXML
    void handleCreateClass() {
        String className = newClassNameField.getText();
        String classDescription = newClassDescriptionField.getText();
        String selectedWaliKelasDisplay = newClassWaliKelasChoiceBox.getValue();
        String selectedSemesterDisplay = newClassSemesterChoiceBox.getValue();

        if (className.isEmpty() || classDescription.isEmpty() || selectedWaliKelasDisplay == null || selectedSemesterDisplay == null) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please fill in all class details.");
            return;
        }

        String waliKelasUserId = waliKelasData.stream()
                .filter(p -> p.getKey().equals(selectedWaliKelasDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        Integer semesterId = semesterData.stream()
                .filter(p -> p.getKey().equals(selectedSemesterDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        if (waliKelasUserId == null || semesterId == null) {
            AlertClass.ErrorAlert("Selection Error", "Invalid Selection", "Could not retrieve IDs for selected Wali Kelas or Semester.");
            return;
        }

        try {
            int newKelasId = classService.createClass(className, classDescription, waliKelasUserId, semesterId);
            if (newKelasId != -1) {
                AlertClass.InformationAlert("Success", "Class Created", "New class '" + className + "' (ID: " + newKelasId + ") has been created successfully with Wali Kelas: " + selectedWaliKelasDisplay);
                newClassNameField.clear();
                newClassDescriptionField.clear();
                newClassWaliKelasChoiceBox.getSelectionModel().clearSelection();
                newClassSemesterChoiceBox.getSelectionModel().clearSelection();
                loadClassesForChoiceBox();
                loadClassesForEdit();
            } else {
                AlertClass.ErrorAlert("Failed", "Class Not Created", "Failed to create class in the database.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to create class", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdateClass() {
        String selectedClassDisplay = editClassChoiceBox.getValue();
        if (selectedClassDisplay == null || selectedClassDisplay.isEmpty()) {
            AlertClass.WarningAlert("Selection Error", "No Class Selected", "Please select a class to update.");
            return;
        }

        String className = newClassNameField.getText();
        String classDescription = newClassDescriptionField.getText();
        String selectedWaliKelasDisplay = newClassWaliKelasChoiceBox.getValue();
        String selectedSemesterDisplay = newClassSemesterChoiceBox.getValue();

        if (className.isEmpty() || classDescription.isEmpty() || selectedWaliKelasDisplay == null || selectedSemesterDisplay == null) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please fill in all class details for update.");
            return;
        }

        String waliKelasUserId = waliKelasData.stream()
                .filter(p -> p.getKey().equals(selectedWaliKelasDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        Integer semesterId = semesterData.stream()
                .filter(p -> p.getKey().equals(selectedSemesterDisplay))
                .map(Pair::getValue)
                .findFirst()
                .orElse(null);

        if (waliKelasUserId == null || semesterId == null) {
            AlertClass.ErrorAlert("Selection Error", "Invalid Selection", "Could not retrieve IDs for selected Wali Kelas or Semester.");
            return;
        }

        String combinedId = editableClassesMap.get(selectedClassDisplay);
        if (combinedId == null) {
            AlertClass.ErrorAlert("Retrieval Error", "Invalid Class Selection", "Could not retrieve class ID for update.");
            return;
        }
        String[] ids = combinedId.split("-");
        int kelasIdToUpdate = Integer.parseInt(ids[0]);
        String currentWaliIdForKelasPK = ids[1];

        try {
            boolean success = classService.updateClass(className, classDescription, waliKelasUserId, semesterId, kelasIdToUpdate, currentWaliIdForKelasPK);
            if (success) {
                AlertClass.InformationAlert("Success", "Class Updated", "Class '" + className + "' has been updated successfully.");
                clearClassEditFields();
                loadClassesForEdit();
                loadClassesForChoiceBox();
                loadSubjectAssignments();
            } else {
                AlertClass.ErrorAlert("Failed", "Class Not Updated", "Failed to update class. Class ID or Wali Kelas might not exist, or there are unhandled dependencies (e.g., if you tried to change the Wali Kelas and FKs prevent it).");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to update class", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteClass() {
        String selectedClassDisplay = editClassChoiceBox.getValue();
        if (selectedClassDisplay == null || selectedClassDisplay.isEmpty()) {
            AlertClass.WarningAlert("Selection Error", "No Class Selected", "Please select a class to delete.");
            return;
        }

        Optional<ButtonType> result = AlertClass.ConfirmationAlert(
                "Confirm Deletion",
                "Delete Class: " + selectedClassDisplay,
                "Are you sure you want to delete this class? This will also delete all associated schedules, materials, tasks, exams, and student enrollments for this class. This action cannot be undone."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String combinedId = editableClassesMap.get(selectedClassDisplay);
            if (combinedId == null) {
                AlertClass.ErrorAlert("Retrieval Error", "Invalid Class Selection", "Could not retrieve class ID for deletion.");
                return;
            }
            String[] ids = combinedId.split("-");
            int kelasIdToDelete = Integer.parseInt(ids[0]);
            String waliUserIdToDelete = ids[1];

            try {
                boolean success = classService.deleteClass(kelasIdToDelete, waliUserIdToDelete);
                if (success) {
                    AlertClass.InformationAlert("Success", "Class Deleted", "Class '" + selectedClassDisplay + "' and all its associated data have been deleted.");
                    clearClassEditFields();
                    loadClassesForEdit();
                    loadClassesForChoiceBox();
                    loadSubjectAssignments();
                } else {
                    AlertClass.ErrorAlert("Failed", "Class Not Deleted", "Failed to delete class. Class might not exist or there are remaining dependencies.");
                }
            } catch (SQLException e) {
                AlertClass.ErrorAlert("Database Error", "Failed to delete class", e.getMessage());
                e.printStackTrace();
            }
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

    private static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return key.toString();
        }
    }

    public static class StudentEntry {
        private final StringProperty studentName;
        private final StringProperty nisNip;
        private final StringProperty userId;

        public StudentEntry(String studentName, String nisNip, String userId) {
            this.studentName = new SimpleStringProperty(studentName);
            this.nisNip = new SimpleStringProperty(nisNip);
            this.userId = new SimpleStringProperty(userId);
        }

        public String getStudentName() {
            return studentName.get();
        }

        public StringProperty studentNameProperty() {
            return studentName;
        }

        public String getNisNip() {
            return nisNip.get();
        }

        public StringProperty nisNipProperty() {
            return nisNip;
        }

        public String getUserId() {
            return userId.get();
        }

        public StringProperty userIdProperty() {
            return userId;
        }
    }

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

    public static class SubjectAssignmentEntry {
        private final StringProperty subjectName;
        private final StringProperty className;
        private final StringProperty teacherName;
        private final String teacherId;
        private final int subjectId;
        private final String kelasWaliId;
        private final int kelasId;

        public SubjectAssignmentEntry(String subjectName, String className, String teacherName,
                                      String teacherId, int subjectId, String kelasWaliId, int kelasId) {
            this.subjectName = new SimpleStringProperty(subjectName);
            this.className = new SimpleStringProperty(className);
            this.teacherName = new SimpleStringProperty(teacherName);
            this.teacherId = teacherId;
            this.subjectId = subjectId;
            this.kelasWaliId = kelasWaliId;
            this.kelasId = kelasId;
        }

        public String getSubjectName() { return subjectName.get(); }
        public StringProperty subjectNameProperty() { return subjectName; }
        public String getClassName() { return className.get(); }
        public StringProperty classNameProperty() { return className; }
        public String getTeacherName() { return teacherName.get(); }
        public StringProperty teacherNameProperty() { return teacherName; }

        public String getTeacherId() { return teacherId; }
        public int getSubjectId() { return subjectId; }
        public String getKelasWaliId() { return kelasWaliId; }
        public int getKelasId() { return kelasId; }
    }
}