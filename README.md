# COMP4107

This is COMP4107 Project

##### Author:

    Chuanyang Zheng(17251311@life.hkbu.edu.hk)

    Pan Feng(19205945@life.hkbu.edu.hk)
    
    Gong Yikai(17251567@life.hkbu.edu.hk)
    
    ZHANG YIJIA(17251281@life.hkbu.edu.hk)

Website:https://github.com/hkbucs/term-project-group-07

Demonstration Link:https://hkbu.zoom.us/rec/share/3fR1NJz28nxITo3NzWXdd_AmXa_Ceaa80CgWrvBfmEm24s6a45jBBtoyyIJw_00m?startTime=1587193012000 (Please Mute THe Voice Of the Video)

Last Update Time: April 18, 2020

## The structure of the project.

    - ect
      - PCS.cfg
     
    
    - src(code)
      - AppKickstarter
        - misc
          - AppThread
          - Lib
          - LogFormatter
          - Mbox
          - Msg
        - timer
          - Appkickstarter
          
          
      - PCS
        - CollectorHandler
          - Emulator
            - CollectorEmulator
            - CollectorEmulator.fxml
            - CollectorEmulatorController
          - CollectorHandler
          
        - DispatcherHandler
          - Emulator
            - DispatcherEmulator
            - DispatcherEmulator.fxml
            - DispatcherEmulatorController
          - DispatcherHandler
          
        - GateHandler
          - Emulator
            - GateEmulator
            - GateEmulator.fxml
            - GateEmulatorController
          - GateHandler
          
        - MotionSensorHandler
          - Emulator
            - MotionSensorEmulator
            - MotionSensorEmulator.fxml
            - MotionSensorEmulatorController
          - MotionSensorHandler
          
        - PayMachineHandler
          - Emulator
            - CollectorEmulator
            - CollectorEmulator.fxml
            - CollectorEmulatorController
          - PayMachineHandler
          
        - PCSCore
          - PCSCore
          - Ticket
          
        - VacancyHandler
          - Emulator
            - VacancyEmulator
            - VacancyEmulator.fxml
            - VacancyEmulatorController
          - VacancyHandler
            
        - PCSEmulatorStarter
        - PCSStarter    
        
   


## Instruction of Compiling,starting and stopping
- Compiling and Starting: Go to src.PCSEmulatorStarter and run the class
- Stopping: Close any GUIs, or close it by task manager

## Explanation Of GUI

### Common Button
Button|Description
--|--
Poll Request|Emulator PCSCore send Poll Request
Poll Ack|Tell PCSCore that Hardware is OK
AutoPoll|If true, Acknowledge PCSCore when receive Poll Request

### Gate
Button|Description
--|--
Gate Open Request|Open Gate
Gate Open Reply|If the gate is open, show that "gate is already open". Else, show "Gate should be open"
Gate Close Request|Close Gate
Gate Close Reply|If the gate is closed, show that "gate is already closed". Else, show "Gate should be closed"
Auto Open|If true, the gate is able to successfully open. Or, the gate will be not opened
Auto Close|If true, the gate is able to successfully closed. Or, the gate will be not closed

### Dispatcher
Button|Description
--|--
Print A Ticket|Dispatch A Ticket
Remove Ticket|Remove A Ticket

### PayMachine
Button|Description
--|--
Insert A Ticket|Input Ticket ID and send the ID to PCS
Oct Pay|Pay By Oct
Remove Ticket|Remove A Ticket

### Collector
Button|Description
--|--
Collector Valid Request|Input A Ticket ID. Then,Collector sends the ID to PCS and valid it.
Collector Positive|Emulate When Valid a true ticket
Collector Negative|Emulator when valid a false ticket
Collector Solve Problem|Solve problem when valid an invalid ticket

### MotionSensor

Button|Description
--|--
Detect|Send Detect Signal to PCS

### Vacancy
No Special Buttons. Receive Signals From PCS
