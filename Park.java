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

		//create ride lunch
		Ride lunch = new Ride(this);
		lunch.APPEAL = 0.1;
		lunch.RIDELENGTH = gen.nextInt(30)+10;
		lunch.RIDERS = CUSTCOUNT;
		lunch.init();
		rides.add(lunch);

		//make the customers:
		for (int i = 0; i < CUSTCOUNT; i++) {
			Customer c = new Customer(this);
			c.starttime = gen.nextInt(maxtime/2);
			c.endtime = gen.nextInt(maxtime/2) + maxtime/2;
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
			
		//change appeal of lunch at peak times
		if(time > maxtime/2 - 110 || time < maxtime/2 + 110) {
		rides.get(rides.size()-1).APPEAL=.75;
		}
		else {
		rides.get(rides.size()-1).APPEAL=.1;
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
}
