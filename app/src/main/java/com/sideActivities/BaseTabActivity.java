package com.sideActivities;

import android.app.TabActivity;
import android.content.Intent;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.calendar.CalendarActivity;
import com.ecml.ChooseSongActivity;
import com.ecml.ECMLActivity;
import com.ecml.FacebookActivity;
import com.ecml.R;
import com.game.GameActivity;
import com.metronome.MetronomeActivity;

/** @class BaseTabActivity
 * <br>
 * This Class is the base of every activity that IS a tabActivity.
 * It was created to factor code for the action bar. It simply adds a drop-down menu
 * allowing the user to move from any activity to any other.
 */
public class BaseTabActivity extends TabActivity implements KeyListener {
	
	protected Menu menu;
	
	/** When the menu button is pressed, initialize the menu. */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Create a back button in the top left corner
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_action_bar, menu);
		this.menu = menu;
		return super.onCreateOptionsMenu(menu);
	}
	
	/** When menu has dropped down, handle click on corresponding items */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
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
}
