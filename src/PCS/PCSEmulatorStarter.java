package PCS;

import AppKickstarter.timer.Timer;

import PCS.CollectorHandler.CollectorHandler;
import PCS.CollectorHandler.Emulator.CollectorEmulator;
import PCS.PCSCore.PCSCore;
import PCS.GateHandler.GateHandler;
import PCS.GateHandler.Emulator.GateEmulator;
import PCS.DispatcherHandler.Emulator.DispatcherEmulator;
import PCS.DispatcherHandler.DispatcherHandler;

import PCS.PayMachineHandler.Emulator.PayMachineEmulator;
import PCS.PayMachineHandler.PayMachineHandler;
import PCS.VacancyHandler.Emulator.VacancyEmulator;
import PCS.VacancyHandler.VacancyHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.ArrayList;

//======================================================================
// PCSEmulatorStarter
public class PCSEmulatorStarter extends PCSStarter {
    //------------------------------------------------------------
    // main
    public static void main(String [] args) {
	new PCSEmulatorStarter().startApp();
    } // main


    //------------------------------------------------------------
    // startHandlers
    @Override
    protected void startHandlers() {
        Emulators.pcsEmulatorStarter = this;
        new Emulators().start();
    } // startHandlers


    //------------------------------------------------------------
    // Emulators
    public static class Emulators extends Application {
        private static PCSEmulatorStarter pcsEmulatorStarter;
        private int PayMachineNumber = pcsEmulatorStarter.PayMachineNumber;

	//----------------------------------------
	// start
        public void start() {
            launch();
	} // start

	//----------------------------------------
	// start
        public void start(Stage primaryStage) {
	    Timer timer = null;
	    PCSCore pcsCore = null;
	    DispatcherEmulator dispatcherEmulator=null;
	    GateEmulator entranceGateEmulator = null;
        GateEmulator exitGateEmulator = null;
        CollectorEmulator collectorEmulator=null;
	    PayMachineEmulator payMachineEmulator = null;
	    VacancyEmulator vacancyEmulator=null;
	    ArrayList<PayMachineEmulator> payMachineEmulatorList = new ArrayList<PayMachineEmulator>();
	    // create emulators
	    try {
	        timer = new Timer("timer", pcsEmulatorStarter);
	        pcsCore = new PCSCore("PCSCore", pcsEmulatorStarter);
            dispatcherEmulator=new DispatcherEmulator("DispatcherHandler",pcsEmulatorStarter);
            collectorEmulator=new CollectorEmulator("CollectorHandler",pcsEmulatorStarter);
            int availableSpaces[]={};
            vacancyEmulator=new VacancyEmulator("VacancyHandler", pcsEmulatorStarter);
	        entranceGateEmulator = new GateEmulator("EntranceGateHandler", pcsEmulatorStarter);
            exitGateEmulator = new GateEmulator("ExitGateHandler", pcsEmulatorStarter);
//            payMachineEmulator = new PayMachineEmulator("PayMachineHandler",pcsEmulatorStarter);
            for(int i = 0; i < PayMachineNumber; i++)
                payMachineEmulatorList.add(new PayMachineEmulator("PayMachineHandler" + Integer.toString(i), pcsEmulatorStarter));

            // start emulator GUIs
            dispatcherEmulator.start();
		entranceGateEmulator.start();
		exitGateEmulator.start();
		collectorEmulator.start();
		vacancyEmulator.start();
//		payMachineEmulator.start();
		for(int i = 0; i < PayMachineNumber; i++)
                payMachineEmulatorList.get(i).start();
	    } catch (Exception e) {
		System.out.println("Emulators: start failed");
		e.printStackTrace();
		Platform.exit();
	    }
	    pcsEmulatorStarter.setTimer(timer);
	    pcsEmulatorStarter.setPCSCore(pcsCore);
	    pcsEmulatorStarter.setDispatcherHandler(dispatcherEmulator);
	    pcsEmulatorStarter.setExitGateHandler(exitGateEmulator);
	    pcsEmulatorStarter.setEntranceGateHandler(entranceGateEmulator);
	    pcsEmulatorStarter.setCollectorHandler(collectorEmulator);
	    pcsEmulatorStarter.setVacancyHandler(vacancyEmulator);
//	    pcsEmulatorStarter.setPayMachineHandler(payMachineEmulator);
         for(int i = 0; i < PayMachineNumber; i++)
             pcsEmulatorStarter.setPayMachineHandler(payMachineEmulatorList.get(i));


	    // start threads
	    new Thread(timer).start();
	    new Thread(pcsCore).start();
	    new Thread(entranceGateEmulator).start();
	    new Thread(exitGateEmulator).start();
	    new Thread(dispatcherEmulator).start();
	    new Thread(collectorEmulator).start();
	    new Thread(vacancyEmulator).start();
//	    new Thread(payMachineEmulator).start();
            for(int i = 0; i < PayMachineNumber; i++)
                new Thread(payMachineEmulatorList.get(i)).start();

	} // start
    } // Emulators


    //------------------------------------------------------------
    //  setters
    private void setTimer(Timer timer) {
        this.timer = timer;
    }
    private void setPCSCore(PCSCore pcsCore) {
        this.pcsCore = pcsCore;
    }
    private void setExitGateHandler(GateHandler gateHandler) {
	this.exitGateHandler = gateHandler;
    }
    private void setEntranceGateHandler(GateHandler gateHandler) {
        this.entranceGateHandler= gateHandler;
    }
    private void setDispatcherHandler(DispatcherHandler dispatcherHandler) {
        this.dispatcherHandler = dispatcherHandler;
    }
    private void setCollectorHandler(CollectorHandler collectorHandler){
        this.collectorHandler=collectorHandler;
    }
    private void setVacancyHandler(VacancyHandler vacancyHandler){this.vacancyHandler=vacancyHandler;}
    private void setPayMachineHandler(PayMachineHandler payMachineHandler){
        this.payMachineList.add(payMachineHandler);
    }
} // PCSEmulatorStarter
