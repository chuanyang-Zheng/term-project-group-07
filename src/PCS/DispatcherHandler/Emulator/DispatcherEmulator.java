package PCS.DispatcherHandler.Emulator;

import AppKickstarter.misc.Msg;
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


//======================================================================
// DispatcherEmulator
public class DispatcherEmulator extends DispatcherHandler {
    private Stage myStage;
    private DispatcherController dispatcherController;
    private final PCSStarter pcsStarter;
    private final String id;



    //------------------------------------------------------------
    // DispatcherEmulator
    public DispatcherEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
//        this.DispatcherOpenTime = Integer.parseInt(this.pcsStarter.getProperty("Dispatcher.DispatcherOpenTime"));
////        this.DispatcherCloseTime = Integer.parseInt(this.pcsStarter.getProperty("Dispatcher.DispatcherCloseTime"));
//        this.autoOpen = true;
//        this.autoClose = true;
//        this.autoPoll = true;
    } // DispatcherEmulator


    //------------------------------------------------------------
    // start
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


    @Override
    protected void SendAddTicket(String mymsg) {
        logFine(mymsg);
    }

    //receive ticket ID
    protected void ReceiveTicketID(String mymsg){

        Date nowT = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String timestr = sdf.format(nowT);
        dispatcherController.appendTextArea("Welcome to our parking lot.\n"+"Your ticket ID is "+mymsg+".\n"+"The time now is " + timestr + ".\nThe parking fee is $5/s.\nHave a good time!");

        dispatcherController.showTicket(mymsg,timestr);
        //pcsCore.send(new Msg(id,mbox,Msg.Type.RemoveTicket,"Remove Ticket Now"));

    }
    protected void HandleRemoveTicket(String mymsg){


        pcsCore.send(new Msg(id,mbox,Msg.Type.RemoveTicket,"Remove Ticket Now"));
        dispatcherController.appendTextArea("Ticket removed.\nPlease take care of your ticket.");

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
