// ProjectBDPBOgacor/src/main/java/module-info.java
module org.example.projectbdpbogacor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.prefs;
    requires javafx.graphics; // Add this line explicitly

    opens org.example.projectbdpbogacor to javafx.fxml, javafx.graphics; // Add javafx.graphics here too
    opens org.example.projectbdpbogacor.Admin to javafx.fxml;
    opens org.example.projectbdpbogacor.Kepala to javafx.fxml;
    opens org.example.projectbdpbogacor.Guru to javafx.fxml;
    opens org.example.projectbdpbogacor.Wali to javafx.fxml;
    opens org.example.projectbdpbogacor.Siswa to javafx.fxml;
    opens org.example.projectbdpbogacor.model to javafx.base;
    opens org.example.projectbdpbogacor.Controller to javafx.fxml;
    opens org.example.projectbdpbogacor.Service to javafx.fxml;

    exports org.example.projectbdpbogacor;
    exports org.example.projectbdpbogacor.Admin;
    exports org.example.projectbdpbogacor.Kepala;
    exports org.example.projectbdpbogacor.Guru;
    exports org.example.projectbdpbogacor.Wali;
    exports org.example.projectbdpbogacor.Siswa;
    exports org.example.projectbdpbogacor.Controller;
    exports org.example.projectbdpbogacor.Service;
}