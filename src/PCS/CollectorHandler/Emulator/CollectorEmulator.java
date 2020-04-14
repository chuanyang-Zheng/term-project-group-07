package PCS.CollectorHandler.Emulator;

import PCS.CollectorHandler.CollectorHandler;
import PCS.PCSStarter;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Collector Emulator Combine by Collector Handler and Collector Controller
 * This class is mainly combined by Collector Handler and Collector Controller. It is a subclass of Collector Handler.
 * @author Chuanyang Zheng
 */
public class CollectorEmulator extends CollectorHandler {

    /**
     * It will be used in Start() method to create Collector GUI
     */
    private Stage myStage;

    /**
     * It is the Collector Controller. A Emulator Of Collector
     */
    private CollectorEmulatorController collectorEmulatorController;

    /**
     * PCS Start Object
     */
    private final PCSStarter pcsStarter;

    /**
     * The ID The Collector Emulator
     */
    private final String id;

    /**
     * Collector Constructor
     * @param id The ID Of Collector Emulator
     * @param pcsStarter PCS Starter Object
     */
    public CollectorEmulator(String id, PCSStarter pcsStarter){
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
    }

    //------------------------------------------------------------
    // start

    /**
     * Start A GUI
     * The method starts a GUI that is interactive with users
     * @exception  Exception throw Exception
     * @author Chuanyang Zheng
     */
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "CollectorEmulator.fxml";
        loader.setLocation(CollectorEmulator.class.getResource(fxmlName));
        root = loader.load();
        collectorEmulatorController = (CollectorEmulatorController) loader.getController();
        collectorEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 470));
        myStage.setTitle("Collector Emulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // CollectorEmulator



    /**
     * Send Alarm signal To Collector.
     * Add Alarm Information in console and Controller
     * @author Chuanyang Zheng
     */
    protected void sendStartAlarmSignal() {
        logWarning("The Ticket Is Wrong. Ring Alarm!");
    }


    /**
     * Send Alarm signal To Collector.
     * Add Stop Alarm Information in console and Controller
     * @author Chuanyang Zheng
     */
    protected void sendStopAlarmSignal() {
        logFine("Already Solve Problem. Stop Alarm!");
    }

    /**
     * Send "Ticket is Positive" signal To Collector.
     * Add "Ticket is Positive" Information in console and Controller
     * @author Chuanyang Zheng
     */
    protected void sendPositiveSignal(){
        logFine("The Ticket Is Valid");
    }

    @Override
    protected void sendParseIntFailSignal(String information) {
        logWarning(information);
    }

    /**
     * Log Fine Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logFine(String logMsg) {
        collectorEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine


    /**
     * Log Info Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logInfo(String logMsg) {
        collectorEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo


    /**
     * Log Warning Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logWarning(String logMsg) {
        collectorEmulatorController.appendTextArea("[WARNING]: " + logMsg);
        log.warning(id + ": " + logMsg);
    } // logWarning


    /**
     * Log Severe Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logSevere(String logMsg) {
        collectorEmulatorController.appendTextArea("[SEVERE]: " + logMsg);
        log.severe(id + ": " + logMsg);
    } // logSevere
}
