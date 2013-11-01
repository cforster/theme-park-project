import java.util.Arrays;
import java.util.Random;

public class Customer {
	//PARAMETERS:
	double FREEFACTOR = 1.0;    //lower number, more likely to take a free tick
	double WAITFACTOR = 1.0;     //lower number, more likely to pick a ride with a long wait

	//declarations:
	RiderStatus[] status;
	Park p;
	public int starttime=0;
	public int endtime=0;
	Random gen = new Random();

	//constructor
	public Customer(Park p) {
		status = new RiderStatus[p.maxtime];
		Arrays.fill(status, RiderStatus.FREE);
		this.p = p;
	}


	public void tick()
	{
		// if the customer hasn't arrived yet, do nothing.
		if(p.time<starttime || p.time>endtime)
		{
			status[p.time] = RiderStatus.GONE;
			return; 
		}
		//consider waiting for a ride:
		if(status[p.time]==RiderStatus.FREE) {			
			//pick a ride:
			for (int i = 0; i < 100*FREEFACTOR; i++) {  //try to get on a ride,
														//then tick backoff.
				Ride r = p.rides.get(gen.nextInt(p.rides.size()));
				double threshold = r.APPEAL*Math.pow(r.waittime[p.time],-1.0*WAITFACTOR); //Math.pow returns first argument raised to the second argument
				if(gen.nextDouble()<threshold) {
					r.line.put(this);
					return;
				}
			}
		}
	}
}
