package com.johnzaro.medialab_flight_simulation.core;

import com.johnzaro.medialab_flight_simulation.data_structures.Airport;
import com.johnzaro.medialab_flight_simulation.data_structures.Flight;
import com.johnzaro.medialab_flight_simulation.data_structures.Position;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import static com.johnzaro.medialab_flight_simulation.core.GlobalVariables.*;

public class PowerOn
{
	static void loadProgramIcons()
	{
		icons.add(Toolkit.getDefaultToolkit().getImage(PowerOn.class.getResource("/images/icon_16.jpg")));
		icons.add(Toolkit.getDefaultToolkit().getImage(PowerOn.class.getResource("/images/icon_32.jpg")));
		icons.add(Toolkit.getDefaultToolkit().getImage(PowerOn.class.getResource("/images/icon_64.jpg")));
		icons.add(Toolkit.getDefaultToolkit().getImage(PowerOn.class.getResource("/images/icon_128.jpg")));
	}

	public static void loadDefaultDataFiles()
	{
		if(openDefaultDataFiles())
		{
			if(loadMap())
			{
				if(loadAirports())
				{
					mainWindow.setSimulationCanStart(loadFlights());
				}
			}
		}
		if(mainWindow.simulationCanStart()) mainWindow.appendMessageToMessageBoard("Default simulation can be started", GREEN_COLOR);
		else mainWindow.appendMessageToMessageBoard("Default simulation cannot be started", RED_COLOR);
	}

	public static void loadDataFilesWithID(String folder, String mapID)
	{
		if(openDataFilesWithID(folder, mapID))
		{
			if(loadMap())
			{
				if(loadAirports())
					mainWindow.setSimulationCanStart(loadFlights());
			}
		}
	}

	private static boolean openDefaultDataFiles()
	{
		//if find default files return true else return false
		try
		{
			worldInputStream = PowerOn.class.getResourceAsStream("/default_input/world_default.txt");
			airportsInputStream = PowerOn.class.getResourceAsStream("/default_input/airports_default.txt");
			flightsInputStream = PowerOn.class.getResourceAsStream("/default_input/flights_default.txt");

			if(worldInputStream == null || airportsInputStream == null || flightsInputStream == null) throw new Exception();

			mainWindow.appendMessageToMessageBoard("Found and opened default data files", BLUE_COLOR);
			return true;
		}
		catch(Exception e)
		{
			GlobalVariables.mainWindow.appendMessageToMessageBoard("Error!!! Could not find or open the default data files\n" + TAB + "Cannot continue loading anything else", RED_COLOR);
			return false;
		}
	}

	private static boolean openDataFilesWithID(String folder, String mapID)
	{
		try
		{
			worldInputStream = new FileInputStream(folder + File.separator + "world_" + mapID + ".txt");
			airportsInputStream = new FileInputStream(folder + File.separator + "airports_" + mapID + ".txt");
			flightsInputStream = new FileInputStream(folder + File.separator + "flights_" + mapID + ".txt");

			mainWindow.appendMessageToMessageBoard("Found and opened data files with MapID: " + mapID, BLUE_COLOR);
			return true;
		}
		catch(Exception e)
		{
			GlobalVariables.mainWindow.appendMessageToMessageBoard(
					"Error!!! Could not find or open the data files with\n" + TAB + "mapID: " + mapID + " in the specified folder\n" + TAB + "Cannot continue loading anything else", RED_COLOR);
			return false;
		}
	}

	private static boolean loadMap()
	{
		//if map is valid return true else return false

		Scanner input = null;

		map = new int[60][30];

		try
		{
			input = new Scanner(worldInputStream);
			String[] line;

			for(int y = 0; y < 30; y++)
			{
				line = input.nextLine().split(",");
				for(int x = 0; x < 60; x++)
				{
					if(isNotValidInteger(line[x]))
					{
						GlobalVariables.mainWindow.appendMessageToMessageBoard(String.format("Error!!! Map data is invalid at line: %d, column: %d\n" + TAB + "Cannot continue loading anything else",
								y + 1, x + 1), RED_COLOR);
						return false;
					}

					map[x][y] = Integer.parseInt(line[x]);
				}
			}

			input.close();
			worldInputStream.close();

			mainWindow.appendMessageToMessageBoard("Map was loaded successfully", BLUE_COLOR);
			return true;
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(mainWindow, e.getStackTrace(), "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		finally
		{
			if(input != null) input.close();
		}
	}

	private static boolean loadAirports()
	{
		//if at least one airport is valid return true else return false

		Scanner input = null;

		try
		{
			input = new Scanner(airportsInputStream);
			String[] line;

			ArrayList<Airport> airportsList = new ArrayList<>();
			int counter = 0;
			boolean notUnique;
			int invalidAirports = 0;

			while(input.hasNextLine())
			{
				line = input.nextLine().split(",");

				Airport airport = new Airport();
				counter++;

				if(isNotValidInteger(line[0]) || isNotValidInteger(line[1]) || isNotValidInteger(line[2]) || line[3] == null || isNotValidInteger(line[4]) || isNotValidInteger(line[5]) || !isBoolean(line[6]))
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(String.format("Error!!! Failed to parse info for the airport No.%d", counter), RED_COLOR);
					invalidAirports++;
					continue;
				}

				airport.setUniqueID(Integer.parseInt(line[0]));
				notUnique = false;
				for(Airport a: airportsList)
				{
					if(a.getUniqueID() == airport.getUniqueID())
					{
						notUnique = true;
						break;
					}
				}
				if(notUnique)
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(String.format("Error!!! Airport No.%d has not unique id: (%d)", counter, airport.getUniqueID()), RED_COLOR);
					invalidAirports++;
					continue;
				}

				//in the file the 1st coordinate is our Y and the 2nd coordinate is our X
				airport.setPosition(new Position((Integer.parseInt(line[2]) * 16 + 8) * PIXELS_TO_METERS, (Integer.parseInt(line[1]) * 16 + 8) * PIXELS_TO_METERS));
				airport.setName(line[3]);

				airport.setOrientation(Integer.parseInt(line[4]));
				if(airport.getOrientation() < 1 || airport.getOrientation() > 4)
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(String.format("Error!!! airport No.%d with id %s has invalid info:\n" + TAB + "%s", counter, airport.getUniqueID(), "Wrong airport orientation"), RED_COLOR);
					invalidAirports++;
					continue;
				}

				airport.setType(Integer.parseInt(line[5]));
				if(airport.getType() < 1 || airport.getType() > 3)
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(String.format("Error!!! airport No.%d with id %s has invalid info:\n" + TAB + "%s", counter, airport.getUniqueID(), "Wrong airport type"), RED_COLOR);
					invalidAirports++;
					continue;
				}

				airport.setHeight(getHeightOfPosition(airport.getPosition()));

				airport.setOpen(toBoolean(line[6]));

				airportsList.add(airport);
			}

			airports = airportsList.toArray(new Airport[0]);

			input.close();
			airportsInputStream.close();

			if(airports.length > 0)
			{
				mainWindow.appendMessageToMessageBoard(String.format("Number of airports: %d\n" + TAB + "Could not load: %d\n" + TAB + "Successfully loaded: %d", counter, invalidAirports, airports.length), BLUE_COLOR);
				return true;
			}
			else
			{
				mainWindow.appendMessageToMessageBoard("Could not load any of the " + counter + " airports.\n" + TAB + "Cannot continue loading anything else", RED_COLOR);
				return false;
			}
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(mainWindow, e.getStackTrace(), "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		finally
		{
			if(input != null) input.close();
		}
	}

	private static boolean loadFlights()
	{
		//if at least one flight is valid return true else return false

		Scanner input = null;

		try
		{
			input = new Scanner(flightsInputStream);
			String[] line;

			ArrayList<Flight> flightList = new ArrayList<>();
			int counter = 0;
			boolean notUnique;
			int invalidFlights = 0;

			while(input.hasNextLine())
			{
				line = input.nextLine().split(",");

				Flight flight = new Flight();
				counter++;

				if(isNotValidInteger(line[0]) || isNotValidInteger(line[1]) || isNotValidInteger(line[2]) || isNotValidInteger(line[3]) ||
						line[4] == null || isNotValidInteger(line[5]) || isNotValidInteger(line[6]) || isNotValidInteger(line[7]) || isNotValidInteger(line[8]))
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(String.format("Error!!! Failed to parse info for the flight No.%d", counter), RED_COLOR);
					invalidFlights++;
					continue;
				}

				flight.setUniqueID(Integer.parseInt(line[0]));
				notUnique = false;
				for(Flight f: flightList)
				{
					if(f.getUniqueID() == flight.getUniqueID())
					{
						notUnique = true;
						break;
					}
				}
				if(notUnique)
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(
							String.format("Error!!! Flight No.%d has not unique id: (%d)", counter, flight.getUniqueID()), RED_COLOR);
					invalidFlights++;
					continue;
				}

				flight.setStartTime(Integer.parseInt(line[1]) * 60);	//convert minutes to seconds

				int takeOffAirportID = Integer.parseInt(line[2]);
				int landingAirportID = Integer.parseInt(line[3]);

				for(Airport airport : airports)
				{
					if(airport.getUniqueID() == takeOffAirportID) flight.setTakeOffAirport(airport);
					if(airport.getUniqueID() == landingAirportID) flight.setLandingAirport(airport);

					if(flight.getTakeOffAirport() != null && flight.getLandingAirport() != null) break;
				}
				if(flight.getTakeOffAirport() == null)
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(
							String.format("Error!!! Flight No.%d with id %s has invalid info:\n" + TAB + "%s", counter, flight.getUniqueID(), "Take off airport does not exist"), RED_COLOR);
					invalidFlights++;
					continue;
				}
				if(!flight.getTakeOffAirport().isOpen())
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(
							String.format("Error!!! Flight No.%d with id %s has invalid info:\n" + TAB + "%s", counter, flight.getUniqueID(), "Take off airport is closed"), RED_COLOR);
					invalidFlights++;
					continue;
				}
				if(flight.getLandingAirport() == null)
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(
							String.format("Error!!! Flight No.%d with id %s has invalid info:\n" + TAB + "%s", counter, flight.getUniqueID(), "Landing airport does not exist"), RED_COLOR);
					invalidFlights++;
					continue;
				}
				if(!flight.getLandingAirport().isOpen())
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(
							String.format("Error!!! Flight No.%d with id %s has invalid info:\n" + TAB + "%s", counter, flight.getUniqueID(), "Landing airport is closed"), RED_COLOR);
					invalidFlights++;
					continue;
				}

				flight.setName(line[4]);

				int flightAircraft = Integer.parseInt(line[5]);
				if((flight.getTakeOffAirport().getType() == AIRPORT_SINGLE_ENGINE_ONLY || flight.getLandingAirport().getType() == AIRPORT_SINGLE_ENGINE_ONLY) && flightAircraft != SINGLE_ENGINE_AIRCRAFT_TYPE ||
				   (flight.getTakeOffAirport().getType() == AIRPORT_TURBO_PROP_JET || flight.getLandingAirport().getType() == AIRPORT_TURBO_PROP_JET) && flightAircraft == SINGLE_ENGINE_AIRCRAFT_TYPE)
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(
							String.format("Error!!! Flight No.%d with id %s has invalid info:\n" + TAB + "%s", counter, flight.getUniqueID(), "Aircraft not suitable for the chosen airports"), RED_COLOR);
					invalidFlights++;
					continue;
				}

				if(flightAircraft == SINGLE_ENGINE_AIRCRAFT_TYPE) flight.setAircraft(SINGLE_ENGINE_AIRCRAFT);
				else if(flightAircraft == TURBO_PROP_AIRCRAFT_TYPE) flight.setAircraft(TURBO_PROP_AIRCRAFT);
				else if(flightAircraft == JET_AIRCRAFT_TYPE) flight.setAircraft(JET_AIRCRAFT);
				else
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(
							String.format("Error!!! Flight No.%d with id %s has invalid info:\n" + TAB + "%s", counter, flight.getUniqueID(), "Wrong aircraft type"), RED_COLOR);
					invalidFlights++;
					continue;
				}

				flight.setFlightSpeed(Integer.parseInt(line[6]) * KNOTS_TO_METERS_PER_SECOND * 1.2);
				if(flight.getFlightSpeed() > flight.getAircraft().getMaxSpeed())
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(
							String.format("Error!!! Flight No.%d with id %s has invalid info:\n" + TAB + "%s", counter, flight.getUniqueID(), "Max speed exceeds the limits of the chosen aircraft"), RED_COLOR);
					invalidFlights++;
					continue;
				}

				flight.setFlightHeight(Integer.parseInt(line[7]) * FEET_TO_METERS);
				if(flight.getFlightHeight() > flight.getAircraft().getMaxHeight())
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(
							String.format("Error!!! Flight No.%d with id %s has invalid info:\n" + TAB + "%s", counter, flight.getUniqueID(), "Flight height exceeds the limits of the chosen aircraft"), RED_COLOR);
					invalidFlights++;
					continue;
				}

				flight.setFlightFuel(Integer.parseInt(line[8]));
				if(flight.getFlightFuel() > flight.getAircraft().getMaxFuel())
				{
					GlobalVariables.mainWindow.appendMessageToMessageBoard(
							String.format("Error!!! Flight No.%d with id %s has invalid info:\n" + TAB + "%s", counter, flight.getUniqueID(), "Fuel exceeds the limits of the chosen aircraft"), RED_COLOR);
					invalidFlights++;
					continue;
				}

				flightList.add(flight);
			}

			flights = flightList.toArray(new Flight[0]);

			input.close();
			flightsInputStream.close();

			if(flights.length > 0)
			{
				mainWindow.appendMessageToMessageBoard(String.format("Number of flights: %d\n" + TAB + "Could not load: %d\n" + TAB + "Successfully loaded: %d", counter, invalidFlights, flights.length), BLUE_COLOR);
				return true;
			}
			else
			{
				mainWindow.appendMessageToMessageBoard("Could not load any of the " + counter + " flights.\n" + TAB + "Simulation cannot be started", RED_COLOR);
				return false;
			}
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(mainWindow, e.getStackTrace(), "Error!!! ", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		finally
		{
			if(input != null) input.close();
		}
	}
	private static boolean isNotValidInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
		}
		catch(NumberFormatException | NullPointerException e)
		{
			return true;
		}
		// only got here if we didn't return true
		return s.startsWith("-");
	}

	private static boolean isBoolean(String s)
	{
		return s != null && (s.equals("0") || s.equals("1"));
	}

	private static boolean toBoolean(String s)
	{
		return s.equals("1");
	}
}
