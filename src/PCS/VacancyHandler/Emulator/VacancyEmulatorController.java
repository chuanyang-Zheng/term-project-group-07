package PCS.VacancyHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.logging.Logger;

public class VacancyEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private PCS.VacancyHandler.Emulator.VacancyEmulator VacancyEmulator;
    private MBox VacancyMBox;
    public TextArea VacancyTextArea;
    public TextArea TicketIDField;
    public TextArea EnterField;
    private int lineNo = 0;
    private String ticket_id,ticket_enter;

    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, PCS.VacancyHandler.Emulator.VacancyEmulator VacancyEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.VacancyEmulator = VacancyEmulator;
        this.VacancyMBox = appKickstarter.getThread(id).getMBox();
    } // initialize


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Print a ticket":
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Reminder");
                alert.setHeaderText("Pick up your ticket please.");
                alert.setContentText("Ticket is your basis to pay and leave.");

                alert.showAndWait();

                VacancyMBox.send(new Msg(id, null, Msg.Type.AddTicket, id ));
                //VacancyMBox.send(new Msg(id, null, Msg.Type.ReceiveID, id ));
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        Platform.runLater(() -> VacancyTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea
    public void showTicket(String tmpid, String tmpenter){
        ticket_enter = tmpenter;
        TicketIDField.setText(tmpid);
        EnterField.setText(ticket_enter);
    }
}
