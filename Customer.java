import java.util.Arrays;
import java.util.Random;
import java.util.List;

public class Customer {
	//PARAMETERS:
    double FREEFACTOR = 1.0;    //lower number, more likely to take a free tick
    double WAITFACTOR = 1.0; 

    public void KidsOrAdults() {
	Random ranGen = new Random();
	boolean isChild = ranGen.nextBoolean();   //random generator for boolean
	if(isChild) {
	    WAITFACTOR = 0.5;   //if the majority of customers are kids, the waitfactor will change so lines are longer because children and willing to wait longer
	}
	else {
	    WAITFACTOR = 1.5; //if the majority of customers are not children, the lines are normal length 
	}
     }

	//declarations:
	RiderStatus[] status;
	public int starttime=0;
	public int endtime=0;
	Random gen = new Random();
       

	//constructor
	public Customer(int maxtime) {
		status = new RiderStatus[maxtime];
		Arrays.fill(status, RiderStatus.FREE);
	}


    public void tick(int time, List<Ride> rides)
	{
	    KidsOrAdults(); 

		// if the customer hasn't arrived yet, do nothing.
		if(time<starttime || time>endtime)
		{
			status[time] = RiderStatus.GONE;
			return; 
		}
		//consider waiting for a ride:
		if(status[time]==RiderStatus.FREE) {			
			//pick a ride:
			for (int i = 0; i < 100*FREEFACTOR; i++) {  //try to get on a ride,
														//then tick backoff.
			    Ride r = rides.get(gen.nextInt(rides.size()));
				double threshold = r.APPEAL*Math.pow(r.waittime[time],-1.0*WAITFACTOR);
				if(gen.nextDouble()<threshold) {
					r.line.put(this);
					return;
				}
			}
		}
	}
}
