package com.johnzaro.medialab_flight_simulation.core;

import com.johnzaro.medialab_flight_simulation.data_structures.*;
import com.johnzaro.medialab_flight_simulation.screens.MainWindow;
import com.johnzaro.medialab_flight_simulation.screens.PopUp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GlobalVariables
{
	public static MainWindow mainWindow;
	public static PopUp popUp;

	public static final String PROGRAM_TITLE = "MediaLab Flight Simulation";

	public static final ArrayList<Image> icons = new ArrayList<>();

	static InputStream worldInputStream, airportsInputStream, flightsInputStream;

	public static int[][] map;
	public static Airport[] airports;
	public static Flight[] flights;

	public static Font font = new Font("Tahoma", Font.PLAIN, 15);
	public static Font fontBold = new Font("Tahoma", Font.BOLD, 15);

	public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	public static final String TAB = "    ";

	public static final Color RED_COLOR = new Color(192, 57, 43);
	public static final Color BLUE_COLOR = new Color(41, 128, 185);
	public static final Color GREEN_COLOR = new Color(39, 174, 96);

	public static final int AIRCRAFT_ICON_SIZE = 25;

	public static final double KNOTS_TO_METERS_PER_SECOND = 0.514444;
	public static final double NAUTICAL_MILES_TO_METERS = 1852;
	public static final double FEET_TO_METERS = 0.3048;
	public static final double PIXELS_TO_METERS = 2315;
	public static final double METERS_TO_PIXELS = 1.0 / PIXELS_TO_METERS;

	public static final double CRASH_DISTANCE = 2.0 * NAUTICAL_MILES_TO_METERS;
	public static final double CRASH_HEIGHT = 500.0 * FEET_TO_METERS;

	private static final int MAP_WIDTH_IN_PIXELS = 60 * 16;
	private static final int MAP_HEIGHT_IN_PIXELS = 30 * 16;
	public static final double MAP_WIDTH_IN_METERS = MAP_WIDTH_IN_PIXELS * PIXELS_TO_METERS;
	public static final double MAP_HEIGHT_IN_METERS = MAP_HEIGHT_IN_PIXELS * PIXELS_TO_METERS;

	public final static int AIRPORT_ORIENTATION_NORTH = 1;
	public final static int AIRPORT_ORIENTATION_EAST = 2;
	public final static int AIRPORT_ORIENTATION_SOUTH = 3;
	public final static int AIRPORT_ORIENTATION_WEST = 4;

	public static final int AIRPORT_SINGLE_ENGINE_ONLY = 1;
	public static final int AIRPORT_TURBO_PROP_JET = 2;
	public static final int AIRPORT_ALL = 3;

	public static final int TAKE_OFF_POSITION = 0;
	public static final int CHANGE_SPEED_TO_NORMAL_POSITION = 1;
	public static final int STOP_CLIMBING_POSITION = 2;
	public static final int START_DESCENDING_POSITION = 3;
	public static final int CHANGE_SPEED_TO_LANDING_POSITION = 4;
	public static final int LANDED_POSITION = 5;
	public static final int NUMBER_OF_FLIGHT_POSITIONS = 6;

	public static final int TAKE_OFF_MOMENT = 0;
	public static final int CHANGE_SPEED_TO_NORMAL_MOMENT = 1;
	public static final int STOP_CLIMBING_MOMENT = 2;
	public static final int START_DESCENDING_MOMENT = 3;
	public static final int CHANGE_SPEED_TO_LANDING_MOMENT = 4;
	public static final int LANDED_MOMENT = 5;
	public static final int NUMBER_OF_FLIGHT_MOMENTS = 6;

	public static final int FLIGHT_PHASE_ASCENDING = 1;
	public static final int FLIGHT_PHASE_NORMAL = 2;
	public static final int FLIGHT_PHASE_DESCENDING = 3;

	public static final int POPUP_AIRPORTS = 1;
	public static final int POPUP_AIRCRAFTS = 2;
	public static final int POPUP_FLIGHTS = 3;

	public static final int FLIGHT_NOT_TAKEN_OFF_YET = 0;
	public static final int FLIGHT_ACTIVE = 1;
	public static final int FLIGHT_LANDED = 2;
	public static final int FLIGHT_CRASHED = 3;

	static final int SINGLE_ENGINE_AIRCRAFT_TYPE = 1;
	static final int TURBO_PROP_AIRCRAFT_TYPE = 2;
	static final int JET_AIRCRAFT_TYPE = 3;

	public static final Aircraft SINGLE_ENGINE_AIRCRAFT = new SingleEngineAircraft();
	public static final Aircraft TURBO_PROP_AIRCRAFT = new TurboPropAircraft();
	public static final Aircraft JET_AIRCRAFT = new JetAircraft();

	public static final Color[] mapColors =
			{
					new Color(52, 152, 219),
					new Color(46, 204, 113),
					new Color(39, 174, 96),
					new Color(34,139,34),
					new Color(244, 179, 80),
					new Color(209, 131, 73),
					new Color(145, 80, 20),
			};

	public static final int MAP_COLOR_0 = 0;
	public static final int MAP_COLOR_0_200 = 1;
	public static final int MAP_COLOR_200_400 = 2;
	public static final int MAP_COLOR_400_700 = 3;
	public static final int MAP_COLOR_700_1500 = 4;
	public static final int MAP_COLOR_1500_3500 = 5;
	public static final int MAP_COLOR_OVER_3500 = 6;

	public static final Image AIRPORT_ICON_FOR_MAP = Toolkit.getDefaultToolkit().getImage(GlobalVariables.class.getResource("/images/airport.png"));
	public static final ImageIcon AIRPORT_ICON = new ImageIcon(GlobalVariables.class.getResource("/images/airport_icon.jpg"));
	public static final ImageIcon AIRCRAFT_ICON = new ImageIcon(GlobalVariables.class.getResource("/images/aircraft_icon.jpg"));
	public static final ImageIcon FLIGHT_ICON = new ImageIcon(GlobalVariables.class.getResource("/images/flight_icon.jpg"));

	private static BufferedImage singleEngineAircraftImage = null;
	private static BufferedImage turboPropAircraftImage = null;
	private static BufferedImage jetAircraftImage = null;

	static
	{
		try
		{
			singleEngineAircraftImage = ImageIO.read(GlobalVariables.class.getResourceAsStream("/images/small.png"));
			turboPropAircraftImage = ImageIO.read(GlobalVariables.class.getResourceAsStream("/images/middle.png"));
			jetAircraftImage = ImageIO.read(GlobalVariables.class.getResourceAsStream("/images/big.png"));
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, e.getStackTrace(), "Error trying to load aircraft icons", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static BufferedImage getSingleEngineAircraftImage()
	{
		return singleEngineAircraftImage;
	}
	public static BufferedImage getTurboPropAircraftImage()
	{
		return turboPropAircraftImage;
	}
	public static BufferedImage getJetAircraftImage()
	{
		return jetAircraftImage;
	}

	public static int getHeightOfPosition(Position p)
	{
		if(p != null)
		{
			int mapX = (int) Math.floor(p.getX() * METERS_TO_PIXELS / 16.0);
			int mapY = (int) Math.floor(p.getY() * METERS_TO_PIXELS / 16.0);

			return map[mapX][mapY];
		}
		return -1;
	}
}
