package psl.meet.server.clocks;

public interface MEETClock extends Runnable {

    public String getClockName();

    public long getTime();

}
