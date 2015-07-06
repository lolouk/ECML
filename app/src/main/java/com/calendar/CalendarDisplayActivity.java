package com.calendar;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.view.View;
import android.widget.Button;

import com.ecml.R;

/**
 * @class CalendarRehearsalProgramActivity : Display the user calendar, for instance its google agenda. 
 *        
 * @author Anaïs
 */

public class CalendarDisplayActivity extends Activity {

	//Create an array with all the calendar properties
	public static final String[] EVENT_PROJECTION = new String[] { Calendars._ID, // 0
			Calendars.ACCOUNT_NAME, // 1
			Calendars.CALENDAR_DISPLAY_NAME // 2

	};


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_display);

		Button calendar = (Button) findViewById(R.id.calendar);

		calendar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// A date-time specified in milliseconds since the epoch.
				long startMillis = 0;
				//Create the calendar by asking the user which calendar should be used
				Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();

				builder.appendPath("time");
				ContentUris.appendId(builder, startMillis);

				Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
				startActivity(intent);
			}
		});

	}

}
