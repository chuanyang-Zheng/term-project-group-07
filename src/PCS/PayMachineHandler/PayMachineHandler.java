package PCS.PayMachineHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
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

    //------------------------------------------------------------
    // Pay Machine Handler Constructor
    public PayMachineHandler(String id,AppKickstarter pcss) {
        super(id, pcss);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
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
        switch (msg.getType()) {
            case TicketRequest:  SendRequest(msg.getDetails());  break;
            case TicketFee:      FeeReceive(msg.getDetails()); break;
            case PaymentACK:	 SendPaymentACK(msg.getDetails());break;
            case Terminate:	     quit = true;break;
        }
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
    protected void FeeReceive(String mymsg){
        String []str = mymsg.split(",");
        TicketFee = Float.parseFloat(str[1]);
    }
    // Receive Msg with id,fee,entertime

    //------------------------
    // Send Payment ACK
    protected void SendPaymentACK(String mymsg){
        log.fine(id+ ":ticket"+ mymsg + "Paid already.");
    }
    // Send Payment ACK

    //------------------------------------------------------------
    // PM Status
    private enum PayMachineStatus {
        GateOpened,
        GateClosed,
        GateOpening,
        GateClosing,
    }
} // Pay Machine Handler
