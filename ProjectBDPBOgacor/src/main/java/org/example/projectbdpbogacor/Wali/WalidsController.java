// ProjectBDPBOgacor/src/main/java/org/example/projectbdpbogacor/Wali/WalidsController.java
package org.example.projectbdpbogacor.Wali;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.projectbdpbogacor.Aset.AlertClass;
import org.example.projectbdpbogacor.DBSource.DBS; // Still needed for some direct DB ops
import org.example.projectbdpbogacor.Controller.BaseController; // Import BaseController
import org.example.projectbdpbogacor.Service.ClassService; // Import ClassService
import org.example.projectbdpbogacor.Service.EnrollmentService; // Import EnrollmentService
import org.example.projectbdpbogacor.Service.SemesterService; // Import SemesterService
import org.example.projectbdpbogacor.Service.UserService; // Import UserService
import org.example.projectbdpbogacor.Service.GradeService; // Import GradeService
import org.example.projectbdpbogacor.Service.AttendanceService; // Import AttendanceService
import org.example.projectbdpbogacor.model.AbsensiWaliEntry;
import org.example.projectbdpbogacor.model.NilaiEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.awt.Desktop;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;


// Extend BaseController
public class WalidsController extends BaseController {

    private final ClassService classService = new ClassService();
    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final SemesterService semesterService = new SemesterService();
    private final UserService userService = new UserService();
    private final GradeService gradeService = new GradeService();
    private final AttendanceService attendanceService = new AttendanceService();


    @FXML
    private Label welcomeUserLabel;
    @FXML
    private TabPane waliTabPane;

    // Student Attendance
    @FXML
    private ChoiceBox<String> attendanceClassChoiceBox;
    @FXML
    private ChoiceBox<String> attendanceStudentChoiceBox;
    @FXML
    private TableView<AbsensiWaliEntry> absensiTable;
    @FXML
    private TableColumn<AbsensiWaliEntry, String> studentNameAbsensiColumn;
    @FXML
    private TableColumn<AbsensiWaliEntry, String> tanggalAbsensiColumn;
    @FXML
    private TableColumn<AbsensiWaliEntry, String> statusAbsensiColumn;
    @FXML
    private TableColumn<AbsensiWaliEntry, String> mapelAbsensiColumn;
    @FXML
    private TableColumn<AbsensiWaliEntry, String> kelasAbsensiColumn;
    @FXML
    private TableColumn<AbsensiWaliEntry, String> jamMulaiAbsensiColumn;
    @FXML
    private TableColumn<AbsensiWaliEntry, String> jamSelesaiAbsensiColumn;

    // Print Report Card
    @FXML
    private ChoiceBox<String> raporClassChoiceBox;
    @FXML
    private ChoiceBox<String> raporStudentChoiceBox;
    @FXML
    private ChoiceBox<String> raporSemesterChoiceBox;
    @FXML
    private TableView<NilaiEntry> nilaiUjianTable;
    @FXML
    private TableColumn<NilaiEntry, String> jenisNilaiColumn;
    @FXML
    private TableColumn<NilaiEntry, String> namaMapelNilaiColumn;
    @FXML
    private TableColumn<NilaiEntry, Integer> nilaiColumn;

    // Maps to store IDs corresponding to displayed names in ChoiceBoxes
    private Map<String, String> classesMap = new HashMap<>(); // Display -> Kelas_Users_user_id-kelas_id
    private Map<String, String> studentsMap = new HashMap<>(); // Display -> user_id
    private Map<String, Integer> semestersMap = new HashMap<>(); // Display -> semester_id

    @FXML
    void initialize() {
        setLoggedInUserId(org.example.projectbdpbogacor.HelloApplication.getInstance().getLoggedInUserId());
        loadUserName(welcomeUserLabel);

        // Initialize TableView columns
        initAbsensiTable();
        initNilaiUjianTable();

        // Load initial data for ChoiceBoxes
        loadClassesForWaliKelas();
        loadSemesters();

        // Add listeners to tab and choice boxes
        attendanceClassChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleClassSelectionForAttendance());
        attendanceStudentChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadAbsensiData());

        raporClassChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleClassSelectionForRapor());
        raporStudentChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadNilaiData());
        raporSemesterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadNilaiData());

        // Add listeners to tabs to load data when selected
        waliTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                if (newTab.getText().equals("Student Attendance")) {
                    loadClassesForWaliKelas();
                    if (!attendanceClassChoiceBox.getItems().isEmpty()) {
                        attendanceClassChoiceBox.setValue(attendanceClassChoiceBox.getItems().get(0));
                    }
                } else if (newTab.getText().equals("Print Report Card")) {
                    loadClassesForWaliKelas();
                    loadSemesters();
                    if (!raporClassChoiceBox.getItems().isEmpty()) {
                        raporClassChoiceBox.setValue(raporClassChoiceBox.getItems().get(0));
                    }
                    if (!raporSemesterChoiceBox.getItems().isEmpty()) {
                        raporSemesterChoiceBox.setValue(raporSemesterChoiceBox.getItems().get(0));
                    }
                }
            }
        });

        // Load initial data for the currently selected tab (which is usually the first one)
        if (waliTabPane.getSelectionModel().getSelectedItem().getText().equals("Student Attendance")) {
            loadClassesForWaliKelas();
            if (!attendanceClassChoiceBox.getItems().isEmpty()) {
                attendanceClassChoiceBox.setValue(attendanceClassChoiceBox.getItems().get(0));
            }
        } else if (waliTabPane.getSelectionModel().getSelectedItem().getText().equals("Print Report Card")) {
            loadClassesForWaliKelas();
            loadSemesters();
            if (!raporClassChoiceBox.getItems().isEmpty()) {
                raporClassChoiceBox.setValue(raporClassChoiceBox.getItems().get(0));
            }
            if (!raporSemesterChoiceBox.getItems().isEmpty()) {
                raporSemesterChoiceBox.setValue(raporSemesterChoiceBox.getItems().get(0));
            }
        }
    }

    // --- Common Helper Methods for ChoiceBoxes ---
    private void loadClassesForWaliKelas() {
        classesMap.clear();
        attendanceClassChoiceBox.getItems().clear();
        raporClassChoiceBox.getItems().clear();

        try (ResultSet rs = enrollmentService.getClassesByWaliKelas(loggedInUserId)) {
            while (rs.next()) {
                int kelasId = rs.getInt("kelas_id");
                String namaKelas = rs.getString("nama_kelas");
                String waliId = rs.getString("wali_id");
                String combinedId = waliId + "-" + kelasId; // Store as "WaliID-KelasID" as Kelas has a composite primary key
                classesMap.put(namaKelas, combinedId);
                attendanceClassChoiceBox.getItems().add(namaKelas);
                raporClassChoiceBox.getItems().add(namaKelas);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load classes", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStudentsInClass(String selectedClassDisplay, ChoiceBox<String> studentChoiceBoxToPopulate) {
        studentChoiceBoxToPopulate.getItems().clear();
        studentsMap.clear();

        if (selectedClassDisplay == null || selectedClassDisplay.isEmpty()) {
            return;
        }

        String combinedId = classesMap.get(selectedClassDisplay);
        if (combinedId == null) {
            return;
        }
        String[] ids = combinedId.split("-");
        String waliIdFromKelas = ids[0];
        int kelasId = Integer.parseInt(ids[1]);

        try (ResultSet rs = enrollmentService.getStudentsInClass(kelasId, waliIdFromKelas)) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String nama = rs.getString("nama");
                String nisNip = rs.getString("NIS_NIP");
                String display = nama + " (NIS: " + nisNip + ")";
                studentsMap.put(display, userId);
                studentChoiceBoxToPopulate.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load students", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSemesters() {
        semestersMap.clear();
        raporSemesterChoiceBox.getItems().clear();

        try (ResultSet rs = semesterService.getAllSemesters()) {
            while (rs.next()) {
                int semesterId = rs.getInt("semester_id");
                String tahunAjaran = rs.getString("tahun_ajaran");
                String semesterName = rs.getString("semester");
                String display = tahunAjaran + " - " + semesterName;
                semestersMap.put(display, semesterId);
                raporSemesterChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load semesters", e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Student Attendance Methods ---
    private void initAbsensiTable() {
        studentNameAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        tanggalAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        statusAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        mapelAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        kelasAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
        jamMulaiAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("jamMulai"));
        jamSelesaiAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("jamSelesai"));
    }

    @FXML
    void handleClassSelectionForAttendance() {
        String selectedClassDisplay = attendanceClassChoiceBox.getValue();
        loadStudentsInClass(selectedClassDisplay, attendanceStudentChoiceBox);
        loadAbsensiData();
    }

    @FXML
    void loadAbsensiData() {
        ObservableList<AbsensiWaliEntry> absensiList = FXCollections.observableArrayList();
        String selectedClassDisplay = attendanceClassChoiceBox.getValue();
        String selectedStudentDisplay = attendanceStudentChoiceBox.getValue();

        int kelasId = 0;
        String waliUserId = null;
        if (selectedClassDisplay != null && !selectedClassDisplay.isEmpty()) {
            String combinedClassId = classesMap.get(selectedClassDisplay);
            if (combinedClassId != null) {
                String[] ids = combinedClassId.split("-");
                waliUserId = ids[0]; // Wali ID is first in combinedId
                kelasId = Integer.parseInt(ids[1]);
            }
        }

        String studentUserId = null;
        if (selectedStudentDisplay != null && !selectedStudentDisplay.isEmpty()) {
            studentUserId = studentsMap.get(selectedStudentDisplay);
        }

        try (ResultSet rs = attendanceService.getAttendanceByFilters(loggedInUserId, kelasId, waliUserId, 0, studentUserId, null)) {
            while (rs.next()) {
                absensiList.add(new AbsensiWaliEntry(
                        rs.getString("student_name"), // Need to add student name to service method if not already there
                        rs.getTimestamp("tanggal").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        rs.getString("status"),
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas"),
                        rs.getString("jam_mulai"),
                        rs.getString("jam_selsai")
                ));
            }
            absensiTable.setItems(absensiList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load attendance data", e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Print Report Card Methods ---
    private void initNilaiUjianTable() {
        jenisNilaiColumn.setCellValueFactory(new PropertyValueFactory<>("jenisNilai"));
        namaMapelNilaiColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        nilaiColumn.setCellValueFactory(new PropertyValueFactory<>("nilai"));
    }

    @FXML
    void handleClassSelectionForRapor() {
        String selectedClassDisplay = raporClassChoiceBox.getValue();
        loadStudentsInClass(selectedClassDisplay, raporStudentChoiceBox);
        loadNilaiData();
    }

    @FXML
    void loadNilaiData() {
        ObservableList<NilaiEntry> nilaiList = FXCollections.observableArrayList();
        String selectedStudentDisplay = raporStudentChoiceBox.getValue();
        String selectedSemesterDisplay = raporSemesterChoiceBox.getValue();

        if (selectedStudentDisplay == null || selectedSemesterDisplay == null) {
            nilaiUjianTable.setItems(FXCollections.emptyObservableList());
            return;
        }

        String studentUserId = studentsMap.get(selectedStudentDisplay);
        Integer semesterId = semestersMap.get(selectedSemesterDisplay);

        if (studentUserId == null || semesterId == null) {
            AlertClass.WarningAlert("Selection Error", "Invalid Selection", "Please select a valid student and semester.");
            return;
        }

        try (ResultSet rs = gradeService.getGradesByStudentAndSemester(studentUserId, semesterId)) {
            while (rs.next()) {
                nilaiList.add(new NilaiEntry(
                        rs.getString("jenis_nilai"),
                        rs.getString("nama_mapel"),
                        rs.getInt("nilai")
                ));
            }
            nilaiUjianTable.setItems(nilaiList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load exam scores", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handlePrintReportCard() {
        String selectedClassDisplay = raporClassChoiceBox.getValue();
        String selectedStudentDisplay = raporStudentChoiceBox.getValue();
        String selectedSemesterDisplay = raporSemesterChoiceBox.getValue();

        if (selectedClassDisplay == null || selectedStudentDisplay == null || selectedSemesterDisplay == null) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please select a class, student, and semester to print the report card.");
            return;
        }

        String studentUserId = studentsMap.get(selectedStudentDisplay);
        Integer semesterId = semestersMap.get(selectedSemesterDisplay);

        if (studentUserId == null || semesterId == null) {
            AlertClass.ErrorAlert("Selection Error", "Invalid Selection", "Could not retrieve IDs for selected student or semester.");
            return;
        }

        try {
            // 1. Fetch student biodata
            Map<String, String> studentBiodata = userService.getUserDetails(studentUserId); // Use UserService

            // 2. Fetch grades (already loaded in nilaiUjianTable, but we can re-fetch for robustness)
            ObservableList<NilaiEntry> grades = nilaiUjianTable.getItems();

            // 3. Fetch attendance summary
            Map<String, Integer> attendanceSummary = getAttendanceSummary(studentUserId, semesterId);

            // 4. Generate HTML content
            String htmlContent = generateReportCardHtml(studentBiodata, grades, attendanceSummary, selectedClassDisplay, selectedSemesterDisplay);

            // 5. Save to a temporary HTML file
            File raporDir = new File("Rapor");
            if (!raporDir.exists()) {
                raporDir.mkdirs();
            }

            File outputFile = new File(raporDir, "rapor_" + studentUserId + "_" + semesterId + ".html");
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(htmlContent);
            }

            // 6. Open the file in the default browser
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(outputFile.toURI());
                AlertClass.InformationAlert("Report Card Generated", "Report card saved and opened.", "The report card for " + studentBiodata.get("nama") + " has been generated and opened in your browser.");
            } else {
                AlertClass.InformationAlert("Report Card Generated", "Report card saved.", "The report card for " + studentBiodata.get("nama") + " has been generated at: " + outputFile.getAbsolutePath() + "\nYou can open it manually.");
            }

        } catch (IOException e) {
            AlertClass.ErrorAlert("File Error", "Failed to generate report card file", e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to retrieve data for report card", e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, Integer> getAttendanceSummary(String studentUserId, int semesterId) throws SQLException {
        // Fetch the semester's start date
        LocalDateTime semesterStart = null;
        String semesterName = null;
        String tahunAjaran = null;

        String semesterDateSql = "SELECT tahun_ajaran, semester, tahun FROM Semester WHERE semester_id = ?"; // This should be in SemesterService
        try (Connection con = DBS.getConnection(); // Direct DB access, migrate to SemesterService
             PreparedStatement stmt = con.prepareStatement(semesterDateSql)) {
            stmt.setInt(1, semesterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                semesterStart = rs.getTimestamp("tahun").toLocalDateTime();
                semesterName = rs.getString("semester");
                tahunAjaran = rs.getString("tahun_ajaran");
            } else {
                System.err.println("Semester with ID " + semesterId + " not found.");
                return new HashMap<>(); // Return empty summary
            }
        }

        if (semesterStart == null) {
            return new HashMap<>();
        }

        LocalDateTime semesterEnd;
        if ("Ganjil".equalsIgnoreCase(semesterName)) {
            semesterEnd = LocalDateTime.of(semesterStart.getYear(), 12, 31, 23, 59, 59);
        } else if ("Genap".equalsIgnoreCase(semesterName)) {
            int endYear = semesterStart.getYear();
            if (tahunAjaran != null && tahunAjaran.contains("/")) {
                try {
                    String[] years = tahunAjaran.split("/");
                    if (years.length == 2) {
                        endYear = Integer.parseInt(years[1]);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing tahun_ajaran: " + e.getMessage());
                }
            }
            semesterEnd = LocalDateTime.of(endYear, 6, 30, 23, 59, 59);
        } else {
            semesterEnd = semesterStart.plusMonths(6).minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        }

        return attendanceService.getAttendanceSummary(studentUserId, semesterStart, semesterEnd); // Use AttendanceService
    }


    private String generateReportCardHtml(Map<String, String> biodata, ObservableList<NilaiEntry> grades, Map<String, Integer> attendance, String className, String semesterName) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Report Card - ").append(biodata.get("nama")).append("</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("        .container { width: 800px; margin: 0 auto; border: 1px solid #ccc; padding: 20px; box-shadow: 2px 2px 8px rgba(0,0,0,0.1); }\n");
        html.append("        h1, h2 { text-align: center; color: #333; }\n");
        html.append("        .info-section, .grades-section, .attendance-section { margin-bottom: 20px; }\n");
        html.append("        .info-section p { margin: 5px 0; }\n");
        html.append("        table { width: 100%; border-collapse: collapse; margin-top: 10px; }\n");
        html.append("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("        th { background-color: #f2f2f2; }\n");
        html.append("        .footer { text-align: right; margin-top: 30px; font-size: 0.9em; color: #555; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <h1>School Report Card</h1>\n");
        html.append("        <h2>").append(semesterName).append("</h2>\n");

        html.append("        <div class=\"info-section\">\n");
        html.append("            <h3>Student Information:</h3>\n");
        html.append("            <p><strong>Name:</strong> ").append(biodata.get("nama")).append("</p>\n");
        html.append("            <p><strong>Student ID (NIS/NIP):</strong> ").append(biodata.get("NIS_NIP")).append("</p>\n");
        html.append("            <p><strong>Class:</strong> ").append(className).append("</p>\n");
        html.append("            <p><strong>Gender:</strong> ").append(biodata.get("gender").equals("L") ? "Laki-laki" : "Perempuan").append("</p>\n");
        html.append("            <p><strong>Email:</strong> ").append(biodata.get("email")).append("</p>\n");
        html.append("            <p><strong>Phone:</strong> ").append(biodata.get("nomer_hp")).append("</p>\n");
        html.append("            <p><strong>Address:</strong> ").append(biodata.get("alamat")).append("</p>\n");
        html.append("        </div>\n");

        html.append("        <div class=\"grades-section\">\n");
        html.append("            <h3>Academic Grades:</h3>\n");
        html.append("            <table>\n");
        html.append("                <thead>\n");
        html.append("                    <tr>\n");
        html.append("                        <th>Subject</th>\n");
        html.append("                        <th>Type of Grade</th>\n");
        html.append("                        <th>Score</th>\n");
        html.append("                    </tr>\n");
        html.append("                </thead>\n");
        html.append("                <tbody>\n");
        if (grades != null && !grades.isEmpty()) {
            for (NilaiEntry grade : grades) {
                html.append("                    <tr>\n");
                html.append("                        <td>").append(grade.getNamaMapel()).append("</td>\n");
                html.append("                        <td>").append(grade.getJenisNilai()).append("</td>\n");
                html.append("                        <td>").append(grade.getNilai()).append("</td>\n");
                html.append("                    </tr>\n");
            }
        } else {
            html.append("                    <tr><td colspan=\"3\">No grades available for this semester.</td></tr>\n");
        }
        html.append("                </tbody>\n");
        html.append("            </table>\n");
        html.append("        </div>\n");

        html.append("        <div class=\"attendance-section\">\n");
        html.append("            <h3>Attendance Summary:</h3>\n");
        html.append("            <p><strong>Hadir (Present):</strong> ").append(attendance.getOrDefault("Hadir", 0)).append(" days</p>\n");
        html.append("            <p><strong>Alpha (Absent without leave):</strong> ").append(attendance.getOrDefault("Alpha", 0)).append(" days</p>\n");
        html.append("            <p><strong>Ijin (Excused):</strong> ").append(attendance.getOrDefault("Ijin", 0)).append(" days</p>\n");
        html.append("        </div>\n");

        html.append("        <div class=\"footer\">\n");
        html.append("            <p>Generated on: ").append(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("</p>\n");
        html.append("            <p>Wali Kelas: ").append(welcomeUserLabel.getText().replace("Welcome, ", "")).append("</p>\n");
        html.append("        </div>\n");

        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }
}