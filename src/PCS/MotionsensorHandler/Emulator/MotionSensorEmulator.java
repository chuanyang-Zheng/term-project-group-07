package PCS.MotionsensorHandler.Emulator;

import AppKickstarter.misc.Msg;
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

/**
 * Motion Sensor Detect Up
 * @author Chuanyang Zheng
 */
public class MotionSensorEmulator extends MotionSensorHandler {

    /**
     * Stage will be used in start() method
     */
    private Stage myStage;

    /**
     * Motion Sensor Controller. An Emulator Of Motion Sensor
     */
    private MotionSensorEmulatorController motionSensorEmulatorController;

    /**
     * PCSStarter
     */
    private final PCSStarter pcsStarter;

    /**
     * The ID the Motion Sensor Emulator
     */
    private final String id;
    /**
     * Auto Poll
     */
    private boolean autoPoll;

    /**
     *
     * @param id: the ID of MotionSensor
     * @param pcsStarter:PCSCore
     * @param floorNumber:floor number. The floor that the motion sensor detecting
     * @param detectUp: If it is true, the motion sensor detects up.
     * @author Chuanyang Zheng
     */
    public MotionSensorEmulator(String id, PCSStarter pcsStarter,int floorNumber, boolean detectUp) {
        super(id, pcsStarter,floorNumber,detectUp);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
        this.autoPoll = true;
    } // Motion Sensor Emulator

    /**
     * Start GUI
     * @exception Exception throws Exception
     * @author Chuanyang ZHeng
     */
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
    protected void sendPollReq() {
        logFine("Poll request received.  [autoPoll is " + (autoPoll ? "on]" : "off]"));
        if (autoPoll) {
            logFine("Send poll ack.");
            mbox.send(new Msg(id, mbox, Msg.Type.PollAck, ""));
        }
    } // sendPollReq
    private final void logFine(String logMsg) {
        motionSensorEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine

}
