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
            case AddTicket:  SendAddTicket(msg.getDetails());  break;

            case Terminate:	     quit = true;break;
        }
        return quit;
    }
    // processMsg

    //------------------------
// Send Fee Request
    protected void SendAddTicket(String mymsg){
        pcsCore.send(new Msg(id, mbox, Msg.Type.AddTicket, mymsg));

    }


    //------------------------------------------------------------
    // PM Status
    private enum DispatcherStatus {
        DispatcherOpened,
        DispatcherClosed,
        DispatcherOpening,
        DispatcherClosing,
    }
} // DispatcherHandler
