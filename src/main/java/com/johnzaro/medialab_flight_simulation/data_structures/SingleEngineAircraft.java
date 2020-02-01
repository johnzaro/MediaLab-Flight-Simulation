package com.johnzaro.medialab_flight_simulation.data_structures;

public class SingleEngineAircraft extends Aircraft
{
	private static final int TAKE_OFF_LANDING_SPEED = 60; // knots
	private static final int MAX_SPEED = 110; // knots
	private static final int MAX_FUEL = 280; // kg
	private static final int FUEL_CONSUMPTION = 3; // kg/nm
	private static final int MAX_HEIGHT = 8000; // feet
	private static final int CLIMB_DESCENT_RATE = 700; // ft/min

	public SingleEngineAircraft()
	{
		super(TAKE_OFF_LANDING_SPEED, MAX_SPEED, MAX_FUEL, FUEL_CONSUMPTION, MAX_HEIGHT, CLIMB_DESCENT_RATE);
	}
}
