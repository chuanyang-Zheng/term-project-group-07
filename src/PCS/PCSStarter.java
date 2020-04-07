package PCS;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;

import PCS.CollectorHandler.CollectorHandler;
import PCS.DispatcherHandler.DispatcherHandler;
import PCS.DispatcherHandler.Emulator.DispatcherEmulator;
import PCS.PCSCore.PCSCore;
import PCS.GateHandler.GateHandler;

import PCS.PayMachineHandler.Emulator.PayMachineEmulator;
import PCS.PayMachineHandler.PayMachineHandler;
import javafx.application.Platform;

import java.util.ArrayList;


//======================================================================
// PCSStarter
public class PCSStarter extends AppKickstarter {
    protected Timer timer;
    protected PCSCore pcsCore;
	protected DispatcherHandler dispatcherHandler;
    protected GateHandler exitGateHandler;
	protected GateHandler entranceGateHandler;
    protected CollectorHandler collectorHandler;
    protected ArrayList<PayMachineHandler> payMachineList = new ArrayList<PayMachineHandler>();
//	public int PayMachineNumber;
    //------------------------------------------------------------
    // main
    public static void main(String [] args) {
        new PCSStarter().startApp();
    } // main


    //------------------------------------------------------------
    // PCSStart
    public PCSStarter() {
	super("PCSStarter", "etc/PCS.cfg");
    } // PCSStart


    //------------------------------------------------------------
    // startApp
    protected void startApp() {
	// start our application
	log.info("");
	log.info("");
	log.info("============================================================");
	log.info(id + ": Application Starting...");

	startHandlers();
    } // startApp


    //------------------------------------------------------------
    // startHandlers
    protected void startHandlers() {
    	System.out.println("PCSStart!");
	// create handlers
	try {
	    timer = new Timer("timer", this);
	    pcsCore = new PCSCore("PCSCore", this);
		dispatcherHandler = new DispatcherHandler("DispatcherHandler", this);
	    exitGateHandler = new GateHandler("ExitGateHandler", this);
	    entranceGateHandler=new GateHandler("ExitGateHandler",this);
	    collectorHandler=new CollectorHandler("CollectorHandler",this);
		for(int i = 0; i < PayMachineNumber; i++)
			payMachineList.add(new PayMachineHandler("PayMachineHandler " + Integer.toString(i),this));



	} catch (Exception e) {
	    System.out.println("AppKickstarter: startApp failed");
	    e.printStackTrace();
	    Platform.exit();
	}

	// start threads
	new Thread(timer).start();
	new Thread(pcsCore).start();
	new Thread(dispatcherHandler).start();
	new Thread(exitGateHandler).start();
	for(int i = 0; i < PayMachineNumber; i++)
			new Thread(payMachineList.get(i)).start();

	} // startHandlers


    //------------------------------------------------------------
    // stopApp
    public void stopApp() {
	log.info("");
	log.info("");
	log.info("============================================================");
	log.info(id + ": Application Stopping...");
	pcsCore.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
	exitGateHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
	entranceGateHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
	collectorHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
	for(int i=0;i<payMachineList.size();i++){
		payMachineList.get(i).getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
	}
	timer.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
    } // stopApp
} // PCS.PCSStarter
