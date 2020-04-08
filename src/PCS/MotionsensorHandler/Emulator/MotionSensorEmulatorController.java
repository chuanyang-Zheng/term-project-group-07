package PCS.MotionsensorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.logging.Logger;

public class MotionSensorEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private MotionSensorEmulator motionSensorEmulator;
    private MBox motionSensorBox;
    public TextArea motionSensorTextArea;

    private int lineNo = 0;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, MotionSensorEmulator motionSensorEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.motionSensorEmulator = motionSensorEmulator;
        this.motionSensorBox = appKickstarter.getThread(id).getMBox();
    } // initialize

    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Detect":
                motionSensorBox.send(new Msg(id, null, Msg.Type.MotionSensorDetect, "Motion Sensor Detect"));
                break;

//            case "Gate Open Reply":
//                motionSensorBox.send(new Msg(id, null, Msg.Type.GateOpenReply, "GateOpenReply"));
//                break;
//
//            case "Gate Close Request":
//                motionSensorBox.send(new Msg(id, null, Msg.Type.GateCloseRequest, "GateCloseReq"));
//                break;
//
//            case "Gate Close Reply":
//                motionSensorBox.send(new Msg(id, null, Msg.Type.GateCloseReply, "GateCloseReply"));
//                break;
//
//            case "Poll Request":
//                appendTextArea("Send poll request.");
//                motionSensorBox.send(new Msg(id, null, Msg.Type.Poll, ""));
//                break;
//
//            case "Poll ACK":
//                appendTextArea("Send poll ack.");
//                motionSensorBox.send(new Msg(id, null, Msg.Type.PollAck, ""));
//                break;


            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        Platform.runLater(() -> motionSensorTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea
}
