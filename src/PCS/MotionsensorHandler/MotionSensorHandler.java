package PCS.MotionsensorHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import PCS.GateHandler.GateHandler;

public class MotionSensorHandler extends AppThread {
    protected final MBox pcsCore;
    private MotionSensorStatus motionSensorStatus;
    private int floorNumber;
    private boolean detectUp;

    public MotionSensorHandler(String id, AppKickstarter appKickstarter, int floorNumber, boolean detectUp) {
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        motionSensorStatus = MotionSensorStatus.MotionSensorRunning;
        this.floorNumber=floorNumber;
        this.detectUp=detectUp;
    } // MotionSensorHandler

    /**
     * @param Input: Nothing.
     * Return:Nothing.
     * @Author Yijia Zhang
     * @description run Part of handler. It deal with logic commannds
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

    protected boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
//            case GateOpenRequest:  handleGateOpenRequest();  break;
//            case GateCloseRequest: handleGateCloseRequest(); break;
//            case GateOpenReply:	   handleGateOpenReply();    break;
//            case GateCloseReply:   handleGateCloseReply();   break;
//            case Poll:		   handlePollReq();	     break;
//            case PollAck:	   handlePollAck();	     break;
            case MotionSensorDetect:
                handleMotionSensorDetect(msg);
            case Terminate:	   quit = true;		     break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;
    } // processMsg

    /**
     * @param
     */
    public void handleMotionSensorDetect(Msg msg){
        log.info(id + ": motion sensor detect message received");
        switch (motionSensorStatus){
            case MotionSensorRunning:
                if(this.detectUp)
                    pcsCore.send(new Msg(id,mbox, Msg.Type.MotionSensorDetectUp,floorNumber+""));
                else
                    pcsCore.send(new Msg(id,mbox,Msg.Type.MotionSensorDetectDown,floorNumber+""));
            case MotionSensorTerminated:
                log.warning(id+" is terminated. Please Check");
        }
    }

    private enum MotionSensorStatus {
        MotionSensorRunning,
        MotionSensorTerminated
    }
}
