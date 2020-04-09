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
        Long parkedTime = 0L;
        String []currentTicket = mymsg.split(",");
        float fee = Float.parseFloat(currentTicket[2]);
        Date nowT = new Date(Long.parseLong(currentTicket[3]));
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String timestr = sdf.format(nowT);
        parkedTime = fee != 0?(System.currentTimeMillis() - Long.parseLong(currentTicket[3])) / 1000:0;
        PayMachineController.appendTextArea("You have parked " + Long.toString(parkedTime) + "h and you need to pay $" + fee + "  ($5/s)");
        PayMachineController.updateTicket(currentTicket[1],currentTicket[2],timestr);
    }
    protected void ExitReceive(String mymsg){
        String []currentTicket = mymsg.split(",");
        String ticketid = PayMachineController.TicketIDField.getText();
        String PaidFee = PayMachineController.FeeField.getText();
        Date nowT = new Date(Long.parseLong(currentTicket[3]));
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String timestr = sdf.format(nowT);
        Date ExitTime = new Date(Long.parseLong(currentTicket[3]));
        String Exitstr = sdf.format(nowT);
        PayMachineController.appendTextArea("Ticket ID: " + ticketid);
        PayMachineController.appendTextArea("Paid: " + PaidFee);
        PayMachineController.appendTextArea("Please exit before: " + timestr);
        PayMachineController.updateTicket(ticketid,"0",Exitstr);
    }
    protected void SendPaymentACK(String mymsg){
        String []tmp = mymsg.split(",");
        PayMachineController.appendTextArea("Thank you for payment!!!!");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PaymentACK, tmp[1]));
    }


} //  PayMachineEmulator
