package PCS.PayMachineHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import javax.swing.*;


//======================================================================
// GateEmulatorController
public class PayMachineController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private PayMachineEmulator PayMachineEmulator;
    private MBox payMBox;
    public TextArea gateTextArea;
    public TextArea TicketIDField;
    public TextArea FeeField;
    public TextArea EnterField;
    public TextArea ExitField;
    private int lineNo = 0;
    private String ticket_id,ticket_fee,ticket_enter;

    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, PayMachineEmulator PayMachineEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.PayMachineEmulator = PayMachineEmulator;
        this.payMBox = appKickstarter.getThread(id).getMBox();
    } // initialize


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Insert ticket":
                TextInputDialog dialog = new TextInputDialog("0");
                dialog.setTitle("Inserting Ticket....");
                dialog.setContentText("Please Input Ticket ID:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent())
                    ticket_id = result.get();
                if(ticket_id == null || ticket_id.isEmpty())
                    new Alert(Alert.AlertType.ERROR, "Please input valid ID :(", new ButtonType[]{ButtonType.OK}).show();
                else
                    payMBox.send(new Msg(id, null, Msg.Type.TicketRequest, id + "," + ticket_id));
                break;
            case "Pay by Oct":
                if(ticket_id == null || ticket_id.isEmpty())
                    new Alert(Alert.AlertType.ERROR, "Please insert first :)", new ButtonType[]{ButtonType.OK}).show();
                else {
                    payMBox.send(new Msg(id, null, Msg.Type.PaymentACK, id + "," + ticket_id));
                    appendTextArea("Payment Finished");
                    appendTextArea("Please remove your ticket :)");
                }
                break;
            case "Remove ticket":
                payMBox.send(new Msg(id, null, Msg.Type.TicketRemoveACK, id + "," + ticket_id));
                appendTextArea("Ticket removed!");
                appendTextArea("Please exit before exit time~");
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        Platform.runLater(() -> gateTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea
    public void updateTicket(String tmpid,String tmpfee, String tmpenter){
        ticket_fee = tmpfee; ticket_enter = tmpenter;
        TicketIDField.setText(tmpid);
        FeeField.setText(ticket_fee);
        if(Float.parseFloat(tmpfee) == 0)
            ExitField.setText(ticket_enter);
        else {
            EnterField.setText(ticket_enter);
            ExitField.setText("");
        }
    }
} // GateEmulatorController
