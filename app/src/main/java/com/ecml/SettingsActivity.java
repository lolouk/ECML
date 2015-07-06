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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;


/** @class SettingsActivity
 * <br>
 *  This activity is created by the "Settings" menu option.<br>
 *  The user can change settings such as:
 *  <ul>
 *  	<li>Which tracks to display</li>
 *  	<li>Which tracks to mute</li>
 *  	<li>Which instruments to use during playback</li>
 *  	<li>Whether to scroll horizontally or vertically</li>
 *  	<li>Whether to display the piano or not</li>
 *  	<li>Whether to display note letters or not</li>
 *  	<li>Whether to display note colors or not</li>
 *  	<li>Transpose the notes to another key</li>
 *  	<li>Change the key signature or time signature displayed</li>
 *  	<li>Change how notes are combined into chords (the time interval)</li>
 *  	<li>Change the colors for shading the left/right hands</li>
 *  	<li>Whether to display measure numbers</li>
 *  	<li>Play selected measures in a loop</li>
 *  	<li>How long to wait before starting to play the music (delay)</li>
 *  </ul>
 * When created, pass an Intent parameter containing MidiOptions.<br>
 * When destroyed, this activity passes the result MidiOptions to the Intent.
 */
public class SettingsActivity extends PreferenceActivity 
    implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    public static final String settingsID = "settings";
    public static final String defaultSettingsID = "defaultSettings";

    private MidiOptions defaultOptions;  /** The initial option values */
    private MidiOptions options;         /** The option values */

    private Preference restoreDefaults;           /** Restore default settings */
    private Preference setAllTracks;			  /** Unmute and display all tracks */
    private CheckBoxPreference[] selectTracks;    /** Which tracks to display */
    private CheckBoxPreference[] muteTracks;      /** Which tracks to mute */
    private ListPreference[] selectInstruments;   /** Instruments to use per track */
    private Preference setAllToPiano;             /** Set all instruments to piano */
    private CheckBoxPreference scrollVertically;  /** Scroll vertically/horizontally */
    private CheckBoxPreference showPiano;         /** Show the piano */
    private CheckBoxPreference showNoteColors;	  /** Display a different color for each note */
    private CheckBoxPreference showMeasures;      /** Show the measure numbers */
    private CheckBoxPreference showLyrics;        /** Show the lyrics */
    private ListPreference delay;				  /** How long to wait before playing the song */
    private CheckBoxPreference twoStaffs;         /** Combine tracks into two staffs */
    private ListPreference showNoteLetters;       /** Show the note letters */
    private ListPreference transpose;             /** Transpose notes */
    private ListPreference key;                   /** Key Signature to use */
    private ListPreference time;                  /** Time Signature to use */
    private ListPreference combineInterval;       /** Interval (msec) to combine notes */
    
    private ColorPreference shade1Color;          /** Right-hand color */
    private ColorPreference shade2Color;          /** Left-hand color */
    

    /** Play the measures from start to end in a loop */
    private CheckBoxPreference playMeasuresInLoop;
    private ListPreference loopStart;
    private ListPreference loopEnd;


    /** Create the Settings activity. Retrieve the initial option values
     *  (MidiOptions) from the Intent.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        setTitle("ECML: Settings");
        options = (MidiOptions) this.getIntent().getSerializableExtra(settingsID);
        defaultOptions = (MidiOptions) this.getIntent().getSerializableExtra(defaultSettingsID);
        createView();
    }

    /** Create all the preference widgets in the view */
    private void createView() {
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        createRestoreDefaultPrefs(root);
        createRestoreAllTracks(root);
        createTrackPrefs(root);
        createMutePrefs(root);
        createInstrumentPrefs(root);

        PreferenceCategory sheetTitle = new PreferenceCategory(this);
        sheetTitle.setTitle(R.string.sheet_prefs_title);
        root.addPreference(sheetTitle);

        createScrollPrefs(root);
        createShowPianoPrefs(root);
        createShowNoteColorsPrefs(root);
        createShowLyricsPrefs(root);
        createDelayPrefs(root);
        if (options.tracks.length != 2) {
            createTwoStaffsPrefs(root);
        }
        createShowLetterPrefs(root);
        createTransposePrefs(root);
        createKeySignaturePrefs(root);
        createTimeSignaturePrefs(root);
        createCombineIntervalPrefs(root);
        createColorPrefs(root);
        createPlayMeasuresInLoopPrefs(root);
        setPreferenceScreen(root);
    }

    /** For each list dialog, we display the value selected in the "summary" text.
     *  When a new value is selected from the list dialog, update the summary
     *  to the selected entry.
     */
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ListPreference list = (ListPreference) preference;
        int index = list.findIndexOfValue((String)newValue);
        CharSequence entry = list.getEntries()[index];
        preference.setSummary(entry);
        return true;
    }

    /** When the 'restore defaults' preference is clicked, restore the default settings */
    public boolean onPreferenceClick(Preference preference) {
    	updateOptions();
        if (preference == restoreDefaults) {
            options = defaultOptions.copy();
            createView(); 
        }
        else if (preference == setAllToPiano) {
            for (int i = 0; i < options.instruments.length; i++) {
                options.instruments[i] = 0;
            }
            createView();
        }
        else if (preference == setAllTracks ) {
        	for (int i = 0 ; i < options.tracks.length ; i++) {
        		options.tracks[i] = true;
        		options.mute[i] = false;
        	}
        	createView();
        }
        return true;
    }
    
    


    /** Create the "Select Tracks to Display" checkboxes. */
    private void createTrackPrefs(PreferenceScreen root) {
        PreferenceCategory selectTracksTitle = new PreferenceCategory(this);
        selectTracksTitle.setTitle(R.string.select_tracks_to_display);
        root.addPreference(selectTracksTitle);
        selectTracks = new CheckBoxPreference[options.tracks.length];
        for (int i = 0; i < options.tracks.length; i++) {
            selectTracks[i] = new CheckBoxPreference(this);
            selectTracks[i].setTitle("Track " + i);
            selectTracks[i].setChecked(options.tracks[i]);
            root.addPreference(selectTracks[i]);
        }
    }

    /** Create the "Select Tracks to Mute" checkboxes. */
    private void createMutePrefs(PreferenceScreen root) {
        PreferenceCategory muteTracksTitle = new PreferenceCategory(this);
        muteTracksTitle.setTitle(R.string.select_tracks_to_mute);
        root.addPreference(muteTracksTitle);
        muteTracks = new CheckBoxPreference[options.mute.length];
        for (int i = 0; i < options.mute.length; i++) {
            muteTracks[i] = new CheckBoxPreference(this);
            muteTracks[i].setTitle("Track " + i);
            muteTracks[i].setChecked(options.mute[i]);
            root.addPreference(muteTracks[i]);
        }
    }


    /** Create the "Select Instruments For Each Track " lists.
     *  The list of possible instruments is in MidiFile.java.
     */
    private void createInstrumentPrefs(PreferenceScreen root) {
        PreferenceCategory selectInstrTitle = new PreferenceCategory(this);
        selectInstrTitle.setTitle(R.string.select_instruments_per_track);
        root.addPreference(selectInstrTitle);
        selectInstruments = new ListPreference[options.tracks.length];
        for (int i = 0; i < options.instruments.length; i++) {
            selectInstruments[i] = new ListPreference(this);
            selectInstruments[i].setOnPreferenceChangeListener(this);
            selectInstruments[i].setEntries(MidiFile.Instruments);
            selectInstruments[i].setEntryValues(MidiFile.Instruments);
            selectInstruments[i].setTitle("Track " + i);
            selectInstruments[i].setValueIndex(options.instruments[i]);
            selectInstruments[i].setSummary( selectInstruments[i].getEntry() );
            root.addPreference(selectInstruments[i]);
        }
    }

    /** Create the "Scroll Vertically" preference */
    private void createScrollPrefs(PreferenceScreen root) {
        scrollVertically = new CheckBoxPreference(this);
        scrollVertically.setTitle(R.string.scroll_vertically);
        scrollVertically.setChecked(options.scrollVert);
        root.addPreference(scrollVertically);
    }

    /** Create the "Show Piano" preference */
    private void createShowPianoPrefs(PreferenceScreen root) {
        showPiano = new CheckBoxPreference(this);
        showPiano.setTitle(R.string.show_piano);
        showPiano.setChecked(options.showPiano);
        root.addPreference(showPiano);
    }
    
    /** Create the "Show Note colors" preference */
    private void createShowNoteColorsPrefs(PreferenceScreen root) {
        showNoteColors = new CheckBoxPreference(this);
        showNoteColors.setTitle(R.string.show_note_colors);
        showNoteColors.setChecked(options.showNoteColors);
        root.addPreference(showNoteColors);
    }

    /** Create the "Show Lyrics" preference */
    private void createShowLyricsPrefs(PreferenceScreen root) {
        showLyrics = new CheckBoxPreference(this);
        showLyrics.setTitle(R.string.show_lyrics);
        showLyrics.setChecked(options.showLyrics);
        root.addPreference(showLyrics);
    }
    
    /** Create the "Delay" preference */
    private void createDelayPrefs(PreferenceScreen root) {
    	int selected = options.delay/1000 - 1;
    	delay = new ListPreference(this);
    	delay.setOnPreferenceChangeListener(this);
    	delay.setTitle(R.string.delay);
    	delay.setEntries(R.array.delay_entries);
    	delay.setEntryValues(R.array.delay_values);
    	delay.setValueIndex(selected);
    	delay.setSummary(delay.getEntry());
    	delay.setDialogTitle(R.string.choose_delay);
    	root.addPreference(delay);
    }

    /** Create the "Show Note Letters" preference */
    private void createShowLetterPrefs(PreferenceScreen root) {
        showNoteLetters = new ListPreference(this);
        showNoteLetters.setOnPreferenceChangeListener(this);
        showNoteLetters.setTitle(R.string.show_note_letters);
        showNoteLetters.setEntries(R.array.show_note_letter_entries);
        showNoteLetters.setEntryValues(R.array.show_note_letter_values);
        showNoteLetters.setValueIndex(options.showNoteLetters);
        showNoteLetters.setSummary(showNoteLetters.getEntry());
        showNoteLetters.setDialogTitle(R.string.choose_notation);
        root.addPreference(showNoteLetters);
    }


    /** Create the "Combine to Two Staffs" preference. */
    private void createTwoStaffsPrefs(PreferenceScreen root) {
        twoStaffs = new CheckBoxPreference(this);
        if (options.tracks.length == 1) {
            twoStaffs.setTitle(R.string.split_to_two_staffs);
            twoStaffs.setSummary(R.string.split_to_two_staffs_summary);
        }
        else {
            twoStaffs.setTitle(R.string.combine_to_two_staffs);
            twoStaffs.setSummary(R.string.combine_to_two_staffs_summary);
        }
        twoStaffs.setChecked(options.twoStaffs);
        root.addPreference(twoStaffs);
    }

    /** Create the "Transpose Notes" preference.
     *  The values range from 12, 11, 10, .. -10, -11, -12
     */
    private void createTransposePrefs(PreferenceScreen root) {
        int transposeIndex = 12 - options.transpose;
        transpose = new ListPreference(this);
        transpose.setOnPreferenceChangeListener(this);
        transpose.setTitle(R.string.transpose);
        transpose.setEntries(R.array.transpose_entries);
        transpose.setEntryValues(R.array.transpose_values);
        transpose.setValueIndex(transposeIndex);
        transpose.setSummary(transpose.getEntry());
        transpose.setDialogTitle(R.string.choose_transpose);
        root.addPreference(transpose);
    }

    /** Create the "Key Signature" preference */
    private void createKeySignaturePrefs(PreferenceScreen root) {
        key = new ListPreference(this);
        key.setOnPreferenceChangeListener(this);
        key.setTitle(R.string.key_signature);
        key.setEntries(R.array.key_signature_entries);
        key.setEntryValues(R.array.key_signature_values);
        key.setValueIndex(options.key + 1);
        key.setSummary(key.getEntry());
        key.setDialogTitle(R.string.choose_key);
        root.addPreference(key);
    }

    /** Create the "Time Signature" preference */
    private void createTimeSignaturePrefs(PreferenceScreen root) {
        String[] values = { "Default", "3/4", "4/4" };
        int selected = 0;
        if (options.time != null && options.time.getNumerator() == 3)
            selected = 1;
        else if (options.time != null && options.time.getNumerator() == 4)
            selected = 2;

        time = new ListPreference(this);
        time.setOnPreferenceChangeListener(this);
        time.setTitle(R.string.time_signature);
        time.setEntries(values);
        time.setEntryValues(values);
        time.setValueIndex(selected);
        time.setSummary(time.getEntry());
        time.setDialogTitle(R.string.choose_time_signature);
        root.addPreference(time);
    }


    /** Create the "Combine Notes Within Interval"  preference.
     *  Notes within N milliseconds are combined into a single chord,
     *  even though their start times may be slightly different.
     */
    private void createCombineIntervalPrefs(PreferenceScreen root) {
        int selected = options.combineInterval/20  - 1;
        combineInterval = new ListPreference(this);
        combineInterval.setOnPreferenceChangeListener(this);
        combineInterval.setTitle(R.string.combine_interval);
        combineInterval.setEntries(R.array.combine_interval_entries);
        combineInterval.setEntryValues(R.array.combine_interval_values);
        combineInterval.setValueIndex(selected);
        combineInterval.setSummary(combineInterval.getEntry() );
        combineInterval.setDialogTitle(R.string.choose_combine_interval);
        root.addPreference(combineInterval);
    }


    /* Create the "Left-hand color" and "Right-hand color" preferences */
    private void createColorPrefs(PreferenceScreen root) {
        shade1Color = new ColorPreference(this);
        shade1Color.setColor(options.shade1Color);
        shade1Color.setTitle(R.string.right_hand_color);
        root.addPreference(shade1Color);

        shade2Color = new ColorPreference(this);
        shade2Color.setColor(options.shade2Color);
        shade2Color.setTitle(R.string.left_hand_color);
        root.addPreference(shade2Color);
    }


    /** Create the "Play Measures in a Loop" preference.
     *
     *  Note that we display the measure numbers starting at 1, 
     *  but the actual playMeasuresInLoopStart field starts at 0.
     */
    private void createPlayMeasuresInLoopPrefs(PreferenceScreen root) {
        String[] values = new String[options.lastMeasure + 1];
        for (int measure = 0; measure < values.length; measure++) {
            values[measure] = "" + (measure+1);
        }

        PreferenceCategory playLoopTitle = new PreferenceCategory(this);
        playLoopTitle.setTitle(R.string.play_measures_in_loop_title);
        root.addPreference(playLoopTitle);

        showMeasures = new CheckBoxPreference(this);
        showMeasures.setTitle(R.string.show_measures);
        showMeasures.setChecked(options.showMeasures);
        root.addPreference(showMeasures);

        playMeasuresInLoop = new CheckBoxPreference(this);
        playMeasuresInLoop.setTitle(R.string.play_measures_in_loop);
        playMeasuresInLoop.setChecked(options.playMeasuresInLoop);
        root.addPreference(playMeasuresInLoop);

        loopStart = new ListPreference(this);
        loopStart.setOnPreferenceChangeListener(this);
        loopStart.setTitle(R.string.play_measures_in_loop_start);
        loopStart.setEntries(values);
        loopStart.setEntryValues(values);
        loopStart.setValueIndex(options.playMeasuresInLoopStart);
        loopStart.setSummary(loopStart.getEntry() );
        loopStart.setDialogTitle(R.string.choose_start_measure);
        root.addPreference(loopStart);

        loopEnd = new ListPreference(this);
        loopEnd.setOnPreferenceChangeListener(this);
        loopEnd.setTitle(R.string.play_measures_in_loop_end);
        loopEnd.setEntries(values);
        loopEnd.setEntryValues(values);
        loopEnd.setValueIndex(options.playMeasuresInLoopEnd);
        loopEnd.setSummary(loopEnd.getEntry() );
        loopEnd.setDialogTitle(R.string.choose_end_measure);
        root.addPreference(loopEnd);
    }

    /** Create the "Restore Default Settings" preference */
    private void createRestoreDefaultPrefs(PreferenceScreen root) {
        restoreDefaults = new Preference(this);
        restoreDefaults.setTitle(R.string.restore_defaults);
        restoreDefaults.setOnPreferenceClickListener(this);
        root.addPreference(restoreDefaults);
    } 
    
    /** Create the "Unmute and Display all Tracks" preference */
    private void createRestoreAllTracks(PreferenceScreen root) {
    	setAllTracks = new Preference(this);
    	setAllTracks.setTitle(R.string.set_all_tracks);
    	setAllTracks.setOnPreferenceClickListener(this);
    	root.addPreference(setAllTracks);
    } 

    /** Update the MidiOptions based on the preferences selected. */
    private void updateOptions() {
        for (int i = 0; i < options.tracks.length; i++) {
            options.tracks[i] = selectTracks[i].isChecked();
        }
        for (int i = 0; i < options.mute.length; i++) {
            options.mute[i] = muteTracks[i].isChecked();
        }
        for (int i = 0; i < options.tracks.length; i++) {
            ListPreference entry = selectInstruments[i];
            options.instruments[i] = entry.findIndexOfValue(entry.getValue());
        }
        options.scrollVert = scrollVertically.isChecked();
        options.showPiano = showPiano.isChecked();
        options.showNoteColors = showNoteColors.isChecked();
        options.showLyrics = showLyrics.isChecked();
        options.delay = Integer.parseInt(delay.getValue());
        if (twoStaffs != null)
            options.twoStaffs = twoStaffs.isChecked();
        else
            options.twoStaffs = false;

        options.showNoteLetters = Integer.parseInt(showNoteLetters.getValue());
        options.transpose = Integer.parseInt(transpose.getValue());
        options.key = Integer.parseInt(key.getValue());
        if (time.getValue().equals("Default")) {
            options.time = null;
        }
        else if (time.getValue().equals("3/4")) {
            options.time = new TimeSignature(3, 4, options.defaultTime.getQuarter(),
                                             options.defaultTime.getTempo());
        }
        else if (time.getValue().equals("4/4")) {
            options.time = new TimeSignature(4, 4, options.defaultTime.getQuarter(),
                                             options.defaultTime.getTempo());
        }
        options.combineInterval = Integer.parseInt(combineInterval.getValue());
        options.shade1Color = shade1Color.getColor();
        options.shade2Color = shade2Color.getColor();
        options.showMeasures = showMeasures.isChecked();
        options.playMeasuresInLoop = playMeasuresInLoop.isChecked();
        options.playMeasuresInLoopStart = Integer.parseInt(loopStart.getValue()) - 1;
        options.playMeasuresInLoopEnd = Integer.parseInt(loopEnd.getValue()) - 1;
    }

    /** When the back button is pressed, update the MidiOptions.
     *  Return the updated options as the 'result' of this Activity.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        updateOptions();
        intent.putExtra(SettingsActivity.settingsID, options);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }
        
}


