// ProjectBDPBOgacor/src/main/java/org/example/projectbdpbogacor/Guru/GurudsController.java
package org.example.projectbdpbogacor.Guru;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.projectbdpbogacor.Aset.AlertClass;
import org.example.projectbdpbogacor.DBSource.DBS; // Needed for direct DB access if not all moved to service
import org.example.projectbdpbogacor.Controller.BaseController; // Import BaseController
import org.example.projectbdpbogacor.Service.*; // Import all services
import org.example.projectbdpbogacor.model.JadwalEntry;
import org.example.projectbdpbogacor.model.NilaiEntry;
import org.example.projectbdpbogacor.model.TugasEntry;
import org.example.projectbdpbogacor.model.MateriEntry;
import org.example.projectbdpbogacor.model.UjianEntry;
import org.example.projectbdpbogacor.model.PengumumanEntry;
import org.example.projectbdpbogacor.model.AbsensiEntry;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

import java.sql.Connection;
import java.sql.Date; // For java.sql.Date
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Extend BaseController
public class GurudsController extends BaseController {

    private final UserService userService = new UserService();
    private final ClassService classService = new ClassService();
    private final SubjectService subjectService = new SubjectService();
    private final ScheduleService scheduleService = new ScheduleService(); // Instantiate ScheduleService
    private final AssignmentService assignmentService = new AssignmentService(); // Instantiate AssignmentService
    private final MaterialService materialService = new MaterialService(); // Instantiate MaterialService
    private final ExamService examService = new ExamService(); // Instantiate ExamService
    private final AnnouncementService announcementService = new AnnouncementService(); // Instantiate AnnouncementService
    private final SemesterService semesterService = new SemesterService(); // Instantiate SemesterService
    private final EnrollmentService enrollmentService = new EnrollmentService(); // Instantiate EnrollmentService
    private final GradeService gradeService = new GradeService(); // Instantiate GradeService
    private final AttendanceService attendanceService = new AttendanceService(); // Instantiate AttendanceService


    @FXML
    private Label welcomeUserLabel;
    @FXML
    private TabPane guruTabPane;

    // Class Schedule
    @FXML
    private ChoiceBox<String> scheduleClassChoiceBox;
    @FXML
    private ChoiceBox<String> scheduleSubjectChoiceBox;
    @FXML
    private TableView<JadwalEntry> jadwalKelasTable;
    @FXML
    private TableColumn<JadwalEntry, String> hariColumn;
    @FXML
    private TableColumn<JadwalEntry, String> jamMulaiColumn;
    @FXML
    private TableColumn<JadwalEntry, String> jamSelesaiColumn;
    @FXML
    private TableColumn<JadwalEntry, String> namaMapelColumn;
    @FXML
    private TableColumn<JadwalEntry, String> namaKelasJadwalColumn;

    // Input Grades
    @FXML
    private ChoiceBox<String> gradeClassChoiceBox;
    @FXML
    private ChoiceBox<String> gradeSubjectChoiceBox;
    @FXML
    private ChoiceBox<String> gradeStudentChoiceBox;
    @FXML
    private ChoiceBox<String> gradeSemesterChoiceBox;
    @FXML
    private ChoiceBox<String> gradeTypeChoiseBox;
    @FXML
    private TextField scoreField;
    @FXML
    private TableView<NilaiEntry> existingGradesTable;
    @FXML
    private TableColumn<NilaiEntry, String> existingJenisNilaiColumn;
    @FXML
    private TableColumn<NilaiEntry, String> existingNamaMapelColumn;
    @FXML
    private TableColumn<NilaiEntry, Integer> existingNilaiColumn;

    // Manage Assignments
    @FXML
    private ChoiceBox<String> tugasClassChoiceBox;
    @FXML
    private ChoiceBox<String> tugasSubjectChoiceBox;
    @FXML
    private TextArea tugasKeteranganArea;
    @FXML
    private DatePicker tugasDeadlinePicker;
    @FXML
    private Button addTugasButton;
    @FXML
    private TableView<TugasEntry> tugasTable;
    @FXML
    private TableColumn<TugasEntry, String> tugasKeteranganColumn;
    @FXML
    private TableColumn<TugasEntry, String> tugasDeadlineColumn;
    @FXML
    private TableColumn<TugasEntry, String> tugasTanggalRilisColumn;
    @FXML
    private TableColumn<TugasEntry, String> tugasMapelColumn;
    @FXML
    private TableColumn<TugasEntry, String> tugasKelasColumn;
    @FXML
    private MenuItem deleteTugasButton;

    // Manage Materials
    @FXML
    private ChoiceBox<String> materiClassChoiceBox;
    @FXML
    private ChoiceBox<String> materiSubjectChoiceBox;
    @FXML
    private TextField materiNamaMateriField;
    @FXML
    private Button addMateriButton;
    @FXML
    private TableView<MateriEntry> materiTable;
    @FXML
    private TableColumn<MateriEntry, String> materiNamaMateriColumn;
    @FXML
    private TableColumn<MateriEntry, String> materiMapelColumn;
    @FXML
    private TableColumn<MateriEntry, String> materiKelasColumn;
    @FXML
    private MenuItem deleteMateriButton;

    // Manage Exams
    @FXML
    private ChoiceBox<String> ujianClassChoiceBox;
    @FXML
    private ChoiceBox<String> ujianSubjectChoiceBox;
    @FXML
    private ChoiceBox<String> ujianJenisUjianChoiceBox;
    @FXML
    private DatePicker ujianTanggalPicker;
    @FXML
    private Button addUjianButton;
    @FXML
    private TableView<UjianEntry> ujianTable;
    @FXML
    private TableColumn<UjianEntry, String> ujianJenisColumn;
    @FXML
    private TableColumn<UjianEntry, String> ujianTanggalColumn;
    @FXML
    private TableColumn<UjianEntry, String> ujianMapelColumn;
    @FXML
    private TableColumn<UjianEntry, String> ujianKelasColumn;
    @FXML
    private MenuItem deleteUjianButton;

    // Announcements
    @FXML
    private TextArea announcementTextArea;
    @FXML
    private Button createAnnouncementButton;
    @FXML
    private TableView<PengumumanEntry> guruAnnouncementTable;
    @FXML
    private TableColumn<PengumumanEntry, String> guruAnnouncementWaktuColumn;
    @FXML
    private TableColumn<PengumumanEntry, String> guruAnnouncementContentColumn;
    @FXML
    private Button guruUpdateAnnouncementButton;
    @FXML
    private Button guruDeleteAnnouncementButton;

    // Manage Absensi Tab
    @FXML
    private ChoiceBox<String> absensiClassChoiceBox;
    @FXML
    private ChoiceBox<String> absensiSubjectChoiceBox;
    @FXML
    private ChoiceBox<String> absensiStudentChoiceBox;
    @FXML
    private DatePicker absensiDatePicker;
    @FXML
    private ChoiceBox<String> absensiStatusChoiceBox;
    @FXML
    private Button recordAbsensiButton;
    @FXML
    private TableView<AbsensiEntry> absensiTable;
    @FXML
    private TableColumn<AbsensiEntry, String> absensiTanggalColumn;
    @FXML
    private TableColumn<AbsensiEntry, String> absensiStatusColumn;
    @FXML
    private TableColumn<AbsensiEntry, String> absensiMapelColumn;
    @FXML
    private TableColumn<AbsensiEntry, String> absensiKelasColumn;
    @FXML
    private TableColumn<AbsensiEntry, String> absensiJamMulaiColumn;
    @FXML
    private TableColumn<AbsensiEntry, String> absensiJamSelesaiColumn;


    // Maps to store IDs corresponding to displayed names in ChoiceBoxes
    private Map<String, String> classesMap = new HashMap<>(); // Display -> Kelas_kelas_id-Kelas_Users_user_id
    private Map<String, Integer> subjectsMap = new HashMap<>(); // Display -> mapel_id
    private Map<String, String> studentsMap = new HashMap<>(); // Display -> user_id
    private Map<String, Integer> semestersMap = new HashMap<>(); // Display -> semester_id


    @FXML
    void initialize() {
        setLoggedInUserId(org.example.projectbdpbogacor.HelloApplication.getInstance().getLoggedInUserId());
        loadUserName(welcomeUserLabel);

        // Initialize TableView columns
        initJadwalKelasTable();
        initExistingGradesTable();
        initTugasTable();
        initMateriTable();
        initUjianTable();
        initGuruAnnouncementTable();
        initAbsensiTable();

        // Load initial data for ChoiceBoxes
        loadClassesTaughtByGuru();
        loadSubjectsTaughtByGuru();
        loadSemesters();

        populateCommonChoiceBoxes();

        // Add listeners for grade input
        gradeClassChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleGradeClassSelection());
        gradeSubjectChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleGradeSubjectSelection());
        gradeStudentChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadExistingGrades());
        gradeSemesterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadExistingGrades());

        ujianJenisUjianChoiceBox.getItems().addAll("UTS", "UAS", "Harian");
        ujianJenisUjianChoiceBox.setValue("UTS");

        gradeTypeChoiseBox.getItems().addAll("UTS", "UAS", "TUGAS 1", "TUGAS 2", "TUGAS 3", "TUGAS 4");
        gradeTypeChoiseBox.setValue("Tugas 1");

        guruAnnouncementTable.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                announcementTextArea.setText(newSelection.getPengumuman());
            } else {
                announcementTextArea.clear();
            }
        });

        absensiClassChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleAbsensiClassSelection();
            loadAbsensiData();
        });
        absensiSubjectChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            loadStudentsForAbsensi(absensiClassChoiceBox.getValue(), newValue);
            loadAbsensiData();
        });
        absensiStudentChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadAbsensiData());
        absensiDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> loadAbsensiData());

        absensiStatusChoiceBox.getItems().addAll("Hadir", "Alpha", "Ijin");
        absensiStatusChoiceBox.setValue("Hadir");

        guruTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                switch (newTab.getText()) {
                    case "Class Schedule":
                        loadClassesTaughtByGuru();
                        loadSubjectsTaughtByGuru();
                        loadJadwalKelas();
                        break;
                    case "Input Grades":
                        loadClassesTaughtByGuru();
                        loadSubjectsTaughtByGuru();
                        loadSemesters();
                        loadExistingGrades();
                        break;
                    case "Manage Assignments":
                        populateCommonChoiceBoxes();
                        loadTugas();
                        break;
                    case "Manage Materials":
                        populateCommonChoiceBoxes();
                        loadMateri();
                        break;
                    case "Manage Exams":
                        populateCommonChoiceBoxes();
                        loadUjian();
                        break;
                    case "Announcements":
                        loadGuruAnnouncements();
                        break;
                    case "Manage Absensi":
                        loadAbsensiClasses();
                        loadAbsensiSubjects();
                        loadAbsensiData();
                        break;
                }
            }
        });
        loadGuruAnnouncements();
    }

    private void populateCommonChoiceBoxes() {
        tugasClassChoiceBox.setItems(FXCollections.observableArrayList(classesMap.keySet()));
        tugasSubjectChoiceBox.setItems(FXCollections.observableArrayList(subjectsMap.keySet()));

        materiClassChoiceBox.setItems(FXCollections.observableArrayList(classesMap.keySet()));
        materiSubjectChoiceBox.setItems(FXCollections.observableArrayList(subjectsMap.keySet()));

        ujianClassChoiceBox.setItems(FXCollections.observableArrayList(classesMap.keySet()));
        ujianSubjectChoiceBox.setItems(FXCollections.observableArrayList(subjectsMap.keySet()));
    }

    private void loadClassesTaughtByGuru() {
        classesMap.clear();
        scheduleClassChoiceBox.getItems().clear();
        gradeClassChoiceBox.getItems().clear();
        absensiClassChoiceBox.getItems().clear();

        try (ResultSet rs = enrollmentService.getClassesTaughtByGuru(loggedInUserId)) {
            while (rs.next()) {
                int kelasId = rs.getInt("kelas_id");
                String namaKelas = rs.getString("nama_kelas");
                String waliId = rs.getString("wali_id");
                String waliName = rs.getString("wali_name");
                String display = namaKelas + " (Wali: " + waliName + ")";
                String combinedId = kelasId + "-" + waliId;
                classesMap.put(display, combinedId);
                scheduleClassChoiceBox.getItems().add(display);
                gradeClassChoiceBox.getItems().add(display);
                absensiClassChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load classes for teacher", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSubjectsTaughtByGuru() {
        subjectsMap.clear();
        scheduleSubjectChoiceBox.getItems().clear();
        gradeSubjectChoiceBox.getItems().clear();
        absensiSubjectChoiceBox.getItems().clear();

        try (ResultSet rs = enrollmentService.getSubjectsTaughtByGuru(loggedInUserId)) {
            while (rs.next()) {
                int mapelId = rs.getInt("mapel_id");
                String namaMapel = rs.getString("nama_mapel");
                subjectsMap.put(namaMapel, mapelId);
                scheduleSubjectChoiceBox.getItems().add(namaMapel);
                gradeSubjectChoiceBox.getItems().add(namaMapel);
                absensiSubjectChoiceBox.getItems().add(namaMapel);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load subjects for teacher", e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Class Schedule Methods ---
    private void initJadwalKelasTable() {
        hariColumn.setCellValueFactory(new PropertyValueFactory<>("hari"));
        jamMulaiColumn.setCellValueFactory(new PropertyValueFactory<>("jamMulai"));
        jamSelesaiColumn.setCellValueFactory(new PropertyValueFactory<>("jamSelesai"));
        namaMapelColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        namaKelasJadwalColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
    }

    @FXML
    void loadJadwalKelas() {
        ObservableList<JadwalEntry> jadwalList = FXCollections.observableArrayList();
        String selectedClassDisplay = scheduleClassChoiceBox.getValue();
        String selectedSubjectDisplay = scheduleSubjectChoiceBox.getValue();

        int kelasId = 0;
        String waliUserId = null;
        if (selectedClassDisplay != null && !selectedClassDisplay.isEmpty()) {
            String combinedClassId = classesMap.get(selectedClassDisplay);
            if (combinedClassId != null) {
                String[] ids = combinedClassId.split("-");
                kelasId = Integer.parseInt(ids[0]);
                waliUserId = ids[1];
            }
        }

        int mapelId = 0;
        if (selectedSubjectDisplay != null && !selectedSubjectDisplay.isEmpty()) {
            Integer subId = subjectsMap.get(selectedSubjectDisplay);
            if (subId != null) {
                mapelId = subId;
            }
        }

        try (ResultSet rs = scheduleService.getSchedulesByTeacherAndClass(loggedInUserId, kelasId, waliUserId, mapelId)) {
            while (rs.next()) {
                jadwalList.add(new JadwalEntry(
                        rs.getString("hari"),
                        rs.getString("jam_mulai"),
                        rs.getString("jam_selsai"),
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas"),
                        ""
                ));
            }
            jadwalKelasTable.setItems(jadwalList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load class schedule", e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Input Grades Methods ---
    private void initExistingGradesTable() {
        existingJenisNilaiColumn.setCellValueFactory(new PropertyValueFactory<>("jenisNilai"));
        existingNamaMapelColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        existingNilaiColumn.setCellValueFactory(new PropertyValueFactory<>("nilai"));
    }

    private void loadSemesters() {
        semestersMap.clear();
        gradeSemesterChoiceBox.getItems().clear();

        try (ResultSet rs = semesterService.getAllSemesters()) {
            while (rs.next()) {
                int semesterId = rs.getInt("semester_id");
                String tahunAjaran = rs.getString("tahun_ajaran");
                String semesterName = rs.getString("semester");
                String display = tahunAjaran + " - " + semesterName;
                semestersMap.put(display, semesterId);
                gradeSemesterChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load semesters", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleGradeClassSelection() {
        String selectedClassDisplay = gradeClassChoiceBox.getValue();
        studentsMap.clear();
        gradeStudentChoiceBox.getItems().clear();

        if (selectedClassDisplay == null || selectedClassDisplay.isEmpty()) {
            loadExistingGrades();
            return;
        }

        String combinedClassId = classesMap.get(selectedClassDisplay);
        if (combinedClassId == null) {
            loadExistingGrades();
            return;
        }
        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        try (ResultSet rs = enrollmentService.getStudentsInClass(kelasId, waliUserId)) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String nama = rs.getString("nama");
                String nisNip = rs.getString("NIS_NIP");
                String display = nama + " (NIS: " + nisNip + ")";
                studentsMap.put(display, userId);
                gradeStudentChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load students for class", e.getMessage());
            e.printStackTrace();
        }
        loadExistingGrades();
    }

    @FXML
    void handleGradeSubjectSelection() {
        loadExistingGrades();
    }

    @FXML
    void handleSubmitGrade() {
        String selectedStudentDisplay = gradeStudentChoiceBox.getValue();
        String selectedSubjectDisplay = gradeSubjectChoiceBox.getValue();
        String selectedSemesterDisplay = gradeSemesterChoiceBox.getValue();
        String jenisNilai = gradeTypeChoiseBox.getValue();
        String scoreText = scoreField.getText();

        if (selectedStudentDisplay == null || selectedSubjectDisplay == null || selectedSemesterDisplay == null ||
                jenisNilai == null || scoreText.isEmpty()) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please fill in all grade details.");
            return;
        }

        int score;
        try {
            score = Integer.parseInt(scoreText);
            if (score < 0 || score > 100) {
                AlertClass.WarningAlert("Input Error", "Invalid Score", "Score must be between 0 and 100.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertClass.WarningAlert("Input Error", "Invalid Score", "Please enter a valid number for the score.");
            return;
        }

        String studentUserId = studentsMap.get(selectedStudentDisplay);
        Integer mapelId = subjectsMap.get(selectedSubjectDisplay);
        Integer semesterId = semestersMap.get(selectedSemesterDisplay);

        if (studentUserId == null || mapelId == null || semesterId == null) {
            AlertClass.ErrorAlert("Selection Error", "Invalid Selection", "Could not retrieve IDs for selected student, subject, or semester.");
            return;
        }

        try {
            // Check if this guru is assigned to teach this subject in the student's class
            // This needs to be done through enrollment service, requires class ID of student
            // For simplicity, for now we will skip this detailed check here and rely on DB constraints or broader logic
            // A more robust solution would retrieve student's class ID and then call enrollmentService.checkTeacherAssignment

            int raporId = gradeService.getOrCreateRaporId(studentUserId, semesterId);

            if (raporId == -1) {
                AlertClass.ErrorAlert("Database Error", "Failed to create or retrieve Rapor entry", "Could not associate grade with a report card.");
                return;
            }

            boolean success = gradeService.addGrade(score, jenisNilai, mapelId, raporId);
            if (success) {
                AlertClass.InformationAlert("Success", "Grade Submitted", "Grade for " + selectedStudentDisplay + " submitted successfully.");
                scoreField.clear();
                loadExistingGrades();
            } else {
                AlertClass.ErrorAlert("Failed", "Grade Not Submitted", "Failed to submit grade to the database.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to submit grade", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadExistingGrades() {
        ObservableList<NilaiEntry> existingGrades = FXCollections.observableArrayList();
        String selectedStudentDisplay = gradeStudentChoiceBox.getValue();
        String selectedSubjectDisplay = gradeSubjectChoiceBox.getValue();
        String selectedSemesterDisplay = gradeSemesterChoiceBox.getValue();

        if (selectedStudentDisplay == null || selectedSubjectDisplay == null || selectedSemesterDisplay == null) {
            existingGradesTable.setItems(FXCollections.emptyObservableList());
            return;
        }

        String studentUserId = studentsMap.get(selectedStudentDisplay);
        Integer mapelId = subjectsMap.get(selectedSubjectDisplay);
        Integer semesterId = semestersMap.get(selectedSemesterDisplay);

        if (studentUserId == null || mapelId == null || semesterId == null) {
            existingGradesTable.setItems(FXCollections.emptyObservableList());
            return;
        }

        try (ResultSet rs = gradeService.getGradesByStudentAndSemesterAndSubject(studentUserId, semesterId, mapelId)) {
            while (rs.next()) {
                existingGrades.add(new NilaiEntry(
                        rs.getString("jenis_nilai"),
                        rs.getString("nama_mapel"),
                        rs.getInt("nilai")
                ));
            }
            existingGradesTable.setItems(existingGrades);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load existing grades", e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Manage Assignments Methods ---
    private void initTugasTable() {
        tugasKeteranganColumn.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
        tugasDeadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        tugasTanggalRilisColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalDirelease"));
        tugasMapelColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        tugasKelasColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
    }

    @FXML
    void handleAddTugas() {
        String selectedClassDisplay = tugasClassChoiceBox.getValue();
        String selectedSubjectDisplay = tugasSubjectChoiceBox.getValue();
        String keterangan = tugasKeteranganArea.getText();
        LocalDate deadlineDate = tugasDeadlinePicker.getValue();

        if (selectedClassDisplay == null || selectedSubjectDisplay == null || keterangan.isEmpty() || deadlineDate == null) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please fill in all assignment details.");
            return;
        }

        String combinedClassId = classesMap.get(selectedClassDisplay);
        Integer mapelId = subjectsMap.get(selectedSubjectDisplay);

        if (combinedClassId == null || mapelId == null) {
            AlertClass.ErrorAlert("Selection Error", "Invalid Selection", "Could not retrieve IDs for selected class or subject.");
            return;
        }

        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        LocalDateTime deadline = deadlineDate.atTime(23, 59, 59);
        LocalDateTime releaseDate = LocalDateTime.now();

        try {
            boolean isAssigned = enrollmentService.checkTeacherAssignment(loggedInUserId, mapelId, waliUserId, kelasId);
            if (!isAssigned) {
                AlertClass.ErrorAlert("Authorization Error", "Not Authorized", "You are not assigned to teach this subject in this class.");
                return;
            }

            boolean success = assignmentService.addAssignment(keterangan, deadline, releaseDate, waliUserId, mapelId, kelasId);
            if (success) {
                AlertClass.InformationAlert("Success", "Assignment Added", "New assignment has been added successfully.");
                tugasKeteranganArea.clear();
                tugasDeadlinePicker.setValue(null);
                tugasClassChoiceBox.getSelectionModel().clearSelection();
                tugasSubjectChoiceBox.getSelectionModel().clearSelection();
                loadTugas();
            } else {
                AlertClass.ErrorAlert("Failed", "Assignment Not Added", "Failed to add assignment to the database.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to add assignment", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void loadTugas() {
        ObservableList<TugasEntry> tugasList = FXCollections.observableArrayList();
        try (ResultSet rs = assignmentService.getAssignmentsByTeacher(loggedInUserId)) {
            while (rs.next()) {
                tugasList.add(new TugasEntry(
                        rs.getInt("tugas_id"),
                        rs.getString("keterangan"),
                        rs.getTimestamp("deadline").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        rs.getTimestamp("tanggal_direlease").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas"),
                        rs.getString("Kelas_Users_user_id"),
                        rs.getInt("Kelas_kelas_id")
                ));
            }
            tugasTable.setItems(tugasList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load assignments", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteTugas() {
        TugasEntry selectedTugas = tugasTable.getSelectionModel().getSelectedItem();
        if (selectedTugas == null) {
            AlertClass.WarningAlert("Selection Error", "No Assignment Selected", "Please select an assignment to delete.");
            return;
        }

        Optional<javafx.scene.control.ButtonType> result = AlertClass.ConfirmationAlert(
                "Confirm Deletion",
                "Delete Assignment",
                "Are you sure you want to delete this assignment? This action cannot be undone."
        );

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                boolean success = assignmentService.deleteAssignment(selectedTugas.getTugasId(), selectedTugas.getKelasWaliId(), selectedTugas.getKelasId());
                if (success) {
                    AlertClass.InformationAlert("Success", "Assignment Deleted", "Assignment deleted successfully.");
                    loadTugas();
                } else {
                    AlertClass.ErrorAlert("Failed", "Deletion Failed", "Failed to delete assignment. It might not exist or you lack permission.");
                }
            } catch (SQLException e) {
                AlertClass.ErrorAlert("Database Error", "Failed to delete assignment", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // --- Manage Materials Methods ---
    private void initMateriTable() {
        materiNamaMateriColumn.setCellValueFactory(new PropertyValueFactory<>("namaMateri"));
        materiMapelColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        materiKelasColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
    }

    @FXML
    void handleAddMateri() {
        String selectedClassDisplay = materiClassChoiceBox.getValue();
        String selectedSubjectDisplay = materiSubjectChoiceBox.getValue();
        String namaMateri = materiNamaMateriField.getText();

        if (selectedClassDisplay == null || selectedSubjectDisplay == null || namaMateri.isEmpty()) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please fill in all material details.");
            return;
        }

        String combinedClassId = classesMap.get(selectedClassDisplay);
        Integer mapelId = subjectsMap.get(selectedSubjectDisplay);

        if (combinedClassId == null || mapelId == null) {
            AlertClass.ErrorAlert("Selection Error", "Invalid Selection", "Could not retrieve IDs for selected class or subject.");
            return;
        }

        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        try {
            boolean isAssigned = enrollmentService.checkTeacherAssignment(loggedInUserId, mapelId, waliUserId, kelasId);
            if (!isAssigned) {
                AlertClass.ErrorAlert("Authorization Error", "Not Authorized", "You are not assigned to teach this subject in this class.");
                return;
            }

            boolean success = materialService.addMaterial(namaMateri, waliUserId, mapelId, kelasId);
            if (success) {
                AlertClass.InformationAlert("Success", "Material Added", "New material has been added successfully.");
                materiNamaMateriField.clear();
                materiClassChoiceBox.getSelectionModel().clearSelection();
                materiSubjectChoiceBox.getSelectionModel().clearSelection();
                loadMateri();
            } else {
                AlertClass.ErrorAlert("Failed", "Material Not Added", "Failed to add material to the database.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to add material", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void loadMateri() {
        ObservableList<MateriEntry> materiList = FXCollections.observableArrayList();
        try (ResultSet rs = materialService.getMaterialsByTeacher(loggedInUserId)) {
            while (rs.next()) {
                materiList.add(new MateriEntry(
                        rs.getInt("materi_id"),
                        rs.getString("nama_materi"),
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas"),
                        rs.getString("Kelas_Users_user_id"),
                        rs.getInt("Kelas_kelas_id")
                ));
            }
            materiTable.setItems(materiList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load materials", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteMateri() {
        MateriEntry selectedMateri = materiTable.getSelectionModel().getSelectedItem();
        if (selectedMateri == null) {
            AlertClass.WarningAlert("Selection Error", "No Material Selected", "Please select a material to delete.");
            return;
        }

        Optional<javafx.scene.control.ButtonType> result = AlertClass.ConfirmationAlert(
                "Confirm Deletion",
                "Delete Material",
                "Are you sure you want to delete this material? This action cannot be undone."
        );

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                boolean success = materialService.deleteMaterial(selectedMateri.getMateriId(), selectedMateri.getKelasWaliId(), selectedMateri.getKelasId());
                if (success) {
                    AlertClass.InformationAlert("Success", "Material Deleted", "Material deleted successfully.");
                    loadMateri();
                } else {
                    AlertClass.ErrorAlert("Failed", "Deletion Failed", "Failed to delete material. It might not exist or you lack permission.");
                }
            } catch (SQLException e) {
                AlertClass.ErrorAlert("Database Error", "Failed to delete material", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // --- Manage Exams Methods ---
    private void initUjianTable() {
        ujianJenisColumn.setCellValueFactory(new PropertyValueFactory<>("jenisUjian"));
        ujianTanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        ujianMapelColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        ujianKelasColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
    }

    @FXML
    void handleAddUjian() {
        String selectedClassDisplay = ujianClassChoiceBox.getValue();
        String selectedSubjectDisplay = ujianSubjectChoiceBox.getValue();
        String jenisUjian = ujianJenisUjianChoiceBox.getValue();
        LocalDate tanggalUjianDate = ujianTanggalPicker.getValue();

        if (selectedClassDisplay == null || selectedSubjectDisplay == null || jenisUjian == null || tanggalUjianDate == null) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please fill in all exam details.");
            return;
        }

        String combinedClassId = classesMap.get(selectedClassDisplay);
        Integer mapelId = subjectsMap.get(selectedSubjectDisplay);

        if (combinedClassId == null || mapelId == null) {
            AlertClass.ErrorAlert("Selection Error", "Invalid Selection", "Could not retrieve IDs for selected class or subject.");
            return;
        }

        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        LocalDateTime tanggalUjian = tanggalUjianDate.atTime(8, 0, 0);

        try {
            boolean isAssigned = enrollmentService.checkTeacherAssignment(loggedInUserId, mapelId, waliUserId, kelasId);
            if (!isAssigned) {
                AlertClass.ErrorAlert("Authorization Error", "Not Authorized", "You are not assigned to teach this subject in this class.");
                return;
            }

            boolean success = examService.addExam(jenisUjian, tanggalUjian, waliUserId, mapelId, kelasId);
            if (success) {
                AlertClass.InformationAlert("Success", "Exam Added", "New exam has been added successfully.");
                ujianJenisUjianChoiceBox.getSelectionModel().clearSelection();
                ujianTanggalPicker.setValue(null);
                ujianClassChoiceBox.getSelectionModel().clearSelection();
                ujianSubjectChoiceBox.getSelectionModel().clearSelection();
                loadUjian();
            } else {
                AlertClass.ErrorAlert("Failed", "Exam Not Added", "Failed to add exam to the database.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to add exam", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void loadUjian() {
        ObservableList<UjianEntry> ujianList = FXCollections.observableArrayList();
        try (ResultSet rs = examService.getExamsByTeacher(loggedInUserId)) {
            while (rs.next()) {
                ujianList.add(new UjianEntry(
                        rs.getInt("ujian_id"),
                        rs.getString("jenis_ujian"),
                        rs.getTimestamp("tanggal").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas"),
                        rs.getString("Kelas_Users_user_id"),
                        rs.getInt("Kelas_kelas_id")
                ));
            }
            ujianTable.setItems(ujianList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load exams", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteUjian() {
        UjianEntry selectedUjian = ujianTable.getSelectionModel().getSelectedItem();
        if (selectedUjian == null) {
            AlertClass.WarningAlert("Selection Error", "No Exam Selected", "Please select an exam to delete.");
            return;
        }

        Optional<javafx.scene.control.ButtonType> result = AlertClass.ConfirmationAlert(
                "Confirm Deletion",
                "Delete Exam",
                "Are you sure you want to delete this exam? This action cannot be undone."
        );

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                boolean success = examService.deleteExam(selectedUjian.getUjianId(), selectedUjian.getKelasWaliId(), selectedUjian.getKelasId());
                if (success) {
                    AlertClass.InformationAlert("Success", "Exam Deleted", "Exam deleted successfully.");
                    loadUjian();
                } else {
                    AlertClass.ErrorAlert("Failed", "Deletion Failed", "Failed to delete exam. It might not exist or you lack permission.");
                }
            } catch (SQLException e) {
                AlertClass.ErrorAlert("Database Error", "Failed to delete exam", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // --- Announcements Methods for Guru ---
    private void initGuruAnnouncementTable() {
        guruAnnouncementWaktuColumn.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        guruAnnouncementContentColumn.setCellValueFactory(new PropertyValueFactory<>("pengumuman"));
    }

    @FXML
    void handleCreateGuruAnnouncement() {
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
                loadGuruAnnouncements();
            } else {
                AlertClass.ErrorAlert("Failed", "Announcement Not Published", "Failed to publish announcement.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to publish announcement", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void loadGuruAnnouncements() {
        ObservableList<PengumumanEntry> announcementList = FXCollections.observableArrayList();
        try (ResultSet rs = announcementService.getAnnouncementsByUserId(loggedInUserId)) {
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
            guruAnnouncementTable.setItems(announcementList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load announcements", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdateGuruAnnouncement() {
        PengumumanEntry selectedAnnouncement = guruAnnouncementTable.getSelectionModel().getSelectedItem();
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

        try {
            boolean success = announcementService.updateAnnouncement(pengumumanId, updatedContent, loggedInUserId);
            if (success) {
                AlertClass.InformationAlert("Success", "Announcement Updated", "Announcement updated successfully.");
                announcementTextArea.clear();
                loadGuruAnnouncements();
            } else {
                AlertClass.ErrorAlert("Failed", "Announcement Not Updated", "Failed to update announcement. It might not exist or you lack permission.");
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to update announcement", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteGuruAnnouncement() {
        PengumumanEntry selectedAnnouncement = guruAnnouncementTable.getSelectionModel().getSelectedItem();
        if (selectedAnnouncement == null) {
            AlertClass.WarningAlert("Selection Error", "No Announcement Selected", "Please select an announcement to delete.");
            return;
        }

        Optional<javafx.scene.control.ButtonType> result = AlertClass.ConfirmationAlert(
                "Confirm Deletion",
                "Delete Announcement",
                "Are you sure you want to delete this announcement? This action cannot be undone."
        );

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            int pengumumanId = selectedAnnouncement.getPengumumanId();

            try {
                boolean success = announcementService.deleteAnnouncement(pengumumanId, loggedInUserId);
                if (success) {
                    AlertClass.InformationAlert("Success", "Announcement Deleted", "Announcement deleted successfully.");
                    announcementTextArea.clear();
                    loadGuruAnnouncements();
                } else {
                    AlertClass.ErrorAlert("Failed", "Announcement Not Deleted", "Failed to delete announcement. It might not exist or you lack permission.");
                }
            } catch (SQLException e) {
                AlertClass.ErrorAlert("Database Error", "Failed to delete announcement", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // NEW: Manage Absensi Methods
    private void initAbsensiTable() {
        absensiTanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        absensiStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        absensiMapelColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        absensiKelasColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
        absensiJamMulaiColumn.setCellValueFactory(new PropertyValueFactory<>("jamMulai"));
        absensiJamSelesaiColumn.setCellValueFactory(new PropertyValueFactory<>("jamSelesai"));
    }
    private void loadAbsensiClasses() {
        loadClassesTaughtByGuru();
    }
    private void loadAbsensiSubjects() {
        loadSubjectsTaughtByGuru();
    }

    @FXML
    private void handleAbsensiClassSelection() {
        String selectedClassDisplay = absensiClassChoiceBox.getValue();
        absensiStudentChoiceBox.getItems().clear();
        studentsMap.clear();

        if (selectedClassDisplay == null || selectedClassDisplay.isEmpty()) {
            return;
        }

        String combinedClassId = classesMap.get(selectedClassDisplay);
        if (combinedClassId == null) {
            return;
        }
        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        try (ResultSet rs = enrollmentService.getStudentsInClass(kelasId, waliUserId)) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String nama = rs.getString("nama");
                String nisNip = rs.getString("NIS_NIP");
                String display = nama + " (NIS: " + nisNip + ")";
                studentsMap.put(display, userId);
                absensiStudentChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load students for attendance", e.getMessage());
            e.printStackTrace();
        }

        loadSubjectsForAbsensi(selectedClassDisplay);
        loadAbsensiData();
    }

    private void loadSubjectsForAbsensi(String selectedClassDisplay) {
        absensiSubjectChoiceBox.getItems().clear();
        subjectsMap.clear();

        if (selectedClassDisplay == null || selectedClassDisplay.isEmpty()) {
            return;
        }

        String combinedClassId = classesMap.get(selectedClassDisplay);
        if (combinedClassId == null) {
            return;
        }
        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        try (ResultSet rs = enrollmentService.getSubjectsForAbsensi(loggedInUserId, kelasId, waliUserId)) {
            while (rs.next()) {
                int mapelId = rs.getInt("mapel_id");
                String namaMapel = rs.getString("nama_mapel");
                subjectsMap.put(namaMapel, mapelId);
                absensiSubjectChoiceBox.getItems().add(namaMapel);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load subjects for absensi", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStudentsForAbsensi(String selectedClassDisplay, String selectedSubjectDisplay) {
        absensiStudentChoiceBox.getItems().clear();
        studentsMap.clear();

        if (selectedClassDisplay == null || selectedClassDisplay.isEmpty() || selectedSubjectDisplay == null || selectedSubjectDisplay.isEmpty()) {
            return;
        }

        String combinedClassId = classesMap.get(selectedClassDisplay);
        Integer mapelId = subjectsMap.get(selectedSubjectDisplay);

        if (combinedClassId == null || mapelId == null) {
            return;
        }
        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        try (ResultSet rs = enrollmentService.getStudentsForAbsensi(kelasId, waliUserId, mapelId)) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String nama = rs.getString("nama");
                String nisNip = rs.getString("NIS_NIP");
                String display = nama + " (NIS: " + nisNip + ")";
                studentsMap.put(display, userId);
                absensiStudentChoiceBox.getItems().add(display);
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load students for selected class and subject", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void recordAbsensi() {
        String selectedClassDisplay = absensiClassChoiceBox.getValue();
        String selectedSubjectDisplay = absensiSubjectChoiceBox.getValue();
        String selectedStudentDisplay = absensiStudentChoiceBox.getValue();
        LocalDate selectedDate = absensiDatePicker.getValue();
        String status = absensiStatusChoiceBox.getValue();

        if (selectedClassDisplay == null || selectedSubjectDisplay == null || selectedStudentDisplay == null || selectedDate == null || status == null) {
            AlertClass.WarningAlert("Input Error", "Missing Information", "Please select a class, subject, student, date, and status.");
            return;
        }

        String combinedClassId = classesMap.get(selectedClassDisplay);
        Integer mapelId = subjectsMap.get(selectedSubjectDisplay);
        String studentUserId = studentsMap.get(selectedStudentDisplay);

        if (combinedClassId == null || mapelId == null || studentUserId == null) {
            AlertClass.ErrorAlert("Selection Error", "Invalid Selection", "Could not retrieve IDs for selected class, subject, or student.");
            return;
        }

        String[] ids = combinedClassId.split("-");
        int kelasId = Integer.parseInt(ids[0]);
        String waliUserId = ids[1];

        String dayOfWeekInIndonesian = convertDayToIndonesian(selectedDate.getDayOfWeek().toString());
        Integer jadwalId = null;

        try {
            jadwalId = scheduleService.getJadwalId(kelasId, waliUserId, mapelId, dayOfWeekInIndonesian);
            if (jadwalId == -1 || jadwalId == null) {
                AlertClass.WarningAlert("Schedule Not Found", "No matching schedule found for this class, subject, and selected date's day (" + dayOfWeekInIndonesian + ").", "Please ensure a schedule exists for the selected criteria.");
                return;
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to retrieve schedule ID", e.getMessage());
            e.printStackTrace();
            return;
        }

        try {
            boolean success = attendanceService.recordAttendance(studentUserId, jadwalId, status, selectedDate.atStartOfDay());
            if (success) {
                AlertClass.InformationAlert("Success", "Attendance Recorded", "Attendance for " + selectedStudentDisplay + " on " + selectedDate + " recorded as " + status + ".");
            } else {
                AlertClass.ErrorAlert("Failed", "Attendance Not Recorded", "Failed to record attendance.");
            }
            loadAbsensiData();
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to record/update attendance", e.getMessage());
            e.printStackTrace();
        }
    }

    private String convertDayToIndonesian(String englishDay) {
        switch (englishDay) {
            case "MONDAY": return "Senin";
            case "TUESDAY": return "Selasa";
            case "WEDNESDAY": return "Rabu";
            case "THURSDAY": return "Kamis";
            case "FRIDAY": return "Jumat";
            case "SATURDAY": return "Sabtu";
            case "SUNDAY": return "Minggu";
            default: return englishDay;
        }
    }

    @FXML
    void loadAbsensiData() {
        ObservableList<AbsensiEntry> absensiList = FXCollections.observableArrayList();
        String selectedClassDisplay = absensiClassChoiceBox.getValue();
        String selectedSubjectDisplay = absensiSubjectChoiceBox.getValue();
        String selectedStudentDisplay = absensiStudentChoiceBox.getValue();
        LocalDate selectedDate = absensiDatePicker.getValue();

        int kelasId = 0;
        String waliUserId = null;
        if (selectedClassDisplay != null && !selectedClassDisplay.isEmpty()) {
            String combinedClassId = classesMap.get(selectedClassDisplay);
            if (combinedClassId != null) {
                String[] ids = combinedClassId.split("-");
                kelasId = Integer.parseInt(ids[0]);
                waliUserId = ids[1];
            } else {
                absensiTable.setItems(FXCollections.emptyObservableList());
                return;
            }
        } else {
            absensiTable.setItems(FXCollections.emptyObservableList());
            return;
        }

        int mapelId = 0;
        if (selectedSubjectDisplay != null && !selectedSubjectDisplay.isEmpty()) {
            Integer subId = subjectsMap.get(selectedSubjectDisplay);
            if (subId != null) {
                mapelId = subId;
            } else {
                absensiTable.setItems(FXCollections.emptyObservableList());
                return;
            }
        } else {
            absensiTable.setItems(FXCollections.emptyObservableList());
            return;
        }

        String studentUserId = null;
        if (selectedStudentDisplay != null && !selectedStudentDisplay.isEmpty()) {
            studentUserId = studentsMap.get(selectedStudentDisplay);
            if (studentUserId == null) {
                absensiTable.setItems(FXCollections.emptyObservableList());
                return;
            }
        }

        Date sqlDate = (selectedDate != null) ? Date.valueOf(selectedDate) : null;

        try (ResultSet rs = attendanceService.getAttendanceByFilters(loggedInUserId, kelasId, waliUserId, mapelId, studentUserId, sqlDate)) {
            while (rs.next()) {
                absensiList.add(new AbsensiEntry(
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
            absensiTable.setItems(FXCollections.emptyObservableList());
        }
    }
}