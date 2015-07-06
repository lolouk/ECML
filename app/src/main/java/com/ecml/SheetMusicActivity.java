/*
 * Copyright (c) 2011-2012 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */

package com.ecml;

/***************************************************************************************************************/
/***************************************************************************************************************/
/***************************************************************************************************************/
/******************************************** IMPORTS FOR ADD-UPS **********************************************/
/***************************************************************************************************************/
/***************************************************************************************************************/
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.calendar.CalendarActivity;
import com.game.GameActivity;
import com.metronome.Metronome;
import com.metronome.MetronomeActivity;
import com.sideActivities.AudioRecordingActivity;
import com.sideActivities.TuningForkActivity;
import com.sideActivities.VideoRecordingActivity;
import com.sideActivities.YoutubeActivity;

/***************************************************************************************************************/
/***************************************************************************************************************/
/**************************************** END OF IMPORTS FOR ADD-UPS *******************************************/
/***************************************************************************************************************/
/***************************************************************************************************************/
/***************************************************************************************************************/

/**
 * @class SheetMusicActivity
 * <br>
 * The SheetMusicActivity is the main activity.<br>
 * The main components are:<br>
 * - MidiPlayer : The buttons and speed bar at the top.<br>
 * - Piano : For highlighting the piano notes during playback.<br>
 * - SheetMusic : For highlighting the sheet music notes during playback.
 * 
 */

public class SheetMusicActivity extends Activity implements SurfaceHolder.Callback, KeyListener {

	/******************************************* MidiSheet variables ******************************************/
	
	public static final String MidiTitleID = "MidiTitleID";
	public static final int settingsRequestCode = 1;

	private MidiPlayer player; 		/* The play/stop/rewind toolbar */
	private Piano piano; 			/* The piano at the top */
	private SheetMusic sheet; 		/* The sheet music */
	private LinearLayout layout; 	/* THe layout */
	private MidiFile midifile; 		/* The midi file to play */
	private MidiOptions options; 	/* The options for sheet music and sound */
	private long midiCRC; 			/* CRC of the midi bytes */
	private String title; 			/* Title of the current Song */

	/**************************************** End of MidiSheet variables *****************************************/

	/*************************************************************************************************************/
	/*************************************************************************************************************/
	/*************************************************************************************************************/
	/**************************************** VARIABLES FOR ADD-UPS **********************************************/
	/*************************************************************************************************************/
	/*************************************************************************************************************/

	/*************************************** Audio Recording Variables *******************************************/

	private long audioFileName;		/* File name of last Audio Record */
	private String pathAudio;		/* Path of last Audio Record */
	private String ext = ".mp4";	/* Extension of Audio and VIDEO Record files */

	private static final String AUDIO_RECORDER_FOLDER = "AudioRecords";	/* Audio Records folder name */
	private MediaRecorder audioMediaRecorder;							/* Audio Media Recorder */
	private int output_format = MediaRecorder.OutputFormat.MPEG_4;		/* Output format (mp4) */
	private MediaPlayer mediaPlayer = new MediaPlayer();				/* Media Player */

	private boolean existAudioRecord;					/* Whether or not an Audio Record already exists */
	private boolean isAudioReplayPaused;				/* Whether or not the Audio Media Player is paused */
	private boolean isAudioRecording;					/* Whether or not the Audio Media Recorder is recording */
	private boolean isAudioRecordingAndPlayingMusic;	/* Whether or not the Audio Media Record is recording
														 * while the music is playing */

	private Handler timer;

	/************************************** End of Audio Recording Variables **********************************/

	/***************************************** Video Recording Variables **************************************/

	private long videoFileName;					/* File name of last Video Record */
	private File videoFile;						/* The file storing the Video Record */
	private SurfaceView surfaceView;			/* The Surface View needed for the Camera */
	private SurfaceHolder surfaceHolder;		/* The Surface Holder needed for the Camera */
	private MediaRecorder videoMediaRecorder;	/* Video Media Recorder */
	private Camera camera;						/* The Camera */
	private static final String VIDEO_RECORDER_FOLDER = "VideoRecords";	/* Video Records folder name */
	private String pathVideo;			/* Path of last Video Record */
	private boolean existVideoRecord;	/* Whether or not an Video Record already exists */
	private boolean isVideoRecording;	/* Whether or not the Video Media Recorder is recording */
	private boolean front;		/* Whether the front camera or the back camera is used */
	private String cameraSide;			/* The String telling which camera is used accordingly to 'front' */
	private static final String FRONT_SIDE = "Front";
	private static final String BACK_SIDE = "Back";

	private View topLayout;		/* The Layout in which the Camera is displayed (needs to be improved) */

	/************************************ End of Video Recording Variables ************************************/

	/****************************************** File Variables ************************************************/

	private static String ECMLPath = "ECML/";		/* Path to the ECML folder from the sdcard */
	private static final String MUSIC_SHEET_FOLDER = "MusicSheets"; /* Music Sheet folder name */

	/***************************************** End of File Variables *******************************************/

	/****************************************** Metronome Variables ********************************************/

	private Metronome metronome;	/* The Metronome */
	private SeekBar slider;			/* The Slider for the Metronome's Tempo */
	private View abMetronome;		/* The Metronome View in the Action Bar */

	/************************************** End of Metronome Variables *****************************************/

	private final Context context = this;	/* Context of this activity */
	private Menu menu;						/* Actually the Action Bar and NOT the overflowing menu */
	private VolumeListener volumeListener;	/* The Volume Listener to handle volume changes */

	/************************************** Countdown Variables ***********************************************/

	private int number;
	private Countdown countdown;

	/**********************************************************************************************************/
	/**********************************************************************************************************/
	/************************************ END OF VARIABLES FOR ADD-UPS ****************************************/
	/**********************************************************************************************************/
	/**********************************************************************************************************/
	/**********************************************************************************************************/

	/**
	 * Create this SheetMusicActivity.<br>
	 * The Intent should have two parameters:<br>
	 * - data: The uri of the midi file to open.<br>
	 * - MidiTitleID: The title of the song (String)<br>
	 */
	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		setTheme(android.R.style.Theme_Holo_Light);
		
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.orange));
		ab.setBackgroundDrawable(colorDrawable);

		ClefSymbol.LoadImages(this);
		TimeSigSymbol.LoadImages(this);
		MidiPlayer.LoadImages(this);

		// Parse the MidiFile from the raw bytes
		Uri uri = this.getIntent().getData();
		title = this.getIntent().getStringExtra(MidiTitleID);
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
		options.showNoteColors = settings.getBoolean("showNoteColors", false);
		String json = settings.getString("" + midiCRC, null);
		MidiOptions savedOptions = MidiOptions.fromJson(json);
		if (savedOptions != null) {
			options.merge(savedOptions);
		}
		createView();
		createSheetMusic(options);
		
		metronome = new Metronome(this);


		/**********************************************************************************************************/
		/**********************************************************************************************************/
		/**********************************************************************************************************/
		/********************************************* BUTTONS ****************************************************/
		/**********************************************************************************************************/
		/**********************************************************************************************************/

		isAudioRecording = false;
		isVideoRecording = false;

		chooseFrontOrBack();
		
		surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
//		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		/*************************************** End of side activities *******************************************/

		/**********************************************************************************************************/
		/**********************************************************************************************************/
		/********************************************* END OF BUTTONS *********************************************/
		/**********************************************************************************************************/
		/**********************************************************************************************************/
		/**********************************************************************************************************/

	} // END ONCREATE

	/** Create the MidiPlayer and Piano views */
	void createView() {
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		player = new MidiPlayer(this);
		volumeListener = new VolumeListener(this, new Handler(), player);
		getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, volumeListener );
		piano = new Piano(this);

		topLayout = getLayoutInflater().inflate(R.layout.main_top, layout, false);
		topLayout.setVisibility(View.GONE);
		layout.addView(topLayout);

		player.getPianoButton().setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				options.showPiano = !options.showPiano;
				player.SetPiano(piano, options);
				SharedPreferences.Editor editor = getPreferences(0).edit();
				editor.putBoolean("showPiano", options.showPiano);
				String json = options.toJson();
				if (json != null) {
					editor.putString("" + midiCRC, json);
				}
				editor.commit();
			}
		});

		player.getPlayAndRecordButton().setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startAudioRecordingAndPlayingMusic();
			}
		});

		player.getPlayRecordButton().setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				playAudio();
			}
		});

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
		params.gravity = Gravity.CENTER_HORIZONTAL;

		layout.addView(player);
		layout.addView(piano, params);
		setContentView(layout);
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.orange));
		player.SetPiano(piano, options);
		layout.requestLayout();
	}

	/** Create the SheetMusic view with the given options */
	private void createSheetMusic(MidiOptions options) {
		if (sheet != null) {
			layout.removeView(sheet);
		}
		
		if (!options.showPiano) {
			piano.setVisibility(View.GONE);
		} else {
			piano.setVisibility(View.VISIBLE);
		}
		sheet = new SheetMusic(this);
		sheet.init(midifile, options, false, 1, 2);
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
					// If we touch while music is playing, pause the midi player
					if (player != null && player.playstate == player.playing) {
						// If the user was recording himself while the music was playing
						// pause the midi player and also stop recording
						player.Pause();
						stopAudioRecordingAndPlayingMusic();
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

	/** Always display this activity in landscape mode. */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/** When the menu button is pressed, initialize the menus. */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		/***********************************************************************************************************/
		/***********************************************************************************************************/
		/***********************************************************************************************************/
		/********************************************* ACTION BAR **************************************************/
		/***********************************************************************************************************/
		/***********************************************************************************************************/

		// Create a back button in the top left corner
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);
		
		this.menu = menu;

		/***************************************** METRONOME ACTION VIEW *******************************************/
		/** Get the action view of the menu item whose id is video */
		abMetronome = (View) menu.findItem(R.id.metronome).getActionView();

		setSliderListener();
		
		TextView startMetronome = (TextView) abMetronome.findViewById(R.id.startmetronome);
		TextView stopMetronome = (TextView) abMetronome.findViewById(R.id.stopmetronome);

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
		/************************************ END OF METRONOME ACTION VIEW *****************************************/

		/***********************************************************************************************************/
		/***********************************************************************************************************/
		/***************************************** END OF ACTION BAR ***********************************************/
		/***********************************************************************************************************/
		/***********************************************************************************************************/
		/***********************************************************************************************************/

		if (player != null) {
			player.Pause();
		}

		return true;
	}

	/**
	 * Callback when a menu item is selected.<br>
	 * - Song Settings : Adjust the sheet music and sound options<br>
	 * - Save As Images: Save the sheet music as PNG images<br>
	 * - Help : Display the HTML help screen<br>
	 * <br>
	 * Callback when an action bar item is selected.<br>
	 * - Youtube : Launch Youtube in a new browser or uses the activity if the user wants to<br>
	 * - Video : Display the Camera Preview screen<br>
	 * 		<ul>
	 * 			<li> Start Recording : starts the video recording with a preview</li>
	 * 			<li> Stop Recording : closes the Camera Preview screen and stops recording</li>
	 * 			<li> Play Last Record : Open the replay of last record in a new activity</li>
	 * 			<li> Switch Camera : Switch the camera from back to front and vise versa</li>
	 * 		</ul>
	 * - Audio :
	 * 		<ul>
	 * 			<li> Start Recording : starts the audio recording</li>
	 * 			<li> Stop Recording : stops recording</li>
	 * 			<li> Play Last Record : replays last record</li>
	 * 			<li> Pause Replay : pauses the current replay</li>
	 * 		</ul>
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.song_settings:
				changeSettings();
				return true;
			case R.id.save_images:
				showSaveImagesDialog();
				return true;
			case R.id.help:
				showHelp();
				return true;
			case R.id.youtube:
				showYoutube();
				return true;
			case R.id.video:
				surfaceView.setVisibility(View.VISIBLE);
				topLayout.setVisibility(View.VISIBLE);
				return true;
			case R.id.startVideoRecording:
				surfaceView.setVisibility(View.VISIBLE);
				try {
					startVideoRecording();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			case R.id.stopVideoRecording:
				stopVideoRecording();
				surfaceView.setVisibility(View.GONE);
				topLayout.setVisibility(View.GONE);
				return true;
			case R.id.replayVideoRecording:
				replayVideoRecording();
				return true;
			case R.id.switchCamera:
				switchCamera();
				return true;
			case R.id.startAudioRecording:
				startAudioRecording();
				return true;
			case R.id.stopAudioRecording:
				stopAudioRecording();
				return true;
			case R.id.replayAudioRecording:
				playAudio();
				return true;
			case R.id.pauseReplayAudioRecording:
				pauseAudio();
				return true;
			case R.id.mainScreen:
				Intent mainScreen = new Intent(getApplicationContext(), ECMLActivity.class);
				// Go to the main screen and kill any other living activities
				mainScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainScreen);
				return true;
			case R.id.chooseSongActivity:
				Intent chooseSongActivity = new Intent(getApplicationContext(), ChooseSongActivity.class);
				startActivity(chooseSongActivity);
				finish();
				return true;
			case R.id.calendarActivity:
				Intent calendarActivity = new Intent(getApplicationContext(), CalendarActivity.class);
				startActivity(calendarActivity);
				finish();
				return true;
			case R.id.audioActivity:
				Intent audioActivity = new Intent(getApplicationContext(), AudioRecordingActivity.class);
				startActivity(audioActivity);
				finish();
				return true;
			case R.id.videoActivity:
				Intent videoActivity = new Intent(getApplicationContext(), VideoRecordingActivity.class);
				startActivity(videoActivity);
				finish();
				return true;
			case R.id.gameActivity:
				Intent gameActivity = new Intent(getApplicationContext(), GameActivity.class);
				startActivity(gameActivity);
				finish();
				return true;
			case R.id.messengerActivity:
				Intent messengerActivity = new Intent(getApplicationContext(), com.androidim.Login.class);
				startActivity(messengerActivity);
				finish();
				return true;
			case R.id.youtubeActivity:
				Intent youtubeActivity = new Intent(getApplicationContext(), YoutubeActivity.class);
				startActivity(youtubeActivity);
				finish();
				return true;
			case R.id.metronomeActivity:
				Intent metronomeActivity = new Intent(getApplicationContext(), MetronomeActivity.class);
				startActivity(metronomeActivity);
				finish();
				return true;
			case R.id.tuningForkActivity:
				Intent tuningForkActivity = new Intent(getApplicationContext(), TuningForkActivity.class);
				startActivity(tuningForkActivity);
				finish();
				return true;
			case R.id.communicationActivity:
				Intent communicationActivity = new Intent(getApplicationContext(), FacebookActivity.class);
				startActivity(communicationActivity);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * To change the sheet music options, start the SettingsActivity. Pass the
	 * current MidiOptions as a parameter to the Intent. Also pass the 'default'
	 * MidiOptions as a parameter to the Intent. When the SettingsActivity has
	 * finished, the onActivityResult() method will be called.
	 */
	private void changeSettings() {
		MidiOptions defaultOptions = new MidiOptions(midifile);
		Intent intent = new Intent(this, SettingsActivity.class);
		intent.putExtra(SettingsActivity.settingsID, options);
		intent.putExtra(SettingsActivity.defaultSettingsID, defaultOptions);
		startActivityForResult(intent, settingsRequestCode);
	}

	/** Show the "Save As Images" dialog */
	private void showSaveImagesDialog() {
		LayoutInflater inflator = LayoutInflater.from(this);
		final View dialogView = inflator.inflate(R.layout.save_images_dialog, null);
		final EditText filenameView = (EditText) dialogView.findViewById(R.id.save_images_filename);
		filenameView.setText(midifile.getFileName().replace("_", " "));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.save_images_str);
		builder.setView(dialogView);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface builder, int whichButton) {
				saveAsImages(filenameView.getText().toString());
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface builder, int whichButton) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/** Save the current sheet music as PNG images. */
	private void saveAsImages(String name) {
		String filename = name;
		boolean scrollVert = options.scrollVert;
		if (!options.scrollVert) {
			options.scrollVert = true;
			createSheetMusic(options);
		}
		try {
			int numpages = sheet.GetTotalPages();
			for (int page = 1; page <= numpages; page++) {
				Bitmap image = Bitmap.createBitmap(SheetMusic.PageWidth + 40, SheetMusic.PageHeight + 40, Bitmap.Config.ARGB_8888);
				Canvas imageCanvas = new Canvas(image);
				sheet.DrawPage(imageCanvas, page);
				File path = Environment.getExternalStoragePublicDirectory(ECMLPath + MUSIC_SHEET_FOLDER);
				File file;
				if (numpages > 1) {
					file = new File(path, "" + filename + " -  page " + page + ".png");
				} else {
					file = new File(path, "" + filename + ".png");
				}
				path.mkdirs();
				OutputStream stream = new FileOutputStream(file);
				image.compress(Bitmap.CompressFormat.PNG, 0, stream);
				image = null;
				stream.close();

				// Inform the media scanner about the file
				MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null, null);
			}
		} catch (IOException e) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Error saving image to file " + ECMLPath + MUSIC_SHEET_FOLDER + filename + ".png");
			builder.setCancelable(false);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (NullPointerException e) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Ran out of memory while saving image to file " + ECMLPath + MUSIC_SHEET_FOLDER + filename + ".png");
			builder.setCancelable(false);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		options.scrollVert = scrollVert;
		createSheetMusic(options);
	}

	/** Show the HTML help screen. */
	private void showHelp() {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}

	/** Change blanks and ":" to "+" in a String */
	private String spaceToPlus(String title) {
		String newTitle = "";
		boolean lastIsPlus = false; // tell whether last char is a '+' or not
		for (int i = 0; i < title.length(); i++) {
			if ((title.charAt(i) == ' ' || title.charAt(i) == ':'))
				if (!lastIsPlus) {
					// Last character is not a +, we can add one
				newTitle = newTitle + "+";
				lastIsPlus = true;
				} else {
					// Last character is a +, we cannot add another one
			} else {
				newTitle = newTitle + title.charAt(i);
				lastIsPlus = false;
			}
		}
		return newTitle;
	}

	/** Get the current instrument for track 0 */
	public String instrumentYoutube() {
		String instrument = "";
		if (MidiOptions.instruments[0] == 0 || MidiOptions.instruments[0] == 1 || MidiOptions.instruments[0] == 2 || MidiOptions.instruments[0] == 3
				|| MidiOptions.instruments[0] == 4 || MidiOptions.instruments[0] == 5 || MidiOptions.instruments[0] == 6) {
			instrument = "piano";
		} else if (MidiOptions.instruments[0] == 25 || MidiOptions.instruments[0] == 26 || MidiOptions.instruments[0] == 27 || MidiOptions.instruments[0] == 28
				|| MidiOptions.instruments[0] == 29 || MidiOptions.instruments[0] == 30 || MidiOptions.instruments[0] == 31 || MidiOptions.instruments[0] == 32) {
			instrument = "guitar";
		}

		else if (MidiOptions.instruments[0] == 33 || MidiOptions.instruments[0] == 34 || MidiOptions.instruments[0] == 35 || MidiOptions.instruments[0] == 36) {
			instrument = "bass";
		} else {
			instrument = MidiFile.Instruments[MidiOptions.instruments[0]];
		}
		return instrument;
	}

	/** Launch Youtube on Navigator and search for the current song */
	private void showYoutube() {
		Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
		String instrument = instrumentYoutube();
		myWebLink.setData(Uri.parse("http://www.youtube.com/results?search_query=" + spaceToPlus(title + " " + instrument)));
		startActivity(myWebLink);
	}

	/**
	 * This is the callback when the SettingsActivity is finished. Get the
	 * modified MidiOptions (passed as a parameter in the Intent). Save the
	 * MidiOptions. The key is the CRC checksum of the midi data, and the value
	 * is a JSON dump of the MidiOptions. Finally, re-create the SheetMusic View
	 * with the new options.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode != settingsRequestCode) {
			return;
		}
		options = (MidiOptions) intent.getSerializableExtra(SettingsActivity.settingsID);

		// Check whether the default instruments have changed.
		for (int i = 0; i < options.instruments.length; i++) {
			if (options.instruments[i] != midifile.getTracks().get(i).getInstrument()) {
				options.useDefaultInstruments = false;
			}
		}
		// Save the options.
		SharedPreferences.Editor editor = getPreferences(0).edit();
		editor.putBoolean("scrollVert", options.scrollVert);
		editor.putInt("shade1Color", options.shade1Color);
		editor.putInt("shade2Color", options.shade2Color);
		editor.putBoolean("showPiano", options.showPiano);
		String json = options.toJson();
		if (json != null) {
			editor.putString("" + midiCRC, json);
		}
		editor.commit();

		// Recreate the sheet music with the new options.
		createSheetMusic(options);
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
		if (ReadWriteXMLFile.readActivityByNumber(number,getApplicationContext()) != null) {
			countdown = new Countdown(ReadWriteXMLFile.readActivityByNumber(number,getApplicationContext()).getCountdown(), 1000, number, getApplicationContext());
			countdown.start();
		}
	}

	/** When this activity pauses, stop the music and all recordings */
	@Override
	protected void onPause() {
		if (player != null) {
			player.Pause();
		}
		super.onPause();
		
		stopAudioRecordingAndPlayingMusic();
		
		if (isAudioRecording) {
			stopAudioRecording();
		}
		
		if (isVideoRecording) {
			stopVideoRecording();
		}

		if (countdown != null) {
			countdown.cancel();
			countdown = null;
		}
		
		mediaPlayer.stop();
		metronome.stopMetronome();
		getApplicationContext().getContentResolver().unregisterContentObserver(volumeListener);
	}

	@Override
	protected void onStop() {
		if (countdown != null) {
			countdown.cancel();
			countdown = null;
		}
		super.onStop();
	}


		/**********************************************************************************************************/
	/**********************************************************************************************************/
	/**********************************************************************************************************/
	/************************************** FUNCTIONS FOR ADD-UPS *********************************************/
	/**********************************************************************************************************/
	/**********************************************************************************************************/

	/************************************ Audio Recording functions *******************************************/

	/** Get the Filename of the next Audio Record, also update the path */
	private String getFilenameAudio() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, ECMLPath + AUDIO_RECORDER_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}
		audioFileName = System.currentTimeMillis();
		pathAudio = file.getAbsolutePath();
		return (pathAudio + "/" + audioFileName + ext);
	}

	/** Start Audio Recording if not recording audio or video yet */
	private void startAudioRecording() {
		if (!isVideoRecording && !isAudioRecording) {
			audioMediaRecorder = new MediaRecorder();
			audioMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			audioMediaRecorder.setOutputFormat(output_format);
			audioMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			audioMediaRecorder.setOutputFile(getFilenameAudio());
			audioMediaRecorder.setOnErrorListener(errorListener);
			audioMediaRecorder.setOnInfoListener(infoListener);
			try {
				Toast.makeText(context, "Start Audio Recording", Toast.LENGTH_SHORT).show();
				audioMediaRecorder.prepare();
				audioMediaRecorder.start();
				isAudioRecording = true;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(context, "Stop Recording first", Toast.LENGTH_SHORT).show();
		}
	}

	/** Stop Audio Recording if currently recording */
	private void stopAudioRecording() {
		if (isAudioRecording) {
			if (null != audioMediaRecorder) {
				Toast.makeText(context, "Stop Audio Recording", Toast.LENGTH_SHORT).show();
				audioMediaRecorder.stop();
				audioMediaRecorder.reset();
				audioMediaRecorder.release();
				audioMediaRecorder = null;
				existAudioRecord = true;
				isAudioRecording = false;
			}
		} else {
			Toast.makeText(context, "Not Recording", Toast.LENGTH_SHORT).show();
		}
	}

	/** Replay last Audio Record
	 * if not currently audio or video recording
	 * and if there is one to replay
	 * and if it's not already playing it
	 */
	private void playAudio() {
		if (!isVideoRecording && !isAudioRecording) {
			if (existAudioRecord) {
				if (!mediaPlayer.isPlaying()) {
					if (!isAudioReplayPaused) { // if the Media Player is not paused, then load the last
												// Audio Record because it may have not been done yet
						mediaPlayer.reset();
						try {
							mediaPlayer.setDataSource(pathAudio + "/" + audioFileName + ext);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							mediaPlayer.prepare();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					isAudioReplayPaused = false;
					Toast.makeText(context, "Playing Last Audio Record", Toast.LENGTH_SHORT).show();
					mediaPlayer.start();
				} else {
					Toast.makeText(context, "Replay already Playing", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(context, "No Recent Audio Record", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context, "Stop Recording First", Toast.LENGTH_SHORT).show();
		}
	}

	/** Pause the Replay if it's currently playing */
	private void pauseAudio() {
		if (mediaPlayer.isPlaying()) {
			Toast.makeText(context, "Pausing Audio Replay", Toast.LENGTH_SHORT).show();
			mediaPlayer.pause();
			isAudioReplayPaused = true;
		}
		else {
			Toast.makeText(context, "Not Playing", Toast.LENGTH_SHORT).show();
		}
	}

	/** Check if there are no errors */
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(SheetMusicActivity.this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	/** Listen to the info the Media Recorder could give */
	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(SheetMusicActivity.this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	/************************************* End of Audio Recording Functions ***********************************/

	/**************************************** Video Recording Functions ***************************************/
	
	/** Get the Filename of the next Video Record, also updates the path */
	private String getFilenameVideo() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		videoFile = new File(filepath, ECMLPath + VIDEO_RECORDER_FOLDER);
		if (!videoFile.exists()) {
			videoFile.mkdirs();
		}
		videoFileName = System.currentTimeMillis();
		pathVideo = videoFile.getAbsolutePath();
		return (pathVideo + "/" + videoFileName + ext);
	}

	/** Start Video Recording if not recording audio or video yet */
	private void startVideoRecording() throws IOException {
		if (!isVideoRecording && !isAudioRecording) {
			if (front) {
				camera = openFrontFacingCamera();
			} else {
				camera = Camera.open();
			}
			videoMediaRecorder = new MediaRecorder(); // Works well
			if (camera != null) {
				camera.stopPreview();
				camera.unlock();
				videoMediaRecorder.setCamera(camera);
		
				videoMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
				videoMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
				videoMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		
				if (front) {
					videoMediaRecorder.setProfile(CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_FRONT, CamcorderProfile.QUALITY_HIGH));
				} else {
					videoMediaRecorder.setProfile(CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_BACK, CamcorderProfile.QUALITY_HIGH));
				}
		
				videoMediaRecorder.setOutputFile(getFilenameVideo());
				videoMediaRecorder.setVideoFrameRate(10);
		
				videoMediaRecorder.prepare();
				isVideoRecording = true;
				videoMediaRecorder.start();
			}
		} else {
			Toast.makeText(context, "Stop Recording first", Toast.LENGTH_SHORT).show();
		}
	}

	/** Stop Video Recording if currently recording,
	 * and release video media recorder
	 * and release camera for other apps
	 */
	private void stopVideoRecording() {
		if (isVideoRecording) {
			try {
				existVideoRecord = true;
				videoMediaRecorder.stop();
			} catch (RuntimeException stopException) {
				videoFile.delete();
				Toast.makeText(context, "Video Recording Failed", Toast.LENGTH_SHORT).show();
			}
			releaseMediaRecorder();
			releaseCamera();
			isVideoRecording = false;
		} else {
			Toast.makeText(context, "Not Recording", Toast.LENGTH_SHORT).show();
		}
	}

	/** Release MediaRecorder to save memory
	 * and to avoid that other apps get unable
	 * to use this ressource
	 */
	private void releaseMediaRecorder() {
		if (videoMediaRecorder != null) {
			videoMediaRecorder.reset(); // clear recorder configuration
			videoMediaRecorder.release(); // release the recorder object
			videoMediaRecorder = null;
			camera.lock(); // lock camera for later use
		}
	}

	/** Release the camera for other apps */
	private void releaseCamera() {
		if (camera != null) {
			camera.release(); // release the camera for other applications
			if (front) {
				camera = openFrontFacingCamera();
				camera.release();
			} else {
				camera = Camera.open();
				camera.release();
			}

		}
	}

	/** Replay last Audio Record
	 * if not currently audio or video recording
	 * and if there is one to replay
	 */
	private void replayVideoRecording() {
		if (!isVideoRecording && !isAudioRecording) {
			if (existVideoRecord) {
				String filename = videoFileName + ext;
				String lastvideo = pathVideo + "/" + filename;
				Intent intentToPlayVideo = new Intent(Intent.ACTION_VIEW);
				intentToPlayVideo.setDataAndType(Uri.parse(lastvideo), "video/*");
				startActivity(intentToPlayVideo);
				Toast.makeText(context, "Playing Last Video Record", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "No Recent Video Record", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context, "Stop Recording First", Toast.LENGTH_SHORT).show();
		}
	}

	/** Necessary function for the camera */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/** Necessary function for the camera */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	/** Necessary function for the camera */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
	
	/** Set the Front Camera if there is one, otherwise the Back Camera
	 * and if there isn't either, don't do anything
	 */
	private void chooseFrontOrBack() {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		int cameraCount = Camera.getNumberOfCameras();
		int i = 0;
		boolean chosen = false;
		while (i < cameraCount || !chosen) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				chosen = true;
				front = true;
				cameraSide = FRONT_SIDE;
			} else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				chosen = true;
				front = false;
				cameraSide = BACK_SIDE;
			}
			i++;
		}
	}

	/** Open the front facing camera */
	private Camera openFrontFacingCamera() {
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		Log.i("CameraNumber", "" + cameraCount);
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				try {
					cam = Camera.open(camIdx);
				} catch (RuntimeException e) {

				}
			}
		}

		return cam;

	}
	
	/** Switch the camera from front to back and vice versa */
	private void switchCamera() {
		if (Camera.getNumberOfCameras() > 1) {
			front = !front;
			if (front) {
				cameraSide = FRONT_SIDE;
			}
			else {
				cameraSide = BACK_SIDE;
			}
			Toast.makeText(context, "Camera Switched: Now using " + cameraSide + " Camera" , Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Cannot Switch Camera: Now using " + cameraSide + " Camera" , Toast.LENGTH_SHORT).show();
		}
	}

	/************************************* End of Video Recording Functions ***********************************/

	/*********************************** Menu Button and Back Button Listener *********************************/

	@Override
	public int getInputType() {
		return 0;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		int action = event.getAction();
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			if (action == KeyEvent.ACTION_UP && menu != null && menu.findItem(R.id.mainDropDownMenu) != null) {
				// Open the overflow menu as if we pressed the onscreen settings button
				menu.performIdentifierAction(R.id.mainDropDownMenu, 0);
				return true;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyOther(View view, Editable text, KeyEvent event) {
		return false;
	}

	@Override
	public void clearMetaKeyState(View view, Editable content, int states) {
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int action = event.getAction();
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (countdown != null) {
				countdown.cancel();
				countdown = null;
			}
			if (action == KeyEvent.ACTION_DOWN) { // That case needs to be added because there's now a keyListener
				this.finish();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
		return false;
	}

	/************************** End of Menu Button and Back Button Listener Functions *************************/

	/****************************************** Metronome Functions *******************************************/

	/** Update the View for the Tempo */
	private void updateTempoView() {
		TextView tempoView = ((TextView) abMetronome.findViewById(R.id.tempo));
		tempoView.setText("Tempo : " + metronome.getTempo() + " bpm");
	}

	/** Set the Slider Listener */
	private void setSliderListener() {
		slider = (SeekBar) abMetronome.findViewById(R.id.slider);
		slider.setMax(200-1); 						// -1 to avoid reaching 0
		slider.setProgress(metronome.getTempo()-1); // -1 to avoid reaching 0
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

	/**************************************** End of Metronome Functions **************************************/

	/************************************** Recording and Playing Functions ***********************************/
	
	/** Start Audio Recording and Start the Media Player */
	private void startAudioRecordingAndPlayingMusic() {
		if (!isAudioRecording && !isVideoRecording) {
			player.getPlayRecordButton().setVisibility(View.VISIBLE);
			player.mute();
			isAudioRecordingAndPlayingMusic = true;
			try {
				Thread.sleep(options.delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			startAudioRecording();
			timer = new Handler();
			timer.postDelayed(player.DoPlay, 0); // we should find a nicer way to call DoPlay but works fine
		}
	}
	
	/** Stop Audio Recording and Stop the Media Player */
	private void stopAudioRecordingAndPlayingMusic() {
		if (isAudioRecordingAndPlayingMusic) {
			player.Pause();
			player.player.stop();	// these two lines are here to prevent the player from
	        player.player.reset();	// playing outloud the very last note supposedly played
			stopAudioRecording();
			player.unmute();
			isAudioRecordingAndPlayingMusic = false;
		}
	}
    
    /********************************** End of Recording and Playing Functions ********************************/
    
	/**********************************************************************************************************/
	/**********************************************************************************************************/
	/************************************* END OF FUNCTIONS FOR ADD-UPS ***************************************/
	/**********************************************************************************************************/
	/**********************************************************************************************************/
	/**********************************************************************************************************/

} // END !!

