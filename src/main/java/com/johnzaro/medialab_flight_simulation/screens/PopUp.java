package com.johnzaro.medialab_flight_simulation.code.screens;

import com.johnzaro.medialab_flight_simulation.core.GlobalVariables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static com.johnzaro.medialab_flight_simulation.core.GlobalVariables.*;

public class PopUp extends JFrame
{
	private JLabel title;
	private JButton closeButton;
	private JTable table;
	private JScrollPane scrollPane;

	private String[][] dataTable;

	private String[] columnNamesForAirports = {"Unique ID", "Name", "Aircrafts Allowed", "State", "Orientation"};
	private String[] columnNamesForAircrafts = {"Take off Airport Name", "Landing Airport Name", "Current Speed", "Current Height", "Current Fuel"};
	private String[] columnNamesForFlights = {"Take off Airport Name", "Landing Airport Name", "Aircraft type", "Flight state"};

	public PopUp()
	{
		setSize(new Dimension(1000, 500));
		setResizable(false);
		setUndecorated(true);
		setLocationRelativeTo(mainWindow);
		setIconImages(GlobalVariables.icons);
		setLayout(null);
		getContentPane().setBackground(new Color(127, 140, 141));

		getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

		title = new JLabel();
		title.setFont(GlobalVariables.font.deriveFont(30f));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setVerticalAlignment(SwingConstants.CENTER);
		title.setBounds(getWidth() / 2 - 150, 15, 300, 50);
		add(title);

		closeButton = new JButton("Close");
		closeButton.setFocusable(false);
		closeButton.setFont(GlobalVariables.font);
		closeButton.setCursor(GlobalVariables.HAND_CURSOR);
		int closeButtonWidth = 125;
		int closeButtonHeight = 40;

		closeButton.setBounds(getWidth() / 2 - closeButtonWidth / 2, 440, closeButtonWidth, closeButtonHeight);
		add(closeButton);

		closeButton.addActionListener(l -> hidePopUp());
	}

	private void setupTable(int typeOfTable)
	{
		if(typeOfTable == POPUP_AIRPORTS)
		{
			title.setText("Airports");
			title.setIcon(AIRPORT_ICON);
			dataTable = getAirportsTable();
			table = new JTable(dataTable, columnNamesForAirports);
		}
		else if(typeOfTable == POPUP_AIRCRAFTS)
		{
			title.setText("Aircrafts");
			title.setIcon(AIRCRAFT_ICON);
			dataTable = getAircraftsTable();
			table = new JTable(dataTable, columnNamesForAircrafts);
		}
		else
		{
			title.setText("Flights");
			title.setIcon(FLIGHT_ICON);
			dataTable = getFlightsTable();
			table = new JTable(dataTable, columnNamesForFlights);
		}

		//center all column header names in table
		DefaultTableCellRenderer ren = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
		ren.setHorizontalAlignment(SwingConstants.CENTER);
		table.getTableHeader().setDefaultRenderer(ren);

		//set bold font to column header names
		table.getTableHeader().setFont(fontBold);

		//set column header height
		Dimension d = table.getTableHeader().getPreferredSize();
		d.height = 40;
		table.getTableHeader().setPreferredSize(d);

		//set black line border to column header
		table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.BLACK));

		//set font to table cells and center all text
		DefaultTableCellRenderer r = new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
														   boolean hasFocus, int row, int column)
			{
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setHorizontalAlignment(SwingConstants.CENTER);
				setFont(GlobalVariables.font);
				return this;
			}
		};
		for(int i = 0; i < table.getColumnModel().getColumnCount(); i++)
			table.getColumnModel().getColumn(i).setCellRenderer(r);

		//set row height
		table.setRowHeight(30);

		//set black line border to table
		table.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		table.setEnabled(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);

		scrollPane = new JScrollPane(table);
		scrollPane.getVerticalScrollBar().setCursor(GlobalVariables.HAND_CURSOR);

		//set scrollPane transparent
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		//remove border from scrollPane
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		scrollPane.setBounds(25, 80, getWidth() - 50, 340);
		add(scrollPane);
	}

	private String[][] getAirportsTable()
	{
		String[][] tempTable = new String[airports.length][5];

		for(int i = 0; i < airports.length; i++)
		{
			tempTable[i][0] = String.valueOf(airports[i].getUniqueID());
			tempTable[i][1] = airports[i].getName();

			switch(airports[i].getType())
			{
				case AIRPORT_SINGLE_ENGINE_ONLY: tempTable[i][2] = "SingleEngine only"; break;
				case AIRPORT_TURBO_PROP_JET: tempTable[i][2] = "TurboProp and Jet only"; break;
				case AIRPORT_ALL: tempTable[i][2] = "All"; break;
			}
			if(airports[i].isOpen()) tempTable[i][3] = "Open";
			else tempTable[i][3] = "Closed";

			switch(airports[i].getOrientation())
			{
				case AIRPORT_ORIENTATION_NORTH: tempTable[i][4] = "North"; break;
				case AIRPORT_ORIENTATION_EAST: tempTable[i][4] = "East"; break;
				case AIRPORT_ORIENTATION_SOUTH: tempTable[i][4] = "South"; break;
				case AIRPORT_ORIENTATION_WEST: tempTable[i][4] = "West"; break;
			}
		}
		return tempTable;
	}

	private String[][] getAircraftsTable()
	{
		int numOfActiveFlights = mainWindow.getNumOfActiveFlights();
		String[][] tempTable = new String[numOfActiveFlights][5];

		int flightsCounter = 0, tableCounter = 0;
		while(tableCounter < numOfActiveFlights)
		{
			if(flights[flightsCounter].getState() == FLIGHT_ACTIVE)
			{
				tempTable[tableCounter][0] = flights[flightsCounter].getTakeOffAirport().getName();
				tempTable[tableCounter][1] = flights[flightsCounter].getLandingAirport().getName();
				tempTable[tableCounter][2] = String.valueOf(getSpeedInKnots(flights[flightsCounter].getCurrentSpeedXYZ()));
				tempTable[tableCounter][3] = String.valueOf(getHeightInFeet(flights[flightsCounter].getCurrentHeight()));
				tempTable[tableCounter][4] = String.valueOf((int) Math.round(flights[flightsCounter].getCurrentFuel()));
				tableCounter++;
			}
			flightsCounter++;
		}
		return tempTable;
	}

	private String[][] getFlightsTable()
	{
		String[][] tempTable = new String[flights.length][4];

		for(int i = 0; i < flights.length; i++)
		{
			tempTable[i][0] = flights[i].getTakeOffAirport().getName();
			tempTable[i][1] = flights[i].getLandingAirport().getName();

			if(flights[i].getAircraft() == SINGLE_ENGINE_AIRCRAFT) tempTable[i][2] = "Single Engine";
			else if(flights[i].getAircraft() == TURBO_PROP_AIRCRAFT) tempTable[i][2] = "TurboProp";
			else tempTable[i][2] = "Jet";

			switch(flights[i].getState())
			{
				case FLIGHT_NOT_TAKEN_OFF_YET: tempTable[i][3] = "Not taken off yet"; break;
				case FLIGHT_ACTIVE: tempTable[i][3] = "Active"; break;
				case FLIGHT_LANDED: tempTable[i][3] = "Landed"; break;
				case FLIGHT_CRASHED: tempTable[i][3] = "Crashed"; break;
			}
		}
		return tempTable;
	}

	private void hidePopUp()
	{
		setVisible(false);
		remove(scrollPane);
		mainWindow.setEnabled(true);
		mainWindow.requestFocus();
	}

	private int getSpeedInKnots(double speedInMPer1_2Sec)
	{
		speedInMPer1_2Sec /= 1.2;
		speedInMPer1_2Sec /= KNOTS_TO_METERS_PER_SECOND;
		return (int) Math.round(speedInMPer1_2Sec);
	}

	private int getHeightInFeet(double heightInMeters)
	{
		return (int) Math.round(heightInMeters / FEET_TO_METERS);
	}

	public void showPopUp(int typeOfPopUp)
	{
		setupTable(typeOfPopUp);

		setVisible(true);
	}
}
