package com.ecml;

import android.app.Application;
import android.content.Intent;

/** @class ECML
 * <br>
 * This class stores all variables that need to be available from any activity of the application.
 *
 */
public class ECML extends Application {

		public static FileUri song;	/* This variable stores the latest chosen song so that
									 * the user can go back to score whenever the user wants */
		public static Intent intent;	/* This variable is used to go to back the score from the games */

}
