package com.game;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class StaffView extends View {
	 // TODO: the following as result of measuring.
    private static int staffHeight = 300;
    private static int lineDistance = staffHeight / 7;

    // The note name is essentially encoding the 8 positions on the staff, with an additional
    // character describing if this is sharp or flat. For the 'flat' version, we encode
    // 9 positions on the staff as it is essentially the first note of the next octave.
    // The letters are purely encoding these positions and are choosen as letters for easier
    // debug output, but they don't have any other meaning otherwise.
    private static final String noteNames[][] = {
        { "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Hb" /* H = G + 1 */},
        { "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#" },
    };

    public StaffView(Context context) {
        this(context, null);
    }

    public StaffView(Context context, AttributeSet attributes) {
        super(context, attributes);
        staffPaint = new Paint();
        staffPaint.setColor(Color.BLACK);
        staffPaint.setStrokeWidth(staffHeight / (5 * 10));  // 10% between lines

        backgroundColor = new Paint();
        backgroundColor.setColor(Color.WHITE);
        currentNote = 10;

        noteBody = new NoteRenderer(lineDistance);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We want to fill everything.
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (measureHeight > 3 * staffHeight)
            measureHeight = 3 * staffHeight;
        setMeasuredDimension(measureWidth, measureHeight);
    }

    public void setKeyDisplay(int k) {
        if (k != keyDisplay) {
            keyDisplay = k;
            invalidate();
        }
    }
    // Push a note, 0 being 55Hz A, -1 invalid.
    public void pushNote(int note) {
        if (note != currentNote) {
            currentNote = note;  // TODO: keep short history.
            invalidate();
        }
    }

    private int getNotePosition() {
        final int octave = currentNote / 12;
        final String notename = noteNames[keyDisplay][currentNote % 12];
        final int position = (notename.charAt(0) - 'A') + 7 * octave;
        return position - 6;  // relative to lowest line.
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(backgroundColor);
        final int origin = (canvas.getHeight() - 4 * lineDistance) / 2
                + lineDistance;  // need more space at the top.
        for (int i = 0; i < 5; ++i) {
            final int posY = origin + i * lineDistance;
            canvas.drawLine(0, posY, canvas.getWidth(), posY, staffPaint);
        }
        if (currentNote < 0)
            return;
        final int notePos = getNotePosition();
        final int centerY = origin + 4 * lineDistance
                - (notePos * lineDistance/2);
        final int centerX = 2 * canvas.getWidth() / 3;
        final float barLength = 3.2f * lineDistance;
        final String noteName = noteNames[keyDisplay][currentNote % 12];
        final float noteOffset
                = noteBody.draw(canvas, centerX, centerY,
                                noteName, notePos < 4 ? barLength : -barLength);

        final float helpLeft = centerX - 1.8f * noteOffset;
        final float helpRight = centerX + 1.8f * noteOffset;
        for (int i = notePos / 2; i < 0; ++i) {
            canvas.drawLine(helpLeft, origin + 4 * lineDistance - i * lineDistance,
                            helpRight, origin + 4 * lineDistance - i * lineDistance,
                            staffPaint);
        }
        for (int i = 4; i <= notePos / 2; ++i) {
            canvas.drawLine(helpLeft, origin + 4 * lineDistance - i * lineDistance,
                            helpRight, origin + 4 * lineDistance - i * lineDistance,
                            staffPaint);
        }
    }

    private static class NoteRenderer {
        public NoteRenderer(float height) {
            notePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            notePaint.setColor(Color.BLACK);
            notePaint.setStrokeWidth(0);
            notePaint.setStyle(Paint.Style.FILL);
            notePaint.setTextSize(1.8f * lineDistance);

            barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            barPaint.setColor(Color.BLACK);
            barPaint.setStyle(Paint.Style.STROKE);
            barPaint.setStrokeWidth(staffHeight / 70);

            // Unscientific attempt to make it look pleasing.
            float ovalWidth = 1.4f * height;
            float ovalHeight = 0.95f * height;
            float angle = -30.0f;
            noteOffsetX = (0.85f * ovalWidth) / 2.0f;
            noteOffsetY = (0.3f * ovalHeight) / 2.0f;

            Bitmap ovalTemplate = Bitmap.createBitmap((int)ovalWidth,
                                                      (int)ovalWidth,
                                                      Bitmap.Config.ALPHA_8);
            Canvas c = new Canvas(ovalTemplate);

            Matrix tiltMatrix = new Matrix();
            tiltMatrix.postRotate(angle, ovalTemplate.getWidth()/2.0f,
                                  ovalTemplate.getHeight()/2.0f);
            c.setMatrix(tiltMatrix);
            float offsetY = (ovalTemplate.getHeight() - ovalHeight)/2.0f;
            RectF r = new RectF(0, offsetY, ovalWidth, ovalHeight + offsetY);
            c.drawOval(r, notePaint);
            noteBitmap = ovalTemplate;
        }

        // Draw note body into canas, centered around "centerX" and "centerY".
        // The length of the bar to drawl is given in "barLength";
        // pointing upwards if positive, downwards if negative.
        public float draw(Canvas c, float centerX, float centerY,
                         String notename, float barLength) {
            final float noteLeft = centerX - noteOffsetX;
            final float noteRight = centerX + noteOffsetX;
            c.drawBitmap(noteBitmap,
                         centerX - 0.5f * noteBitmap.getWidth(),
                         centerY - 0.5f * noteBitmap.getHeight(),
                         notePaint);
            if (barLength > 0) {
                c.drawLine(noteRight, centerY - noteOffsetY,
                           noteRight, centerY - barLength, barPaint);
            } else {
                c.drawLine(noteLeft, centerY + noteOffsetY,
                           noteLeft, centerY - barLength, barPaint);
            }
            if (notename.length() > 1) {
                float accidentalOffsetY = 0.0f;
                String accidental = "";
                switch (notename.charAt(1)) {
                    case '#':
                        accidental = "♯";
                        accidentalOffsetY = 0.5f * noteBitmap.getHeight();
                        break;
                    case 'b':
                        accidental = "♭";
                        accidentalOffsetY = 0.3f * noteBitmap.getHeight();
                        break;
                }
                c.drawText(accidental,
                           centerX - 4.0f * noteOffsetX,
                           centerY + accidentalOffsetY,
                           notePaint);
            }
            return noteOffsetX;
        }

        private final Paint notePaint;
        private final Paint barPaint;
        private final Bitmap noteBitmap;
        private final float noteOffsetX;
        private final float noteOffsetY;
    }

    private final NoteRenderer noteBody;
    private final Paint staffPaint;
    private final Paint backgroundColor;

    private int keyDisplay;
    private int currentNote;
}
