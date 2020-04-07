package PCS.VacancyHandler.Emulator;

import AppKickstarter.misc.Msg;
import PCS.VacancyHandler.Emulator.VacancyEmulatorController;
import PCS.VacancyHandler.Emulator.VacancyEmulator;
import PCS.VacancyHandler.VacancyHandler;
import PCS.PCSStarter;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VacancyEmulator extends VacancyHandler {
    private Stage myStage;
    private PCS.VacancyHandler.Emulator.VacancyEmulatorController VacancyEmulatorController;
    private final PCSStarter pcsStarter;
    private final String id;


    //------------------------------------------------------------
    // VacancyEmulator
    public VacancyEmulator(String id, PCSStarter pcsStarter,int[] availableSpaces) {
        super(id, pcsStarter,availableSpaces);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
//        this.VacancyOpenTime = Integer.parseInt(this.pcsStarter.getProperty("Vacancy.VacancyOpenTime"));
////        this.VacancyCloseTime = Integer.parseInt(this.pcsStarter.getProperty("Vacancy.VacancyCloseTime"));
//        this.autoOpen = true;
//        this.autoClose = true;
//        this.autoPoll = true;
    } // VacancyEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "VacancyEmulator.fxml";
        loader.setLocation(VacancyEmulator.class.getResource(fxmlName));
        root = loader.load();
        VacancyEmulatorController = loader.getController();
        VacancyEmulatorController.initialize(super.id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 470));
        myStage.setTitle(id);
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // VacancyEmulator


    //@Override
    protected void SendAddTicket(String mymsg) {

        if (!mymsg.equals(super.id)) return;
        pcsCore.send(new Msg(id, mbox, Msg.Type.AddTicket, mymsg));
    }

    //receive ticket ID
    protected void ReceiveTicketID(String mymsg) {

        Date nowT = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String timestr = sdf.format(nowT);
        VacancyEmulatorController.appendTextArea("Welcome to our parking lot.\n" + "Your ticket ID is " + mymsg + ".\n" + "The time now is " + timestr + ".\nThe parking fee is $5/s.\nHave a good time!");

        VacancyEmulatorController.showTicket(mymsg, timestr);
        pcsCore.send(new Msg(id, mbox, Msg.Type.RemoveTicket, "Remove Ticket Now"));

    }
}
