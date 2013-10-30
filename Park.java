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
    public Park()
    {
	this(10000, 10, 12*60);
    }

    public Park(int custCount, int rideCount, int duration) {
		//PARAMETERS:
		
		//declarations:
		Random gen = new Random();
		customers = new ArrayList<Customer>();
		rides = new ArrayList<Ride>();

		//make the rides:
		for (int i = 0; i < rideCount; i++) {
		    Ride r = new Ride(duration);
			//make appeal dependent on ride length and number of riders
			r.RIDELENGTH = gen.nextInt(4) + 2;
			r.RIDERS = gen.nextInt(100) + 20;
			r.APPEAL = 1.0 - (((r.RIDERS/r.RIDELENGTH)-10)*.01);
			r.init();
			rides.add(r);
		}

		//make the customers:
		for (int i = 0; i < custCount; i++) {
			Customer c = new Customer(duration);
			c.starttime = gen.nextInt(duration/2);
			c.endtime = gen.nextInt(duration/2) + duration/2;
			customers.add(c);
		}

		//run sim:
		while (time < duration) {
			for (Ride ride : rides) {
				ride.tick(time);
			}

			for (Customer customer : customers) {
			    customer.tick(time, rides);
			}

			time++;			
		}

		drawRideChart(rides);
		drawAttendChart(duration, customers);
	}

    public List<Ride> getRides()
    {
	return rides;
    }
	
    public void drawAttendChart(int duration, List<Customer> custData) {
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
		for (int j = 0; j<duration; j++) {
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
