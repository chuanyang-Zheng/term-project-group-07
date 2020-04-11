package PCS.PCSCore;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;
import PCS.PayMachineHandler.Emulator.PayMachineController;

import java.util.ArrayList;
import java.util.Arrays;


//======================================================================
// PCSCore

/**
 * PCSCore CLass Connected ALL Handlers
 * @author Chuanyang Zheng(17251311)
 * @author Pan Feng (19205945)
 */
public class PCSCore extends AppThread {
    /**
     * Entrance Gate Box
     */
    private MBox entranceGateBox;
    /**
     * Exit Gate Box
     */
    private MBox exitGateBox;

    /**
     * Collector Box
     */
    private MBox collectorMbox;

    /**
     * Dispatcher Box
     */
    private MBox dispatcherMbox;

    /**
     * Vacancy Box
     */
    private MBox vacancyMbox;

    /**
     * payMachine Box
     */
    private ArrayList<MBox> payMBox = new ArrayList<MBox>();

    /**
     *Poll Time
     */
    private final int pollTime;

    /**
     * Pool Time ID will be used for Timer weak Up
     */
    private final int PollTimerID = 1;

    /**
     * For Demo
     */
    private final int openCloseGateTime;        // for demo only!!!

    /**
     * For Demo
     */
    private final int OpenCloseGateTimerID = 2;        // for demo only!!!

    /**
     * Will be used for asking exit Gate to open, when collector solves problems
     */
    private final int collectorSolveProblemGateWaitTimeID = 3;

    /**
     * Will be used for asking entrance gate to close, when a driver removes ticket
     */
    private final int ticketDispatcherTicketRemoveID=4;

    /**
     * For Demo
     */
    private boolean gateIsClosed = true;        // for demo only!!!

    /**
     * Ticket List
     */
    private ArrayList<Ticket> ticketList = new ArrayList<>();

    /**
     * Exit Time=Insert Cart Time + exitTimeCoefficient. If necessary, change it in PCS.cfg
     */
    private long exitTimeCoefficient = Long.parseLong(appKickstarter.getProperty("Ticket.exitTimeCoefficient"));

    /**
     * Calculating Parking Fee. If necessary, change it in PCS.cfg
     */
    private float calculateFeeCoefficient = Float.parseFloat(appKickstarter.getProperty("Ticket.calculateFeeCoefficient"));

    /**
     * The time gate starting closing after opening. If necessary, open it in PCS.cfg
     */
    private int gateOpenWaitTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.GateOpenWaitTime"));

    /**
     * The time gate using to open. If necessary, open it in PCS.cfg
     */
    private int gateOpenTime = Integer.parseInt(appKickstarter.getProperty("Gate.GateOpenTime"));

    /**
     * The number of floors. If necessary, open it in PCS.cfg
     */
    private int totalFloorNumber=Integer.parseInt(appKickstarter.getProperty("TotalFloorNumber"));

    /**
     * Number of available parking spaces in each floor
     */
    private int[] availableParkingSpaces=new int[totalFloorNumber];

    /**
     * The number of pay machines
     */
    public int PayMachineNumber;
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
        PayMachineNumber = Integer.parseInt(appKickstarter.getProperty("PayMachineNumber"));
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
        for (int i = 0; i < PayMachineNumber; i++)
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
    //------------------------------------------------------------
    // PayStateUpdate
    /**
     *
     * @param PID is the Paymachine ID
     * @param TicketID is the Ticket ID
     * Set the Paymachine ID to this ticket
     */
    public void PayStateUpdate(String PID, String TicketID) {
        int z = FindTicketByID(Integer.parseInt(TicketID));
        ticketList.get(z).setPayMachineID(PID);
        log.fine(id + ":Payment Updated");
    }//PayStateUpdate

    //------------------------------------------------------------
    // SendTicketFee
    /**
     * A Function to send the TicketFee
     *
     * @param msg:The received message
     * Find the specified Ticket
     * Calculated the current Fee of that ticket
     * Send the Fee to the Paymachine
     * @author Pan Feng
     */
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
                //SendTicketFee
            }
        }
        catch (Exception e){
            e.printStackTrace();
            log.warning(id+": Calculate Fee Fail");
        }
    }
    //------------------------------------------------------------
    // SendExitInfo
    /**
     * A Function to send the EXit Information
     *
     * @param msg:The received message
     * Find the specified Ticket and send the exitinfo to paymachine handler
     * @author Pan Feng
     */
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
    }//SendExitInfo

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

            boolean valid = validTicket(Integer.parseInt(msg.getDetails()));// bug

            if (valid) {
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
    //------------------------------------------------------------
    // FindTicketByID
    /**
     * A Function to search a ticket by TicketID
     *
     * @param TargetID:The Ticket ID
     * @return If one ticket has the TargetID, return the position index of the Ticket in the TicketList. Else, return -1
     * @author Pan Feng
     */
    public int FindTicketByID(int TargetID) {
        for (int i = 0; i < ticketList.size(); ++i)
            if (ticketList.get(i).ticketID == TargetID)
                return i;
        log.warning(id + "No such Ticket =_= called" + TargetID);
        return -1;
    }//FindTicketByID

    public boolean validTicket(int ticketID) {
        log.info(id + " valid ticket " + ticketID);
        int indexOfTicket = FindTicketByID(ticketID);
        if(indexOfTicket > -1)
                return ticketList.get(indexOfTicket).valid(log, id);
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
            vacancyMbox.send(new Msg(id,mbox, Msg.Type.VacancyDisUpdateRequest,arrayToString(availableParkingSpaces)));

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
            vacancyMbox.send(new Msg(id,mbox, Msg.Type.VacancyDisUpdateRequest,arrayToString(availableParkingSpaces)));

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
            availableParkingSpaces[initial]=Math.min(availableParkingSpaces[initial]+1,Integer.parseInt(appKickstarter.getProperty("Vacancy.Level"+(initial+1))));
        }

        if(now<0){
            log.fine("One driver Goes outside the parking lot");
        }
        else if (now>totalFloorNumber-1){
            log.warning("now in array is "+now+". But Total Number is "+totalFloorNumber);
        }
        else {
            availableParkingSpaces[now]=Math.max(availableParkingSpaces[now]-1,0);
        }
    }
    public void handleDisplayVacancyRequest(){
        String vacancyMsg=String.valueOf(availableParkingSpaces[0]);
        for (int i=1;i<totalFloorNumber;i++){
            vacancyMsg+=",";
            vacancyMsg+=String.valueOf(availableParkingSpaces[i]);
        }
        vacancyMbox.send(new Msg(id,mbox,Msg.Type.VacancyDisUpdateRequest,vacancyMsg));
    }

    public String arrayToString(int[] arrayInformation){
        StringBuilder tep= new StringBuilder();
        for(int i=0;i<arrayInformation.length;i++){
            tep.append(arrayInformation[i]).append(" ");
        }
        return tep.toString();
    }

//    public void resetAvailableParkingSpace(int floorNumebr,int[] )

} // PCSCore
