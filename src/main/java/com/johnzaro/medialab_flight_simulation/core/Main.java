package com.johnzaro.medialab_flight_simulation.core;

import com.johnzaro.medialab_flight_simulation.screens.MainWindow;
import com.johnzaro.medialab_flight_simulation.screens.PopUp;

import javax.swing.*;

import static com.johnzaro.medialab_flight_simulation.core.GlobalVariables.*;
import static com.johnzaro.medialab_flight_simulation.core.PowerOn.*;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			//προσπαθησε να θεσεις το look & feel ως αυτο του λειτουργικου συστηματος που τρεχεις
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			//αν δε πετυχει θεσε το default look & feel
			UIManager.getCrossPlatformLookAndFeelClassName();
		}

		//φορτωσε τα εικονιδια της εφαρμογης
		loadProgramIcons();

		//δημιουργια των objects για τα δυο JFrame της εφαρμογης
		mainWindow = new MainWindow();
		popUp = new PopUp();

		//προσπαθησε να φορτωσεις τα default input files
		loadDefaultDataFiles();

		//αφου φορτωθουν τα input files ζωγραφισε το χαρτη
		mainWindow.createMapPanel();
	}
}
