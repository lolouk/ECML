package com.metronome;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;

/** @class Horloge
 * 
 * @author Nicolas
 *
 * The Horloge is called by a Metronome and will play sounds at the correct time for it,
 * by using a timer.
 */

public class Horloge {

	private Timer timer;				/* The timer */
	private int currentBeep = 1; 		/* Tracks the number of Beeps */
	private ToneGenerator beep;			/* Beep depending on the current Volume */
	private AudioManager audioManager;	/* The audio manager to control volume */
	private int volume;					/* Current Volume */
	private int volumeMax;				/* Maximum Volume of the Device */
	private int accentBeep;				/* Time Signature */

	/** Create a new Horloge
	 * 
	 * @param tempo The tempo in bpm
	 * @param accentBeep The time signature : if n=accentBeep, every n sounds will be different
	 * @param context The context of the activity
	 */
	public Horloge(int tempo, int accentBeep, Context context) {

		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		// Get the current volume
		volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		// Get the maximum volume of the device
		volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// Set the beep according to the current volume (need to be in %)
		beep = new ToneGenerator(AudioManager.FLAG_PLAY_SOUND, volume * 100 / volumeMax);
		timer = new Timer();
		this.accentBeep = accentBeep;

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				try {
					// If the volume has changed, then update the beep
					if (volume != audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) {
						volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
						beep.release();
						beep = new ToneGenerator(AudioManager.FLAG_PLAY_SOUND, volume * 100 / volumeMax);
					}

					// If there's a time signature and current beep should be accented, then play a different sound
					if (Horloge.this.accentBeep != 0 && currentBeep % Horloge.this.accentBeep == 0) {
						Horloge.this.beep.startTone(ToneGenerator.TONE_SUP_DIAL, 100);
					// Else play the regular sound
					} else {
						Horloge.this.beep.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
					}
					
					// Update current beep
					currentBeep++;
					
				} catch (Exception e) {
					System.err.println("ERROR when beeping");
				}
			}
			
		};
		
		// Notify the timer to start next beep at the right time
		timer.schedule(timerTask, new Date(), 60000 / tempo);
	}

	/** Stop playing future beeps */
	public void stop() {
		beep.release();
		timer.cancel();
	}

	/** Remove all canceled tasks from the task queue. If there are no
     * other references on the tasks, then after this call they are free
     * to be garbage collected.
     *
     * @return the number of canceled tasks that were removed from the task
     *         queue.
     */
	public void purge() {
		timer.purge();
	}

}
