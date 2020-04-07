package PCS.VacancyHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

public class VacancyHandler extends AppThread {
    protected final MBox pcsCore;
    private  VacancyDisStatus vacancyDisStatus;

    public VacancyHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        vacancyDisStatus = VacancyDisStatus.VacancyDisRunning;
    } // VacancyDisHandler

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

    public boolean processMsg(Msg msg){
        boolean quit = false;

        switch (msg.getType()) {
            case VancancyDisUpdateRequest:  handleVacancyDisUpdateRequest(msg);  break;
//            case VancancyDisUpdateReply: handleVacancyDisUpdateReply(msg); break;
//            case Poll:		   handlePollReq();	     break;
//            case PollAck:	   handlePollAck();	     break;
            case Terminate:	   quit = true;		     break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;
    }

    public void handleVacancyDisUpdateRequest(Msg msg){
        log.info(id + ": vacancy display update request received");
        //check Receive String Is correct or not.
        switch (vacancyDisStatus){
            case VacancyDisRunning:
                log.info(id+": Vacancy is running. Show Updated Available Parking Spaces");
                String[] spliInformation=msg.getDetails().split("\\s+");
                sendVacancyDisUpdateSingal();
                break;
            default:
                log.warning(id+" unknow vacancy display status ["+vacancyDisStatus+"]");
        }
    }



    public void sendVacancyDisUpdateSingal(){

        // fixme: send Vacancy Update signal to hardware
        log.info(id + ": sending vacancy update signal to hardware.");

    }

    private enum VacancyDisStatus {
        VacancyDisRunning,
        VacancyDisTerminated
    }
}
