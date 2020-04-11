package PCS.MotionsensorHandler.Emulator;

import PCS.GateHandler.Emulator.GateEmulator;
import PCS.GateHandler.Emulator.GateEmulatorController;
import PCS.MotionsensorHandler.MotionSensorHandler;
import PCS.PCSStarter;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


public class MotionSensorEmulator extends MotionSensorHandler {
    private Stage myStage;
    private MotionSensorEmulatorController motionSensorEmulatorController;
    private final PCSStarter pcsStarter;
    private final String id;

    /**
     *
     * @param id: the ID of MotionSensor
     * @param pcsStarter:PCSCore
     * @param floorNumber:floor number. The floor that the motion sensor detecting
     * @param detectUp: If it is true, the motion sensor detects up.
     */
    public MotionSensorEmulator(String id, PCSStarter pcsStarter,int floorNumber, boolean detectUp) {
        super(id, pcsStarter,floorNumber,detectUp);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
    } // Motion Sensor Emulator

    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "MotionSensorEmulator.fxml";
        loader.setLocation(MotionSensorEmulator.class.getResource(fxmlName));
        root = loader.load();
        motionSensorEmulatorController = (MotionSensorEmulatorController) loader.getController();
        motionSensorEmulatorController.initialize(super.getID(), pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 470));
        myStage.setTitle(id);
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // MotionSensorEmulator

    /**
     * Log Info Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logInfo(String logMsg) {
        motionSensorEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo


    /**
     * Log Warning Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logWarning(String logMsg) {
        motionSensorEmulatorController.appendTextArea("[WARNING]: " + logMsg);
        log.warning(id + ": " + logMsg);
    } // logWarning


    /**
     * Log Severe Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logSevere(String logMsg) {
        motionSensorEmulatorController.appendTextArea("[SEVERE]: " + logMsg);
        log.severe(id + ": " + logMsg);
    } // logSevere
}
