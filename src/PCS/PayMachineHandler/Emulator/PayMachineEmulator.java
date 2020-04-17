package PCS.PayMachineHandler.Emulator;

import AppKickstarter.misc.*;
import PCS.PCSStarter;
import PCS.PayMachineHandler.PayMachineHandler;
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
// PayMachineEmulator
public class PayMachineEmulator extends PayMachineHandler {
    private Stage myStage;
    private PayMachineController PayMachineController;
    private final PCSStarter pcsStarter;
    private final String id;

    /**
     * Auto Poll
     */
    private boolean autoPoll;

    //------------------------------------------------------------
    //  PayMachineEmulator
    public PayMachineEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
        this.autoPoll = true;
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
    } //  start
    @Override
    protected final boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
            case EmulatorAutoPollToggle:
                handlePayMachineEmulatorAutoPollToggle();
                break;
            default:
                quit = super.processMsg(msg);
        }
        return quit;
    } // processMsg
    //------------------------------------------------------------
    // FeeReceive
    /**
     * Override the FeeReceive()
     *  Display the Ticket the ticket information in the buttom "textbox".
     *  Update the Ticket Information information in textarea.
     *  Protocol message format: PayMachine ID,Ticket ID,TicketFee,EnterTime
     * @param mymsg received message
     * @author Pan Feng
     */
    protected void FeeReceive(String mymsg){
        Long parkedTime = 0L; // initialization
        String []currentTicket = mymsg.split(","); // proccess the protocal;
        float fee = Float.parseFloat(currentTicket[2]); // get the fee from message
        Date nowT = new Date(Long.parseLong(currentTicket[3])); // get the time from message
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss"); // set the time format
        String timestr = sdf.format(nowT); // set the time format
        parkedTime = fee != 0?(System.currentTimeMillis() - Long.parseLong(currentTicket[3])) / 1000:0; //is the payment is done ,no parking time,otherwise calculate the parking time
        PayMachineController.appendTextArea("You have parked " + Long.toString(parkedTime) + "s and you need to pay $" + fee + "  ($"+parkingFeeCoefficient+"/s)");
        PayMachineController.updateTicket(currentTicket[1],currentTicket[2],timestr); // update the display of upper textArea
    }// FeeReceive

    //------------------------------------------------------------
    // ExitReceive
    /**
     * Override the ExitReceive()
     *  Display the Ticket the ticket information in the buttom "textbox".
     *  Update the Ticket Information information in textarea.
     *  Protocol message format: PayMachine ID,Ticket ID,TicketFee,ExitTime
     * @param mymsg Received message
     * @author Pan Feng
     */
    protected void ExitReceive(String mymsg){
        String []currentTicket = mymsg.split(","); // process the protocal message
        String ticketid = PayMachineController.TicketIDField.getText(); // get the ticket ID
        String PaidFee = PayMachineController.FeeField.getText();// get the Fee
        Date nowT = new Date(Long.parseLong(currentTicket[3])); // get the Exit time
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String timestr = sdf.format(nowT); // Date format
        PayMachineController.appendTextArea("Ticket ID: " + ticketid);
        PayMachineController.appendTextArea("Paid: " + PaidFee);
        PayMachineController.appendTextArea("Please exit before: " + timestr); // Display in textBox
        PayMachineController.updateTicket(ticketid,"0",timestr); // Update the TextArea
    }// ExitReceive

    //------------------------------------------------------------
    // SendPaymentACK
    /**
     * Override the SendPaymentACK()
     *  Display the Ticket the ticket information in the buttom "textbox".
     *  Send the Payment finished Acknowledgement to PCSCore
     *  Protocol message format is PaymachineID,TicketID
     * @param mymsg Received message
     * @author Pan Feng
     */
    protected void SendPaymentACK(String mymsg){
        String []tmp = mymsg.split(",");
        PayMachineController.appendTextArea("Thank you for payment!!!!");
        PayMachineController.appendTextArea("Please remove your ticket :)"); // Display the reminder in TextBox
        pcsCore.send(new Msg(id, mbox, Msg.Type.PaymentACK, tmp[1])); // Send the Payment ACK to PCSCore
    }//SendPaymentACK

    //------------------------------------------------------------
    // RemovalFinished
    /**
     * Override the RemovalFinished()
     *  Display the Ticket the ticket information in the buttom "textbox".
     *
     * @author Pan Feng
     */
    protected void RemovalFinished(){
        PayMachineController.appendTextArea("Ticket removed!");
        PayMachineController.appendTextArea("Please exit before exit time~"); // Display the reminder in TextBox
    }//RemovalFinished
    //------------------------------------------------------------
    // handlePayMachineEmulatorAutoPollToggle:

    /**
     * Handle Pay Machine Emulator Auto Poll Toggle
     * @return return autoPoll
     * @author Pan Feng
     */
    public final boolean handlePayMachineEmulatorAutoPollToggle() {
        autoPoll = !autoPoll;
        log.info("Auto poll change: " + (autoPoll ? "off --> on" : "on --> off"));
        return autoPoll;
    } // handleGateEmulatorAutoPollToggle

    //------------------------------------------------------------
    // sendPollReq
    @Override
    protected void sendPollReq() {
        logFine("Poll request received.  [autoPoll is " + (autoPoll ? "on]" : "off]"));
        if (autoPoll) {
            logFine("Send poll ack.");
            mbox.send(new Msg(id, mbox, Msg.Type.PollAck, ""));
        }
    } // sendPollReq

    //------------------------------------------------------------
    // logFine

    /**
     * Log Fine Type Information and add it to Controller
     * @param logMsg Log Information
     * @author Pan Feng
     */
    private final void logFine(String logMsg) {
        PayMachineController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine

} //  PayMachineEmulator
