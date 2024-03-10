module ro.mpp2024.transportcompany {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
            requires com.dlsc.formsfx;
                    requires org.kordamp.bootstrapfx.core;
            
    opens ro.mpp2024.transportcompany to javafx.fxml;
    exports ro.mpp2024.transportcompany;
}