/*
 * Copyright (c) 2011-2013 Madhav Vaidyanathan
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

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.game.ReadingGameBeginner;
import com.game.ReadingGameNormal;
import com.game.SpeedGamelvl1;
import com.game.SpeedGamelvln;
import com.sideActivities.BaseTabActivity;

/** @class ChooseSongActivity
 * <br>
 * The ChooseSongActivity class is a tabbed view for choosing a song to play.<br>
 * There are 3 tabs:<br>
 * - All    (AllSongsActivity)    : Display a list of all songs<br>
 * - Recent (RecentSongsActivity) : Display of list of recently opened songs<br>
 * - Browse (FileBrowserActivity) : Let the user browse the filesystem for songs
 */
public class ChooseSongActivity extends BaseTabActivity implements OnTabChangeListener {

	private Intent intent;
    static ChooseSongActivity globalActivity;
	public static final String mode = "mode";
    public static final String number = "number";
	public static final int level = 1;

    @Override
    public void onCreate(Bundle state) {
    	
        globalActivity = this;
        super.onCreate(state);
        
        // Set the Action Bar Title of the Activity (corresponding to the first Tab)
        setTitle("ECML: Choose Song");
       
        Bitmap allFilesIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.allfilesicon);
        Bitmap recentFilesIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.recentfilesicon);
        Bitmap browseFilesIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.browsefilesicon);

        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("All")
                .setIndicator("All", new BitmapDrawable(allFilesIcon))
                .setContent(new Intent(this, AllSongsActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("Recent")
                .setIndicator("Recent", new BitmapDrawable(recentFilesIcon))
                .setContent(new Intent(this, RecentSongsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        tabHost.addTab(tabHost.newTabSpec("Browse")
                .setIndicator("Browse", new BitmapDrawable(browseFilesIcon))
                .setContent(new Intent(this, FileBrowserActivity.class)));
        
        tabHost.setOnTabChangedListener(this);
    }
    
    /** Update Action bar Title when changing Tab */
    @Override
    public void onTabChanged(String tabId) {
    	if("All".equals(tabId)) {
    		setTitle("ECML: Choose Song");
    	}
    	if("Recent".equals(tabId)) {
    		setTitle("ECML: Recent Songs");
    	}
    	if("Browse".equals(tabId)) {
    		setTitle("ECML: Browse Files");
    	}
    }

    public static void openFile(FileUri file) {
        globalActivity.doOpenFile(file);
    }

    /** Open the chosen file in the right activity */ 
    public void doOpenFile(FileUri file) {
        byte[] data = file.getData(this);
        if (data == null || data.length <= 6 || !MidiFile.hasMidiHeader(data)) {
            ChooseSongActivity.showErrorDialog("Error: Unable to open song: " + file.toString(), this);
            return;
        }

        ECML.song = file;
        updateRecentFile(file);

        String mode = "";
        // Get the mode for which we are opening a file
        if (ECML.intent != null) {
        	mode = ECML.intent.getStringExtra(ChooseSongActivity.mode);
        }
        Log.d("MODE" , "" + mode);

        // Get the number of the current activity
        int number = this.getIntent().getIntExtra("number",0);

        // Get the level of the mode (speed/reading of notes) if there is one 
        int lvl = this.getIntent().getIntExtra("level", level);

        if (mode.equals("speed")) {
			if (lvl == 1) {
				intent = new Intent(Intent.ACTION_VIEW, file.getUri(), this, SpeedGamelvl1.class);
				intent.putExtra(SpeedGamelvl1.MidiTitleID, file.toString());
                intent.putExtra("number",number);
                startActivity(intent);
                finish();
			} else {
				intent = new Intent(Intent.ACTION_VIEW, file.getUri(), this, SpeedGamelvln.class);
				intent.putExtra(SpeedGamelvln.MidiTitleID, file.toString());
				intent.putExtra("level", lvl);
                intent.putExtra("number",number);
                startActivity(intent);
                finish();
			}
			
		}
        else if (mode.equals("reading")) {
			if (lvl == 1) {
				intent = new Intent(Intent.ACTION_VIEW, file.getUri(), this, ReadingGameBeginner.class);
				intent.putExtra(ReadingGameBeginner.MidiTitleID, file.toString());
                intent.putExtra("number",number);
                startActivity(intent);
                finish();
            }
			else {
				intent = new Intent(Intent.ACTION_VIEW, file.getUri(), this, ReadingGameNormal.class);
				intent.putExtra(ReadingGameNormal.MidiTitleID, file.toString());
                intent.putExtra("number",number);
                startActivity(intent);
                finish();
			}
			
		}
        else if (mode.equals("chooseSong")) {
			intent = new Intent(Intent.ACTION_VIEW, file.getUri(), this, SheetMusicActivity.class);
            intent.putExtra("number",number);
            startActivity(intent);
            finish();
		}
        else if (mode.equals("studentActivities")) {
            intent = new Intent();
            intent.putExtra("song", file.toString());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }


    /** Show an error dialog with the given message */
    public static void showErrorDialog(String message, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
           }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /** Save the given FileUri into the "recentFiles" preferences.
     *  Save a maximum of 10 recent files.
     */
    public void updateRecentFile(FileUri recentfile) {
        try {
            SharedPreferences settings = getSharedPreferences("midisheetmusic.recentFiles", 0);
            SharedPreferences.Editor editor = settings.edit();
            JSONArray prevRecentFiles = null;
            String recentFilesString = settings.getString("recentFiles", null);
            if (recentFilesString != null) {
                prevRecentFiles = new JSONArray(recentFilesString);
            }
            else {
                prevRecentFiles = new JSONArray();
            }
            JSONArray recentFiles = new JSONArray();
            JSONObject recentFileJson = recentfile.toJson();
            recentFiles.put(recentFileJson);
            for (int i = 0; i < prevRecentFiles.length(); i++) {
                if (i >= 10) {
                    break; // only store the 10 most recent files
                }
                JSONObject file = prevRecentFiles.getJSONObject(i); 
                if (!FileUri.equalJson(recentFileJson, file)) {
                    recentFiles.put(file);
                }
            }
            editor.putString("recentFiles", recentFiles.toString() );
            editor.commit();
        }
        catch (Exception e) {
        }
    }
    
}

