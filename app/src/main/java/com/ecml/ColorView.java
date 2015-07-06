/*
 * Copyright (c) 2012 Madhav Vaidyanathan
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.view.MotionEvent;
import android.view.View;

/** @class ColorView
 * <br>
 *  Display a circle showing various colors to choose from.
 *  On the top left corner, display a preview of the selected color.
 */
public class ColorView extends View {
	private Paint[] colorRings; /* Rings of color to display */
	private Paint colorPreview; /* Small circle showing preview of color */
	private int selectedColor; 	/* Currently selected color */
	private int center; 		/* The center of the circle */
	private int circleRadius; 	/* The radius of the circle */
	private static final float PI = 3.1415926f; /* Approximation of Pi */

	/** Create a color view
	 * 
	 * @param context The context
	 * @param color The current color used for shading notes
	 */
	public ColorView(Context context, int color) {
		super(context);
		center = 100;
		circleRadius = 90;
		selectedColor = color;
	}

	/** Get the current selected color */ 
	public int getSelectedColor() {
		return selectedColor;
	}

	/** Return the color wheel colors, for the given percent.
	 * Percent is from 0.0 to 1.0, from center to outer-rim.
	 * 0.0 is white
	 * 1.0 is the brightest main color (pure red, pure green, etc)
	 */
	private int[] colorsForRing(float percent) {
		if (percent < 0)
			percent = 0;
		if (percent > 1)
			percent = 1;

		percent = 1 - percent;
		int[] colors = new int[7];
		colors[0] = Color.rgb(255, (int) (255 * percent), (int) (255 * percent));
		colors[1] = Color.rgb(255, (int) (255 * percent), 255);
		colors[2] = Color.rgb((int) (255 * percent), (int) (255 * percent), 255);
		colors[3] = Color.rgb((int) (255 * percent), 255, 255);
		colors[4] = Color.rgb((int) (255 * percent), 255, (int) (255 * percent));
		colors[5] = Color.rgb(255, 255, (int) (255 * percent));
		colors[6] = Color.rgb(255, (int) (255 * percent), (int) (255 * percent));
		return colors;
	}

	/** Create the color wheel.
	 * Create 64 color rings, where each rings displays a rainbow gradient.
	 */
	private void initColorRings() {
		colorRings = new Paint[64];
		for (int i = 0; i < 64; i++) {
			colorRings[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
			Shader s = new SweepGradient(0, 0, colorsForRing(i / 64.0f), null);
			colorRings[i].setShader(s);
			colorRings[i].setStyle(Paint.Style.STROKE);
			colorRings[i].setStrokeWidth(circleRadius / 64.0f + 0.5f);
		}
		colorPreview = new Paint(Paint.ANTI_ALIAS_FLAG);
		colorPreview.setColor(selectedColor);
	}


	/** Draw a preview of the selected color in the top-left corner.
	 * Draw the full color circle, by drawing concentric ovals
	 * with increasing radius, using the colorRing gradients.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRoundRect(new RectF(center / 10, center / 10, center / 4, center / 4), 5, 5, colorPreview);

		canvas.translate(center, center);

		for (int i = 1; i < colorRings.length; i++) {
			float radius = circleRadius * i * 1.0f / (colorRings.length - 1);
			canvas.drawOval(new RectF(-radius, -radius, radius, radius), colorRings[i]);
		}
	}


	/** Set the circle's center, based on the available width/height */
	@Override
	protected void onMeasure(int widthspec, int heightspec) {
		int specwidth = MeasureSpec.getSize(widthspec);
		int specheight = MeasureSpec.getSize(heightspec);

		center = specwidth / 2;
		if (specheight > 0 && specheight < specwidth) {
			center = specheight / 2;
		}
		if (center <= 0) {
			center = 100;
		}
		circleRadius = center - 10;
		setMeasuredDimension(center * 2, center * 2);
		initColorRings();
	}

	/** Return the average of the two colors, using the given percent
	 * 
	 * @param color1
	 * @param color2
	 * @param percent
	 */
	private int average(int color1, int color2, float percent) {
		return color1 + java.lang.Math.round(percent * (color2 - color1));
	}

	/** Given the radius and angle (from 0 to 1) determine the color selected.
	 * 
	 * @param radius
	 * @param angleUnit
	 */
	private int calculateColor(float radius, float angleUnit) {
		int[] colors = colorsForRing(radius / circleRadius);
		if (angleUnit <= 0) {
			return colors[0];
		}
		if (angleUnit >= 1) {
			return colors[colors.length - 1];
		}

		float p = angleUnit * (colors.length - 1);
		int i = (int) p;
		p -= i;

		// now p is just the fractional part [0...1) and i is the index
		int c0 = colors[i];
		int c1 = colors[i + 1];
		int a = average(Color.alpha(c0), Color.alpha(c1), p);
		int r = average(Color.red(c0), Color.red(c1), p);
		int g = average(Color.green(c0), Color.green(c1), p);
		int b = average(Color.blue(c0), Color.blue(c1), p);

		return Color.argb(a, r, g, b);
	}



	/** When the user clicks on the color wheel, update
	 * the selected color, and the preview pane.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX() - center;
		float y = event.getY() - center;
		float radius = (float) Math.sqrt(x * x + y * y);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			if (radius > circleRadius) {
				break;
			}
			float angle = (float) java.lang.Math.atan2(y, x);
			// need to turn angle [-PI ... PI] into unit [0....1]
			float angleUnit = angle / (2 * PI);
			if (angleUnit < 0) {
				angleUnit += 1;
			}
			// Update the selected color after calculation
			selectedColor = calculateColor(radius, angleUnit);
			// Display the selected color
			colorPreview.setColor(selectedColor);
			invalidate();
			break;
		}
		return true;
	}
}