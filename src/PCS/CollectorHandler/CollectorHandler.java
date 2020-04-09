package PCS.CollectorHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

/**
 * @description Ticket Collector Class.
 * @Author Chuanyang Zheng
 */
public class CollectorHandler extends AppThread {
    protected final MBox pcsCore;
    private CollectorStatus collectorStatus;

    /**
     *
     * @param id:A String ID of the Collector. For example, CollecotrHandler
     * @param appKickstarter: An appKickstarter
     * @description CollectorHandler Constructor
     * @Author Chuanyang Zheng
     */
    public CollectorHandler(String id, AppKickstarter appKickstarter){
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        collectorStatus = CollectorStatus.CollectorAvailable;
    }


    /**
     * @description A method used to receive and judge Msg type
     * @Author Chuanyang Zheng
     */
    public void run() {
        Thread.currentThread().setName(id);
        log.info(id + ": starting...");

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            quit = processMsg(msg);
        }
    }


    /**
     * @param msg:a message received
     * @return if message type is terminated, return false. Else, return true
     * @description : A method used to process message.
     * @Author Chuanyang Zheng
     */
    public boolean processMsg(Msg msg){
        boolean quit = false;

        switch (msg.getType()) {
            case CollectorValidRequest:  handleCollectorValidRequest(msg);  break;
            case CollectorPositive:   handleCollectorPositive();   break;
            case CollectorNegative: handleCollectorNegative(); break;
            case CollectorSolveProblem:	   handleCollctorSolveProblem();    break;
            case Poll:		   handlePollReq();	     break;
            case PollAck:	   handlePollAck();	     break;
            case Terminate:	   quit = true;		     break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;
    }

    /**
     * @param msg:A msg received from processMsg(Msg msg) method
     * @description Handle Collector Valid Request. First Check Collector Status. If Collector is available, send valid request to PCSCore
     * @Author Chuanyang Zheng
     */
    public void handleCollectorValidRequest(Msg msg){
        log.info(id + ": collector valid request received");
        CollectorStatus oldStatus=collectorStatus;

        switch (collectorStatus){
            case CollectorAvailable:
                int ticketID = -1;
                try {
                    try {
                        ticketID = Integer.parseInt(msg.getDetails());
                    } catch (Exception e) {
                        throw new Exception(id + ": collector receive invalid message: [" + msg.getDetails() + "]");
                    }
                    pcsCore.send(new Msg(id, mbox, Msg.Type.CollectorValidRequest, msg.getDetails()));
                    collectorStatus=CollectorStatus.CollectorWaitValidation;
                } catch (Exception e) {
                    log.warning(e.getMessage());
                    e.printStackTrace();
                }
                break;

            case CollectorWaitValidation:
                log.warning(id+" Collector is waiting validation result. Ignore Validation Request");
                break;
            case CollectorWarning:
                log.warning(id+" Collector is warning. Ignore Validation Request");
        }
        log.fine(id+": Collector Status from "+oldStatus+"-> "+collectorStatus);
    }

    //** Input:Nothing*/
    //** Return:Nothing*/
    //A simple function. The ticket is valid. Therefore, we log received message.

    /**
     * @description The ticket is valid. Therefore, we log received message.
     * @Author Chuanyang Zheng
     */
    public void handleCollectorPositive(){
        log.info(id + ": collector receive positive validation");
        CollectorStatus oldStatus=collectorStatus;
        switch (collectorStatus){
            case CollectorAvailable:
                log.warning(id+": Collector is Available Now. Wrong State!");
                break;
            case CollectorWaitValidation:
                collectorStatus=CollectorStatus.CollectorAvailable;
                sendPositiveSignal();
                break;
            case CollectorWarning:
                log.warning(id+": Collector is Warning Now. Wrong State!");
                break;
        }
        log.fine(id+": Collector Status from "+oldStatus+"-> "+collectorStatus);
    }

    //**Input:Nothing*/
    //**Return Nothing*/
    //PCS believe that the ticket is invalid. Therefore, in the method, ring alrams, ask staff to solve problem.

    /**
     * @description PCS believe that the ticket is invalid. Therefore, in the method, ring alrams, ask staff to solve problem
     * @Author Chuanyang Zheng
     */
    public void handleCollectorNegative(){
        log.info(id+": collector receive negative validation");
        CollectorStatus oldStatus=collectorStatus;
        switch (collectorStatus){
            case CollectorAvailable:
                log.warning(id+": Collector is Available Now. Wrong State!");
                break;
            case CollectorWaitValidation:
                sendStartAlarmSignal();
                collectorStatus=CollectorStatus.CollectorWarning;
                break;
            case CollectorWarning:
                log.warning(id+": Collector is Warning Now. Wrong State");
        }
        log.fine(id+": Collector Status from "+oldStatus+"-> "+collectorStatus);
    }

    //**Input:No thing
    //**Return: Nothing
    //**After solve problem, we use the function to tell PCS Core.

    /**
     * @description After solve problem, we use the function to tell PCS Core.
     * @Author Chuanyang Zheng
     */
    public void handleCollctorSolveProblem(){
        log.info(id+": collector receive Problem-Solve Message");
        CollectorStatus oldStatus=collectorStatus;
        switch (collectorStatus){
            case CollectorAvailable:
                log.warning(id+": collector is Now "+collectorStatus+" Wrong State!");
                break;
            case CollectorWaitValidation:
                log.warning(id+": collector is Now "+collectorStatus+" Wrong State!");
                break;
            case CollectorWarning:
                sendStopAlarmSignal();
                pcsCore.send(new Msg(id,mbox, Msg.Type.CollectorSolveProblem,""));
                log.info(id+" Inform PCSCore than already solve problem ");
                collectorStatus=CollectorStatus.CollectorAvailable;
                break;
        }
        log.fine(id+": Collector Status from "+oldStatus+"-> "+collectorStatus);

    }

    /**
     * @description Send Ring Alarm Signal To Collector Controller.
     * @Author Chuanyang Zheng
     */
    protected void sendStartAlarmSignal(){
        log.fine(id+" Ring Alarm!");
    }

    /**
     * @description Solve Problem, Send Stop Alarm Signal To Collector
     * @Author Chuanyang Zheng
     */
    protected void sendStopAlarmSignal(){
        log.fine(id+": Stop Alarm!");
    }

    /**
     * @description Handle Pool Request
     * @Author Chuanyang Zheng
     */
    protected final void handlePollReq() {
        log.info(id + ": poll request received.  Send poll request to hardware.");
        sendPollReq();
    } // handlePollReq

    /**
     * @description Send Pool Request
     * @Author Chuanyang Zheng
     */
    protected void sendPollReq() {
        // fixme: send gate poll request to hardware
        log.info(id + ": poll request received");
    } // sendPollReq

    /**
     * @description PCSCore Valid A true Ticket. CollectorHandler Sends Signal To Collector
     * @Author Chuanyang Zheng
     */
    protected void sendPositiveSignal(){
        log.info(id+": Ticket is Valid!");
    }


    //------------------------------------------------------------
    // handlePollAck

    /**
     * @description handler Pool Acknowledgement
     * @Author Chuanyang Zheng
     */
    protected final void handlePollAck() {
        log.info(id + ": poll ack received.  Send poll ack to PCS Core.");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
    } // handlePollAck

    /**
     * @description Collector Status. Totally Three:CollectorAvailable,CollectorWaitValidation,CollectorWarning
     * @Author Chuanyang Zheng
     */
    private enum CollectorStatus {
        CollectorAvailable,
        CollectorWaitValidation,
        CollectorWarning,
    }
}
