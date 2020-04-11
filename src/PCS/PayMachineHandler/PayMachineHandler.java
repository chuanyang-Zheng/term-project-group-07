package PCS.PayMachineHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import PCS.GateHandler.GateHandler;
import PCS.PCSStarter;
import PCS.PayMachineHandler.Emulator.PayMachineController;
import PCS.PayMachineHandler.Emulator.PayMachineEmulator;
import javafx.scene.control.Alert;


//======================================================================
// Pay Machine Handler
public class PayMachineHandler extends AppThread {
    protected final MBox pcsCore;
    private PayMachineStatus PMS;

    //------------------------------------------------------------
    // Pay Machine Handler Constructor
    public PayMachineHandler(String id,AppKickstarter pcss) {
        super(id, pcss);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        PMS = PayMachineStatus.idle;
    } // Pay Machine Handler Constructor


    //------------------------------------------------------------
    // run
    public void run() {
        Thread.currentThread().setName(id);
        log.info(id + ": starting...");

        for (boolean quit = false; !quit;) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            quit = processMsg(msg);
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run


    /**
     * A Function to process the recieved Message
     *
     * @param msg:The received message
     * Switch the message type to different cases
     * Handle it case by case
     * @author Pan Feng
     */
    protected boolean processMsg(Msg msg) {
        boolean quit = false;
        boolean flag = false;
        String []tmp = msg.getDetails().split(",");
        switch (msg.getType()) {
            case TicketRequest:
                if(PMS == PayMachineStatus.idle) {
                    SendRequest(msg.getDetails());
                    PMS = PayMachineStatus.WaitPaymentReply;
                    flag = true;
                }
            break;
            case TicketFee:
                if(PMS == PayMachineStatus.WaitPaymentReply) {
                    FeeReceive(msg.getDetails());
                    PMS = PayMachineStatus.WaitDriver;
                    flag = true;
                }
            break;
            case PaymentACK:
                if(PMS == PayMachineStatus.WaitDriver) {
                    SendPaymentACK(msg.getDetails());
                    PMS = PayMachineStatus.WaitExitInfo;
                    SendExitInfoRequest(msg.getDetails());
                    flag = true;
                }
            break;
            case ExitInfo:
                if(PMS == PayMachineStatus.WaitExitInfo) {
                    PMS = PayMachineStatus.WaitRemoval;
                    ExitReceive(msg.getDetails());
                    flag = true;
                }
                break;
            case TicketRemoveACK:
                if(PMS == PayMachineStatus.WaitRemoval) {
                    PMS = PayMachineStatus.idle;
                    flag = true;
                    RemovalFinished();
                }
            break;
            case Terminate:	     quit = true;break;
        }
        if(flag)
            handleStatus();
        else
            log.warning(id + " is " + PMS + " cannot do it!!!");
        return quit;
    }
    // processMsg

    /**
     * A Function to process the recieved Message
     *
     * @param mymsg:The received message
     * Send the request to PCSCore for fee.
     * @author Pan Feng
     */
    protected void SendRequest(String mymsg){
        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketRequest, mymsg));
    }
    /**
     * A Function to process the recieved Message
     *
     * @param mymsg:The received message
     * Send the request to PCSCore for ExitInfo.
     * @author Pan Feng
     */
    protected void SendExitInfoRequest(String mymsg){
        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketExitInfoRequest, mymsg));
    }
    /**
     * A Function to display reminder
     * Display the Ticket is Removed
     * @author Pan Feng
     */
    protected void RemovalFinished(){
        log.info(id + "Ticket Removed");
    }
    /**
     * A Function to handle the Fee message
     *  Display the Fee is received
     * @author Pan Feng
     */
    protected void FeeReceive(String mymsg){
        String []str = mymsg.split(",");
    }
    /**
     * A Function to handle the Fee message
     *  Display the Fee is received
     * @author Pan Feng
     */
    protected void ExitReceive(String mymsg){
        log.info("Exit INfo received");
    }
    //------------------------
    // Send Payment ACK
    protected void SendPaymentACK(String mymsg){
        log.fine(id+ ":ticket"+ mymsg + "Paid already.");
    }
    // Send Payment ACK
    /**
     * A Function to Display the PayMachine Status case by case
     * @author Pan Feng
     */
    protected final void handleStatus() {

        PayMachineStatus oldStatus = PMS;
        switch (PMS) {
            case idle:
                log.info(id + ": I am idle please use me.");
                break;

            case WaitPaymentReply:
                log.info(id + ": I am waiting for Ticket Info");
                break;

            case WaitDriver:
                log.info(id + ": Waiting for driver's Payment");
                break;
            case WaitExitInfo:
                log.info(id + ": Waiting for ExitInfo");
                break;
            case WaitRemoval:
                log.info(id + ": Waiting for Removal");
                break;
        }

        if (oldStatus != PMS) {
            log.fine(id + ": gate status change: " + oldStatus + " --> " + PMS);
        }
    } // handleGateOpenRequest




    //------------------------------------------------------------
    // PM Status
    private enum PayMachineStatus {
        idle,
        WaitPaymentReply,
        WaitDriver,
        WaitExitInfo,
        WaitRemoval
    }
} // Pay Machine Handler
