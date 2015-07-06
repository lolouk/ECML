
package com.ecml;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.preference.Preference;
import android.view.View;

/**
 * @class ColorPreference
 * <br>
 *  The ColorPreference is used in a PreferenceScreen to let
 *  the user choose a color for an option.
 * <br>
 *  This Preference displays text, plus an additional color box
 *  and a Cancel, an Off and an OK Button
 */

public class ColorPreference extends Preference {

	private View colorview; 		/* The view displaying the selected color */
	private int color; 				/* The selected color */
	private Context context;		/* The context */
	private AlertDialog dialog;		/* The Dialog to be used (allows the use of negative, neutral and positive buttons) */
	private ColorView colorView;	/* The colorView that allows us to get the selected Color */


	/** Create a ColorPreference
	 * 
	 * @param context The context
	 */
	public ColorPreference(Context context) {
		super(context);
		this.context = context;
		setWidgetLayoutResource(R.layout.color_preference);
	}

	/** Set the color that will be chosen after validating.<br>
	 * This color will also be displayed in the top left corner
	 * of the Alert Dialog
	 *  
	 * @param color The color chosen
	 */
	public void setColor(int color) {
		this.color = color;
		if (colorview != null) {
			colorview.setBackgroundColor(color);
		}
	}

	/** Get the current color.<br>
	 * This color is normally the one displayed in the top left hand corner
	 * of the Alert Dialog */
	public int getColor() {
		return color;
	}


	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		colorview = (View) view.findViewById(R.id.color_preference_widget);
		if (color != 0) {
			colorview.setBackgroundColor(color);
		}
	}

	/** When clicked, display the color picker dialog */
	protected void onClick() {

		// Create a builder to create the alert dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Pick a Color");

		// Set the cancel button
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface builder, int whichButton) {
			}
		});

		// Set the Off button (it isn't really off, it is just unseen because it is white)
		builder.setNeutralButton("Off", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface builder, int which) {
				color = Color.WHITE; // Do NOT use Color.TRANSPARENT because it does NOT work right
				colorview.setBackgroundColor(color);
			}
		});

		// Set the OK button to validate the choice
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface builder, int whichButton) {
				color = colorView.getSelectedColor();
				colorview.setBackgroundColor(color);
			}
		});

		// Create the color circle where the user can select the color desired
		colorView = new ColorView(getContext(), color);
		builder.setView(colorView);

		// Actually create the Alert Dialog
		dialog = builder.create();
		dialog.show();
	}
}
