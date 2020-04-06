package PCS.DispatcherHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import PCS.DispatcherHandler.Emulator.DispatcherEmulator;
import PCS.DispatcherHandler.DispatcherHandler;


//======================================================================
// DispatcherHandler
public class DispatcherHandler extends AppThread {
    protected final MBox pcsCore;
    private DispatcherHandler.DispatcherStatus PMS;
    private DispatcherEmulator PayEmu;
    protected float TicketFee;

    //------------------------------------------------------------
    // Pay Machine Handler Constructor
    public DispatcherHandler(String id,AppKickstarter pcss) {
        super(id, pcss);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
    } // Dispatcher Handler Constructor


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
        String []tmp = mymsg.split(",");
        TicketFee = Float.parseFloat(tmp[1]);
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
    private enum DispatcherStatus {
        DispatcherOpened,
        DispatcherClosed,
        DispatcherOpening,
        DispatcherClosing,
    }
} // DispatcherHandler
