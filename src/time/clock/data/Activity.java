package time.clock.data;

import java.util.Date;

public class Activity {
	private String employeeID;	//Employee ID associated with the activity
	private String activity;	//The type of activity
	private Date date;			//The date in which the activity was performed.
	
	public Activity(String employeeID, String activity, Date date) {
		this.employeeID = employeeID;
		this.activity = activity;
		this.date = date;
	}

	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}//Activity
