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
                handleAddTicket(msg);
                break;

            case ReceiveTicketID:
                handleReceiveTicketID(msg);
                break;

            case RemoveTicket:
                handleRemoveTicket(msg);
                dispatcherStatus=DispatcherStatus.idle;
                break;

            case Terminate:	     quit = true;break;
        }
        return quit;
    }
    // processMsg

    /**
     *Handle AddTicket
     * @param msg Msg Received from processMsg when AddTicket Msg Type Receive
     */
    protected void handleAddTicket(Msg msg){
        log.info(id+": Handle Add Ticket");
        DispatcherStatus oldStatus=dispatcherStatus;
        switch (dispatcherStatus){
            case idle:
                SendAddTicket("Created new Ticket");
                pcsCore.send(new Msg(id, mbox, Msg.Type.AddTicket, ""));
                dispatcherStatus=DispatcherStatus.waitPCSCoreReply;
                break;
            case waitPCSCoreReply:
                log.warning(id+": Dispatcher is WaitPCSCoreReply! Ignore Add Ticket");
                break;
            case waitForRemoval:
                log.warning(id+": Dispatcher is WaitForRemoval! Ignore Add Ticket");
                break;
        }
        log.info(id+": Dispatcher status from ["+oldStatus+"] to ["+dispatcherStatus+"]");
    }

    protected void handleReceiveTicketID(Msg msg){
        log.info(id+": Handle Receive Ticket ID");
        DispatcherStatus oldStatus=dispatcherStatus;
        switch (dispatcherStatus){
            case idle:
                log.warning(id+": Dispatcher is Idle. Ignore Receive Ticket ID");
                break;
            case waitPCSCoreReply:
                ReceiveTicketID(id+" Receive Ticket ID");
                dispatcherStatus=DispatcherStatus.waitForRemoval;
                break;
            case waitForRemoval:
                log.warning(id+": Dispatcher is WaitForRemoval! Ignore Reply");
                break;
        }
        log.info(id+": Dispatcher status from ["+oldStatus+"] to ["+dispatcherStatus+"]");
    }

    protected void handleRemoveTicket(Msg msg){
        log.info(id+": Handle Remove");
        DispatcherStatus oldStatus=dispatcherStatus;
        switch (dispatcherStatus){
            case idle:
                log.warning(id+": Dispatcher is Idle. Ignore Remove Ticket");
                break;
            case waitPCSCoreReply:
                log.warning(id+": Dispatcher is Wait PCSCore Reply. Ignore Remove Ticket");
                break;
            case waitForRemoval:
                log.fine("Remove Ticket");
                dispatcherStatus=DispatcherStatus.idle;
                break;
        }
        log.info(id+": Dispatcher status from ["+oldStatus+"] to ["+dispatcherStatus+"]");
    }

    protected void SendAddTicket(String mymsg){
        log.info("Creating new Ticket");
    }
    protected void ReceiveTicketID(String mymsg){
        log.info("Ticket ID received");
    }



    //------------------------------------------------------------
    // PM Status
    private enum DispatcherStatus {
        idle,
        waitPCSCoreReply,
        waitForRemoval
    }
} // DispatcherHandler
