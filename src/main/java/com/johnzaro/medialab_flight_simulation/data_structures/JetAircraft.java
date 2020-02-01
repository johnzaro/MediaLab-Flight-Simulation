package com.johnzaro.medialab_flight_simulation.data_structures;

public class JetAircraft extends Aircraft
{
	private static final int TAKE_OFF_LANDING_SPEED = 140; // knots
	private static final int MAX_SPEED = 280; // knots
	private static final int MAX_FUEL = 16000; // kg
	private static final int FUEL_CONSUMPTION = 15; // kg/nm
	private static final int MAX_HEIGHT = 28000; // feet
	private static final int CLIMB_DESCENT_RATE = 2300; // ft/min

	public JetAircraft()
	{
		super(TAKE_OFF_LANDING_SPEED, MAX_SPEED, MAX_FUEL, FUEL_CONSUMPTION, MAX_HEIGHT, CLIMB_DESCENT_RATE);
	}
}
