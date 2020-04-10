package PCS.DispatcherHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import PCS.CollectorHandler.CollectorHandler;
import PCS.DispatcherHandler.Emulator.DispatcherEmulator;
import PCS.DispatcherHandler.DispatcherHandler;


//======================================================================
// DispatcherHandler
public class DispatcherHandler extends AppThread {
    protected final MBox pcsCore;
    private DispatcherStatus dispatcherStatus;


    //------------------------------------------------------------
    // Pay Machine Handler Constructor
    public DispatcherHandler(String id,AppKickstarter pcss) {
        super(id, pcss);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        dispatcherStatus=DispatcherStatus.idle;
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
        DispatcherStatus oldStatus=dispatcherStatus;
        switch (msg.getType()) {
            case AddTicket:
                SendAddTicket(msg.getDetails());
                dispatcherStatus=DispatcherStatus.waitForRemoval;
                break;

            case ReceiveTicketID:
                ReceiveTicketID(msg.getDetails());
                break;

            case RemoveTicket:
                HandleRemoveTicket(msg.getDetails());
                dispatcherStatus=DispatcherStatus.idle;
                break;

            case Terminate:	     quit = true;break;
        }
        return quit;
    }
    // processMsg

    //------------------------
// Send Fee Request
    protected void SendAddTicket(String mymsg){
        log.info("Created new Ticket");
    }
    protected void ReceiveTicketID(String mymsg){
        log.info("Ticket ID received");
    }
    protected void HandleRemoveTicket(String mymsg){
        log.info("Ticket removed");
    }



    //------------------------------------------------------------
    // PM Status
    private enum DispatcherStatus {
        idle,
        waitForRemoval
    }
} // DispatcherHandler
