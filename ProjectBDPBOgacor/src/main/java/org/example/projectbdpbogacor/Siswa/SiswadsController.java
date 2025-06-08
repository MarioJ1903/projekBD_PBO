package org.example.projectbdpbogacor.Siswa;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.projectbdpbogacor.Aset.AlertClass;
import org.example.projectbdpbogacor.DBSource.DBS; // Needed for direct DB access if not all moved to service
import org.example.projectbdpbogacor.Controller.BaseController; // Import BaseController
import org.example.projectbdpbogacor.Service.*; // Import all services
import org.example.projectbdpbogacor.model.AbsensiEntry;
import org.example.projectbdpbogacor.model.JadwalEntry;
import org.example.projectbdpbogacor.model.MateriEntry;
import org.example.projectbdpbogacor.model.NilaiEntry;
import org.example.projectbdpbogacor.model.TugasEntry;
import org.example.projectbdpbogacor.model.UjianEntry;
import org.example.projectbdpbogacor.model.PengumumanEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Map;

// Extend BaseController
public class SiswadsController extends BaseController {

    private final UserService userService = new UserService();
    private final ScheduleService scheduleService = new ScheduleService();
    private final GradeService gradeService = new GradeService();
    private final AssignmentService assignmentService = new AssignmentService();
    private final MaterialService materialService = new MaterialService();
    private final ExamService examService = new ExamService();
    private final AttendanceService attendanceService = new AttendanceService();
    private final AnnouncementService announcementService = new AnnouncementService();


    @FXML
    private Label welcomeUserLabel;
    @FXML
    private TabPane siswaTabPane;

    // Biodata
    @FXML
    private Label userIdLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label nisNipLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;

    // Jadwal Kelas Table
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
    @FXML
    private TableColumn<JadwalEntry, String> namaPengajarJadwalColumn;

    // Nilai Ujian Table
    @FXML
    private TableView<NilaiEntry> nilaiUjianTable;
    @FXML
    private TableColumn<NilaiEntry, String> jenisNilaiColumn;
    @FXML
    private TableColumn<NilaiEntry, String> namaMapelNilaiColumn;
    @FXML
    private TableColumn<NilaiEntry, Integer> nilaiColumn;

    // Tugas Table
    @FXML
    private TableView<TugasEntry> tugasTable;
    @FXML
    private TableColumn<TugasEntry, String> keteranganTugasColumn;
    @FXML
    private TableColumn<TugasEntry, String> deadlineTugasColumn;
    @FXML
    private TableColumn<TugasEntry, String> tanggalRilisTugasColumn;
    @FXML
    private TableColumn<TugasEntry, String> mapelTugasColumn;
    @FXML
    private TableColumn<TugasEntry, String> kelasTugasColumn;

    // Materi Table
    @FXML
    private TableView<MateriEntry> materiTable;
    @FXML
    private TableColumn<MateriEntry, String> namaMateriColumn;
    @FXML
    private TableColumn<MateriEntry, String> mapelMateriColumn;
    @FXML
    private TableColumn<MateriEntry, String> kelasMateriColumn;

    // Ujian Table
    @FXML
    private TableView<UjianEntry> ujianTable;
    @FXML
    private TableColumn<UjianEntry, String> jenisUjianColumn;
    @FXML
    private TableColumn<UjianEntry, String> tanggalUjianColumn;
    @FXML
    private TableColumn<UjianEntry, String> mapelUjianColumn;
    @FXML
    private TableColumn<UjianEntry, String> kelasUjianColumn;

    // Absensi Table
    @FXML
    private TableView<AbsensiEntry> absensiTable;
    @FXML
    private TableColumn<AbsensiEntry, String> tanggalAbsensiColumn;
    @FXML
    private TableColumn<AbsensiEntry, String> statusAbsensiColumn;
    @FXML
    private TableColumn<AbsensiEntry, String> mapelAbsensiColumn;
    @FXML
    private TableColumn<AbsensiEntry, String> kelasAbsensiColumn;
    @FXML
    private TableColumn<AbsensiEntry, String> jamMulaiAbsensiColumn;
    @FXML
    private TableColumn<AbsensiEntry, String> jamSelesaiAbsensiColumn;

    // Feedback
    @FXML
    private TextArea feedbackTextArea;

    // Announcements
    @FXML
    private TableView<PengumumanEntry> announcementTable;
    @FXML
    private TableColumn<PengumumanEntry, String> announcementWaktuColumn;
    @FXML
    private TableColumn<PengumumanEntry, String> announcementContentColumn;

    @FXML
    void initialize() {
        setLoggedInUserId(org.example.projectbdpbogacor.HelloApplication.getInstance().getLoggedInUserId());
        loadUserName(welcomeUserLabel);

        // Initialize TableViews
        initJadwalKelasTable();
        initNilaiUjianTable();
        initTugasTable();
        initMateriTable();
        initUjianTable();
        initAbsensiTable();
        initAnnouncementTable();

        // Load data when tabs are selected
        siswaTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                switch (newTab.getText()) {
                    case "Biodata":
                        loadBiodata();
                        break;
                    case "Jadwal Kelas":
                        loadJadwalKelas();
                        break;
                    case "Nilai Ujian":
                        loadNilaiUjian();
                        break;
                    case "Tugas":
                        loadTugas();
                        break;
                    case "Materi":
                        loadMateri();
                        break;
                    case "Ujian":
                        loadUjian();
                        break;
                    case "Absensi":
                        loadAbsensi();
                        break;
                    case "Pengumuman":
                        loadAnnouncements();
                        break;
                }
            }
        });

        loadBiodata(); // Load biodata initially
    }

    private void loadBiodata() {
        try {
            Map<String, String> biodata = userService.getUserDetails(loggedInUserId);
            if (!biodata.isEmpty()) {
                userIdLabel.setText(biodata.get("user_id"));
                usernameLabel.setText(biodata.get("username"));
                nisNipLabel.setText(biodata.get("NIS_NIP"));
                nameLabel.setText(biodata.get("nama"));
                genderLabel.setText(biodata.get("gender").equals("L") ? "Laki-laki" : "Perempuan");
                addressLabel.setText(biodata.get("alamat"));
                emailLabel.setText(biodata.get("email"));
                phoneLabel.setText(biodata.get("nomer_hp"));
            }
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load biodata", e.getMessage());
            e.printStackTrace();
        }
    }

    private void initJadwalKelasTable() {
        hariColumn.setCellValueFactory(new PropertyValueFactory<>("hari"));
        jamMulaiColumn.setCellValueFactory(new PropertyValueFactory<>("jamMulai"));
        jamSelesaiColumn.setCellValueFactory(new PropertyValueFactory<>("jamSelesai"));
        namaMapelColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        namaKelasJadwalColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
        namaPengajarJadwalColumn.setCellValueFactory(new PropertyValueFactory<>("namaPengajar"));
    }

    private void loadJadwalKelas() {
        ObservableList<JadwalEntry> jadwalList = FXCollections.observableArrayList();
        try (ResultSet rs = scheduleService.getSchedulesByStudent(loggedInUserId)) {
            while (rs.next()) {
                jadwalList.add(new JadwalEntry(
                        rs.getString("hari"),
                        rs.getString("jam_mulai"),
                        rs.getString("jam_selsai"),
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas"),
                        rs.getString("nama_pengajar")
                ));
            }
            jadwalKelasTable.setItems(jadwalList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load class schedule", e.getMessage());
            e.printStackTrace();
        }
    }

    private void initNilaiUjianTable() {
        jenisNilaiColumn.setCellValueFactory(new PropertyValueFactory<>("jenisNilai"));
        namaMapelNilaiColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        nilaiColumn.setCellValueFactory(new PropertyValueFactory<>("nilai"));
    }

    private void loadNilaiUjian() {
        ObservableList<NilaiEntry> nilaiList = FXCollections.observableArrayList();
        try (ResultSet rs = gradeService.getGradesByStudent(loggedInUserId)) {
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

    private void initTugasTable() {
        keteranganTugasColumn.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
        deadlineTugasColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        tanggalRilisTugasColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalDirelease"));
        mapelTugasColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        kelasTugasColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
    }

    private void loadTugas() {
        ObservableList<TugasEntry> tugasList = FXCollections.observableArrayList();
        try (ResultSet rs = assignmentService.getAssignmentsByStudent(loggedInUserId)) {
            while (rs.next()) {
                tugasList.add(new TugasEntry(
                        rs.getString("keterangan"),
                        rs.getTimestamp("deadline").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        rs.getTimestamp("tanggal_direlease").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas")
                ));
            }
            tugasTable.setItems(tugasList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load assignments", e.getMessage());
            e.printStackTrace();
        }
    }

    private void initMateriTable() {
        namaMateriColumn.setCellValueFactory(new PropertyValueFactory<>("namaMateri"));
        mapelMateriColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        kelasMateriColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
    }

    private void loadMateri() {
        ObservableList<MateriEntry> materiList = FXCollections.observableArrayList();
        try (ResultSet rs = materialService.getMaterialsByStudent(loggedInUserId)) {
            while (rs.next()) {
                materiList.add(new MateriEntry(
                        rs.getString("nama_materi"),
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas")
                ));
            }
            materiTable.setItems(materiList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load materials", e.getMessage());
            e.printStackTrace();
        }
    }

    private void initUjianTable() {
        jenisUjianColumn.setCellValueFactory(new PropertyValueFactory<>("jenisUjian"));
        tanggalUjianColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        mapelUjianColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        kelasUjianColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
    }

    private void loadUjian() {
        ObservableList<UjianEntry> ujianList = FXCollections.observableArrayList();
        try (ResultSet rs = examService.getExamsByStudent(loggedInUserId)) {
            while (rs.next()) {
                ujianList.add(new UjianEntry(
                        rs.getString("jenis_ujian"),
                        rs.getTimestamp("tanggal").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas")
                ));
            }
            ujianTable.setItems(ujianList);
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to load exams", e.getMessage());
            e.printStackTrace();
        }
    }

    private void initAbsensiTable() {
        tanggalAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        statusAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        mapelAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("namaMapel"));
        kelasAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
        jamMulaiAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("jamMulai"));
        jamSelesaiAbsensiColumn.setCellValueFactory(new PropertyValueFactory<>("jamSelesai"));
    }

    private void loadAbsensi() {
        ObservableList<AbsensiEntry> absensiList = FXCollections.observableArrayList();
        try (ResultSet rs = attendanceService.getAttendanceByStudent(loggedInUserId)) {
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
            AlertClass.ErrorAlert("Database Error", "Failed to load attendance", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleSubmitFeedback() {
        String feedbackContent = feedbackTextArea.getText();
        if (feedbackContent.isEmpty()) {
            AlertClass.WarningAlert("Input Error", "Feedback Empty", "Please enter your feedback.");
            return;
        }

        // This should be handled by a FeedbackService
        String sql = "INSERT INTO Feedback (feedback, Users_user_id) VALUES (?, ?)";
        try (Connection con = DBS.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, feedbackContent);
            stmt.setString(2, loggedInUserId);
            stmt.executeUpdate();
            AlertClass.InformationAlert("Success", "Feedback Submitted", "Your feedback has been successfully submitted.");
            feedbackTextArea.clear();
        } catch (SQLException e) {
            AlertClass.ErrorAlert("Database Error", "Failed to submit feedback", "An error occurred while submitting feedback: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Announcements Methods ---
    private void initAnnouncementTable() {
        announcementWaktuColumn.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        announcementContentColumn.setCellValueFactory(new PropertyValueFactory<>("pengumuman"));
    }

    private void loadAnnouncements() {
        ObservableList<PengumumanEntry> announcementList = FXCollections.observableArrayList();
        try (ResultSet rs = announcementService.getAllAnnouncements()) { // All users can see all announcements
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
}