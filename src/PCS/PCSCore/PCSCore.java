package PCS.PCSCore;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.util.ArrayList;
import java.util.Arrays;


//======================================================================
// PCSCore
public class PCSCore extends AppThread {
    private MBox entranceGateBox;
    private MBox exitGateBox;
    private MBox collectorMbox;
    private MBox dispatcherMbox;
    private MBox vacancyMbox;
    private ArrayList<MBox> payMBox = new ArrayList<MBox>();
    private final int pollTime;
    private final int PollTimerID = 1;
    private final int openCloseGateTime;        // for demo only!!!
    private final int OpenCloseGateTimerID = 2;        // for demo only!!!
    private final int collectorSolveProblemGateWaitTimeID = 3;
    private final int ticketDispatcherTicketRemoveID=4;
    private boolean gateIsClosed = true;        // for demo only!!!
    private ArrayList<Ticket> ticketList = new ArrayList<>();
    private long exitTimeCoefficient = Long.parseLong(appKickstarter.getProperty("Ticket.exitTimeCoefficient"));
    private float calculateFeeCoefficient = Float.parseFloat(appKickstarter.getProperty("Ticket.calculateFeeCoefficient"));
    private int gateOpenWaitTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.GateOpenWaitTime"));
    private int gateOpenTime = Integer.parseInt(appKickstarter.getProperty("Gate.GateOpenTime"));
    private int totalFloorNumber=Integer.parseInt(appKickstarter.getProperty("TotalFloorNumber"));
    private int[] availableParkingSpaces=new int[totalFloorNumber];

//	private long exitTimeCoefficient=0;
//	private float calculateFeeCoefficient=0;
//	private int collectorSolveProblemGateWaitTime=0;
//	private int gateOpenTime=0;


    //------------------------------------------------------------
    // PCSCore
    public PCSCore(String id, AppKickstarter appKickstarter) throws Exception {
        super(id, appKickstarter);
        this.pollTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.PollTime"));
        this.openCloseGateTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.OpenCloseGateTime"));        // for demo only!!!
        Ticket trueTicket = new Ticket();
        trueTicket.setExitInformation(exitTimeCoefficient, "Test1", calculateFeeCoefficient);
        Ticket falseTicket = new Ticket();
        ticketList.add(trueTicket);
        ticketList.add(falseTicket);
        Ticket EricVIPTick = new Ticket();
        ticketList.add(EricVIPTick);
        for(int i=0;i<totalFloorNumber;i++){
            String tep="Vacancy.Level"+(i+1);
            availableParkingSpaces[i]=Integer.parseInt(appKickstarter.getProperty(tep));
        }
        log.info(id+" initial vacancy available floor number: "+ Arrays.toString(availableParkingSpaces));
    } // PCSCore


    //------------------------------------------------------------
    // run
    public void run() {
        Thread.currentThread().setName(id);
//	Timer.setTimer(id, mbox, pollTime, PollTimerID);
//	Timer.setTimer(id, mbox, openCloseGateTime, OpenCloseGateTimerID);	// for demo only!!!
        log.info(id + ": starting...");

        entranceGateBox = appKickstarter.getThread("EntranceGateHandler").getMBox();
        exitGateBox = appKickstarter.getThread("ExitGateHandler").getMBox();
        collectorMbox = appKickstarter.getThread("CollectorHandler").getMBox();
        dispatcherMbox = appKickstarter.getThread("DispatcherHandler").getMBox();
        vacancyMbox = appKickstarter.getThread("VacancyHandler").getMBox();
        for (int i = 0; i < appKickstarter.PayMachineNumber; i++)
            payMBox.add(appKickstarter.getThread("PayMachineHandler" + Integer.toString(i)).getMBox());
        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            try {
                switch (msg.getType()) {
                    case TimesUp:
                        handleTimesUp(msg);
                        break;

                    case GateOpenReply:
                        log.info(id + ": " + msg.getSender() + " is opened.");
                        gateIsClosed = false;
                        break;

                    case GateCloseReply:
                        log.info(id + ": " + msg.getSender() + " is closed.");
                        gateIsClosed = true;
                        break;

                    case PollAck:
                        log.info("PollAck: " + msg.getDetails());
                        break;

                    case Terminate:
                        quit = true;
                        break;

                    case CollectorValidRequest:
                        handleCollectorValidRequest(msg);
                        break;
                    case CollectorSolveProblem:
                        exitGateBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, "GateOpenReq"));
                        log.fine(id + ": Collector Solve Problem Now. Collector Is Available Now!");
                        log.info(id + ": inform Exit Gate Open");
                        Timer.setTimer(id, mbox, gateOpenTime + gateOpenWaitTime, collectorSolveProblemGateWaitTimeID);
                        break;
                    case TicketRequest:
                        log.info(id + ":PCS Sent the fee already");
                        SendTicketFee(msg);
                        break;
                    case TicketExitInfoRequest:
                        log.info(id + ":PCS Sent the Exit Info already");
                        SendExitInfo(msg);
                        break;
                    case AddTicket:
                        log.info(id + ":PCS has generated a new ticket");
                        AddTicket();
                        break;
                    case RemoveTicket:
                        log.info(id+": Ticket Dispatcher Ticket Is Removed. Open Gate. ");
                        entranceGateBox.send(new Msg(id,mbox, Msg.Type.GateOpenRequest,"OpenGate"));
                        Timer.setTimer(id,mbox,gateOpenTime + gateOpenWaitTime,ticketDispatcherTicketRemoveID);
                        break;
                    case PaymentACK:
                        log.info(id + ":Payment ACK received");
                        PayStateUpdate(msg.getSender(), msg.getDetails());
                        break;
                    case DisplayVacancyRequest:
                        log.info(id + ":Parking space change request received");
                        handleDisplayVacancyRequest();
                        break;

                    case MotionSensorDetectUp:
                        log.info(id+": MotionSensor Detect Up Message Received");
                        handleMotionSensorDetectUp(msg);
                        break;
                    case MotionSensorDetectDown:
                        log.info(id+": MotionSensor Detect Down Message Received");
                        handleMotionSensorDetectDown(msg);
                        break;

                    default:
                        log.warning(id + ": unknown message type: [" + msg + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run


    public boolean checkStringToInt(String detail) {
        try {
            Integer.parseInt(detail);
        } catch (Exception e) {
            log.warning(Msg.bracketString(id + " PCSCore") + "Integer.parseInt fails: " + Msg.quoteString(detail));
            return false;
        }
        return true;
    }

    public void PayStateUpdate(String PID, String TicketID) {
        int z = FindTicketByID(Integer.parseInt(TicketID));
        ticketList.get(z).setPayMachineID(PID);
        log.fine(id + ":Payment Updated");
    }


    public void SendTicketFee(Msg msg) {
        String[] tmp = msg.getDetails().split(",");
        try {
            int ticketIndexInTicketArrayList = FindTicketByID(Integer.parseInt(tmp[1]));//get ticket index in the ticket array list
            if (ticketIndexInTicketArrayList < 0) {
                log.warning(id + ": Find Invalid Ticket [" + Integer.parseInt(tmp[1] + "] When Calculate Fee"));
            } else {
                Ticket targetTicket = ticketList.get(ticketIndexInTicketArrayList);//get ticket
                if(ticketList.get(ticketIndexInTicketArrayList).getPayMachineID().equals(""))  // the 3rd parameter mapping
                    msg.getSenderMBox().send(new Msg(id, mbox, Msg.Type.TicketFee, tmp[0] + "," + tmp[1] + "," + Float.toString(targetTicket.calculateFee(calculateFeeCoefficient)) + "," + Long.toString(targetTicket.getEnterTime())));
                //send corresponding fee
            }
        }
        catch (Exception e){
            e.printStackTrace();
            log.warning(id+": Calculate Fee Fail");
        }
    }
    public void SendExitInfo(Msg msg) {
        String[] tmp = msg.getDetails().split(",");
        try {
            int ticketIndexInTicketArrayList = FindTicketByID(Integer.parseInt(tmp[1]));//get ticket index in the ticket array list
            if (ticketIndexInTicketArrayList < 0) {
                log.warning(id + ": Find Invalid Ticket [" + Integer.parseInt(tmp[1] + "] When Calculate Fee"));
            } else {
                Ticket targetTicket = ticketList.get(ticketIndexInTicketArrayList);//get ticket
                targetTicket.setExitInformation(exitTimeCoefficient, targetTicket.payMachineID, calculateFeeCoefficient);
                msg.getSenderMBox().send(new Msg(id, mbox, Msg.Type.ExitInfo, tmp[0] + "," + tmp[1] + "," + Float.toString(targetTicket.calculateFee(calculateFeeCoefficient)) + "," + Long.toString(targetTicket.getExitTime())));//send corresponding fee
            }
        }
        catch (Exception e){
            e.printStackTrace();
            log.warning(id+": Calculate Fee Fail");
        }
    }
    public void AddTicket() {
        //String[] tmp = msg.split(",");

        Ticket t=new Ticket();
        ticketList.add(t);
        log.fine(id + ":Ticket added");
        dispatcherMbox.send(new Msg(id,mbox,Msg.Type.ReceiveTicketID,String.valueOf(t.ticketID)));
    }

    public void handleCollectorValidRequest(Msg msg) {
        log.info(id + " Collector Valid Request Receive");
        if (checkStringToInt(msg.getDetails())) {

            boolean valid = validTicket(Integer.parseInt(msg.getDetails()));

            if (valid) {

                ticketList.remove(Integer.parseInt(msg.getDetails()));
                collectorMbox.send(new Msg(id, mbox, Msg.Type.CollectorPositive, ""));
                exitGateBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, "GateOpenReq"));
                Timer.setTimer(id, mbox, gateOpenTime + gateOpenWaitTime, collectorSolveProblemGateWaitTimeID);

                // do something. Such as:
                // delete the ticket.
                //Ask Gate to Open
                //After several seconds, ask gate to closed.

                log.fine(Msg.bracketString(id + " PCSCore valid a true ticket. Get message " + Msg.quoteString(msg.getDetails())));
            } else {
                collectorMbox.send(new Msg(id, mbox, Msg.Type.CollectorNegative, ""));
                log.warning(Msg.bracketString(id + " PCSCore valid a false ticket. Get message " + Msg.quoteString(msg.getDetails())));
            }
        } else {
            collectorMbox.send(new Msg(id, mbox, Msg.Type.CollectorNegative, ""));
            log.warning(Msg.bracketString(id + " PCSCore valid a false ticket. Get message " + Msg.quoteString(msg.getDetails())));
        }
    }

    //------------------------------------------------------------
    // run
    private void handleTimesUp(Msg msg) {
        log.info("------------------------------------------------------------");
        switch (Timer.getTimesUpMsgTimerId(msg)) {
            case PollTimerID:
                log.info("Poll: " + msg.getDetails());
                entranceGateBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                Timer.setTimer(id, mbox, pollTime, PollTimerID);
                break;

//	    case OpenCloseGateTimerID:					// for demo only!!!
//	        if (gateIsClosed) {
//		    log.info(id + ": Open the gate now (for demo only!!!)");
//		    entranceGateBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, ""));
//		} else {
//		    log.info(id + ": Close the gate now (for demo only!!!)");
//		    entranceGateBox.send(new Msg(id, mbox, Msg.Type.GateCloseRequest, ""));
//		}
            case collectorSolveProblemGateWaitTimeID:
                exitGateBox.send(new Msg(id, mbox, Msg.Type.GateCloseRequest, "Close Gate"));
                log.info(id + ": Inform Exit Gate To Close");
                break;
            case ticketDispatcherTicketRemoveID:
                entranceGateBox.send(new Msg(id,mbox,Msg.Type.GateCloseRequest,"Close Gate"));
                log.info(id+": Inform Entrance Gate To Close ");
                break;

            default:
                log.severe(id + ": why am I receiving a timeout with timer id " + Timer.getTimesUpMsgTimerId(msg));
                break;
        }
    } // handleTimesUp

    /**
     * A Function to search a ticket by TicketID
     */
    public int FindTicketByID(int TargetID) {
        for (int i = 0; i < ticketList.size(); ++i)
            if (ticketList.get(i).ticketID == TargetID)
                return i;
        log.warning(id + "No such Ticket =_= called" + TargetID);
        return -1;
    }

    public boolean validTicket(int ticketID) { // To Cheung Yeung: 这里直接用FindByID吧  因为根据ID找Ticket 在很多情况要用
        log.info(id + " valid ticket " + ticketID);
        for (int i = 0; i < ticketList.size(); i++) {
            if (ticketList.get(i).getTicketID() == ticketID) {
                return ticketList.get(i).valid(log, id);
            }
        }
        log.warning(id + ": No Ticket With ID " + ticketID);
        return false;
    }

    /**
     *
     * @param msg Contain Message From MotionSensor Up
     */
    public void handleMotionSensorDetectUp(Msg msg){
        log.info(id+": Begin Handle Detect Up");
        try{
            int floorNumber=Integer.parseInt(msg.getDetails());
            handleParkingSpaceChange(floorNumber-2,floorNumber-1);// If floor number is 3. Now, it goes up. That means, initial floor number is 2. In array, the position is 1, which is floor number -2.

        }
        catch (Exception e){
            e.printStackTrace();
            log.warning(id+": Handler MotionSensor Up Wrong.");
        }
    }

    public void handleMotionSensorDetectDown(Msg msg){
        log.info(id+": Begin Handle Detect Down");
        try{
            int floorNumber=Integer.parseInt(msg.getDetails());
            handleParkingSpaceChange(floorNumber-1,floorNumber-2);// If floor number is 3. Now, it goes up. That means, initial floor number is 2. In array, the position is 1, which is floor number -2.

        }
        catch (Exception e){
            e.printStackTrace();
            log.warning(id+": Handler MotionSensor Down Wrong.");
        }
    }



    /**
     *
     * @param initial:the floor number will decrease one available parking spaces
     * @param now: the floor number will increase one available parking space
     */
    public void handleParkingSpaceChange(int initial,int now){
        if(initial<0){
            log.fine("One driver Comes outside the parking lot");
        }
        else if (initial>totalFloorNumber-1){
            log.warning("initial in array is "+initial+". But Total Number is "+totalFloorNumber);
        }
        else {
            availableParkingSpaces[initial]=availableParkingSpaces[initial]+1;
        }

        if(now<0){
            log.fine("One driver Goes outside the parking lot");
        }
        else if (now>totalFloorNumber-1){
            log.warning("now in array is "+now+". But Total Number is "+totalFloorNumber);
        }
        else {
            availableParkingSpaces[now]=availableParkingSpaces[now]-1;
        }
    }
    public void handleDisplayVacancyRequest(){
        String vacancyMsg=String.valueOf(availableParkingSpaces[0]);
        for (int i=1;i<totalFloorNumber;i++){
            vacancyMsg+=",";
            vacancyMsg+=String.valueOf(availableParkingSpaces[i]);
        }
        vacancyMbox.send(new Msg(id,mbox,Msg.Type.VancancyDisUpdateRequest,vacancyMsg));
    }

} // PCSCore
