package com.metronome;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ecml.R;
import com.sideActivities.BaseActivity;

/** @class MetronomeActivity
 * 
 * @author Nicolas and Anaïs
 * <br>
 * This activity includes a Metronome featuring a time signature which can vary from 0 to 9.<br>
 * <p>You can control it by sliding the seek bar and by adding or removing 1 bpm to the tempo.<br>
 * The Metronome used can vary from 1 to 200 bpm.<br>
 * The default values are 60 bpm and no time signature.</p>
 * Its volume depend on ring volume and not music volume.
 */
public class MetronomeActivity extends BaseActivity {

	private Metronome metronome;		/* Metronome Controller */
	private SeekBar slider;				/* Slider that sets the tempo */
	private int accentBeep;				/* Time Signature */
	private TextView timeSignature;		/* The View for the Time Signature */
	private TextView startMetronome;	/* Start Button */
	private TextView stopMetronome;		/* Stop Button */
	private ImageView minus;			/* Minus Button*/ 
	private ImageView plus;				/* Plus Button*/ 
	private ActionBar actionBar;		/* Action Bar */
	private PopupMenu popup;			/* Popup Menu for the Time Signature */
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metronome);

		/* Buttons */
		startMetronome = (Button) findViewById(R.id.startMetronome);
		stopMetronome = (Button) findViewById(R.id.stopMetronome);
		minus = (ImageView) findViewById(R.id.minusTempo);
		plus = (ImageView) findViewById(R.id.plusTempo);
		timeSignature = (Button) findViewById(R.id.timeSignature);
		updateTimeSignatureView();
		/* End of Buttons */
		
		/* Buttons' Listeners */
		startMetronome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				metronome.startMetronome();
			}
		});

		stopMetronome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				metronome.stopMetronome();
			}
		});

		minus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				minus();
			}
		});

		plus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				plus();
			}
		});

		timeSignature.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				metronome.stopMetronome();
				// Create the instance of PopupMenu
				popup = new PopupMenu(MetronomeActivity.this, timeSignature);
				// Inflate the Popup using xml file
				popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
				
				// Register the Popup with OnMenuItemClickListener
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getTitle() != "Off") {
							accentBeep = Character.getNumericValue(item.getTitle().charAt(0));
						}
						else {
							accentBeep = 0;
						}
						timeSignature.setText("Time Signature: " + item.getTitle());
						metronome.setAccentBeep(accentBeep);
						metronome.startMetronome();
						return true;
					}
				});
				
				popup.show();
			}
		});
		/* End of Buttons' Listeners */

		metronome = new Metronome(this);
		setSliderListener();
	}

	/** When this activity pasuses, stop the metronome */
	@Override
	protected void onPause() {
		super.onPause();
		metronome.stopMetronome();
	}

	/** Update the View for the Tempo */
	private void updateTempoView() {
		TextView tempoView = ((TextView) findViewById(R.id.tempo));
		tempoView.setText("Tempo: " + metronome.getTempo() + " bpm");
	}
	
	/** Update the View for the Time Signature */
	private void updateTimeSignatureView() {
		if (accentBeep == 0) {
			timeSignature.setText("Time Signature: Off");
		}
		else {
			timeSignature.setText("Time Signature: " + accentBeep);
		}
	}

	/** Set the Slider Listener */
	private void setSliderListener() {
		slider = (SeekBar) findViewById(R.id.sliderMetronome);
		slider.setMax(200 - 1); 						// -1 to avoid reaching 0
		slider.setProgress(metronome.getTempo() - 1); 	// -1 to avoid reaching 0
		updateTempoView();
		
		slider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				metronome.startMetronome();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				metronome.stopMetronome();
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				metronome.setTempo(progress); 	// update the Variable Tempo of the Metronome
				updateTempoView(); 				// update the View
			}
			
		});
	}

	/** Add 1 to the tempo */
	void plus() {
		metronome.stopMetronome();
		slider.setProgress(slider.getProgress() + 1); // onProgressChanged updates the Metronome and the View automatically
		metronome.startMetronome();
	}

	/** Remove 1 from the tempo */
	void minus() {
		metronome.stopMetronome();
		slider.setProgress(slider.getProgress() - 1); // onProgressChanged updates the Metronome and the View automatically
		metronome.startMetronome();
	}

}
