import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//importing information to have chart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 * 
 * @author cforster
 * time is always in minutes
 * 
 */

/*
Rachel Kasdin
Improvement: I am going to make a boolean that describes each customer. If the customer is a kid,
they are willing to wait in line longer. If the customer is an adult, they leave the line quickly. 

 */

public class Park {
	
	//globals:
	int maxtime;
	int time=0;
        public List<Customer> customers;  
	public List<Ride> rides;


    //for Michael's Enhancement
	//create random generator
    public static Random generator = new Random();
	//int day have value from 1 to 7; 1 = Sunday, 7 = Saturday
	public static int day = generator.nextInt(7) + 1;


    //for Sunaina's Enhancement
    //establishes good_weather as true initially
   public static boolean good_weather = true;

    //Michael Enhancement
    public int dayCustCount()
    {
		
	//if Friday, Saturday, or Sunday, CUSTCOUNT is between 8000 and 12000
	if(day == 1 || day == 6 || day == 7) {
	    int CUSTCOUNT = generator.nextInt(4000) + 8000;
	    return CUSTCOUNT;
	}
	//other days CUSTCOUNT is between 4000 and 8000
	else {
	    int CUSTCOUNT = generator.nextInt(4000) + 4000;
	    return CUSTCOUNT;
	}
    }
 

 //Sunaina's enhancement
     public int weatherCustCount()
    {
	//create random generator 
	Random gen = new Random(); 
	int ran = gen.nextInt(3);
	
 
	//2/3 of the time, weather is good
	if(ran==0 || ran==1)
	    {
		good_weather =true;
		
	    }
	//1/3 of the time, weather is bad
	else 
	    {
		good_weather=false;
	    }

	int weather_cust = gen.nextInt(1000) +2000; 

	if(good_weather==false) 
	    {
		weather_cust = weather_cust*-1;
	    }

	    return weather_cust; 
    }

    
    
	//main simulation:
    public Park()
    {
	this(10000, 10, 12*60);
    }

    public Park(int custCount, int rideCount, int duration) {
		//PARAMETERS:
		maxtime= 12*60; //12 hours x 60 minutes
		// number of rides and number of customers
		int CUSTCOUNT = dayCustCount() + weatherCustCount();
		int RIDECOUNT = 10;
		
		//declarations:
		Random gen = new Random();
		customers = new ArrayList<Customer>();
		rides = new ArrayList<Ride>();
		

		//make the rides:
		for (int i = 0; i < RIDECOUNT; i++) {
			Ride r = new Ride(maxtime);
			//make appeal dependent on ride length and number of riders
			r.RIDELENGTH = gen.nextInt(4) + 2;
			r.RIDERS = gen.nextInt(100) + 20;
			r.APPEAL = 1.0 - (((r.RIDERS/r.RIDELENGTH)-10)*.01);
			r.init();
			rides.add(r);
		}

		//make the customers:
		for (int i = 0; i < CUSTCOUNT; i++) {
			Customer c = new Customer(maxtime);
			c.starttime = gen.nextInt(maxtime/2);
			c.endtime = gen.nextInt(maxtime/2) + maxtime/2;
			customers.add(c);      
	        }
	      

		//run sim:
		while (time < maxtime) {
			for (Ride ride : rides) {
				ride.tick(time);
			}

			for (Customer customer : customers) {
			    customer.tick(time, rides);
			}

			time++;			
		}

		drawRideChart(rides);
		drawAttendChart(customers);
	}

    public List<Ride> getRides()
    {
	return rides;
    }
	
	public void drawAttendChart(List<Customer> custData) {
		String title = "attendance chart";
		ApplicationFrame frame;
		JFreeChart chart;
		XYSeriesCollection dataset = new XYSeriesCollection();
		frame = new ApplicationFrame(title);  //
		//graph:
		XYSeries attend = new XYSeries("attendance");
		XYSeries free = new XYSeries("free");
		XYSeries wait = new XYSeries("wait");
		XYSeries ride = new XYSeries("ride");
		for (int j = 0; j<this.maxtime; j++) {
			int a = 0;
			int f = 0;
			int w = 0;
			int r = 0;
			for (Customer customer : custData) {
				a+=customer.status[j]==RiderStatus.GONE?0:1; //add one if the rider is not gone
				f+=customer.status[j]==RiderStatus.FREE?1:0;
				w+=customer.status[j]==RiderStatus.WAITING?1:0;
				r+=customer.status[j]==RiderStatus.RIDING?1:0;
			}
			attend.add(j, a);
			free.add(j,f);
			wait.add(j,w);
			ride.add(j,r);
		}
	
		dataset.addSeries(attend);
		dataset.addSeries(free);
		dataset.addSeries(wait);
		dataset.addSeries(ride);
		chart = ChartFactory.createXYLineChart(title, "Time",
				"people", dataset);
		ChartPanel cp = new ChartPanel(chart);
	
		frame.add(cp);
		frame.setVisible(true);
	}

	
	public void drawRideChart(List<Ride> rideData) {
		String title = "ride chart";
		ApplicationFrame frame;
		JFreeChart chart;
		XYSeriesCollection dataset = new XYSeriesCollection();
		frame = new ApplicationFrame(title);  //
		//graph:
		XYSeries[] series = new XYSeries[rideData.size()];
		for (int j = 0; j<rideData.size(); j++) {
			series[j] = new XYSeries(rideData.get(j).toString());
			addAll(series[j], rideData.get(j).waittime);
			dataset.addSeries(series[j]);
		}
		chart = ChartFactory.createXYLineChart(title, "Time",
				"wait time", dataset);
		ChartPanel cp = new ChartPanel(chart);
	
		frame.add(cp);
		frame.setVisible(true);
	}
	
	public void addAll(XYSeries s, int[] data) {
		for (int i = 0; i < data.length; i++) {	
			s.add(i, data[i]);
		}
	}
	public static void main(String[] args) {
		new Park();
		System.out.println("Day of the week is " + day);
		    System.out.println("Good weather? " + good_weather);
	}
   
    
}
