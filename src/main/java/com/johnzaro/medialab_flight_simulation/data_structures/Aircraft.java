package com.johnzaro.medialab_flight_simulation.data_structures;

import com.johnzaro.medialab_flight_simulation.core.GlobalVariables;

public abstract class Aircraft
{
	private double maxSpeed; // m/1.2sec
	private double maxFuel; // kg
	private double maxHeight; // m
	private double fuelConsumption; // kg/m
	private double takeOffLandingSpeed; // m/1.2sec
	private double verticalTakeOffLandingSpeed; // m/1.2sec
	private double horizontalTakeOffLandingSpeed; // m/1.2sec

	Aircraft(int takeOffLandingSpeed, int maxSpeed, int maxFuel, int fuelConsumption, int maxHeight, int climbDescentRate)
	{
		/*first we convert climbDescentRate from ft/min -> m/sec in order to finally calculate horizontalTakeOffLandingSpeed in m/1.2sec
		  1) verticalTakeOffLandingSpeed (m/1.2sec) = climbDescentRate (ft/min) / 60 * FEET_TO_METERS * 1.2
		  2) takeOffLandingSpeed (m/1.2sec) = takeOffLandingSpeed * KNOTS_TO_METERS_PER_SECOND * 1.2;
		  3) horizontalTakeOffLandingSpeed (m/1.2sec) = takeOffLandingSpeed (m/1.2sec) * sin( Arccos( verticalTakeOffLandingSpeed (m/1.2sec) / takeOffLandingSpeed (m/1.2sec) ) )

		  finally we have to convert fuelConsumption from kg/nm to kg/m
		*/

		verticalTakeOffLandingSpeed = climbDescentRate / 60.0 * GlobalVariables.FEET_TO_METERS * 1.2;
		this.takeOffLandingSpeed = takeOffLandingSpeed * GlobalVariables.KNOTS_TO_METERS_PER_SECOND * 1.2;
		horizontalTakeOffLandingSpeed = this.takeOffLandingSpeed * Math.sin(Math.acos(verticalTakeOffLandingSpeed / this.takeOffLandingSpeed));

		this.fuelConsumption = fuelConsumption / GlobalVariables.NAUTICAL_MILES_TO_METERS;

		this.maxSpeed = maxSpeed * GlobalVariables.KNOTS_TO_METERS_PER_SECOND * 1.2;
		this.maxFuel = maxFuel;
		this.maxHeight = maxHeight * GlobalVariables.FEET_TO_METERS;
	}

	public double getMaxSpeed()
	{
		return maxSpeed;
	}

	public double getMaxFuel()
	{
		return maxFuel;
	}

	public double getMaxHeight()
	{
		return maxHeight;
	}

	public double getFuelConsumption()
	{
		return fuelConsumption;
	}

	public double getTakeOffLandingSpeed()
	{
		return takeOffLandingSpeed;
	}

	public double getHorizontalTakeOffLandingSpeed()
	{
		return horizontalTakeOffLandingSpeed;
	}

	public double getVerticalTakeOffLandingSpeed()
	{
		return verticalTakeOffLandingSpeed;
	}
}
