module ro.mpp2024.transportcompany {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
            requires com.dlsc.formsfx;
                    requires org.kordamp.bootstrapfx.core;
    requires org.apache.logging.log4j;
    requires java.sql;

    opens ro.mpp2024.transportcompany to javafx.fxml;
    exports ro.mpp2024.transportcompany;

    opens ro.mpp2024.transportcompany.model to javafx.base;
    exports ro.mpp2024.transportcompany.model;

    opens ro.mpp2024.transportcompany.controller to javafx.fxml;
    exports ro.mpp2024.transportcompany.controller;

    opens ro.mpp2024.transportcompany.service to javafx.base;
    exports ro.mpp2024.transportcompany.service;

    opens ro.mpp2024.transportcompany.repository to javafx.base;
    exports ro.mpp2024.transportcompany.repository;

    opens ro.mpp2024.transportcompany.dtos to javafx.base;
    exports ro.mpp2024.transportcompany.dtos;


}