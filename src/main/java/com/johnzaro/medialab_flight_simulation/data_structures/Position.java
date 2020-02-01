package com.johnzaro.medialab_flight_simulation.data_structures;

public class Position
{
	private double x; // m
	private double y; // m

	public Position(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public void setPosition(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public boolean equals(Position position)
	{
		// fastest speed possible is 280knots ~= 173m/1.2sec so we use the distance of 180m to decide if two positions are equal
		if(position != null) return Math.abs(this.getX() - position.getX()) < 180 && Math.abs(this.getY() - position.getY()) < 180;
		else return false;
	}

	public String toString()
	{
		return String.format("x=%f, y=%f", x, y);
	}
}
