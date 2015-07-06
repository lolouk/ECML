package com.game;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.ecml.MidiNote;
import com.ecml.MidiTrack;
import com.ecml.R;
import com.ecml.SheetMusic;

/**
 * @class ReadingGameBeginnerActivity 
 *        
 * @author Anaïs & Nicolas
 */

public class ReadingGameBeginner extends ReadingGame {

	private TextView textView;					//Display which note must be played and the score
	private int compteurTexte = counter + 1;	//Number of the note that must be played
	private ColorDrawable orangeColor;			//Background color of the choice of note buttons
	private int numberPoints = 0;				//Number of points
	private int firstTry = 0;					//Incremented each time the player try to find a note
	private int numberNote = 30;				//Number of notes in the sheet music
	private Double currentPulseTime = 0.0;		
	private Double prevPulseTime = 0.0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		counter = 0; // note counter
		tracks = midifile.getTracks();
		// We use by default the instrument n°0 which is the piano
		notes = findNotes(tracks, 0);

		//Display textView
		displayText();
		//Create the buttons to choose the right note
		createButtons();

	}

	private void displayText() {
		textView = (TextView) findViewById(R.id.affiche);
		textView.setText("Choose which one is the note number " + compteurTexte + "         " + "Score : " + numberPoints + "/" + numberNote);
	}

	// Log.i("color", "" + SheetMusic.NoteColor(2));
	// SheetMusic.NoteColors[0] = Color.GREEN;

	private void createButtons() {
		// All the buttons
		final Button la = (Button) findViewById(R.id.la);			//A button
		final Button lad = (Button) findViewById(R.id.lad); 		//A sharp button
		final Button si = (Button) findViewById(R.id.si);			//B button
		final Button donote = (Button) findViewById(R.id.donote);	//C button
		final Button dod = (Button) findViewById(R.id.dod);			//C sharp button
		final Button re = (Button) findViewById(R.id.re);			//D button
		final Button red = (Button) findViewById(R.id.red);			//D sharp button
		final Button mi = (Button) findViewById(R.id.mi);			//E button
		final Button fa = (Button) findViewById(R.id.fa);			//F button
		final Button fad = (Button) findViewById(R.id.fad);			//F sharp button
		final Button sol = (Button) findViewById(R.id.sol);			//G button
		final Button sold = (Button) findViewById(R.id.sold);		//G sharp button

		orangeColor = (ColorDrawable) la.getBackground();

		//Switch button to change the note notation from english to classic or reverse
		Switch notation = (Switch) findViewById(R.id.notation);
		notation.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					la.setText("A");
					lad.setText("A#/B\u266D");
					si.setText("B");
					donote.setText("C");
					dod.setText("C#/D\u266D");
					re.setText("D");
					red.setText("D#/E\u266D");
					mi.setText("E");
					fa.setText("F");
					fad.setText("F#/G\u266D");
					sol.setText("G");
					sold.setText("G#/A\u266D");
				} else {
					la.setText("la");
					lad.setText("la#/si\u266D");
					si.setText("si");
					donote.setText("do");
					dod.setText("do#/re\u266D");
					re.setText("re");
					red.setText("re#/mi\u266D");
					mi.setText("mi");
					fa.setText("fa");
					fad.setText("fa#/sol\u266D");
					sol.setText("sol");
					sold.setText("sol#/la\u266D");
				}

			}
		});

		// check the current state before we display the screen
		if (notation.isChecked()) {
			la.setText("A");
			lad.setText("A#/B\u266D");
			si.setText("B");
			donote.setText("C");
			dod.setText("C#/D\u266D");
			re.setText("D");
			red.setText("D#/E\u266D");
			mi.setText("E");
			fa.setText("F");
			fad.setText("F#/G\u266D");
			sol.setText("G");
			sold.setText("G#/A\u266D");
		} else {
			la.setText("la");
			lad.setText("la#/si\u266D");
			si.setText("si");
			donote.setText("do");
			dod.setText("do#/re\u266D");
			re.setText("re");
			red.setText("re#/mi\u266D");
			mi.setText("mi");
			fa.setText("fa");
			fad.setText("fa#/sol\u266D");
			sol.setText("sol");
			sold.setText("sol#/la\u266D");
		}


		// La button
		la.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				testNote("A", la);
			}
		});

		// La sharp button
		lad.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("A#", lad);
			}
		});

		// Si button
		si.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("B", si);
			}
		});

		// Do button
		donote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("C", donote);
			}
		});

		// Do sharp button
		dod.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("C#", dod);
			}
		});

		// Ré button
		re.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("D", re);
			}
		});

		// Ré sharp button
		red.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("D#", red);
			}
		});

		// Mi button
		mi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("E", mi);
			}
		});

		// Fa button
		fa.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("F", fa);
			}
		});

		// Fa sharp button
		fad.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("F#", fad);
			}
		});

		// Sol button
		sol.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("G", sol);
			}
		});

		// Sol sharp button
		sold.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				testNote("G#", sold);
			}
		});

	}

	//Test if the note chosen if the right one
	private void testNote(String letter, Button btn) {
		firstTry = firstTry + 1;
		String test = notes.get(counter).pitch();
		// Check if it is the expected note
		if (letter.equals(test)) {
			if (firstTry == 1) {
				//If the note is right and it is the first try, then the player has one more point
				numberPoints = numberPoints + 1;
			}
			//the button becomes green and then go back to orange
			GreenToOrange(btn);
			firstTry = 0;
			//Log.i("score", "" + numberPoints);
			//The player goes to the next note
			advanceOneNote();
			counter++;
			compteurTexte++;
			textView.setText("Choose which one is the note number " + compteurTexte + "         " + "Score : " + numberPoints + "/" + numberNote);
			checkEnd();
		} else {
			//if the note is not the good one, the button becomes red and then go back to orange
			redToOrange(btn);
		}
	}

	// If we reach the end of the midifile, then we stop the player
	private void checkEnd() {
		TextView percentage = (TextView) findViewById(R.id.percentage);
		TextView appreciation = (TextView) findViewById(R.id.appreciation);
		ImageView star = (ImageView) findViewById(R.id.star);
		TextView score = (TextView) findViewById(R.id.score);
		TextView next = (TextView) findViewById(R.id.nextLevel);

		if (counter == numberNote) {
			//Display the result view
			ReadingGame.choice.setVisibility(View.GONE);
			ReadingGame.result.setVisibility(View.VISIBLE);
			double percentageScore = numberPoints * 100 / numberNote;
			percentage.setText(String.valueOf(percentageScore) + "%");
			//If the percentage of right answer is superior to 90%, then the player can go to the next level
			if (percentageScore >= 90) {
				percentage.setTextColor(Color.GREEN);
				appreciation.setText("Congratulations!");
				if (ReadingGame.level <= 2) {
					ReadingGame.level = ReadingGame.level + 1;
					next.setText("Go to the next level");
					next.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getApplicationContext(), ReadingGameBeginner.class);
							startActivity(intent);
						}
					});
				} else {
					//if the player was already at the 2nd level, he finishes the game
					next.setText("You finished the game!");
				}

			} else {
				percentage.setTextColor(Color.RED);
				appreciation.setText("Try Again!");
				star.setVisibility(View.GONE);
				next.setText("Click here to try again");
				next.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(), ReadingGameBeginner.class);
						startActivity(intent);
					}
				});
			}
			score.setText(numberPoints + "/" + numberNote);

			if (player != null) {
				player.Stop();
			}

		}
	}

	public void advanceOneNote() {
		if (midifile == null || sheet == null) {
			return;
		}
		/* Remove any highlighted notes */
		sheet.ShadeNotes(-10, player.getCurrentPulseTime().intValue(), SheetMusic.DontScroll);
		prevPulseTime = currentPulseTime;
//		currentPulseTime += notes.get(counter).getDuration();
	    currentPulseTime = 0.0 + notes.get(counter).getStartTime() + notes.get(counter).getDuration();
		if (currentPulseTime <= midifile.getTotalPulses()) {
			player.setCurrentPulseTime(currentPulseTime);
		}
		player.setCurrentPulseTime(currentPulseTime);
		player.setPrevPulseTime(prevPulseTime);
		reshadeNotes();
	}

	//Highlight the current note
	public void reshadeNotes() {
		sheet.ShadeNotes(currentPulseTime.intValue(), prevPulseTime.intValue(), SheetMusic.ImmediateScroll);
	}

	//Found the notes and put them in an array
	private ArrayList<MidiNote> findNotes(ArrayList<MidiTrack> tracks, int instrument) {

		int i = 0;
		search = true;
		while (search) {
			if (instrument == tracks.get(i).getInstrument()) {
				search = false;
			} else {
				i++;
			}
		}
		return tracks.get(i).getNotes();
	}

	private void redToOrange(Button btn) {
		// Let's change background's color from red to initial orange color.
		ColorDrawable[] color = { new ColorDrawable(Color.RED), orangeColor };
		TransitionDrawable trans = new TransitionDrawable(color);
		// This will work also on old devices. The latest API says you have to
		// use setBackground instead.
		btn.setBackgroundDrawable(trans);
		trans.startTransition(300);
	}

	private void GreenToOrange(Button btn) {
		// Let's change background's color from green to initial orange color.
		ColorDrawable[] color = { new ColorDrawable(Color.GREEN), orangeColor };
		TransitionDrawable trans = new TransitionDrawable(color);
		// This will work also on old devices. The latest API says you have to
		// use setBackground instead.
		btn.setBackgroundDrawable(trans);
		trans.startTransition(300);
	}

}
