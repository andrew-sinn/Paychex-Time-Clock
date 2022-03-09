package time.clock.data;

public class Employee {
	private String uniqueID;	//Employee ID
	private String role;		//Employee role
	private boolean onShift;	//If they are on shift
	private boolean onBreak;	//If they are on break
	private boolean onLunch;	//If they are on lunch
	
	/**
	 * Set up an employee, setting values for shift, break and lunch to false.
	 * @param uniqueID Employee ID
	 * @param role	   Assigned role.
	 */
	
	public Employee(String uniqueID, String role) {
		this.uniqueID = uniqueID;
		this.role = role;
		onShift = false;
		onBreak = false;
		onLunch = false;
	}

	public String getUniqueID() {
		return uniqueID;
	}
	
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isOnShift() {
		return onShift;
	}

	public void setOnShift(boolean onShift) {
		this.onShift = onShift;
	}

	public boolean isOnBreak() {
		return onBreak;
	}

	public void setOnBreak(boolean onBreak) {
		this.onBreak = onBreak;
	}

	public boolean isOnLunch() {
		return onLunch;
	}

	public void setOnLunch(boolean onLunch) {
		this.onLunch = onLunch;
	}
}//Employee
