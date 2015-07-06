package com.game;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ecml.ChooseSongActivity;
import com.ecml.ECML;
import com.ecml.R;
import com.sideActivities.BaseActivity;

public class SpeedGameModeActivity extends BaseActivity {
	
TextView rules;

	/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.game_speed_mode);
			
			

			// Back to the score button
			Button backToScore = (Button) findViewById(R.id.back);
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

			// Help button
			Button help = (Button) findViewById(R.id.help);
			help.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showHelpDialog();
				}
				
			});

			// Change game button
			Button game = (Button) findViewById(R.id.game);
			game.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), GameActivity.class);
					startActivity(intent);
					finish();
				}
				
			});
			
			// lvl1 button
			Button lvl1 = (Button) findViewById(R.id.lvl1);
			lvl1.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ECML.intent = new Intent(getApplicationContext(), ChooseSongActivity.class);
					ECML.intent.putExtra(ChooseSongActivity.mode, "speed");
					ECML.intent.putExtra("level", 1);
					startActivity(ECML.intent);
				}
				
			});
			
			// lvl2 button
			Button lvl2 = (Button) findViewById(R.id.lvl2);
			lvl2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ECML.intent = new Intent(getApplicationContext(), ChooseSongActivity.class);
					ECML.intent.putExtra(ChooseSongActivity.mode,"speed");
					ECML.intent.putExtra("level", 2);
					startActivity(ECML.intent);
				}
				
			});
			
			// lvl3 button
			Button lvl3 = (Button) findViewById(R.id.lvl3);
			lvl3.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ECML.intent = new Intent(getApplicationContext(), ChooseSongActivity.class);
					ECML.intent.putExtra(ChooseSongActivity.mode,"speed");
					ECML.intent.putExtra("level", 3);
					startActivity(ECML.intent);
				}
				
			});
			
			// lvl4 button
			Button lvl4 = (Button) findViewById(R.id.lvl4);
			lvl4.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ECML.intent = new Intent(getApplicationContext(), ChooseSongActivity.class);
					ECML.intent.putExtra(ChooseSongActivity.mode,"speed");
					ECML.intent.putExtra("level", 4);
					startActivity(ECML.intent);
				}
				
			});

		}

		/** Create the Help Alert Dialog */
		private void showHelpDialog() {
			LayoutInflater inflator = LayoutInflater.from(this);
			final View dialogView = inflator.inflate(R.layout.help_speed, null);
		
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("HELP");
			builder.setView(dialogView);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface builder, int whichButton) {

				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		
}

