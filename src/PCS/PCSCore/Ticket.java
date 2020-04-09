package PCS.PCSCore;


import java.util.logging.*;

public class Ticket {
    protected final long enterTime;
    protected final int ticketID;

    //if smaller than 0, it means that it is not valid.
    protected long exitTime=-1;
    protected float parkingFee=-1;
    protected String payMachineID = "";
    protected boolean payJudge=false;


    protected static int TicketCount=0;

    public Ticket(){
        enterTime=System.currentTimeMillis();

        ticketID=TicketCount++;
    }
    public void enterPrint(){


    }
    public float calculateFee(float coefficient){
        long currentTime = System.currentTimeMillis();
        long time = currentTime-enterTime;//calculate the pay Fee time
        long currentSecond = time /1000 % 60;
        parkingFee = payMachineID.equals("")? coefficient * currentSecond : 0;
        return parkingFee;
    }
    public void setPayMachineID(String ID){
        payMachineID=ID;
    }
    public void setExitTime(long exitTime){
        this.exitTime=exitTime;
    }

    public void setParkingFee(float parkingFee) {
        this.parkingFee = parkingFee;
    }

    public long getEnterTime(){
        return enterTime;
    }
    public float getParkingFee(){
        return parkingFee;
    }

    public int getTicketID() {
        return ticketID;
    }

    public String getPayMachineID() {
        return payMachineID;
    }

    public long getExitTime() {
        return exitTime;
    }

    public boolean valid(Logger log,String id){
        if(ticketID<0)
        {
            log.warning(id+": "+ticketID+" is not valid");
            return false;
        }
        if( System.currentTimeMillis()>exitTime)
        {
            log.warning(id+": "+"Current time"+System.currentTimeMillis()+" is larger than Exit time: "+exitTime);
            return false;
        }
        if(payMachineID.equals(""))
        {
            log.warning(id+": Pay Machine ID is Emypy ["+payMachineID+"]");
            return false;
        }

        if( parkingFee<0){
            log.warning(id+": "+parkingFee+" is smaller than 0");
            return false;
        }

        payJudge=true;


        return true;
    }

    /**
     *
     * @param exitTimeCoefficient:used to calculate exitTime=currentTime+exitTimeCoefficient
     * @param payMachineID:set payMachine
     * @param parkingFee:calculate Parking fee
     * @description set Ticket Information. It will be used after user ging money and pay Machine send the message to PCSCore
     */
    public void setExitInformation(long exitTimeCoefficient,String payMachineID, float parkingFee){
        this.exitTime=System.currentTimeMillis()+exitTimeCoefficient;
        this.payMachineID=payMachineID;
        this.parkingFee=parkingFee;
    }
}
