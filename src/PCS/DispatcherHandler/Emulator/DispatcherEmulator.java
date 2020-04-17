package PCS.DispatcherHandler.Emulator;

import AppKickstarter.misc.Msg;
import PCS.PCSCore.PCSCore;
import PCS.PCSStarter;
import PCS.DispatcherHandler.DispatcherHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Dispatcher Emulator Combine by Dispatcher Handler and Dispatcher Controller
 * This class is mainly combined by Dispatcher Handler and Dispatcher Controller. It is a subclass of Dispatcher Handler.
 * @author Gong Yikai
 */
public class DispatcherEmulator extends DispatcherHandler {
    /**
     * It will be used in Start() method to create Dispatcher GUI
     */
    private Stage myStage;
    /**
     * It is the Dispatcher Controller. A Emulator Of Dispatcher
     */
    private DispatcherController dispatcherController;
    /**
     * PCS Start Object
     */
    private final PCSStarter pcsStarter;
    /**
     * The ID of the Dispatcher Emulator
     */
    private final String id;




    /**
     * Dispatcher Constructor
     * @param id The ID Of Dispatcher Emulator
     * @param pcsStarter PCS Starter Object
     */
    public DispatcherEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";

    } // DispatcherEmulator


    /**
     * Start A GUI
     * The method starts a GUI that is interactive with users
     * @exception  Exception throw Exception
     * @author Gong Yikai
     */
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "DispatcherEmulator.fxml";
        loader.setLocation(DispatcherEmulator.class.getResource(fxmlName));
        root = loader.load();
        dispatcherController = loader.getController();
        dispatcherController.initialize(super.id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 470));
        myStage.setTitle(id);
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // DispatcherEmulator

    /**
     * Inform PCSCore to add a ticket
     * @author Gong Yikai
     */
    @Override
    protected void SendAddTicket(String mymsg) {
        logFine(mymsg);
    }

    /**
     * Receive ticket ID from PCSCore and show it on the GUI
     * @author Gong Yikai
     */
    protected void ReceiveTicketID(Msg msg){
        String mymsg=msg.getDetails();
        Date nowT = new Date(System.currentTimeMillis());
//        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String timestr = PCSCore.getDate(nowT);
        logInfo("Welcome to our parking lot.\n"+"Your ticket ID is "+mymsg+".\n"+"The time now is " + timestr + ".\nThe parking fee is "+parkingFeeCoefficient+"/s.\nHave a good time!");
        dispatcherController.showTicket(mymsg,timestr);
    }

    /**
     * Inform PCSCore the ticket is removed
     * @author Gong Yikai
     */
    protected void SendRemoveTicket(Msg msg){


        pcsCore.send(new Msg(id,mbox,Msg.Type.RemoveTicket,"Remove Ticket Now"));
        logInfo("Ticket removed.\nPlease take care of your ticket.");

    }

    /**
     * Log Fine Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logFine(String logMsg) {
        dispatcherController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine

    /**
     * Log Info Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logInfo(String logMsg) {
        dispatcherController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo


    /**
     * Log Warning Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logWarning(String logMsg) {
        dispatcherController.appendTextArea("[WARNING]: " + logMsg);
        log.warning(id + ": " + logMsg);
    } // logWarning


    /**
     * Log Severe Type Information and Add it to Controller
     * @param logMsg:Log Msg
     * @author Chuanyang Zheng
     */
    private final void logSevere(String logMsg) {
        dispatcherController.appendTextArea("[SEVERE]: " + logMsg);
        log.severe(id + ": " + logMsg);
    } // logSevere




} // DispatcherEmulator
