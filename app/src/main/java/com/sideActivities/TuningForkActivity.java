package com.sideActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ecml.R;


/** @class TuningForkActivity
 * 
 * <br>The Tuning Fork is an activity allowing you to play all pitches from 100Hz to 16000Hz on a tablet.<br>
 * It features buttons to shift up and down octaves aswell as notes. It also includes a slide bar to
 * adjust the reference pitch.<br>
 * An octave is composed of 12 notes from C to B. The tuning fork can theoretically play 124 notes (from 1 to 124).<br>
 * 61 corresponds to A4 (440 Hz)
 * 1 corresponds to A-1
 * 124 corresponds to C10
 *
 */
public class TuningForkActivity extends BaseActivity {

	// Set up the objects that are on the screen
	private TextView freqView;			/* The text view displaying the current frequency */
	private Switch switchButton;		/* The on/off button */
	private int sineFreq;				/* The current note (to be converted) */
	private Button previousOctave;		/* The button to shift down an octave */
	private Button nextOctave;			/* The button to shift up an octave */
	private Button previousNote;		/* The button to shift down a note */
	private Button nextNote;			/* The button to shift up a note */
	private SeekBar pitchBar;			/* The slide bar to adjust the reference pitch */
	private TextView octave;			/* The text view displaying the current octave */
	private TextView note;				/* The text view displaying the current note */
	private TextView refOctave;			/* The button to get back to octave #4 */
	private TextView refNote;			/* The button to get back to the note A of the current octave */
	private TextView refPitch;			/* The button to get back to the middle of the slide bar */
	private boolean running = false;	/* The boolean telling whether or not a sound is being played */

	/* Variables for tone generation */
	private final int sampleRate = 44000;
	private final int targetSamples = 5500;
	private int numSamples = 5500;   // calculated with respect to frequency later
	private int numCycles = 500;    // calculated with respect to frequency later

	/* The array is made bigger than needed so they can be adjusted */
	private double sample[] = new double[targetSamples * 2];
	private byte generatedSnd[] = new byte[2 * 2 * targetSamples];

	/** The array of the notes */
	private String[] notes = { "G\u266F/ A\u266D", "A", "A\u266F/ B\u266D", "B", "C", "C\u266F/ D\u266D", "D", "D\u266F/ E\u266D", "E", "F",
			"F\u266F/ G\u266D", "G" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tuning_fork);

		// Set the buttons
		switchButton = (Switch) findViewById(R.id.switchButton);
		previousOctave = (Button) findViewById(R.id.previousOctave);
		nextOctave = (Button) findViewById(R.id.nextOctave);
		previousNote = (Button) findViewById(R.id.previousNote);
		nextNote = (Button) findViewById(R.id.nextNote);
		// Set the slide bar
		pitchBar = (SeekBar) findViewById(R.id.PitchBar);
		// Set the views to be updated when changing frequency
		octave = (TextView) findViewById(R.id.numberOctave);
		note = (TextView) findViewById(R.id.letterNote);
		freqView = (TextView) findViewById(R.id.numberFrequence);
		// Set the buttons to go back to reference notes
		refOctave = (TextView) findViewById(R.id.adjustOctave);
		refNote = (TextView) findViewById(R.id.adjustNote);
		refPitch = (TextView) findViewById(R.id.textViewAdjRefPitch);
		

		// Set each onClickListener on the corresponding button
		switchButton.setOnCheckedChangeListener(switchListener);
		
		pitchBar.setOnSeekBarChangeListener(sineFreqBarListener);

		previousOctave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sineFreq > 12) {
					sineFreq -= 12;
					updateView();
				}
			}

		});

		nextOctave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sineFreq < 124 - 12) {
					sineFreq += 12;
					updateView();
				}
			}

		});

		previousNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sineFreq > 1) {
					sineFreq -= 1;
					updateView();
				}
			}

		});

		nextNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sineFreq < 124) {
					sineFreq += 1;
					updateView();
				}
			}

		});
		
		// Always give the current note in octave #4 (note between 52 and 63)
		refOctave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sineFreq = ((sineFreq - 4) % 12) + 52;
				updateView();
			}
		});
		
		// Always give the note A in the current octave (note of the form 12k+1)
		refNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sineFreq = ((int) Math.floor((sineFreq - 4.0) / 12) + 1) * 12 + 1;
				updateView();
			}
		});

		// Set the slide bar back to the middle
		refPitch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pitchBar.setProgress(100);
			}
		});

		// Initialize the switch button, the frequency and seek bar to desired values
		switchButton.setChecked(false);
		sineFreq = 61;
		pitchBar.setMax(200);
		pitchBar.setProgress(100);
		updateView();

		// Help button
		Button help = (Button) findViewById(R.id.help_tuning_fork);
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showHelpDialog();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Kill the child thread
		running = false;
	}


	/**
	 * A call-back for when the user presses the switch button
	 */
	OnCheckedChangeListener switchListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// Kill any existing threads in case the button is being spammed
			running = false;

			// Check if light is off, if so, turn it on
			if (switchButton.isChecked()) { // Turn on the sound
				genTone(convertProgress_Hz(sineFreq));
				new BeepTask().execute();
			}
		}
	};
	
	/**
	 * A call-back for when the user change the Sine Frequency
	 */
	OnSeekBarChangeListener sineFreqBarListener = new OnSeekBarChangeListener() {

		public void onStopTrackingTouch(SeekBar seekBar) {
			genTone(convertProgress_Hz(sineFreq));
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (sineFreq < 1)
				sineFreq = 1;
			updateView();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}


	// This runs the process in a background thread so the UI isn't locked up
	private class BeepTask extends AsyncTask<Void, Void, Void> {

		protected void onProgressUpdate(Void... voids) {

		}

		@SuppressWarnings("unused")
		protected void onPostExecute(Void... voids) {
			// Turn the light off when done
		}

		@SuppressWarnings("unused")
		protected void onCancelled(Void... voids) {
			// Turn the light off when done
		}

		@Override
		protected Void doInBackground(Void... voids) {
			running = true;
			playSound();
			// Loop with breaker variable from the parent
			return null;
		}
	}

	// Based on but modified and improved from
	// http://stackoverflow.com/questions/2413426/playing-an-arbitrary-tone-with-android
	// functions for tone generation
	/** Calculates the correct tone according to the frequency */
	private void genTone(double freqOfTone) {

		// Clean out the arrays
		for (int i = 0; i < targetSamples * 2; ++i) {
			sample[i] = 0;
		}
		for (int i = 0; i < targetSamples * 2 * 2; ++i) {
			generatedSnd[i] = (byte) 0x0000;
		}

		// Calculate adjustments to make the sample start and stop evenly
		numCycles = (int) (0.5 + freqOfTone * targetSamples / sampleRate);
		numSamples = (int) (0.5 + numCycles * sampleRate / freqOfTone);

		// Fill out the array
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
		}

		// Convert to 16 bit pcm sound array
		// Assumes the sample buffer is normalized.
		int idx = 0;
		for (double dVal : sample) {
			// Scale loudness by frequency
			double amplitude = (double) (32767 * 5 / (Math.log(freqOfTone)));
			if (amplitude > 32767)
				amplitude = 32767;
			// Scale signal to amplitude
			short val = (short) (dVal * amplitude);
			// In 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
	}

	/** Actually play the sound at the selected frequency after calling genTone(freqOfTone) */
	private void playSound() {

		final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, numSamples * 2, AudioTrack.MODE_STREAM);
		audioTrack.write(generatedSnd, 0, numSamples * 2);
		audioTrack.play();
		while (running == true) {
			audioTrack.write(generatedSnd, 0, numSamples * 2);
		}
		audioTrack.stop();
		running = false;
	}


	/** Convert the note into time and frequency
	 * 
	 * @param note
	 */
	private double convertProgress_Hz(int note) {
		// http://www.phy.mtu.edu/~suits/NoteFreqCalcs.html
		// Java was bad at powers math of non integers, so made a loop to do the
		// powers

		// A440 base pitch is adjusted down 5 octaves by multiplying by
		// 2^(-60/12) = 0.03125
		double Hz = (427.5 + 0.125 * (float) pitchBar.getProgress()) * 0.03125;
		// Raise the base pitch to the 2^n/12 power
		for (int m = 1; m < (note); m++) {
			Hz = Hz * 1.0594630943593;  // 2^(1/12)
		}
		return Hz;
	}

	/** Update the view : the frequency, the octave and the note */
	private void updateView() {
		freqView.setText(Double.toString(convertProgress_Hz(sineFreq)));
		octave.setText(Integer.toString((int) Math.floor((sineFreq - 4.0) / 12)));
		note.setText(notes[sineFreq - 12 * ((sineFreq) / 12)]);
		genTone(convertProgress_Hz(sineFreq));
		if (sineFreq < 37) {
			Toast.makeText(getApplicationContext(), "You can't hear < 100Hz on a tablet speaker", Toast.LENGTH_LONG).show();
		}
	}
	
	/** Open an Alert Dialog explaining how to use the Tuning Fork */
	private void showHelpDialog() {
		LayoutInflater inflator = LayoutInflater.from(this);
		final View dialogView = inflator.inflate(R.layout.help_tuning_fork, null);

		// Create an Alert Dialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("HELP");
		builder.setView(dialogView);
		// Set an OK button
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface builder, int whichButton) {

			}
		});
		// Actually create the Alert Dialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}

}