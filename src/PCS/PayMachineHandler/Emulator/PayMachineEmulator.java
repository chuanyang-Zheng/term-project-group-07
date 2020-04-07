package PCS.PayMachineHandler.Emulator;

import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import PCS.PCSCore.Ticket;
import PCS.PCSStarter;

import PCS.PayMachineHandler.PayMachineHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.text.SimpleDateFormat;
import java.util.Date;


//======================================================================
// PayMachineEmulator
public class PayMachineEmulator extends PayMachineHandler {
    private Stage myStage;
    private PayMachineController PayMachineController;
    private final PCSStarter pcsStarter;
    private final String id;
    private final int GateOpenTimerID = 1;
    private final int GateCloseTimerID = 2;
    private boolean autoOpen;
    private boolean autoClose;
    private boolean autoPoll;


    //------------------------------------------------------------
    //  PayMachineEmulator
    public PayMachineEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
//        this.gateOpenTime = Integer.parseInt(this.pcsStarter.getProperty("Gate.GateOpenTime"));
////        this.gateCloseTime = Integer.parseInt(this.pcsStarter.getProperty("Gate.GateCloseTime"));
//        this.autoOpen = true;
//        this.autoClose = true;
//        this.autoPoll = true;
    } //  PayMachineEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "PayMachineEmulator.fxml";
        loader.setLocation(PayMachineEmulator.class.getResource(fxmlName));
        root = loader.load();
        PayMachineController = loader.getController();
        PayMachineController.initialize(super.id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 470));
        myStage.setTitle(id);
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } //  PayMachineEmulator
    @Override
    protected void FeeReceive(String mymsg){

        String []str = mymsg.split(",");
        if(!str[0].equals(super.id)) return;
        float fee = Float.parseFloat(str[2]);
        Date nowT = new Date(Long.parseLong(str[3]));
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String timestr = sdf.format(nowT);
        Long parkedTime = (System.currentTimeMillis() - Long.parseLong(str[3])) / 1000;
        PayMachineController.appendTextArea("You have parked " + Long.toString(parkedTime) + "s and you need to pay $" + fee + "  ($5/s)");
//        log.fine(id + ": " + mymsg);
        PayMachineController.updateTicket(str[1],str[2],timestr);
    }
    protected void SendPaymentACK(String mymsg){
        String []tmp = mymsg.split(",");
        log.fine(id+ ":ticket"+ mymsg + "Paid already.");
        PayMachineController.appendTextArea("Thank you for payment!!!!");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PaymentACK, tmp[1]));
        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketRequest, mymsg));

    }


} //  PayMachineEmulator
