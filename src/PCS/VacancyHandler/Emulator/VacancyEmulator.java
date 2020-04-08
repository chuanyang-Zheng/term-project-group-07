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
    public VacancyEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
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


    @Override
    public void handleVacancyDisUpdateRequest(Msg mymsg){
        String []str=mymsg.getDetails().split("\\s+");
        Date nowT = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String timestr = sdf.format(nowT);
        VacancyEmulatorController.appendTextArea("This is a real time vacancy display:\n");
        StringBuilder floorNumberInformation=new StringBuilder();
        for(int i=0;i<str.length;i++){
            floorNumberInformation.append("Floor "+(i+1)+": "+str[i]+"\n");
        }
        floorNumberInformation.append("Last update time: "+timestr);
        VacancyEmulatorController.appendTextArea(floorNumberInformation.toString());
        log.info(id+"\n"+floorNumberInformation.toString());
    }
}
