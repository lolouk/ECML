package com.metronome;

import android.app.Activity;

/** @class Metronome
 * 
 * @author Nicolas and Anaïs
 * <br>
 * The Metronome is set to 60 bpm (beats per minute) by defaut without any time signature.
 * Its sound depends on ring volume and not music volume so that music volume and metronome volume
 * are not related.<br>
 * The number of bpm can vary from 1 to 200.<br>
 * The time Signature can vary from 0 to 9.<br>  
 */
public class Metronome {

	private Activity activity;	/* The activity it is used in (normally either SheetMusicActivity or MetronomeActivity) */
	private Horloge horloge;	/* The horloge which actually plays sounds */
	private int tempo;			/* The current Tempo */
	private int accentBeep;		/* The Time Signature, 0 if none */

	/** Create a Metronome with the default parameters : 60 bpm without any time signature
	 * @param activity */
	public Metronome(Activity activity) {
		this.activity = activity;
		tempo = 60;
		accentBeep = 0;
	}

	/** Get the current Tempo */
	public int getTempo() {
		return tempo;
	}

	/** Sets the Tempo */
	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	/** Set the time Signature */
	public void setAccentBeep(int accentBeep) {
		this.accentBeep = accentBeep;
	}

	/** Start the Metronome with its current parameters after stopping it if it was already on */ 
	public void startMetronome() {
		stopMetronome();
		horloge = new Horloge(tempo, accentBeep, activity.getBaseContext());
	}

	/** Stop the Metronome */
	public void stopMetronome() {
		if (horloge != null) {
			horloge.stop();
		}
	}

}
