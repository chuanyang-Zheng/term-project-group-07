package PCS;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;

import PCS.CollectorHandler.CollectorHandler;
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
    protected GateHandler gateHandler;
    protected CollectorHandler collectorHandler;
//    protected PayMachineHandler payHandler1,payHandler2,payHandler3;
    protected ArrayList<PayMachineHandler> PML = new ArrayList<PayMachineHandler>();

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
	// create handlers
	try {
	    timer = new Timer("timer", this);
	    pcsCore = new PCSCore("PCSCore", this);
	    gateHandler = new GateHandler("GateHandler", this);
	    collectorHandler=new CollectorHandler("CollectorHandler",this);
//		payHandler1 = new PayMachineHandler("PayMachineHandler 1",this);
//		payHandler2 = new PayMachineHandler("PayMachineHandler 2",this);
//		payHandler3 = new PayMachineHandler("PayMachineHandler 3",this);
//		PML.add(payHandler1); PML.add(payHandler2); PML.add(payHandler3);
		for(int i = 0; i < 3; i++)
			PML.add(new PayMachineHandler("PayMachineHandler " + Integer.toString(i),this));



	} catch (Exception e) {
	    System.out.println("AppKickstarter: startApp failed");
	    e.printStackTrace();
	    Platform.exit();
	}

	// start threads
	new Thread(timer).start();
	new Thread(pcsCore).start();
	new Thread(gateHandler).start();
//	new Thread(payHandler1).start();
//	new Thread(payHandler2).start();
//	new Thread(payHandler3).start();
	for(int i = 0; i < 3; i++)
			new Thread(PML.get(i)).start();

	} // startHandlers


    //------------------------------------------------------------
    // stopApp
    public void stopApp() {
	log.info("");
	log.info("");
	log.info("============================================================");
	log.info(id + ": Application Stopping...");
	pcsCore.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
	gateHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
	timer.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
    } // stopApp
} // PCS.PCSStarter
