package time.clock.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class TimeClock {

	private String today;					//Today's date
	private List<Employee> listEmployees;	//List of Employees
	private List<Activity> listActivities;	//List of activities of an employee
	
	/**
	 * Constructor for the Time Clock application. Get today's date and initialize data from files in src
	 */
	
	public TimeClock() {
		SimpleDateFormat dateFormat = new SimpleDateFormat ("MM/dd/yyyy");
		today = dateFormat.format(new Date()).toString();
		listActivities = new ArrayList<Activity>();
		listEmployees = new ArrayList<Employee>();
		initializeCalendar();
		initializeEmployees();
	}
	
	/**
	 * Initialize the activities calendar list from external file.
	 */
	
	private void initializeCalendar() {
		
		//File location
		File f = new File("src/calendar.txt");
		
		//Date format
		SimpleDateFormat dateFormat = new SimpleDateFormat ("MM/dd/yyyy hh:mm aa");
		
		try {
			//Use Scanner to read file contents in CSV format
			Scanner input = new Scanner(f);
			while(input.hasNextLine()) {
				//Split line data from CSV format and read in date String as Date object
				String[] activity = input.nextLine().split(",");
				Date timeStamp = null;
				try {
					timeStamp = dateFormat.parse(activity[2]);
				} catch (ParseException e) {
					//File is assumed to be correct format, no error handling here
					e.printStackTrace();
				}
				//Once a line is read, an activity is made and added to the list
				Activity newActivity = new Activity(activity[0], activity[1], timeStamp);
				listActivities.add(newActivity);
			}
			input.close();
		} catch (FileNotFoundException e) {
			//File is assumed to be here, no error handling here
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize employee list from external file.
	 */
	
	private void initializeEmployees() {
		//File location
		File f = new File("src/employees.txt");
		try {
			//Use Scanner to read file contents in CSV format
			Scanner input = new Scanner(f);
			while(input.hasNextLine()) {
				//Split line data from CSV format and call extra function to set up employee.
				String[] employee = input.nextLine().split(",");
				setUpEmployee(employee[0], employee[1], true);
			}
			input.close();
		} catch (FileNotFoundException e) {
			//File is assumed to be here, no error handling here
			e.printStackTrace();
		}
	}
	
	/**
	 * Set up a single employee and add it to the employee list.
	 * 
	 * @param employeeID		The employee ID
	 * @param role				The role of the employee
	 * @param employeeStatus	Boolean for handling current vs new employees
	 */
	
	private void setUpEmployee(String employeeID, String role, boolean employeeStatus) {
		//Construct an employee.
		Employee employee = new Employee (employeeID, role);
		
		//If current employee, compare with the list of activities and current flags accordingly.
		if(employeeStatus) {
			for(Activity singleActivity: listActivities) {
				if(singleActivity.getEmployeeID().equals(employeeID)) {
					//Only set the flags for today in case if the user logs out today. History is forgotten past the current day.
					SimpleDateFormat dateFormat = new SimpleDateFormat ("MM/dd/yyyy");
					String activityDate = dateFormat.format(singleActivity.getDate()).toString();
					if(activityDate.equals(today)) {
						String action = singleActivity.getActivity();
						if(action.equals("clock-in")) {
							employee.setOnShift(true);
						} else if(action.equals("clock-out")) {
							employee.setOnShift(false);
						} else if(action.equals("break-start")) {
							employee.setOnBreak(true);
						} else if(action.equals("break-end")) {
							employee.setOnBreak(false);
						} else if(action.equals("lunch-start")) {
							employee.setOnLunch(true);
						} else if(action.equals("lunch-end")) {
							employee.setOnLunch(false);
						}
					}
				}
			}
		} else {
			//New employee, write the employee to a file. No need to set flags for employee.
			File f = new File("src/employees.txt");
			FileWriter fw;
			
			//Format is: employeeID,role
			String writeToFile = employeeID + "," + role + "\n";
			
			try {
				fw = new FileWriter(f, true);
				fw.write(writeToFile);
				fw.close();
			} catch (IOException e) {
				//File is assumed to be here, no error handling here
				e.printStackTrace();
			}
			
			System.out.println("\nNew employee added successfully: " + employeeID);
		}
		
		//Add the employee
		listEmployees.add(employee);
	}
	
	/**
	 * Display the main header with current time
	 */
	
	public void displayHeader() {
		System.out.println("Paychex Time Clock Application");
		String time = getCurrentTime();
		System.out.println("Current Time: " + time);
	}
	
	/**
	 * Display the login menu and options
	 */
	
	public void displayLoginMenu() {
		System.out.println("\nLogin options:");
		System.out.println("1. Login using your employee ID.");
		System.out.println("2. Register a new employee ID.");
		System.out.println("0. Exit the application.");
	}
	
	/**
	 * Display the home menu after logging in.
	 * @param isAdmin If true, display admin options
	 */
	
	public void displayHomeMenu(boolean isAdmin) {
		System.out.println("\nEmployee options: ");
		System.out.println("1. Start a work shift.");
		System.out.println("2. End a work shift.");
		System.out.println("3. Start a break.");
		System.out.println("4. End a break.");
		System.out.println("5. Start a lunch break.");
		System.out.println("6. End a lunch break.");
		if(isAdmin) {
			System.out.println("7. View reports on Employees.");
		}
		System.out.println("0. Logout Employee.");		
	}
	
	/**
	 * Display admin reports menu
	 */
	
	public void displayReportsMenu() {
		System.out.println("\nReports Menu:");
		System.out.println("1. View an employee ID's shift activity.");
		System.out.println("2. View all employee shift data.");
		System.out.println("0. Return to the Employee home menu.\n");
	}
	
	/**
	 * Function to get the current time in mm/dd/yyyy hh:mm AM/PM format
	 * @return the curent time in string format
	 */
	
	public String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
		String date = dateFormat.format(new Date()).toString();
		return date;
	}
	
	/**
	 * Handles the login menu logic and functionality.
	 */
	
	public void login() {
		
		//Initialize input and options for later.
		Scanner input = new Scanner(System.in);
		int loginOption = -1;
		String employeeID;

		//Display menus
		displayHeader();
		displayLoginMenu();
		
		//If user enters non-integer value or invalid number, they will loop until a valid number is entered
		do {
			System.out.print("\nPlease enter the number of your option above: ");
			try {
				loginOption = input.nextInt();
				boolean employeeCheck;
				if (loginOption == 1) {
					//If user enters a non-existent employee ID, they will loop until a valid one is entered.
					do {
						System.out.print("\nPlease enter your employee ID: ");
						employeeID = input.next();
						employeeCheck = isEmployee(employeeID);
						if(!employeeCheck) {
							System.err.println(employeeID + " is not an employee. Please try again.");
							input.nextLine();
						}
					} while(!employeeCheck);
					//Go to employee home menu logic
					employeeHome(employeeID, input);
					displayHeader();
					displayLoginMenu();
				} else if(loginOption == 2) {
					//If user enters an already existing employee ID, they will loop until a new one is entered.
					do {
						System.out.print("\nPlease enter the employee ID you wish to register: ");
						employeeID = input.next();
						employeeCheck = isEmployee(employeeID);
						if(employeeCheck) {
							System.err.println(employeeID + " is already an employee! Please try again.");
						}
					} while(employeeCheck);
					setUpEmployee(employeeID, "user", false);
					displayLoginMenu();
				} else if(loginOption != 0) {
					System.err.println("You entered an invalid option. Please try again.");
					input.nextLine();
				}
			} catch (InputMismatchException ime) {
				System.err.println("You entered an invalid option. Please try again.");
				input.nextLine();
			}
		} while (loginOption != 0);
		input.close();
	}
	
	/**
	 * Employee home menu logic. Handles the choices for the menu.
	 * @param employeeID  The current Employee logged in
	 * @param input		  The input parser passed in
	 */
	
	public void employeeHome(String employeeID, Scanner input) {
		//Initialize input and options for later
		int homeOption = -1;
		Employee employee = getEmployee(employeeID);
		boolean isAdmin = false;
		
		//Check for admin role here
		if(employee.getRole().equals("admin")) {
			isAdmin = true;
		}
		
		//Display menus, user, and roles here.
		System.out.println();
		displayHeader();
		System.out.println("\nWelcome " + employee.getUniqueID());
		System.out.println("Role: " + employee.getRole());		
		displayHomeMenu(isAdmin);
		
		//Loop until a valid option is selected.
		do {
			System.out.print("\nPlease enter the number of your option above: ");
			try {
				homeOption = input.nextInt();
				switch (homeOption) {
				
				//Case 1 - Start work shift but only if they are an admin or not on a shift.
				case 1:
					if(employee.isOnShift() && !isAdmin) {
						System.err.println("You are already on a shift. Please select a different option.");
						input.nextLine();
					} else {
						employee.setOnShift(true);
						recordActivity(employeeID, "clock-in");
						displayHomeMenu(isAdmin);
					}
					break;
					
				//Case 2 - End work shift but only if they are an admin or on a shift.
				case 2:
					if(!employee.isOnShift() && !isAdmin) {
						System.err.println("You are not on a shift. Please select a different option.");
						input.nextLine();
					} else {
						employee.setOnShift(false);
						recordActivity(employeeID, "clock-out");
						displayHomeMenu(isAdmin);
					}
					break;
				
				//Case 3 - Start break but only if they are an admin or not on a break.	
				case 3:
					if(!employee.isOnShift() && !isAdmin) {
						System.err.println("You are not on a shift. Please select a different option.");
						input.nextLine();
					} else if(employee.isOnBreak() && !isAdmin) {
						System.err.println("You are already on a break. Please select a different option.");
						input.nextLine();
					} else {
						recordActivity(employeeID, "break-start");
						employee.setOnBreak(true);
						displayHomeMenu(isAdmin);
					}
					break;
					
				//Case 4 - End break but only if they are an admin or on a break.	
				case 4:
					if(!employee.isOnShift() && !isAdmin) {
						System.err.println("You are not on a shift. Please select a different option.");
						input.nextLine();
					} else if(!employee.isOnBreak() && !isAdmin) {
						System.err.println("You are not on a break. Please select a different option.");
						input.nextLine();
					} else {
						recordActivity(employeeID, "break-end");
						employee.setOnBreak(false);
						displayHomeMenu(isAdmin);
					}
					break;
				
				//Case 5 - Start lunch but only if they are an admin or not on a lunch break.	
				case 5:
					if(!employee.isOnShift() && !isAdmin) {
						System.err.println("You are not on a shift. Please select a different option.");
						input.nextLine();
					} else if(employee.isOnLunch() && !isAdmin) {
						System.err.println("You are already on a lunch break. Please select a different option.");
						input.nextLine();
					} else {
						recordActivity(employeeID, "lunch-start");
						employee.setOnLunch(true);
						displayHomeMenu(isAdmin);
					}
					break;
				
				//Case 6 - Start break but only if they are an admin or on a lunch break.	
				case 6:
					if(!employee.isOnShift() && !isAdmin) {
						System.err.println("You are not on a shift. Please select a different option.");
						input.nextLine();
					} else if(!employee.isOnLunch() && !isAdmin) {
						System.err.println("You are not on a lunch break. Please select a different option.");
						input.nextLine();
					} else {
						recordActivity(employeeID, "lunch-end");
						employee.setOnLunch(false);
						displayHomeMenu(isAdmin);
					}
					break;
				
				//Case 7 - Goes to reports menu only if they are an admin.	
				case 7:
					if(isAdmin) {
						reportsMenu(input);
						displayHomeMenu(isAdmin);
					} else {
						System.err.println("You entered an invalid option. Please try again.");
						input.nextLine();
					}
					break;
				
				//Case 0 - Logs out current employee.	
				case 0:
					System.out.println("\nLogging out employee.\n");
					break;
				
				//Invalid case
				default:
					System.err.println("You entered an invalid option. Please try again.");
					input.nextLine();	
				}
				
			} catch (InputMismatchException ime) {
				System.err.println("You entered an invalid option. Please try again.");
				input.nextLine();				
			}
		} while (homeOption != 0);
	}
	
	/**
	 * Record the activity made by the employee in the list of activities. Also write activity to the external file.
	 * @param employeeID Employee that made the activity
	 * @param action	 Type of action the employee made.
	 */
	
	public void recordActivity(String employeeID, String action) {
		//Define external file and activity
		File f = new File("src/calendar.txt");
		FileWriter fw;
		Date currentTime = new Date();
		Activity activity = new Activity(employeeID, action, currentTime);

		//Add activity to the list
		listActivities.add(activity);
		
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
		String date = dateFormat.format(currentTime).toString();
		
		//Add activity to the file.
		String writeToFile = employeeID + "," + action + "," + date + "\n";
		try {
			fw = new FileWriter(f, true);
			fw.write(writeToFile);
			fw.close();
		} catch (IOException e) {
			//File is assumed to be here, no error handling here
			e.printStackTrace();
		}

		//Display activity recorded
		if(action.equals("clock-in")) {
			System.out.println("\nShift started. Current time: " + date);
		} else if(action.equals("clock-out")) {
			System.out.println("\nShift ended. Current time: " + date);
		} else if(action.equals("break-start")) {
			System.out.println("\nBreak started. Current time: " + date);
		} else if(action.equals("break-end")) {
			System.out.println("\nBreak ended. Current time: " + date);
		} else if(action.equals("lunch-start")) {
			System.out.println("\nLunch started. Current time: " + date);
		} else if(action.equals("lunch-end")) {
			System.out.println("\nLunch ended. Current time: " + date);
		}
	}
	
	/**
	 * Handles the report menu logic choices
	 * @param input Pass in scanner for input
	 */
	
	public void reportsMenu(Scanner input) {
		int reportOption = -1;
		displayReportsMenu();
		//Loop until a valid option is reached.
		do {
			System.out.print("Please enter the number of your option above: ");
			try {
				reportOption = input.nextInt();
				switch(reportOption) {
				//Case 1: Search activity by employee.
				case 1:
					String employeeID;
					boolean employeeCheck;
					//Loop until a valid Employee ID is selected.
					do {
						System.out.print("\nPlease enter the employee ID that you would like to view shift activity on: ");
						employeeID = input.next();
						employeeCheck = isEmployee(employeeID);
						if(!employeeCheck) {
							System.err.println(employeeID + " is not an employee. Please try again.");
							input.nextLine();
						}
					} while(!employeeCheck);
					//Display single employee data
					displayEmployeeData(employeeID);
					displayReportsMenu();
					break;
				//Case 2: Display all employee activity
				case 2:
					displayAllData();
					displayReportsMenu();
					break;
				
				//Case 0: Return back to the employee home menu
				case 0:
					System.out.println("\nReturning back to employee home menu.");
					break;
				
				//Invalid case
				default:
					System.err.println("You entered an invalid option. Please try again.");
					input.nextLine();		
				}
			} catch (InputMismatchException ime) {
				System.err.println("You entered an invalid option. Please try again.");
				input.nextLine();	
			}
		} while(reportOption != 0);
	}
	
	/**
	 * Function to check if employee ID entered is an employee in the list
	 * @param employeeID employee ID
	 * @return true if employee in list, false otherwise
	 */
	
	public boolean isEmployee(String employeeID) {
		for(Employee emp: listEmployees) {
			if(emp.getUniqueID().equals(employeeID)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get an employee based on the employee ID
	 * @param employee employee ID
	 * @return the matching Employee object
	 */
	
	public Employee getEmployee(String employeeID) {
		Employee employee = null;
		for(Employee emp: listEmployees) {
			if(emp.getUniqueID().equals(employeeID)) {
				employee = emp;
			}
		}
		return employee;
	}
	
	/**
	 * Display a single employee's data
	 * @param employeeID The employee ID.
	 */
	
	public void displayEmployeeData(String employeeID) {
		boolean activityFlag = false;
		for(Activity action: listActivities) {
			if(action.getEmployeeID().equals(employeeID) ) {
				//Report activity if there is one
				if(!activityFlag) {
					System.out.println("\nShift activity for employee: " + employeeID);
					activityFlag = true;
				}
				
				//Translate shift activity
				String activity = action.getActivity();
				if(activity.equals("clock-in")) {
					activity = "Shift started.";
				} else if(activity.equals("clock-out")) {
					activity = "Shift ended.";
				} else if(activity.equals("break-start")) {
					activity = "Break started.";
				} else if(activity.equals("break-end")) {
					activity = "Break ended.";
				} else if(activity.equals("lunch-start")) {
					activity = "Lunch started.";
				} else if(activity.equals("lunch-end")) {
					activity = "Lunch ended.";
				}
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
				String date = dateFormat.format(action.getDate()).toString();
				
				System.out.println(date + ": " + activity);
			}
		}
		
		//If no activities, then display no activities.
		if(!activityFlag) {
			System.out.println("\nNo shift data for employee: " + employeeID);
		}
	}
	
	/**
	 * Display all shift data for all employees in Date, Employee, Activity format.
	 */
	public void displayAllData() {	
		System.out.println("\nAll shift activity:");
		for(Activity action: listActivities) {
			String activity = action.getActivity();
			if(activity.equals("clock-in")) {
				activity = "Shift started.";
			} else if(activity.equals("clock-out")) {
				activity = "Shift ended.";
			} else if(activity.equals("break-start")) {
				activity = "Break started.";
			} else if(activity.equals("break-end")) {
				activity = "Break ended.";
			} else if(activity.equals("lunch-start")) {
				activity = "Lunch started.";
			} else if(activity.equals("lunch-end")) {
				activity = "Lunch ended.";
			}
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
			String date = dateFormat.format(action.getDate()).toString();
			
			System.out.println("Date: " + date + ", Employee: " + action.getEmployeeID() + ", Activity: " + activity);
		}
	}
}//TimeClock
