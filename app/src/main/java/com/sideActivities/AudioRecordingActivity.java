package com.sideActivities;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ecml.R;

/** @class AudioRecordingActivity
 * 
 * @author Nicolas and Anaïs
 * <br>
 * This Activity is mainly composed of :<br>
 *  - a Media Recorder that handles Recording<br>
 *  - a Media Player that handles Replaying the previously performed Audio Records.<br>
 * Main functions :
 * <ul>
 * 		<li>startRecording()</li>
 * 		<li>stopRecording()</li>
 * 		<li>replayAudio()</li>
 * 		<li>pauseAudio()</li>
 * </ul>
 */
public class AudioRecordingActivity extends BaseActivity {

	private long fileName; 			/* File name of last Audio Record */
	private String path;		/* Path of last Audio Record */
	private String ext = ".mp4";	/* Extension of Audio Record files */

	private static final String ECMLPath = "ECML/";						/* Path to ECML Folder */
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecords";	/* Audio Records file name */
	private MediaRecorder mediaRecorder;								/* Media Recorder */
	private int output_format = MediaRecorder.OutputFormat.MPEG_4;		/* Output format (mp4) */
	private MediaPlayer mediaPlayer = new MediaPlayer();				/* Media Player */
	
	private boolean isRecording;	/* Whether or not the Media Recorder is recording */
	private boolean existRecord;	/* Whether or not an Audio Record already exists */
	private boolean isReplayPaused;		/* Whether or not the Media Player is paused */
	
	private ImageView startRecording;	/* The Start Button (for the Media Recorder) */
	private ImageView stopRecording;	/* The Stop Button (for the Media Recorder) */
	private ImageView replayRecording;	/* The Replay Button (for the Media Player) */
	private ImageView pauseReplay;			/* The Pause Button (for the Media Player) */

	private final Context context = this;	/* The Context of the Activity */

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_recording);

		// Start Audio Recording Button
		startRecording = (ImageView) findViewById(R.id.startAudioRecording);
		startRecording.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startAudioRecording();
			}
			
		});

		// Stop Audio Recording Button
		stopRecording = (ImageView) findViewById(R.id.stopAudioRecording);
		stopRecording.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stopAudioRecording();
			}
			
		});

		// Play last Record
		replayRecording = (ImageView) findViewById(R.id.replayAudioRecording);
		replayRecording.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				replayAudio();
			}
			
		});


		// Pause Replay
		pauseReplay = (ImageView) findViewById(R.id.pauseReplay);
		pauseReplay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pauseAudio();
			}
			
		});

	}
	
	/** If activity pauses while recording, then stop recording */
	public void onPause() {
		super.onPause();
		if (isRecording) {
			stopAudioRecording();
		}
	}

	/** Get the Filename of the next Audio Record, also updates the path */
	private String getFilenameAudio() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, ECMLPath + AUDIO_RECORDER_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}
		fileName = System.currentTimeMillis();
		path = file.getAbsolutePath();
		return (path + "/" + fileName + ext);
	}

	/** Start Audio Recording if not recording yet */
	private void startAudioRecording() {
		if (!isRecording) {
			mediaRecorder = new MediaRecorder();
			
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(output_format);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			
			mediaRecorder.setOutputFile(getFilenameAudio());
			mediaRecorder.setOnErrorListener(errorListener);
			mediaRecorder.setOnInfoListener(infoListener);
			try {
				Toast.makeText(context, "Start Audio Recording", Toast.LENGTH_SHORT).show();
				mediaRecorder.prepare();
				mediaRecorder.start();
				isRecording = true;
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
		if (isRecording) {
			if (null != mediaRecorder) {
				Toast.makeText(context, "Stop Audio Recording", Toast.LENGTH_SHORT).show();
				mediaRecorder.stop();
				mediaRecorder.reset();
				mediaRecorder.release();
				mediaRecorder = null;
				existRecord = true;
				isRecording = false;
			}
		} else {
			Toast.makeText(context, "Not Recording", Toast.LENGTH_SHORT).show();
		}
	}

	/** Replay last Audio Record
	 * if not currently recording
	 * and if there is one to replay
	 * and if it's not already playing it
	 */
	private void replayAudio() {
		if (!isRecording) {
			if (existRecord) {
				if (!mediaPlayer.isPlaying()) {
					if (!isReplayPaused) { 	// if the Media Player is not paused, then load the last Audio Record
											// because it may have not been done yet
						mediaPlayer.reset();
						try {
							mediaPlayer.setDataSource(path + "/" + fileName + ext);
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
					isReplayPaused = false;
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
			isReplayPaused = true;
		}
		else {
			Toast.makeText(context, "Not Playing", Toast.LENGTH_SHORT).show();
		}
	}

	/** Check if there are no errors */
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(AudioRecordingActivity.this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	/** Listen to the info the Media Recorder could give */
	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(AudioRecordingActivity.this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

}
