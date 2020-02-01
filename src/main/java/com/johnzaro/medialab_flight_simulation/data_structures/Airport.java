package com.johnzaro.medialab_flight_simulation.data_structures;

public class Airport
{
	private int uniqueID;
	private Position position; //in pixels
	private String name;

	//1 == North, 2 == East, 3 == South, 4 == West
	private int orientation;

	//1 == singleEngine only, 2 == turboProp & jet, 3 == all
	private int type;

	private int height; //in feet

	private boolean isOpen;

	public Airport()
	{
		uniqueID = -1;
		position = null;
		name = null;
		orientation = -1;
		type = -1;
		isOpen = false;
	}

	public int getUniqueID()
	{
		return uniqueID;
	}

	public void setUniqueID(int uniqueID)
	{
		this.uniqueID = uniqueID;
	}

	public Position getPosition()
	{
		return position;
	}

	public void setPosition(Position position)
	{
		this.position = position;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getOrientation()
	{
		return orientation;
	}

	public void setOrientation(int orientation)
	{
		this.orientation = orientation;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public boolean isOpen()
	{
		return isOpen;
	}

	public void setOpen(boolean open)
	{
		isOpen = open;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}
}
