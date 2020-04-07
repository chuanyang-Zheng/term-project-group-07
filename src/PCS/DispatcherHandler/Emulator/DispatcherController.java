package PCS.DispatcherHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.logging.Logger;


//======================================================================
// DispatcherEmulatorController
public class DispatcherController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private PCS.DispatcherHandler.Emulator.DispatcherEmulator DispatcherEmulator;
    private MBox dispatcherMBox;
    public TextArea DispatcherTextArea;
    public TextArea TicketIDField;
    public TextArea EnterField;
    private int lineNo = 0;
    private String ticket_id,ticket_enter;

    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, PCS.DispatcherHandler.Emulator.DispatcherEmulator DispatcherEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.DispatcherEmulator = DispatcherEmulator;
        this.dispatcherMBox = appKickstarter.getThread(id).getMBox();
    } // initialize


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Print a ticket":
                TextInputDialog dialog = new TextInputDialog("0");
                dialog.setTitle("Ticket printed");
                dialog.setContentText("Please pick up your ticket.");
                dispatcherMBox.send(new Msg(id, null, Msg.Type.AddTicket, id ));
                //dispatcherMBox.send(new Msg(id, null, Msg.Type.ReceiveID, id ));
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        Platform.runLater(() -> DispatcherTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea
    public void showTicket(String tmpid, String tmpenter){
        ticket_enter = tmpenter;
        TicketIDField.setText(tmpid);
        EnterField.setText(ticket_enter);
    }
} // DispatcherEmulatorController
