package com.johnzaro.medialab_flight_simulation.data_structures;

import com.johnzaro.medialab_flight_simulation.core.GlobalVariables;

public class Flight
{
	private int uniqueID;
	private int startTime;	//we convert input which is in minutes to seconds
	private Airport takeOffAirport;
	private Airport landingAirport;
	private String name;

	private Aircraft aircraft;

	private double flightSpeed; // m/1.2sec
	private double flightHeight; // m
	private double flightFuel; // kg

	private Position currentPosition;
	private Position[] positions;

	private double moments[];

	private double currentSpeedXYZ; // m/1.2sec
	private double currentSpeedXY; // m/1.2sec
	private double currentSpeedX; // m/1.2sec
	private double currentSpeedY; // m/1.2sec
	private double currentSpeedZ; // m/1.2sec

	private double currentAngle; // 0 = north, clockwise

	private double currentHeight; // m
	private double currentFuel; // kg
	private double currentFuelConsumption; // kg/1.2sec

	private int flightPhase;

	private int state; //0 == not started yet, 1 == active, 2 == finished, 3 == crashed

	public Flight()
	{
		uniqueID = -1;

		startTime = -1;

		takeOffAirport = null;
		landingAirport = null;

		name = null;

		aircraft = null;

		flightSpeed = 0;
		flightHeight = 0;
		flightFuel = 0;

		currentPosition = null;
		positions = new Position[GlobalVariables.NUMBER_OF_FLIGHT_POSITIONS];

		currentSpeedXYZ = 0;
		currentSpeedXY = 0;
		currentSpeedX = 0;
		currentSpeedY = 0;
		currentSpeedZ = 0;

		currentAngle = 0;

		currentHeight = 0;
		currentFuel = 0;
		currentFuelConsumption = 0;

		state = GlobalVariables.FLIGHT_NOT_TAKEN_OFF_YET;
	}

	public void resetFlight()
	{
		state = GlobalVariables.FLIGHT_NOT_TAKEN_OFF_YET;
	}

	public int getUniqueID()
	{
		return uniqueID;
	}

	public void setUniqueID(int uniqueID)
	{
		this.uniqueID = uniqueID;
	}

	public int getStartTime()
	{
		return startTime;
	}

	public void setStartTime(int startTime)
	{
		this.startTime = startTime;
	}

	public Airport getTakeOffAirport()
	{
		return takeOffAirport;
	}

	public void setTakeOffAirport(Airport takeOffAirport)
	{
		this.takeOffAirport = takeOffAirport;
	}

	public Airport getLandingAirport()
	{
		return landingAirport;
	}

	public void setLandingAirport(Airport landingAirport)
	{
		this.landingAirport = landingAirport;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Aircraft getAircraft()
	{
		return aircraft;
	}

	public void setAircraft(Aircraft aircraft)
	{
		this.aircraft = aircraft;
	}

	public double getCurrentSpeedXYZ()
	{
		return currentSpeedXYZ;
	}

	public void setCurrentSpeedXYZ(double currentSpeedXYZ)
	{
		this.currentSpeedXYZ = currentSpeedXYZ;
	}

	public double getCurrentHeight()
	{
		return currentHeight;
	}

	public void setCurrentHeight(double currentHeight)
	{
		this.currentHeight = currentHeight;
	}

	public double getCurrentFuel()
	{
		return currentFuel;
	}

	public void setCurrentFuel(double currentFuel)
	{
		this.currentFuel = currentFuel;
	}

	public double getFlightSpeed()
	{
		return flightSpeed;
	}

	public void setFlightSpeed(double flightSpeed)
	{
		this.flightSpeed = flightSpeed;
	}

	public double getFlightHeight()
	{
		return flightHeight;
	}

	public void setFlightHeight(double flightHeight)
	{
		this.flightHeight = flightHeight;
	}

	public double getFlightFuel()
	{
		return flightFuel;
	}

	public void setFlightFuel(double flightFuel)
	{
		this.flightFuel = flightFuel;
	}

	public int getState()
	{
		return state;
	}

	public void setState(int state)
	{
		this.state = state;
	}

	public Position getCurrentPosition()
	{
		return currentPosition;
	}

	public void setCurrentPosition(Position currentPosition)
	{
		this.currentPosition = currentPosition;
	}

	public double getCurrentSpeedXY()
	{
		return currentSpeedXY;
	}

	public void setCurrentSpeedXY(double currentSpeedXY)
	{
		this.currentSpeedXY = currentSpeedXY;
	}

	public double getCurrentSpeedX()
	{
		return currentSpeedX;
	}

	public void setCurrentSpeedX(double currentSpeedX)
	{
		this.currentSpeedX = currentSpeedX;
	}

	public double getCurrentSpeedY()
	{
		return currentSpeedY;
	}

	public void setCurrentSpeedY(double currentSpeedY)
	{
		this.currentSpeedY = currentSpeedY;
	}

	public double getCurrentSpeedZ()
	{
		return currentSpeedZ;
	}

	public void setCurrentSpeedZ(double currentSpeedZ)
	{
		this.currentSpeedZ = currentSpeedZ;
	}

	public Position[] getPositions()
	{
		return positions;
	}

	public void setPositions(Position[] positions)
	{
		this.positions = positions;
	}

	public double getCurrentAngle()
	{
		return currentAngle;
	}

	public void setCurrentAngle(double currentAngle)
	{
		this.currentAngle = currentAngle;
	}

	public int getFlightPhase()
	{
		return flightPhase;
	}

	public void setFlightPhase(int flightPhase)
	{
		this.flightPhase = flightPhase;
	}

	public double getCurrentFuelConsumption()
	{
		return currentFuelConsumption;
	}

	public void setCurrentFuelConsumption(double currentFuelConsumption)
	{
		this.currentFuelConsumption = currentFuelConsumption;
	}

	public double[] getMoments()
	{
		return moments;
	}

	public void setMoments(double[] moments)
	{
		this.moments = moments;
	}
}
