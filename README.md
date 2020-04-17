#COMP4107
This is COMP4107 Project

Author:Chuanyang Zheng(17251311@life.hkbu.edu.hk)

Last Update Time: April 17, 2020

The structure of the project.

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
        - DispatcherHandler
          - Emulator
            - DispatcherEmulator
            - DispatcherEmulator.fxml
            - DispatcherEmulatorController
        - GateHandler
          - Emulator
            - GateEmulator
            - GateEmulator.fxml
            - GateEmulatorController
        - MotionSensorHandler
          - Emulator
            - MotionSensorEmulator
            - MotionSensorEmulator.fxml
            - MotionSensorEmulatorController
        - PayMachineHandler
          - Emulator
            - CollectorEmulator
            - CollectorEmulator.fxml
            - CollectorEmulatorController
        - PCSCore
          - PCSCore
          - Ticket
        - VacancyHandler
          - Emulator
            - VacancyEmulator
            - VacancyEmulator.fxml
            - VacancyEmulatorController  
        - PCSEmulatorStarter
        - PCSStarter    
        
   


Instruction of Compiling,starting and stopping
- Compiling and Starting: Go to src.PCSEmulatorStarter and run the class
- Stopping: Close any GUIs, or close it by task manager