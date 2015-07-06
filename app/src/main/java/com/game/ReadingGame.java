package com.game;

import java.util.ArrayList;
import java.util.zip.CRC32;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ecml.ChooseSongActivity;
import com.ecml.ClefSymbol;
import com.ecml.Countdown;
import com.ecml.ECML;
import com.ecml.ECMLActivity;
import com.ecml.FileUri;
import com.ecml.MidiFile;
import com.ecml.MidiFileException;
import com.ecml.MidiNote;
import com.ecml.MidiOptions;
import com.ecml.MidiPlayer;
import com.ecml.MidiTrack;
import com.ecml.Piano;
import com.ecml.R;
import com.ecml.ReadWriteXMLFile;
import com.ecml.SheetMusic;
import com.ecml.TimeSigSymbol;
import com.sideActivities.BaseActivity;

/**
 * @class ReadingGame :abstract class which define the main part of all the Reading of notes Game activities.
 *        
 * @author Anaïs
 */


public abstract class ReadingGame extends BaseActivity {

	protected enum KeyDisplay {
		DISPLAY_FLAT, DISPLAY_SHARP,
	}

	protected KeyDisplay keyDisplay = KeyDisplay.DISPLAY_SHARP;
	//Name of the notes (english notation)
	protected static final String noteNames[][] = { { "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab" },
			{ "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#" }, };

	protected boolean point = true;			//If true, the player plays the note at the right time 
	protected int counter = 0;				//Number of notes that the player try to find
	protected int score = 0;				//Score of the the player

	public static int level = 1;			//3 levels : beginner, intermediate and advanced

	protected int number;	//The number of the current activity
	protected Countdown countdown;	//A countdown for the current activity



	/*** MidiSheet variables ***/

	public static final String MidiTitleID = "MidiTitleID";

	public static final int settingsRequestCode = 1;

	protected Thread playingthread;
	protected SheetMusic sheet; /* The sheet music */
	protected Piano piano; 		/* The piano */
	protected LinearLayout layout; /* The layout */
	protected long midiCRC; /* CRC of the midi bytes */

	/*** End of MidiSheet variables ***/

	protected ArrayList<MidiTrack> tracks;	/* The Tracks of the song */
	protected ArrayList<MidiNote> notes;	/* The Notes of the first Track (Track 0) */
	static View choice;						/* Top view where the player can choose a note */
	static View result;						/*Top view that appears when the player finish a song */
	private View topLayout;

	protected MidiFile midifile; 			/* The midi file to play */
	protected MidiOptions options; 			/* The options for sheet music and sound */
	protected MidiPlayer player; 			/* The play/stop/rewind toolbar */
	public boolean search;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_reading_choice);

		/*****************
		 * TOP VIEW WITH THE CHOICE OF NOTES AND THE HELP, BACK TO SCORE, CHANGE
		 * GAME BUTTON
		 **********/

		ClefSymbol.LoadImages(this);
		TimeSigSymbol.LoadImages(this);

		// Parse the MidiFile from the raw bytes
		//There is one song for each level
		Uri uri;
		String title;
		Log.i("level", "" + level);
		if (level == 1) {
			Log.i("levelboucle1", "" + level);
			uri = Uri.parse("file:///android_asset/Easy_Songs__Silent_Night.mid");
			title = this.getIntent().getStringExtra(MidiTitleID);
			if (title == null) {
				title = "Silent Night";
			}
		}
		else if (level == 2) {
			uri = Uri.parse("file:///android_asset/Bach__Invention_No._13.mid");
			title = this.getIntent().getStringExtra(MidiTitleID);
			if (title == null) {
				title = "Bach - Invention n°13";
			}
		}
		else {
			Log.i("levelboucle1", "" + level);
			uri = Uri.parse("file:///android_asset/Chopin__Nocturne_Op._9_No._1_in_B-flat_minor.mid");
			title = this.getIntent().getStringExtra(MidiTitleID);
			if (title == null) {
				title = "Chopin - Nocturn Op.9 N°1 in B flat minor";
			}
		}
		number = this.getIntent().getIntExtra("number",0);

		FileUri file = new FileUri(uri, title);
		this.setTitle("ECML: " + title);
		byte[] data;
		try {
			data = file.getData(this);
			midifile = new MidiFile(data, title);
		} catch (MidiFileException e) {
			this.finish();
			return;
		}

		// Initialize the settings (MidiOptions).
		// If previous settings have been saved, use those

		options = new MidiOptions(midifile);

		CRC32 crc = new CRC32();
		crc.update(data);
		midiCRC = crc.getValue();
		SharedPreferences settings = getPreferences(0);
		options.scrollVert = settings.getBoolean("scrollVert", true);
		options.shade1Color = settings.getInt("shade1Color", options.shade1Color);
		options.shade2Color = settings.getInt("shade2Color", options.shade2Color);
		options.showPiano = settings.getBoolean("showPiano", true);
		options.tracks[0] = true;
		for (int i = 1; i < options.tracks.length; i++) {
			options.tracks[i] = false;
		}
		String json = settings.getString("" + midiCRC, null);
		MidiOptions savedOptions = MidiOptions.fromJson(json);
		if (savedOptions != null) {
			options.merge(savedOptions);
		}
		createView();

		player.mute();

		/* Define a layout */
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		choice = getLayoutInflater().inflate(R.layout.game_reading_choice, layout, false);
		layout.addView(choice);
		setContentView(layout);

		// Back to the score button
		Button backToScore = (Button) findViewById(R.id.back);
		backToScore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//If a song as already be chosen, it will open this one, if not, it will ask the player to choose a new one
				if (ECML.song != null) {
					ECML.intent.putExtra(ChooseSongActivity.mode,"chooseSong");
					ChooseSongActivity.openFile(ECML.song);
				} else {
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

				score = 0;
				counter = 0;
				if (player != null) {
					player.Stop();
				}

				Intent intent = new Intent(getApplicationContext(), GameActivity.class);
				startActivity(intent);
				finish();
			}
		});

		//Add the result view to the layout but not visible at that time
		result = getLayoutInflater().inflate(R.layout.game_points, layout, false);
		layout.addView(result);
		result.setVisibility(View.GONE);
		setContentView(layout);
		
		//Create the sheet music
		createSheetMusic(options);


	}
	
	/** Make the result view appears*/
	public static void result() {
		choice.setVisibility(View.GONE);
		result.setVisibility(View.VISIBLE);
	}

	/** Create the MidiPlayer and Piano views */
	// Even if there is no piano, it is necessary to create the view of the piano anyway
	void createView() {
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		player = new MidiPlayer(this);
		piano = new Piano(this);

		topLayout = getLayoutInflater().inflate(R.layout.main_top, layout, false);
		topLayout.setVisibility(View.GONE);
		layout.addView(topLayout);
		layout.addView(player);
		setContentView(layout);
		player.SetPiano(piano, options);
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.orange));
		layout.requestLayout();
	}

	/** Create the SheetMusic view with the given options */
	private void createSheetMusic(MidiOptions options) {
		if (sheet != null) {
			layout.removeView(sheet);
		}
		sheet = new SheetMusic(this);
		sheet.init(midifile, options, true, 0, 29);
		//for levels 2 and 3, the songs are cut
		if (level == 2) {sheet.init(midifile, options, true, 0, 29);}
		if (level == 3) {sheet.init(midifile, options, true, 0, 29);}
		sheet.setPlayer(player);
		layout.addView(sheet);
		piano.SetMidiFile(midifile, options, player);
		piano.SetShadeColors(options.shade1Color, options.shade2Color);
		player.SetMidiFile(midifile, options, sheet);
		layout.requestLayout();
		sheet.callOnDraw();

		sheet.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction() & MotionEvent.ACTION_MASK;
				boolean result = sheet.getScrollAnimation().onTouchEvent(event);
				switch (action) {
					case MotionEvent.ACTION_DOWN:
						// If we touch while music is playing, stop the midi player
						if (player != null && player.playstate == player.playing) {
							player.Pause();
							sheet.getScrollAnimation().stopMotion();
						}
						return result;

					case MotionEvent.ACTION_MOVE:
						return result;

					case MotionEvent.ACTION_UP:
						return result;

					default:
						return false;
				}
			}
		});
	}

	protected void PauseEcoute() {
		point = false;
		player.Pause();
	}

	/** When this activity resumes, redraw all the views */
	@Override
	protected void onResume() {
		Log.d("ReadingGame:onResume", "call");
		super.onResume();
		layout.requestLayout();
		player.invalidate();
		if (sheet != null) {
			sheet.invalidate();
		}
		layout.requestLayout();
		countdown = new Countdown(ReadWriteXMLFile.readActivityByNumber(number,getApplicationContext()).getCountdown(),1000,number,this.getApplicationContext());
		countdown.start();
	}

	/** When this activity pauses, stop the music */
	@Override
	protected void onPause() {
		Log.d("ReadingGame:onPause","call");
		if (player != null) {
			player.Pause();
			player.unmute();
		}
		if (countdown != null) {
			Log.d("ReadingGame:onPause","stop");
			countdown.cancel();
			countdown = null;
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d("ReadingGame:onStop","call");
		if (countdown != null) {
			Log.d("ReadingGame:onStop","stop");
			countdown.cancel();
			countdown = null;
		}
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (countdown != null) {
				Log.d("ReadingGame:BackButton","stop");
				countdown.cancel();
				countdown = null;
				finish();
			}
		}
		return true;
	}

	/** Create the Help Alert Dialog */
	private void showHelpDialog() {
		LayoutInflater inflator = LayoutInflater.from(this);
		final View dialogView = inflator.inflate(R.layout.help_reading_notes, null);
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
