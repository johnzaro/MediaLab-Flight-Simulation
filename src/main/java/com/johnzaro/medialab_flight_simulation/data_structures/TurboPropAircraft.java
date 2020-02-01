package com.johnzaro.medialab_flight_simulation.data_structures;

public class TurboPropAircraft extends Aircraft
{
	private static final int TAKE_OFF_LANDING_SPEED = 100; // knots
	private static final int MAX_SPEED = 220; // knots
	private static final int MAX_FUEL = 4200; // kg
	private static final int FUEL_CONSUMPTION = 9; // kg/nm
	private static final int MAX_HEIGHT = 16000; // feet
	private static final int CLIMB_DESCENT_RATE = 1200; // ft/min

	public TurboPropAircraft()
	{
		super(TAKE_OFF_LANDING_SPEED, MAX_SPEED, MAX_FUEL, FUEL_CONSUMPTION, MAX_HEIGHT, CLIMB_DESCENT_RATE);
	}
}
