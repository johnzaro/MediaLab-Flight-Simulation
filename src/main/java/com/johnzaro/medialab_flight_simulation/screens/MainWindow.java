package com.johnzaro.medialab_flight_simulation.screens;

import com.johnzaro.medialab_flight_simulation.core.GlobalVariables;
import com.johnzaro.medialab_flight_simulation.data_structures.Airport;
import com.johnzaro.medialab_flight_simulation.data_structures.Flight;
import com.johnzaro.medialab_flight_simulation.data_structures.Position;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.johnzaro.medialab_flight_simulation.core.GlobalVariables.*;
import static com.johnzaro.medialab_flight_simulation.core.PowerOn.loadDataFilesWithID;
import static com.johnzaro.medialab_flight_simulation.core.PowerOn.loadDefaultDataFiles;

public class MainWindow extends JFrame
{
	private final Icon FAST_1 = new ImageIcon(MainWindow.class.getResource("/images/fast1.png"));
	private final Icon FAST_2 = new ImageIcon(MainWindow.class.getResource("/images/fast2.png"));
	private final Icon FAST_3 = new ImageIcon(MainWindow.class.getResource("/images/fast3.png"));

	private JMenuBar menuBar;
	private JMenu menuGame, menuSimulation;
	private JMenuItem menuItemStart, menuItemStop, menuItemLoad, menuItemExit;
	private JMenuItem menuItemAirports, menuItemAircrafts, menuItemFlights;
	private JPanel basicInfoPanel, mapPanel;
	private JLabel simulatedTimeLabel, numOfAircraftsLabel, numOfCollisionsLabel, numOfLandingsLabel;
	private JTextPane messageBoard;
	private JScrollPane scrollPaneForMessageBoard;

	private Timer fastTimer, slowTimer;

	private double simulatedTime;	//in seconds
	private int numOfActiveFlights;
	private int numOfCrashes;
	private int numOfLandings;

	private boolean simulationCanStart;
	private boolean simulationEnded;
	private boolean shouldScroll;

	public MainWindow()
	{
		setTitle(PROGRAM_TITLE);
		setSize(new Dimension(1365, 565));
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setIconImages(GlobalVariables.icons);
		setLayout(new BorderLayout());

		menuBar = new JMenuBar();

		menuGame = new JMenu("Game");
		menuGame.setCursor(GlobalVariables.HAND_CURSOR);
		menuBar.add(menuGame);

		menuSimulation = new JMenu("Simulation");
		menuSimulation.setCursor(GlobalVariables.HAND_CURSOR);
		menuBar.add(menuSimulation);

		menuItemStart = new JMenuItem("Start");
		menuItemStart.setCursor(GlobalVariables.HAND_CURSOR);
		menuGame.add(menuItemStart);

		menuItemStop = new JMenuItem("Stop");
		menuItemStop.setCursor(GlobalVariables.HAND_CURSOR);
		menuGame.add(menuItemStop);

		menuItemLoad = new JMenuItem("Load");
		menuItemLoad.setCursor(GlobalVariables.HAND_CURSOR);
		menuGame.add(menuItemLoad);

		menuItemExit = new JMenuItem("Exit");
		menuItemExit.setCursor(GlobalVariables.HAND_CURSOR);
		menuGame.add(menuItemExit);

		menuItemAirports = new JMenuItem("Airports");
		menuItemAirports.setCursor(GlobalVariables.HAND_CURSOR);
		menuSimulation.add(menuItemAirports);

		menuItemAircrafts = new JMenuItem("Aircrafts");
		menuItemAircrafts.setCursor(GlobalVariables.HAND_CURSOR);
		menuSimulation.add(menuItemAircrafts);

		menuItemFlights = new JMenuItem("Flights");
		menuItemFlights.setCursor(GlobalVariables.HAND_CURSOR);
		menuSimulation.add(menuItemFlights);

		setJMenuBar(menuBar);

		simulatedTimeLabel = new JLabel();
		simulatedTimeLabel.setFont(GlobalVariables.font);

		numOfAircraftsLabel = new JLabel();
		numOfAircraftsLabel.setFont(GlobalVariables.font);

		numOfCollisionsLabel = new JLabel();
		numOfCollisionsLabel.setFont(GlobalVariables.font);

		numOfLandingsLabel = new JLabel();
		numOfLandingsLabel.setFont(GlobalVariables.font);

		basicInfoPanel = new JPanel(new GridLayout(1, 4));
		basicInfoPanel.add(simulatedTimeLabel);
		basicInfoPanel.add(numOfAircraftsLabel);
		basicInfoPanel.add(numOfCollisionsLabel);
		basicInfoPanel.add(numOfLandingsLabel);
		add(basicInfoPanel, BorderLayout.NORTH);

		messageBoard = new JTextPane();
		messageBoard.setFont(GlobalVariables.font);
		messageBoard.setEditable(false);
		messageBoard.setPreferredSize(new Dimension(390, 0));

		scrollPaneForMessageBoard = new JScrollPane(messageBoard);
		scrollPaneForMessageBoard.getVerticalScrollBar().setCursor(GlobalVariables.HAND_CURSOR);
		scrollPaneForMessageBoard.getVerticalScrollBar().addAdjustmentListener(e ->
		{
			if(shouldScroll) e.getAdjustable().setValue(e.getAdjustable().getMaximum());
		});
		add(scrollPaneForMessageBoard, BorderLayout.EAST);

		fastTimer = new Timer(100, l ->
		{
			simulatedTime += 1.2;

			if(simulatedTime % 60 <= 1.3) checkForNewFlights();

			updatePositionForActiveFlights();
			checkForCrashes();
		});
		fastTimer.setCoalesce(false);

		slowTimer = new Timer(1000, l ->
		{
			boolean shouldStop = true;
			for(Flight flight: flights)
			{
				if(flight.getState() == FLIGHT_ACTIVE || flight.getState() == FLIGHT_NOT_TAKEN_OFF_YET)
				{
					shouldStop = false;
					break;
				}
			}
			if(shouldStop)
			{
				stopTimers();

				simulationEnded = true;

				appendMessageToMessageBoard("Simulation ended", BLUE_COLOR);
			}

			updateSimulatedTimeLabel(simulatedTime);
			updateNumOfAircraftsLabel(numOfActiveFlights);
			updateNumOfCrashesLabel(numOfCrashes);
			updateNumOfLandingsLabel(numOfLandings);

			updateMapPanel();
		});

		resetStuff();

		setSimulationCanStart(false);

		setupListeners();
	}

	private void resetStuff()
	{
		simulationEnded = false;

		simulatedTime = 0;
		numOfActiveFlights = 0;
		numOfCrashes = 0;
		numOfLandings = 0;

		updateSimulatedTimeLabel(simulatedTime);
		updateNumOfAircraftsLabel(numOfActiveFlights);
		updateNumOfCrashesLabel(numOfCrashes);
		updateNumOfLandingsLabel(numOfLandings);

		if(flights != null)
		{
			for(Flight flight: flights)
				flight.resetFlight();
		}
	}

	private void startTimers()
	{
		if(simulationEnded)
		{
			resetStuff();
			appendMessageToMessageBoard("Simulation started", BLUE_COLOR);
		}

		fastTimer.start();
		slowTimer.start();
		menuItemLoad.setToolTipText("<HTML>You should stop the running simulation<BR>before loading other files</HTML>");
		menuItemAirports.setToolTipText("<HTML>You should stop the running simulation before<BR>opening the airports information panel</HTML>");
		menuItemAircrafts.setToolTipText("<HTML>You should stop the running simulation before\nopening<BR>the aircrafts information panel</HTML>");
		menuItemFlights.setToolTipText("<HTML>You should stop the running simulation before\nopening<BR>the flights information panel</HTML>");
	}

	private void stopTimers()
	{
		fastTimer.stop();
		slowTimer.stop();
		menuItemLoad.setToolTipText(null);
		menuItemAirports.setToolTipText(null);
		menuItemAircrafts.setToolTipText(null);
		menuItemFlights.setToolTipText(null);

		updateSimulatedTimeLabel(simulatedTime);
		updateNumOfAircraftsLabel(numOfActiveFlights);
		updateNumOfCrashesLabel(numOfCrashes);
		updateNumOfLandingsLabel(numOfLandings);

		updateMapPanel();
	}

	private void setupListeners()
	{
		menuItemStart.addActionListener(l ->
		{
			if(simulationCanStart && !fastTimer.isRunning())
				startTimers();
		});

		menuItemStop.addActionListener(l ->
		{
			if(simulationCanStart && fastTimer.isRunning())
				stopTimers();
		});

		menuItemLoad.addActionListener(l ->
		{
			if(!fastTimer.isRunning())
			{
				String mapID;

				mapID = JOptionPane.showInputDialog(this, "Give the MapID");
				if(mapID != null)
				{
					JFileChooser folderChooser = new JFileChooser();
					folderChooser.setDialogTitle("Select the folder in which the data files with the given mapID are located");
					folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					folderChooser.setAcceptAllFileFilterUsed(false);

					if(folderChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
					{
						setSimulationCanStart(false);
						loadDataFilesWithID(folderChooser.getSelectedFile().toString(), mapID);

						if(simulationCanStart) appendMessageToMessageBoard("New simulation can be started", GREEN_COLOR);
						else loadDefaultDataFiles();

						updateMapPanel();
					}
					else appendMessageToMessageBoard("\"Load new data files\" operation was canceled", BLUE_COLOR);
				}
				else appendMessageToMessageBoard("\"Load new data files\" operation was canceled", BLUE_COLOR);
			}
		});

		menuItemExit.addActionListener(l -> System.exit(0));

		menuItemAircrafts.addActionListener(l ->
		{
			if(!fastTimer.isRunning() && simulationCanStart)
			{
				setEnabled(false);
				popUp.showPopUp(POPUP_AIRCRAFTS);
			}
		});

		menuItemAirports.addActionListener(l ->
		{
			if(!fastTimer.isRunning() && simulationCanStart)
			{
				setEnabled(false);
				popUp.showPopUp(POPUP_AIRPORTS);
			}
		});

		menuItemFlights.addActionListener(l ->
		{
			if(!fastTimer.isRunning() && simulationCanStart)
			{
				setEnabled(false);
				popUp.showPopUp(POPUP_FLIGHTS);
			}
		});
	}

	private void checkForNewFlights()
	{
		for(Flight flight : flights)
		{
			if(flight.getState() == FLIGHT_NOT_TAKEN_OFF_YET && Math.abs(flight.getStartTime() - simulatedTime) <= 1.3)
			{
				Position[] flightPositions = new Position[NUMBER_OF_FLIGHT_POSITIONS];
				flightPositions[TAKE_OFF_POSITION] = flight.getTakeOffAirport().getPosition();
				flightPositions[CHANGE_SPEED_TO_NORMAL_POSITION] = getNeighbourPositionOfAirport(flight.getTakeOffAirport());
				flightPositions[CHANGE_SPEED_TO_LANDING_POSITION] = getNeighbourPositionOfAirport(flight.getLandingAirport());
				flightPositions[LANDED_POSITION] = flight.getLandingAirport().getPosition();

				double[] flightMoments = new double[NUMBER_OF_FLIGHT_MOMENTS];
				flightMoments[TAKE_OFF_MOMENT] = flight.getStartTime();

				//calculate positions and moments related to taking off
				double TspeedXYZ = flight.getAircraft().getTakeOffLandingSpeed() / 1.2;
				double TspeedXY = flight.getAircraft().getHorizontalTakeOffLandingSpeed() / 1.2;
				double TspeedZ = flight.getAircraft().getVerticalTakeOffLandingSpeed() / 1.2;

				double distanceTakeOffToNormalSpeed = getDistance(flightPositions[TAKE_OFF_POSITION], flightPositions[CHANGE_SPEED_TO_NORMAL_POSITION]);
				double verticalDistanceTakeOffToFlightHeight = flight.getFlightHeight() - flight.getTakeOffAirport().getHeight();

				double hypotheticalDurationTakeOffToNormalSpeed = distanceTakeOffToNormalSpeed / TspeedXY;
				double hypotheticalVerticalDistanceTakeOffToNormalSpeed = TspeedZ * hypotheticalDurationTakeOffToNormalSpeed;

				boolean stopClimbingIsAfterNormalSpeed;
				if(verticalDistanceTakeOffToFlightHeight <= hypotheticalVerticalDistanceTakeOffToNormalSpeed)
				{
					stopClimbingIsAfterNormalSpeed = false;
					double durationTakeOffToFlightHeight = verticalDistanceTakeOffToFlightHeight / TspeedZ;
					flightMoments[STOP_CLIMBING_MOMENT] = flightMoments[TAKE_OFF_MOMENT] + durationTakeOffToFlightHeight;

					double distanceToTravelUntilReachingFlightHeight = TspeedXY * durationTakeOffToFlightHeight;
					double angle = getAngle(flightPositions[TAKE_OFF_POSITION], flightPositions[CHANGE_SPEED_TO_NORMAL_POSITION]);
					flightPositions[STOP_CLIMBING_POSITION] = getPositionFromDistance(flightPositions[TAKE_OFF_POSITION], distanceToTravelUntilReachingFlightHeight, angle);

					TspeedZ = 0;
					TspeedXY = TspeedXYZ;
					double distanceStopClimbingToNormalSpeed = getDistance(flightPositions[STOP_CLIMBING_POSITION], flightPositions[CHANGE_SPEED_TO_NORMAL_POSITION]);
					double durationUntilNormalSpeed = distanceStopClimbingToNormalSpeed / TspeedXY;
					flightMoments[CHANGE_SPEED_TO_NORMAL_MOMENT] = flightMoments[STOP_CLIMBING_MOMENT] + durationUntilNormalSpeed;
				}
				else
				{
					stopClimbingIsAfterNormalSpeed = true;

					//in this case hypothetical values are correct
					flightMoments[CHANGE_SPEED_TO_NORMAL_MOMENT] = flightMoments[TAKE_OFF_MOMENT] + hypotheticalDurationTakeOffToNormalSpeed;
					TspeedXYZ = flight.getFlightSpeed() / 1.2;
					TspeedXY = getSpeedXYFromSpeedXYZAndSpeedZ(TspeedXYZ, TspeedZ);

					double verticalDistanceRemaining = verticalDistanceTakeOffToFlightHeight - hypotheticalVerticalDistanceTakeOffToNormalSpeed;
					double timeNeededToClimb = verticalDistanceRemaining / TspeedZ;
					flightMoments[STOP_CLIMBING_MOMENT] = flightMoments[CHANGE_SPEED_TO_NORMAL_MOMENT] + timeNeededToClimb;

					double distanceToTravelUntilReachingFlightHeight = TspeedXY * timeNeededToClimb;
					double angle = getAngle(flightPositions[CHANGE_SPEED_TO_NORMAL_POSITION], flightPositions[CHANGE_SPEED_TO_LANDING_POSITION]);
					flightPositions[STOP_CLIMBING_POSITION] = getPositionFromDistance(flightPositions[CHANGE_SPEED_TO_NORMAL_POSITION], distanceToTravelUntilReachingFlightHeight, angle);
				}
				TspeedZ = 0;
				TspeedXYZ = flight.getFlightSpeed() / 1.2;
				TspeedXY = getSpeedXYFromSpeedXYZAndSpeedZ(TspeedXYZ, TspeedZ);

				//calculate positions and moments related to landing (we think of it as starting from landing position and going back)
				double LspeedXYZ = flight.getAircraft().getTakeOffLandingSpeed() / 1.2;
				double LspeedXY = flight.getAircraft().getHorizontalTakeOffLandingSpeed() / 1.2;
				double LspeedZ = flight.getAircraft().getVerticalTakeOffLandingSpeed() / 1.2;

				double distanceLandedToLandingSpeed = getDistance(flightPositions[LANDED_POSITION], flightPositions[CHANGE_SPEED_TO_LANDING_POSITION]);
				double verticalDistanceLandedToFlightHeight = flight.getFlightHeight() - flight.getLandingAirport().getHeight();

				double hypotheticalDurationLandedToLandingSpeed = distanceLandedToLandingSpeed / LspeedXY;
				double hypotheticalVerticalDistanceLandedToLandingSpeed = LspeedZ * hypotheticalDurationLandedToLandingSpeed;

				double durationLandedToFlightHeight, durationUntilLandingSpeed;
				if(verticalDistanceLandedToFlightHeight <= hypotheticalVerticalDistanceLandedToLandingSpeed)
				{
					durationLandedToFlightHeight = verticalDistanceLandedToFlightHeight / LspeedZ;

					double distanceToTravelUntilReachingFlightHeight = LspeedXY * durationLandedToFlightHeight;
					double angle = getAngle(flightPositions[LANDED_POSITION], flightPositions[CHANGE_SPEED_TO_LANDING_POSITION]);
					flightPositions[START_DESCENDING_POSITION] = getPositionFromDistance(flightPositions[LANDED_POSITION], distanceToTravelUntilReachingFlightHeight, angle);

					LspeedZ = 0;
					LspeedXY = LspeedXYZ;
					double distanceStartDescendingToLandingSpeed = getDistance(flightPositions[START_DESCENDING_POSITION], flightPositions[CHANGE_SPEED_TO_LANDING_POSITION]);
					durationUntilLandingSpeed = distanceStartDescendingToLandingSpeed / LspeedXY;

					if(stopClimbingIsAfterNormalSpeed)
					{
						double distanceFromStopClimbingToLandingSpeed = getDistance(flightPositions[STOP_CLIMBING_POSITION], flightPositions[CHANGE_SPEED_TO_LANDING_POSITION]);
						double durationFromStopClimbingToLandingSpeed = distanceFromStopClimbingToLandingSpeed / TspeedXY;
						flightMoments[CHANGE_SPEED_TO_LANDING_MOMENT] = flightMoments[STOP_CLIMBING_MOMENT] + durationFromStopClimbingToLandingSpeed;
						flightMoments[START_DESCENDING_MOMENT] = flightMoments[CHANGE_SPEED_TO_LANDING_MOMENT] + durationUntilLandingSpeed;
						flightMoments[LANDED_MOMENT] = flightMoments[START_DESCENDING_MOMENT] + durationLandedToFlightHeight;
					}
					else
					{
						double distanceFromNormalToLandingSpeed = getDistance(flightPositions[CHANGE_SPEED_TO_NORMAL_POSITION], flightPositions[CHANGE_SPEED_TO_LANDING_POSITION]);
						double durationFromNormalToLandingSpeed = distanceFromNormalToLandingSpeed / TspeedXY;
						flightMoments[CHANGE_SPEED_TO_LANDING_MOMENT] = flightMoments[CHANGE_SPEED_TO_NORMAL_MOMENT] + durationFromNormalToLandingSpeed;
						flightMoments[START_DESCENDING_MOMENT] = flightMoments[CHANGE_SPEED_TO_LANDING_MOMENT] + durationUntilLandingSpeed;
						flightMoments[LANDED_MOMENT] = flightMoments[START_DESCENDING_MOMENT] + durationLandedToFlightHeight;
					}
				}
				else
				{
					//in this case hypothetical values are correct
					LspeedXYZ = flight.getFlightSpeed() / 1.2;
					LspeedXY = getSpeedXYFromSpeedXYZAndSpeedZ(LspeedXYZ, LspeedZ);

					double verticalDistanceRemaining = verticalDistanceLandedToFlightHeight - hypotheticalVerticalDistanceLandedToLandingSpeed;
					double timeNeededToClimb = verticalDistanceRemaining / LspeedZ;

					double distanceToTravelUntilReachingFlightHeight = LspeedXY * timeNeededToClimb;
					double angle = getAngle(flightPositions[CHANGE_SPEED_TO_LANDING_POSITION], flightPositions[CHANGE_SPEED_TO_NORMAL_POSITION]);
					flightPositions[START_DESCENDING_POSITION] = getPositionFromDistance(flightPositions[CHANGE_SPEED_TO_LANDING_POSITION], distanceToTravelUntilReachingFlightHeight, angle);

					if(stopClimbingIsAfterNormalSpeed)
					{
						double distanceFromStopClimbingToStartDescending = getDistance(flightPositions[STOP_CLIMBING_POSITION], flightPositions[START_DESCENDING_POSITION]);
						double durationFromStopClimbingToStartDescending = distanceFromStopClimbingToStartDescending / TspeedXY;
						flightMoments[START_DESCENDING_MOMENT] = flightMoments[STOP_CLIMBING_MOMENT] + durationFromStopClimbingToStartDescending;
						flightMoments[CHANGE_SPEED_TO_LANDING_MOMENT] = flightMoments[START_DESCENDING_MOMENT] + timeNeededToClimb;
						flightMoments[LANDED_MOMENT] = flightMoments[CHANGE_SPEED_TO_LANDING_MOMENT] + hypotheticalDurationLandedToLandingSpeed;
					}
					else
					{
						double distanceFromNormalToStartDescending = getDistance(flightPositions[CHANGE_SPEED_TO_NORMAL_POSITION], flightPositions[START_DESCENDING_POSITION]);
						double durationFromNormalToStartDescending = distanceFromNormalToStartDescending / TspeedXY;
						flightMoments[START_DESCENDING_MOMENT] = flightMoments[CHANGE_SPEED_TO_NORMAL_MOMENT] + durationFromNormalToStartDescending;
						flightMoments[CHANGE_SPEED_TO_LANDING_MOMENT] = flightMoments[START_DESCENDING_MOMENT] + timeNeededToClimb;
						flightMoments[LANDED_MOMENT] = flightMoments[CHANGE_SPEED_TO_LANDING_MOMENT] + hypotheticalDurationLandedToLandingSpeed;
					}
				}

				flight.setPositions(flightPositions);
				flight.setMoments(flightMoments);

				flight.setCurrentPosition(new Position(flight.getTakeOffAirport().getPosition().getX(), flight.getTakeOffAirport().getPosition().getY()));
				flight.setCurrentHeight(flight.getTakeOffAirport().getHeight());
				flight.setCurrentFuel(flight.getFlightFuel());

				flight.setState(FLIGHT_ACTIVE);
				flight.setFlightPhase(FLIGHT_PHASE_ASCENDING);
				numOfActiveFlights++;
			}
		}
	}

	private void updatePositionForActiveFlights()
	{
		for(Flight flight : flights)
		{
			if(flight.getState() == FLIGHT_ACTIVE)
			{
				flight.setCurrentHeight(flight.getCurrentHeight() + flight.getCurrentSpeedZ());
				Position prevPosition = flight.getCurrentPosition();
				double newX = prevPosition.getX() + flight.getCurrentSpeedX();
				double newY = prevPosition.getY() + flight.getCurrentSpeedY();
				flight.getCurrentPosition().setPosition(newX, newY);
				flight.setCurrentFuel(flight.getCurrentFuel() - flight.getCurrentFuelConsumption());

				if(Math.abs(flight.getMoments()[TAKE_OFF_MOMENT] - simulatedTime) < 1.3)
				{
					flight.setCurrentSpeedXYZ(flight.getAircraft().getTakeOffLandingSpeed());
					flight.setCurrentSpeedXY(flight.getAircraft().getHorizontalTakeOffLandingSpeed());
					flight.setCurrentSpeedZ(flight.getAircraft().getVerticalTakeOffLandingSpeed());
					flight.setCurrentAngle(getAngle(flight.getPositions()[TAKE_OFF_POSITION], flight.getPositions()[CHANGE_SPEED_TO_NORMAL_POSITION]));
					flight.setCurrentSpeedX(getSpeedX(flight.getCurrentSpeedXY(), flight.getCurrentAngle()));
					flight.setCurrentSpeedY(getSpeedY(flight.getCurrentSpeedXY(), flight.getCurrentAngle()));
					flight.setCurrentFuelConsumption(flight.getAircraft().getFuelConsumption() * flight.getCurrentSpeedXYZ());
				}
				if(Math.abs(flight.getMoments()[CHANGE_SPEED_TO_NORMAL_MOMENT] - simulatedTime) < 1.3)
				{
					flight.setCurrentPosition(flight.getPositions()[CHANGE_SPEED_TO_NORMAL_POSITION]);

					flight.setCurrentSpeedXYZ(flight.getFlightSpeed());
					flight.setCurrentAngle(getAngle(flight.getPositions()[CHANGE_SPEED_TO_NORMAL_POSITION], flight.getPositions()[CHANGE_SPEED_TO_LANDING_POSITION]));
					flight.setCurrentSpeedXY(getSpeedXYFromSpeedXYZAndSpeedZ(flight.getCurrentSpeedXYZ(), flight.getCurrentSpeedZ()));
					flight.setCurrentSpeedX(getSpeedX(flight.getCurrentSpeedXY(), flight.getCurrentAngle()));
					flight.setCurrentSpeedY(getSpeedY(flight.getCurrentSpeedXY(), flight.getCurrentAngle()));
					flight.setCurrentFuelConsumption(flight.getAircraft().getFuelConsumption() * flight.getCurrentSpeedXYZ());
				}
				if(Math.abs(flight.getMoments()[STOP_CLIMBING_MOMENT] - simulatedTime) < 1.3)
				{
					flight.setCurrentPosition(flight.getPositions()[STOP_CLIMBING_MOMENT]);
					flight.setCurrentHeight(flight.getFlightHeight());

					flight.setCurrentSpeedZ(0);
					flight.setCurrentSpeedXY(getSpeedXYFromSpeedXYZAndSpeedZ(flight.getCurrentSpeedXYZ(), flight.getCurrentSpeedZ()));
					flight.setCurrentSpeedX(getSpeedX(flight.getCurrentSpeedXY(), flight.getCurrentAngle()));
					flight.setCurrentSpeedY(getSpeedY(flight.getCurrentSpeedXY(), flight.getCurrentAngle()));
					flight.setFlightPhase(FLIGHT_PHASE_NORMAL);
				}
				if(Math.abs(flight.getMoments()[START_DESCENDING_MOMENT] - simulatedTime) < 1.3)
				{
					flight.setCurrentPosition(flight.getPositions()[START_DESCENDING_POSITION]);

					flight.setCurrentSpeedZ(-1.0 * flight.getAircraft().getVerticalTakeOffLandingSpeed());
					flight.setCurrentSpeedXY(getSpeedXYFromSpeedXYZAndSpeedZ(flight.getCurrentSpeedXYZ(), flight.getCurrentSpeedZ()));
					flight.setCurrentSpeedX(getSpeedX(flight.getCurrentSpeedXY(), flight.getCurrentAngle()));
					flight.setCurrentSpeedY(getSpeedY(flight.getCurrentSpeedXY(), flight.getCurrentAngle()));
					flight.setFlightPhase(FLIGHT_PHASE_DESCENDING);
				}
				if(Math.abs(flight.getMoments()[CHANGE_SPEED_TO_LANDING_MOMENT] - simulatedTime) < 1.3)
				{
					flight.setCurrentPosition(flight.getPositions()[CHANGE_SPEED_TO_LANDING_POSITION]);

					flight.setCurrentSpeedXYZ(flight.getAircraft().getTakeOffLandingSpeed());
					flight.setCurrentSpeedXY(flight.getAircraft().getHorizontalTakeOffLandingSpeed());
					flight.setCurrentAngle(getAngle(flight.getPositions()[CHANGE_SPEED_TO_LANDING_POSITION], flight.getPositions()[LANDED_POSITION]));
					flight.setCurrentSpeedX(getSpeedX(flight.getCurrentSpeedXY(), flight.getCurrentAngle()));
					flight.setCurrentSpeedY(getSpeedY(flight.getCurrentSpeedXY(), flight.getCurrentAngle()));
					flight.setCurrentFuelConsumption(flight.getAircraft().getFuelConsumption() * flight.getCurrentSpeedXYZ());
				}
				if(Math.abs(flight.getMoments()[LANDED_MOMENT] - simulatedTime) < 1.3)
				{
					flight.setCurrentPosition(flight.getPositions()[LANDED_POSITION]);
					flight.setCurrentHeight(flight.getLandingAirport().getHeight());

					flight.setCurrentSpeedXYZ(0);
					flight.setCurrentSpeedXY(0);
					flight.setCurrentSpeedX(0);
					flight.setCurrentSpeedY(0);
					flight.setCurrentSpeedZ(0);
					flight.setCurrentFuelConsumption(0);

					flight.setState(FLIGHT_LANDED);
					numOfActiveFlights--;
					numOfLandings++;

					appendMessageToMessageBoard(String.format("Flight \"%s\" with id: %d landed successfully", flight.getName(), flight.getUniqueID()), GREEN_COLOR);
				}
			}
		}
	}

	private void checkForCrashes()
	{
		for(Flight flight : flights)
		{
			if(flight.getState() == FLIGHT_ACTIVE)
			{
				if(!flight.getCurrentPosition().equals(flight.getTakeOffAirport().getPosition()) &&
						!flight.getCurrentPosition().equals(flight.getLandingAirport().getPosition()) &&
						flight.getCurrentHeight() < getHeightOfPosition(flight.getCurrentPosition()))
				{
					flight.setState(FLIGHT_CRASHED);
					appendMessageToMessageBoard(String.format("Flight \"%s\" with id: %d crashed due to\n" + TAB + "low height", flight.getName(), flight.getUniqueID()), RED_COLOR);
					numOfActiveFlights--;
					numOfCrashes++;
				}
				else if(flight.getCurrentFuel() <= 0)
				{
					flight.setState(FLIGHT_CRASHED);
					appendMessageToMessageBoard(String.format("Flight \"%s\" with id: %d crashed due to not\n" + TAB + "enough fuel", flight.getName(), flight.getUniqueID()), RED_COLOR);
					numOfActiveFlights--;
					numOfCrashes++;
				}
				else
				{
					Set<Flight> crashedFlights = new LinkedHashSet<>();
					for(Flight flight2 : flights)
					{
						if(flight2.getState() == FLIGHT_ACTIVE && flight.getUniqueID() != flight2.getUniqueID())
						{
							if(getDistance(flight.getCurrentPosition(), flight2.getCurrentPosition()) < CRASH_DISTANCE &&
									Math.abs(flight.getCurrentHeight() - flight2.getCurrentHeight()) < CRASH_HEIGHT)
							{
								crashedFlights.add(flight);
								crashedFlights.add(flight2);
							}
						}
					}
					for(Flight crashedFlight: crashedFlights)
					{
						crashedFlight.setState(FLIGHT_CRASHED);
						appendMessageToMessageBoard(String.format("Flight %s with id: %d just crashed with\n" + TAB + "another flight", crashedFlight.getName(), crashedFlight.getUniqueID()), RED_COLOR);
						numOfActiveFlights--;
						numOfCrashes++;
					}
				}
			}
		}
	}

	private void updateSimulatedTimeLabel(double simulatedTime)
	{
		int time = (int) Math.floor(simulatedTime);
		int seconds = time % 60;
		int minutes = time / 60;
		int hours = minutes / 60;
		minutes = minutes % 60;

		StringBuilder sb = new StringBuilder();
		sb.append("Simulated Time: ");
		if (hours != 0) sb.append(String.format("%02d:", hours));
		sb.append(String.format("%02d:", minutes));
		sb.append(String.format("%02d", seconds));

		simulatedTimeLabel.setText(sb.toString());
	}

	private void updateNumOfAircraftsLabel(int numOfAircrafts)
	{
		numOfAircraftsLabel.setText("Active Flights: " + numOfAircrafts);
	}

	private void updateNumOfCrashesLabel(int numOfCrashes)
	{
		numOfCollisionsLabel.setText("Crashes: " + numOfCrashes);
	}

	private void updateNumOfLandingsLabel(int numOfLandings)
	{
		numOfLandingsLabel.setText("Landings: " + numOfLandings);
	}

	private void updateMapPanel()
	{
		mapPanel.revalidate();
		mapPanel.repaint();
	}

	private class MapPanel extends JPanel
	{
		private int flightX, flightY;
		private double rotation;
		private double iconCenter;
		private double scale;
		private int iconSize;
		private BufferedImage aircraftImage;
		private BufferedImage scaledImage;
		private Graphics2D scaledImageGraphics;

		private JButton fasterButton;
		private int fastState; // 1==normal, 2==fast, 3==very fast

		MapPanel()
		{
			setLayout(null);

			fasterButton = new JButton();
			fasterButton.setFocusable(false);
			fasterButton.setBorder(BorderFactory.createEmptyBorder());
			fasterButton.setContentAreaFilled(false);
			fasterButton.setCursor(GlobalVariables.HAND_CURSOR);
			fasterButton.setBounds(900, 10, 50, 50);
			add(fasterButton);

			fastState = 1;
			fasterButton.setIcon(FAST_1);

			fasterButton.addActionListener(e ->
			{
				if(fastState == 1)
				{
					fastState = 2;
					fasterButton.setIcon(FAST_2);

					fastTimer.setInitialDelay(10);
					fastTimer.setDelay(10);

					slowTimer.setInitialDelay(100);
					slowTimer.setDelay(100);
				}
				else if(fastState == 2)
				{
					fastState = 3;
					fasterButton.setIcon(FAST_3);

					fastTimer.setInitialDelay(1);
					fastTimer.setDelay(1);

					slowTimer.setInitialDelay(100);
					slowTimer.setDelay(100);
				}
				else
				{
					fastState = 1;
					fasterButton.setIcon(FAST_1);

					fastTimer.setInitialDelay(100);
					fastTimer.setDelay(100);

					slowTimer.setInitialDelay(1000);
					slowTimer.setDelay(1000);
				}
			});
		}

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			if(map != null && airports != null && flights != null)
			{
				for(int y = 0; y < 30; y++)
				{
					for(int x = 0; x < 60; x++)
					{
						g.setColor(getColor(map[x][y]));
						g.fillRect(x * 16, y * 16, 16, 16);
					}
				}

				for(Airport airport : airports)
				{
					g.drawImage(AIRPORT_ICON_FOR_MAP, (int) (airport.getPosition().getX() * METERS_TO_PIXELS - 4), (int) (airport.getPosition().getY() * METERS_TO_PIXELS - 4), null);
				}

				for(Flight flight: flights)
				{
					if(flight.getState() == FLIGHT_ACTIVE)
					{
						rotation = Math.toRadians(flight.getCurrentAngle());

						if(flight.getAircraft() == SINGLE_ENGINE_AIRCRAFT) aircraftImage = getSingleEngineAircraftImage();
						else if(flight.getAircraft() == TURBO_PROP_AIRCRAFT) aircraftImage = getTurboPropAircraftImage();
						else aircraftImage = getJetAircraftImage();

						if(flight.getFlightPhase() == FLIGHT_PHASE_NORMAL)
							scale = 1.0;
						else if(flight.getFlightPhase() == FLIGHT_PHASE_ASCENDING)
							scale = 0.3 + 0.7 * (flight.getCurrentHeight() - flight.getTakeOffAirport().getHeight()) / (flight.getFlightHeight() - flight.getTakeOffAirport().getHeight());
						else
							scale = 1.0 - 0.7 * (flight.getFlightHeight() - flight.getCurrentHeight()) / (flight.getFlightHeight() - flight.getLandingAirport().getHeight());

						iconSize = (int) (scale * AIRCRAFT_ICON_SIZE);
						iconCenter = iconSize / 2.0;
						if(scale != 1.0)
						{
							scaledImage = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
							scaledImageGraphics = scaledImage.createGraphics();
							scaledImageGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
							scaledImageGraphics.drawImage(aircraftImage, 0, 0, iconSize, iconSize, null);
							scaledImageGraphics.dispose();
						}
						else scaledImage = aircraftImage;

						AffineTransform tx = AffineTransform.getRotateInstance(rotation, iconCenter, iconCenter);
						AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

						flightX = (int) (Math.round(flight.getCurrentPosition().getX() * METERS_TO_PIXELS) - iconCenter);
						flightY = (int) (Math.round(flight.getCurrentPosition().getY() * METERS_TO_PIXELS) - iconCenter);
						g.drawImage(op.filter(scaledImage, null), flightX, flightY, null);
					}
				}
			}
		}

		private Color getColor(int height)
		{
			if(height == 0) return mapColors[MAP_COLOR_0];
			else if(height <= 200) return mapColors[MAP_COLOR_0_200];
			else if(height <= 400) return mapColors[MAP_COLOR_200_400];
			else if(height <= 700) return mapColors[MAP_COLOR_400_700];
			else if(height <= 1500) return mapColors[MAP_COLOR_700_1500];
			else if(height <= 3500) return mapColors[MAP_COLOR_1500_3500];
			else return mapColors[MAP_COLOR_OVER_3500];
		}
	}

	private static double getDistance(Position p1, Position p2)
	{
		return Math.sqrt(Math.pow((p2.getX() - p1.getX()), 2) + Math.pow((p2.getY() - p1.getY()), 2));
	}

	private static Position getPositionFromDistance(Position position, double distance, double angle)
	{
		double oldX = position.getX();
		double oldY = position.getY();

		if(angle == 0.0) return new Position(oldX, oldY - distance);
		else if(angle == 90.0) return new Position(oldX + distance, oldY);
		else if(angle == 180.0) return new Position(oldX, oldY + distance);
		else if(angle == -90.0) return new Position(oldX - distance, oldY);
		else
		{
			double newX = oldX + distance * Math.sin(Math.toRadians(angle));
			double newY= oldY + distance * Math.cos(Math.toRadians(angle));
			return new Position(newX, newY);
		}
	}

	private static double getAngle(Position startP, Position endP)
	{
		Position middlePosition = new Position(endP.getX(), startP.getY());

		if(startP.getX() != endP.getX() && startP.getY() != endP.getY())
		{
			double angle = Math.toDegrees(Math.atan(getDistance(startP, middlePosition) / getDistance(middlePosition, endP)));
			if(endP.getY() > startP.getY()) angle = 180 - angle;
			if(endP.getX() < startP.getX()) angle = -1.0 * angle;
			return angle;
		}
		else
		{
			if(startP.getX() == endP.getX())
			{
				if(startP.getY() < endP.getY()) return 180.0;
				else return 0.0;
			}
			else if(startP.getY() == endP.getY())
			{
				if(startP.getX() < endP.getX()) return 90.0;
				else return -90.0;
			}
			else return 0.0;
		}
	}

	private static double getSpeedXYFromSpeedXYZAndSpeedZ(double speedXYZ, double speedZ)
	{
		return speedXYZ * Math.sin(Math.acos(speedZ / speedXYZ));
	}

	private static double getSpeedX(double speedXY, double angleInDegrees)
	{
		return speedXY * Math.sin(Math.toRadians(angleInDegrees));
	}

	private static double getSpeedY(double speedXY, double angleInDegrees)
	{
		return speedXY * Math.cos(Math.toRadians(angleInDegrees)) * -1.0; //speedToNorth is (-), speedToSouth is (+)
	}

	private static Position getNeighbourPositionOfAirport(Airport a)
	{
		if(a != null)
		{
			if(a.getOrientation() == AIRPORT_ORIENTATION_NORTH)
			{
				if(a.getPosition().getY() == 8 * PIXELS_TO_METERS) return new Position(a.getPosition().getX(), 0);
				else return new Position(a.getPosition().getX(), a.getPosition().getY() - 24 * PIXELS_TO_METERS);
			}
			else if(a.getOrientation() == AIRPORT_ORIENTATION_EAST)
			{
				if(MAP_WIDTH_IN_METERS - a.getPosition().getX() == 8 * PIXELS_TO_METERS) return new Position(a.getPosition().getX() + 8 * PIXELS_TO_METERS, a.getPosition().getY());
				else return new Position(a.getPosition().getX() + 24 * PIXELS_TO_METERS, a.getPosition().getY());
			}
			else if(a.getOrientation() == AIRPORT_ORIENTATION_SOUTH)
			{
				if(MAP_HEIGHT_IN_METERS - a.getPosition().getY() == 8 * PIXELS_TO_METERS) return new Position(a.getPosition().getX(), a.getPosition().getY() + 8 * PIXELS_TO_METERS);
				else return new Position(a.getPosition().getX(), a.getPosition().getY() + 24 * PIXELS_TO_METERS);
			}
			else if(a.getOrientation() == AIRPORT_ORIENTATION_WEST)
			{
				if(a.getPosition().getX() == 8 * PIXELS_TO_METERS) return new Position(0, a.getPosition().getY());
				else return new Position(a.getPosition().getX() - 24 * PIXELS_TO_METERS, a.getPosition().getY());
			}
		}
		return null;
	}

	public void appendMessageToMessageBoard(String msg, Color c)
	{
		shouldScroll = true;

		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		messageBoard.setEditable(true);
		messageBoard.setCaretPosition(messageBoard.getDocument().getLength());
		messageBoard.setCharacterAttributes(aset, false);
		messageBoard.replaceSelection(msg + "\n");
		messageBoard.setEditable(false);
		messageBoard.revalidate();

		shouldScroll = false;
	}

	public void createMapPanel()
	{
		mapPanel = new MapPanel();
		add(mapPanel, BorderLayout.CENTER);

		setVisible(true);

		updateMapPanel();
	}

	public int getNumOfActiveFlights()
	{
		return numOfActiveFlights;
	}

	public void setSimulationCanStart(boolean simulationCanStart)
	{
		this.simulationCanStart = simulationCanStart;

		if(simulationCanStart)
		{
			menuItemStart.setToolTipText(null);
			menuItemAirports.setToolTipText(null);
			menuItemAircrafts.setToolTipText(null);
			menuItemFlights.setToolTipText(null);
		}
		else
		{
			menuItemStart.setToolTipText("Simulation cannot be started");
			menuItemAirports.setToolTipText("No airport data is available");
			menuItemAircrafts.setToolTipText("No flight data is available");
			menuItemFlights.setToolTipText("No flight data is available");
		}
	}

	public boolean simulationCanStart()
	{
		return simulationCanStart;
	}
}
