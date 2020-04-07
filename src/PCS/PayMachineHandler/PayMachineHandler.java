package PCS.PayMachineHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import PCS.GateHandler.GateHandler;
import PCS.PCSStarter;
import PCS.PayMachineHandler.Emulator.PayMachineController;
import PCS.PayMachineHandler.Emulator.PayMachineEmulator;


//======================================================================
// Pay Machine Handler
public class PayMachineHandler extends AppThread {
    protected final MBox pcsCore;
    private PayMachineStatus PMS;
    private PayMachineEmulator PayEmu;
    protected float TicketFee;
    protected boolean paid = false;

    //------------------------------------------------------------
    // Pay Machine Handler Constructor
    public PayMachineHandler(String id,AppKickstarter pcss) {
        super(id, pcss);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        PMS = PayMachineStatus.idle;
        paid = false;
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


    //------------------------------------------------------------
    // processMsg
    protected boolean processMsg(Msg msg) {
        boolean quit = false;
        boolean flag = false;
        String []tmp = msg.getDetails().split(",");
        if(!tmp[0].equals(id))
            return quit;
        switch (msg.getType()) {
            case TicketRequest:
                SendRequest(msg.getDetails());
                PMS = PayMachineStatus.WaitPaymentReply;
                flag = true;
            break;
            case TicketFee:
                FeeReceive(msg.getDetails());
                PMS = PayMachineStatus.WaitDriver;
                flag = true;
            break;
            case PaymentACK:
                SendPaymentACK(msg.getDetails());
                PMS = PayMachineStatus.WaitExitInfo;
                SendExitInfoRequest(msg.getDetails());
                flag = true;
            break;
            case ExitInfo:
                PMS = PayMachineStatus.WaitRemoval;
                ExitReceive(msg.getDetails());
                flag = true;
                break;
            case TicketRemoveACK:
                PMS = PayMachineStatus.idle;
                flag = true;
            break;
            case Terminate:	     quit = true;break;
        }
        if(flag)
            handleStatus();
        return quit;
    }
    // processMsg

//------------------------
// Send Fee Request
    protected void SendRequest(String mymsg){
        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketRequest, mymsg));
    }
    // Send Fee Request
    //------------------------
    // Receive Msg with id,fee,entertime
    // Send Fee Request
    protected void SendExitInfoRequest(String mymsg){
        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketExitInfoRequest, mymsg));
    }
    // Send Fee Request
    //------------------------
    // Receive Msg with id,fee,entertime
    protected void FeeReceive(String mymsg){
        String []str = mymsg.split(",");
        TicketFee = Float.parseFloat(str[1]);
    }
    // Receive Msg with id,fee,entertime

    protected void ExitReceive(String mymsg){
        log.info("Exit INfo received");
    }
    //------------------------
    // Send Payment ACK
    protected void SendPaymentACK(String mymsg){
        log.fine(id+ ":ticket"+ mymsg + "Paid already.");
    }
    // Send Payment ACK

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
