package com.ecml;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** This class represents the Android Activity where the user can add an activity in the Sequence of Activities
 *
 */
public class StudentActivities extends Activity {
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_activities);
		setTitle("List of activities");
		final Spinner spinner_activity = (Spinner) findViewById(R.id.activities_spinner);
		final Spinner spinner_duration = (Spinner) findViewById(R.id.duration_spinner);

		// Create an ArrayAdapter using the string array and a default spinner LAYOUT for the choice of an Activity
		final ArrayAdapter<CharSequence> adapter_activity = ArrayAdapter.createFromResource(this, R.array.activity_array, android.R.layout.simple_spinner_item);
		final ArrayAdapter<CharSequence> adapter_duration = ArrayAdapter.createFromResource(this, R.array.duration_array, android.R.layout.simple_spinner_item);

		// Specify the layout to use when THE LIST of choices appears
		adapter_activity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter_duration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// APPLY the adapter to the spinner
		spinner_activity.setAdapter(adapter_activity);
		spinner_duration.setAdapter(adapter_duration);

        final EditText tempoValue = (EditText) findViewById(R.id.tempo);
		Button send = (Button) findViewById(R.id.sendBtn);
		Button song_choice = (Button) findViewById(R.id.song_choice);

        //Pop-up alert
        AlertDialog.Builder alertBuilder= new AlertDialog.Builder(getBaseContext());
        final AlertDialog alert = alertBuilder.create();
        alert.setButton(DialogInterface.BUTTON_NEUTRAL, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.cancel();
            }
        });

		song_choice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getBaseContext(),ChooseSongActivity.class);
				intent.putExtra("song",true);
				startActivity(intent);
			}
		});

		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String type = null;
                int duration;
                int num = ECMLActivity.nbActivities;
                String song = null;
                int tempo = 60;

                //Get the type of the activity
                Integer index1 = spinner_activity.getSelectedItemPosition();
				String activity = (String) adapter_activity.getItem(index1);
				if (activity.equals("Play instrument alone")) {
                    type = ECMLActivity.PRACTICE_ALONE;
				}
				else if (activity.equals("Reading notes")) {
                    type = ECMLActivity.READING_OF_NOTES;
				}
				else if (activity.equals("Check with the teacher")) {
                    type = ECMLActivity.CHECK_WITH_YOUR_TEACHER;
				}
				else if (activity.equals("Play instrument with the piano")) {
                    type = ECMLActivity.PRACTICE_WITH_ACCOMPANIMENT;
				}
				else if (activity.equals("Work difficult parts")) {
                    type = ECMLActivity.SPEED_GAME;
				} else {    //TODO: Add a default value on the spinner which cannot be selected
                    alert.setTitle("Warning");
					alert.setMessage("Please select one activity from the list");
					alert.show();
				}

                //Get the duration of the activity
                Integer index2 = spinner_duration.getSelectedItemPosition();
                duration = Integer.parseInt(adapter_duration.getItem(index2).toString())*60000; //convert in milliseconds

                //Get the song of the activity

                //Get the tempo
                //TODO : min and max values
                tempo = Integer.parseInt(tempoValue.getText().toString());

				if (num==0) {
					ReadWriteXMLFile.write(new ActivityParameters(num,type,duration,song,tempo,true,false,duration),getApplicationContext());
				}
				else {
					if (ReadWriteXMLFile.readActivityByNumber(num-1,getApplicationContext()).isFinished()) {
						ReadWriteXMLFile.write(new ActivityParameters(num,type,duration,song,tempo,true,false,duration),getApplicationContext());
					}
					else {
						ReadWriteXMLFile.write(new ActivityParameters(num,type,duration,song,tempo,false,false,duration),getApplicationContext());
					}
				}
				ECMLActivity.nbActivities++;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Create a back button in the top left corner
		getActionBar().setDisplayHomeAsUpEnabled(true);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
