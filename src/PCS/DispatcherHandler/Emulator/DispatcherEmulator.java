package PCS.DispatcherHandler.Emulator;

import AppKickstarter.misc.Msg;
import PCS.PCSStarter;
import PCS.DispatcherHandler.DispatcherHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.text.SimpleDateFormat;
import java.util.Date;


//======================================================================
// DispatcherEmulator
public class DispatcherEmulator extends DispatcherHandler {
    private Stage myStage;
    private DispatcherController DispatcherController;
    private final PCSStarter pcsStarter;
    private final String id;



    //------------------------------------------------------------
    // DispatcherEmulator
    public DispatcherEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
//        this.DispatcherOpenTime = Integer.parseInt(this.pcsStarter.getProperty("Dispatcher.DispatcherOpenTime"));
////        this.DispatcherCloseTime = Integer.parseInt(this.pcsStarter.getProperty("Dispatcher.DispatcherCloseTime"));
//        this.autoOpen = true;
//        this.autoClose = true;
//        this.autoPoll = true;
    } // DispatcherEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "DispatcherEmulator.fxml";
        loader.setLocation(DispatcherEmulator.class.getResource(fxmlName));
        root = loader.load();
        DispatcherController = loader.getController();
        DispatcherController.initialize(super.id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 470));
        myStage.setTitle(id);
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // DispatcherEmulator
    @Override
    protected void FeeReceive(String mymsg){
        String []str = mymsg.split(",");
        if(!str[0].equals(super.id)) return;
        float fee = Float.parseFloat(str[2]);
        Date nowT = new Date(Long.parseLong(str[3]));
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String timestr = sdf.format(nowT);
        Long parkedTime = (System.currentTimeMillis() - Long.parseLong(str[3])) / 1000;
        DispatcherController.appendTextArea("You have parked " + Long.toString(parkedTime) + "s and you need to pay $" + fee + "  ($5/s)");
//        log.fine(id + ": " + mymsg);
        DispatcherController.showTicket(str[1],timestr);
    }
    protected void SendPaymentACK(String mymsg){
        log.fine(id+ ":ticket"+ mymsg + "Paid already.");
        DispatcherController.appendTextArea("Thank you for payment!!!!");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PaymentACK, mymsg));
        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketRequest, mymsg));
    }


} // DispatcherEmulator
