package com.ecml;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;

/**
 * @class VolumeListener
 * <br>
 * The Volume Listener is used to detect when the volume is muted so
 * that the mute image can be updated when needed. It is also used to
 * remember the value of the volume before a mute call.
 */
public class VolumeListener extends ContentObserver {

	private Context context;	/* Context of the activity */
	private MidiPlayer player;	/* The Midi Player to handle */

	/**
	 * Create a Volume Listener
	 * 
	 * @param context
	 *            The context of the activity
	 * @param handler
	 *            The handler
	 * @param player
	 *            The MidiPlayer to handle
	 */
	public VolumeListener(Context context, Handler handler, MidiPlayer player) {
		super(handler);
		this.context = context;
		this.player = player;
	}

	@Override
	public boolean deliverSelfNotifications() {
		return super.deliverSelfNotifications();
	}

	/**
	 * The Listener itself :
	 * <ul>
	 * 		<li>update volume if needed</li>
	 * 		<li>update the mute image if needed</li>
	 * </ul>
	 */
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);

		// Get the volume
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

		// If the volume has changed and isn't equal 0, then update volume
		if (volume != 0) {
			player.setVolume(volume);
			// If the player is in mute state, then unmute it because it shouldn't be
			if (player.getMute()) {
				player.unmute();
			}
		}

		// If the volume is now equal to 0, put the Midi Player in mute state
		if (volume == 0 && !player.getMute()) {
			player.mute();
		}
	}

}
