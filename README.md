#COMP4107
This is COMP4107 Project

Author:Chuanyang Zheng(17251311@life.hkbu.edu.hk)

Last Update Time: April 17, 2020

##The structure of the project.

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
        
   


##Instruction of Compiling,starting and stopping
- Compiling and Starting: Go to src.PCSEmulatorStarter and run the class
- Stopping: Close any GUIs, or close it by task manager

##Explanation Of GUI
###Gate


姓名|技能|排行
--|:--:|--:
刘备|哭|大哥
关羽|打|二哥
张飞|骂|三弟


