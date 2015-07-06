package com.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ecml.ChooseSongActivity;
import com.ecml.ECML;
import com.ecml.R;
import com.sideActivities.BaseActivity;

/** @class GameActivity
 * <br>
 * The Game Activity is the main screen activity for the different games :
 * <ul>
 * 		<li>Speed</li>
 * 		<li>Musical Ear</li>
 * 		<li>Reading of Notes</li>
 * 		<li>Technique</li>
 * </ul>
 * From this point, you can also choose to go back to the score.
 */
public class GameActivity extends BaseActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		// Start the Speed part of the game
		Button speed = (Button) findViewById(R.id.speed);
		speed.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SpeedGameModeActivity.class);
				startActivity(intent);
				finish();
			}
			
		});

		// Start the Musical Ear game
		Button musicalEar = (Button) findViewById(R.id.musicalEar);
		musicalEar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MusicalEarGameModeActivity.class);
				startActivity(intent);
				finish();
			}
			
		});
		
		// Start the Reading of Notes part of the game
		Button reading = (Button) findViewById(R.id.reading);
		reading.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ReadingGameModeActivity.class);
				startActivity(intent);
				finish();
			}
			
		});
		
		// Start Technique part of the game
		Button technique = (Button) findViewById(R.id.technique);
		technique.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), TechniqueActivity.class);
				startActivity(intent);
				finish();
			}
			
		});
		

		// Back to the score button
		Button backToScore = (Button) findViewById(R.id.backToScore);
		backToScore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ECML.song != null) {
					ECML.intent.putExtra(ChooseSongActivity.mode,"chooseSong");
					ChooseSongActivity.openFile(ECML.song);
				}
				else {
					ECML.intent = new Intent(getApplicationContext(), ChooseSongActivity.class);
					ECML.intent.putExtra(ChooseSongActivity.mode,"chooseSong");
					startActivity(ECML.intent);
				}
				finish();
			}
			
		});
	}

}
