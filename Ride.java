

public class Ride {
	//attributes:
	public double APPEAL;    //the appeal of this ride.
	public int RIDELENGTH;   //the number of minutes a ride takes
	public int RIDERS;       //the number of riders per minute
    public boolean INDOORS;  // wheter the ride is indoors or outdoors

	//data:
	public int[] waittime;  //wait time at each minute in the day.

	//runtime:
	public int status=0;         //amount of time remaining in load interval
	public Queue<Customer> line; //customers waiting to ride
	public Queue<Customer> ride; //customers riding the ride
	public Park p;

	public String toString() {
		return String.format("R:%.3f:%d:%d", APPEAL,RIDELENGTH,RIDERS);
	}

	public Ride(Park p) {
		line = new Queue<Customer>(p.maxtime);
		ride = new Queue<Customer>(p.maxtime);
		//set size of queue:

		waittime = new int[p.maxtime];
		this.p = p;
	}

	public void init() {
		for (int i = 0; i < RIDERS*RIDELENGTH; i++) {
			ride.put(null);
		}
	}
    
    /**
       @author Scatman Screen aka Oliver Kafka
       @param rain - Whether it is raining or not, established in the Park Class
       @param r - Takes each ride, goes through a loop in the main
     */
    public void rain(boolean rain, Ride r)
    {
	if (rain==true)
	    {
		if(r.APPEAL >= .8 && r.INDOORS == false) r.APPEAL = 0;    //Closes the top rides(appeal above .8) that are outdoors during rain, because they become too dangerous to ride
		else if (r.INDOORS == true) r.APPEAL = r.APPEAL + (1-r.APPEAL)*.5;
	    }
	
    }
    
	
	public void tick() {
		//move the riders through:
		for (int i = 0; i < RIDERS; i++) {  
			if(ride.size()>0) ride.get();   //.status[p.time] = RiderStatus.FREE; //free a rider
			if(line.size()>0) ride.put(line.get());  //move from line to ride.
			else ride.put(null); //send an empty customer if necessary
		}
		
		//everybody on ride is riding:
		for (int i = 0; i < ride.size(); i++) {
			if(ride.peek(i)!=null) ride.peek(i).status[p.time] = RiderStatus.RIDING;
		}

		//everybody on the line is waiting:
		for (int i = 0; i < line.size(); i++) {
			line.peek(i).status[p.time] = RiderStatus.WAITING;
		}

		//save the wait time:
		waittime[p.time] = RIDELENGTH*(line.size()/(RIDERS));
	}
}
