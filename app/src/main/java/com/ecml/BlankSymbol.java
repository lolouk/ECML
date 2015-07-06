/*
 * Copyright (c) 2007-2011 Madhav Vaidyanathan
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

import android.graphics.Canvas;
import android.graphics.Paint;


/** @class BlankSymbol
 * <br>
 * The Blank symbol is a music symbol that doesn't draw anything.  This
 * symbol is used for alignment purposes, to align notes in different 
 * staffs which occur at the same time.
 */
public class BlankSymbol implements MusicSymbol {
    private int starttime;	/* The beginning of the new measure */
    private int width; 		/* The width (in pixels) of this symbol */

    /** Create a new BlankSymbol with the given starttime and width */
    public BlankSymbol(int starttime, int width) {
        this.starttime = starttime;
        this.width = width;
    }

    /** Get the time (in pulses) this symbol occurs at.
     * This is used to determine the measure this symbol belongs to.
     */
    public int getStartTime() { return starttime; }

    /** Get the minimum width (in pixels) needed to draw this symbol */
    public int getMinWidth() { return 0; }

    /** Get the width (in pixels) of this symbol. The width is set
     * in SheetMusic.AlignSymbols() to vertically align symbols.
     */
    public int getWidth() { return width; }
    /** Set the width (in pixels) of this symbol. The width is set
     * in SheetMusic.AlignSymbols() to vertically align symbols.
     */
    public void setWidth(int value) { width = value; }

    /** Get the number of pixels this symbol extends above the staff. Used
     *  to determine the minimum height needed for the staff (Staff.FindBounds).
     */
    public int getAboveStaff() { return 0; }

    /** Get the number of pixels this symbol extends below the staff. Used
     *  to determine the minimum height needed for the staff (Staff.FindBounds).
     */
    public int getBelowStaff() { return 0; }

    /** Draw nothing.
     * @param ytop The ylocation (in pixels) where the top of the staff starts.
     */
    public void Draw(Canvas canvas, Paint paint, int ytop) {}

    public String toString() {
        return String.format("BlankSymbol starttime=%1$s width=%2$s", 
                             starttime, width);
    }
}


