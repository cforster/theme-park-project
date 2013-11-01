import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 * 
 * @author cforster
 * @author pirateCaptain
 *
 *
 *
 * time is always in minutes
 * 
 */

public class Park {
	
	//globals:
	int maxtime;
	int time=0;
	public List<Customer> customers;
	public List<Ride> rides;

	//main simulation:
	public Park() {
		//PARAMETERS:
		maxtime= 12*60; //12 hours x 60 minutes
		int CUSTCOUNT = 10000;
		int RIDECOUNT = 10;
		
		//declarations:
		Random gen = new Random();
		customers = new ArrayList<Customer>();
		rides = new ArrayList<Ride>();

		//make the rides:
		for (int i = 0; i < RIDECOUNT; i++) {
			Ride r = new Ride(this);
			r.APPEAL = 0.1+0.9*gen.nextDouble(); 
			r.RIDELENGTH = gen.nextInt(4) + 2;
			r.RIDERS = gen.nextInt(100) + 20;
			r.init();
 			rides.add(r);
		}

 		//make the customers:
		for (int i = 0; i < CUSTCOUNT; i++) {
			Customer c = new Customer(this);
			int startTimeTemp = startTimeGen(800); //generates arrival time
			c.starttime = startTimeTemp;
		        int endTimeTemp = endTimeGen(800); //generates departure time throughout the day
			while (true) { //checks that the end time is later than the arrival time
			if (endTimeTemp > startTimeTemp) {
			    c.endtime = endTimeTemp;
			    break;
			}
			else endTimeGen(800);
			break;
			}

			customers.add(c);
		}
		
		//run sim:
		while (time < maxtime) {
			for (Ride ride : rides) {
				ride.tick();
			}

			for (Customer customer : customers) {
				customer.tick();
			}

			time++;			
		}

		drawRideChart(rides);
		drawAttendChart(customers);
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
	}
    
    /** @param number range to generate from
	@return the customer's departure time
     */
    int endTimeGen (int x) {
	int finalEndTime = 0;
	Random gen = new Random();
	int placeholderInt = gen.nextInt(x);
	if (placeholderInt <= 20) { //first two hours
	    finalEndTime = gen.nextInt(120);
        }
	if (placeholderInt >20 && placeholderInt <= 100) { //second two hours
	    finalEndTime = gen.nextInt(120) + 120;  
        }
        if (placeholderInt >100 && placeholderInt <= 160) { //fifth hour
	    finalEndTime = gen.nextInt(60) + 240;
	}
	if (placeholderInt >160 && placeholderInt <= 300) { // sixth, seventh, and eighth hours
	    finalEndTime = gen.nextInt(180) + 300;
        }
        if (placeholderInt >300 && placeholderInt <= 500) { // ninth hour
	    finalEndTime = gen.nextInt(60) + 480;
        }
        if (placeholderInt >500 && placeholderInt <= 800) { // tenth and eleventh hours
	    finalEndTime = gen.nextInt(120) + 540;
	}
	return finalEndTime;
    }

  
    /**
       @param the number range to generate from
       @return the customer's departure time
     */
    int startTimeGen (int x) { // start time generator function
		    int finalStartTime = 0;
		    Random gen = new Random();
		    int placeholderInt = gen.nextInt(x);
		    if (placeholderInt <= 200) { //first two hours
			finalStartTime = gen.nextInt(120);
		    }
		    if (placeholderInt >200 && placeholderInt <= 400) { //second two hours
			finalStartTime = gen.nextInt(120) + 120;  
		    }
		    if (placeholderInt >400 && placeholderInt <= 600) { //fifth hour
			finalStartTime = gen.nextInt(60) + 240;
		    }
		    if (placeholderInt >600 && placeholderInt <= 700) { // sixth, seventh, and eighth hours
			finalStartTime = gen.nextInt(180) + 300;
		    }
		    if (placeholderInt >700 && placeholderInt <= 770) { // ninth hour
			finalStartTime = gen.nextInt(60) + 480;
		    }
		    if (placeholderInt >770 && placeholderInt <= 799) { // tenth and eleventh hours
			finalStartTime = gen.nextInt(120) + 540;
		    }
		    if (placeholderInt >799) {
			finalStartTime = gen.nextInt(60) + 660;
		    }
		    return finalStartTime;
		}
}
