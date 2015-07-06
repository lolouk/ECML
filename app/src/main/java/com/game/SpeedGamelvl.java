package com.game;

import java.util.ArrayList;
import java.util.zip.CRC32;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecml.ChooseSongActivity;
import com.ecml.ClefSymbol;
import com.ecml.ECML;
import com.ecml.FileUri;
import com.ecml.MidiFile;
import com.ecml.MidiFileException;
import com.ecml.MidiNote;
import com.ecml.MidiOptions;
import com.ecml.MidiPlayer;
import com.ecml.MidiTrack;
import com.ecml.Piano;
import com.ecml.R;
import com.ecml.SheetMusic;
import com.ecml.TimeSigSymbol;
import com.sideActivities.BaseActivity;


/* Abstract Class which defines the main part of all the speedgame activities
 * This game doesn't work properly yet,
 * bugs known so far :
 * - If several conscutives notes are the same, then when the first note is played,
 * the following can be counted as correct to even though they were not intended
 * - The counter doesn't work right for speedGamelvln (lvl > 1)  */

public abstract class SpeedGamelvl extends BaseActivity {


    protected enum KeyDisplay {
        DISPLAY_FLAT,
        DISPLAY_SHARP,
    }
    protected KeyDisplay keyDisplay = KeyDisplay.DISPLAY_SHARP;
    protected static final String noteNames[][] = {
        { "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab" },
        { "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#" },
    };
	
	protected boolean point = true;		/* The boolean telling whether a point has been given yet or not */
	protected int counter;				/* The counter telling where we are on the track */
	protected int score;				/* The score of the user */
	protected TextView scoreDisplay;	/* The score of the user display */
	protected TextView playNoteDisplay;	/* The text displaying which note to play */
	protected TextView percentage;		/* The view to display the percentage of right notes */
	protected TextView appreciation;	/* The view to display the appreciation */
	protected ImageView star;			/* The star image */

	/*** MidiSheet variables ***/

	public static final String MidiTitleID = "MidiTitleID";
	public static final int settingsRequestCode = 1;
	
	protected SheetMusic sheet; 	/* The sheet music */
	protected LinearLayout layout; 	/* THe layout */
	protected Piano piano;		 	/* The piano still needs to be added because of the player */
	protected long midiCRC; 		/* CRC of the midi bytes */

	/*** End of MidiSheet variables ***/
	
	protected ArrayList<MidiTrack> tracks;	/* The Tracks of the song */
	protected ArrayList<MidiNote> notes;	/* The Notes of the first Track (Track 0) */
	public static View speedGameView;
	public static View result;

	protected MidiFile midifile; 	/* The midi file to play */
	protected MidiOptions options; 	/* The options for sheet music and sound */
	protected MidiPlayer player; 	/* The play/stop/rewind toolbar */
	protected MidiNote note;		/* The current note */

	/*** PitchDetection variables ***/

	protected MicrophonePitchPoster pitchPoster;	/* The pitch detector */

	/*** End of PitchDetection variables ***/
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*****************
		 * TOP VIEW WITH THE CHOICE OF NOTES AND THE HELP, BACK TO SCORE, CHANGE
		 * GAME BUTTON
		 **********/

		ClefSymbol.LoadImages(this);
		TimeSigSymbol.LoadImages(this);

		// Parse the MidiFile from the raw bytes
		Uri uri = this.getIntent().getData();
		String title = this.getIntent().getStringExtra(MidiTitleID);
		if (title == null) {
			title = uri.getLastPathSegment();
		}
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

		/* Set the MidiPlayer and Piano */
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		speedGameView = getLayoutInflater().inflate(R.layout.speed_game, layout, false);
		layout.addView(speedGameView);
		
		result = getLayoutInflater().inflate(R.layout.game_points, layout, false);
		layout.addView(result);
		result.setVisibility(View.GONE);
		
		
		player = new MidiPlayer(this);
		piano = new Piano(this);
		player.SetPiano(piano, options);
		layout.addView(player);
		player.setVisibility(View.GONE);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
		layout.addView(piano,params);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		setContentView(layout);
		layout.requestLayout();
		
		player.mute();

		// Back to the score button
		Button backToScore = (Button) findViewById(R.id.back);
		backToScore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ECML.song != null) {
					ECML.intent.putExtra(ChooseSongActivity.mode, "chooseSong");
					ChooseSongActivity.openFile(ECML.song);
				}
				else {
					ECML.intent = new Intent(getApplicationContext(), ChooseSongActivity.class);
					ECML.intent.putExtra(ChooseSongActivity.mode, "chooseSong");
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
				if ( player != null )
				{
					player.Stop();
				}
				if ( pitchPoster != null )
				{
			        pitchPoster.stopSampling();
				}
		        pitchPoster = null;
				
				Intent intent = new Intent(getApplicationContext(), GameActivity.class);
				startActivity(intent);
				finish();
			}
			
		});

		// Stop button
		Button stop = (Button) findViewById(R.id.stop);
		stop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				score = 0;
				counter = 0;
				if ( player != null )
				{
					player.Stop();
				}
				if ( pitchPoster != null )
				{
			        pitchPoster.stopSampling();
				}
		        pitchPoster = null;
			}
			
		});
		
		setContentView(layout);
		createSheetMusic(options);
	}

	/** Create the SheetMusic view with the given options */
	private void createSheetMusic(MidiOptions options) {
		if (sheet != null) {
			layout.removeView(sheet);
		}
		sheet = new SheetMusic(this);
		sheet.init(midifile, options, false, 1,2);
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

	/** Pause the MidiPlayer and Stop listening to the pitch */ 
	protected void pauseListening() {
		point = false;
		if (player != null) {
			player.Pause();
		}
		if ( pitchPoster != null)
		{
			pitchPoster.stopSampling();
		}
		pitchPoster = null;
	}
	
	/** Find the notes of the first Track */
	protected void findNotes() {
		notes = tracks.get(0).getNotes();
	}

	/** When this activity resumes, redraw all the views */
	@Override
	protected void onResume() {
		super.onResume();
		layout.requestLayout();
		player.invalidate();
		piano.invalidate();
		if (sheet != null) {
			sheet.invalidate();
		}
		layout.requestLayout();

	}

	/** When this activity pauses, stop the music */
	@Override
	protected void onPause() {
		if (player != null) {
			player.Pause();
			player.unmute();
		}
		if ( pitchPoster != null)
		{
			pitchPoster.stopSampling();
		}
        pitchPoster = null;
		super.onPause();
	}

	/** Create the Help Alert Dialog */
	private void showHelpDialog() {
		LayoutInflater inflator = LayoutInflater.from(this);
		final View dialogView = inflator.inflate(R.layout.help_speed_detailed, null);

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



