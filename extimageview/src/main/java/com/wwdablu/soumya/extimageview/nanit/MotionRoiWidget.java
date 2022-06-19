package com.wwdablu.soumya.extimageview.nanit;

import static android.graphics.Color.parseColor;
import static java.lang.Math.abs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class MotionRoiWidget extends View implements View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private static final int ROI_COLOR = parseColor("#5fe4c2");

    private static final float LINE_WIDTH = 3f;
    private static final int INNER_ALPHA_LEVEL = 57;

    private static float ROI_HANDLE_RADIUS;
    private static float ROI_MARGINS;
    private static final int HANDLE_ACTIVE_RADIUS = 110;
    private final Paint paint = new Paint();

    private boolean shouldDraw;

    private PointF start;
    private PointF end;

    private MotionRoiCoords initCoords;

    private Listener listener;

    private float prevTouchX = Float.MAX_VALUE;
    private float prevTouchY = Float.MAX_VALUE;
    private GestureDetector gestureDetector;

    public MotionRoiWidget(Context context) {
        super(context);
        init();
    }

    public MotionRoiWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MotionRoiWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private static float forceMargins(float coord, float maxAxis) {
        if (coord < ROI_MARGINS) {
            coord = ROI_MARGINS;
        }
        if (coord > maxAxis + ROI_MARGINS) {
            coord = maxAxis + ROI_MARGINS;
        }
        return coord;
    }

    private static MotionRoiCoords deductMarginTransform(float startX, float startY, float endX, float endY) {
        startX -= ROI_MARGINS;
        startY -= ROI_MARGINS;
        endX -= ROI_MARGINS;
        endY -= ROI_MARGINS;
        return new MotionRoiCoords(startX, startY, endX, endY);
    }

    private static boolean isAdjacent(float c1, float c2) {
        return abs(c1 - c2) < HANDLE_ACTIVE_RADIUS;
    }

    private boolean shouldStartMovement(MotionEvent event) {
        boolean result = false;

        Handle selHandle = getSelectedHandle(event.getX(), event.getY());

        if (selHandle != null) {
            result = true; // inside ROI
        }

        return result;
    }

    private void move(MotionEvent event) {
        float dx = event.getX() - prevTouchX;
        float dy = event.getY() - prevTouchY;

        prevTouchX = event.getX();
        prevTouchY = event.getY();

        Handle selHandle = getSelectedHandle(event.getX(), event.getY());

        if (selHandle != null) {
            updateRoiCoords(selHandle, dx, dy);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (shouldStartMovement(e2)) {
            move(e2);
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        prevTouchX = e.getX();
        prevTouchY = e.getY();

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        listener.onDoubleTap();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public void unregisterListener() {
        this.listener = null;
    }

    public void setInitialDimensions(final MotionRoiCoords coords) {
        this.initCoords = coords;
        this.invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        drawInitialCoords();
    }

    public void drawInitialCoords() {
        if (initCoords == null) {
            return;
        }
        PointF[] postTrans = applyMarginTransform(initCoords);
        start = postTrans[0];
        end = postTrans[1];

        refreshDisplay();
        initCoords = null;
    }

    private PointF[] applyMarginTransform(MotionRoiCoords coords) {
        int width = getWidth();
        int height = getHeight();
        return applyMarginTransformWithDim(coords, width, height);
    }

    private PointF[] applyMarginTransformWithDim(MotionRoiCoords coords, int width, int height) {
        float startx = (float) coords.x0;
        float starty = (float) coords.y0;
        float endx = (float) coords.x1;
        float endy = (float) coords.y1;

        // adjust to margins
        startx += ROI_MARGINS;
        starty += ROI_MARGINS;
        endx += ROI_MARGINS;
        endy += ROI_MARGINS;

        PointF _start = new PointF(startx, starty);
        PointF _end = new PointF(endx, endy);

        float widthWithMargins = width - 2 * ROI_MARGINS;
        float heightWithMargins = height - 2 * ROI_MARGINS;

        _start.x = forceMargins(_start.x, widthWithMargins);
        _start.y = forceMargins(_start.y, heightWithMargins);
        _end.x = forceMargins(_end.x, widthWithMargins);
        _end.y = forceMargins(_end.y, heightWithMargins);

        return new PointF[]{_start, _end};
    }

    private void init() {
        paint.setStrokeWidth(LINE_WIDTH);
        paint.setColor(ROI_COLOR);
        paint.setStyle(Paint.Style.FILL);
        this.setOnTouchListener(this);
        gestureDetector = new GestureDetector(getContext(), this);
        gestureDetector.setOnDoubleTapListener(this);

        ROI_HANDLE_RADIUS = GeneralUtilities.dpsToPixels(getContext(), 7f);
        ROI_MARGINS = (float) (1.5 * ROI_HANDLE_RADIUS);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!shouldDraw || start == null || end == null) {
            return;
        }

        // edges
        canvas.drawLine(start.x, start.y, start.x, end.y, paint);
        canvas.drawLine(end.x, start.y, end.x, end.y, paint);
        canvas.drawLine(start.x, start.y, end.x, start.y, paint);
        canvas.drawLine(start.x, end.y, end.x, end.y, paint);

        // handles
        canvas.drawCircle(start.x, start.y, ROI_HANDLE_RADIUS, paint);
        canvas.drawCircle(start.x, end.y, ROI_HANDLE_RADIUS, paint);
        canvas.drawCircle(end.x, start.y, ROI_HANDLE_RADIUS, paint);
        canvas.drawCircle(end.x, end.y, ROI_HANDLE_RADIUS, paint);

        // inner area
        int origAlpha = paint.getAlpha();
        paint.setAlpha(INNER_ALPHA_LEVEL);
        try {
            canvas.drawRect(start.x, start.y, end.x, end.y, paint);
        } finally {
            paint.setAlpha(origAlpha);
        }
    }

    public void showRoi() {
        // called when entering live stream mode
        shouldDraw = true;
        refreshDisplay();
    }

    public MotionRoiCoords getMotionRoiCoords() {
        return deductMarginTransform(start.x, start.y, end.x, end.y);
    }

    private void updateRoiCoords(Handle selHandle, double dx, double dy) {
        float startx = start.x;
        float starty = start.y;
        float endx = end.x;
        float endy = end.y;

        switch (selHandle) {
            case TopLeft:
                startx += dx;
                starty += dy;
                break;
            case BottomLeft:
                startx += dx;
                endy += dy;
                break;
            case TopRight:
                endx += dx;
                starty += dy;
                break;
            case BottomRight:
                endx += dx;
                endy += dy;
                break;
            case InsideArea:
                startx += dx;
                starty += dy;
                endx += dx;
                endy += dy;
                break;
        }

        if (isValidCoords(startx, starty, endx, endy)) {
            start.x = startx;
            start.y = starty;
            end.x = endx;
            end.y = endy;
            refreshDisplay();
        }
    }

    public void refreshDisplay() {
        this.invalidate();
    }

    private boolean isValidCoords(double startX, double startY, double endX, double endY) {
        boolean result = false;

        // stay within crib limits
        double maxWidth = getWidth() - ROI_MARGINS;
        double maxHeight = getHeight() - ROI_MARGINS;

        if (startX >= ROI_MARGINS && startY >= ROI_MARGINS && endX <= maxWidth && endY <= maxHeight
                && endX - startX >= 0.1 * maxWidth && endY - startY >= 0.1 * maxHeight) {
            result = true;
        }

        return result;
    }

    @Nullable
    private Handle getSelectedHandle(float touchX, float touchY) {

        if (isAdjacent(touchX, start.x) && isAdjacent(touchY, start.y)) {
            return Handle.TopLeft;
        }
        if (isAdjacent(touchX, start.x) && isAdjacent(touchY, end.y)) {
            return Handle.BottomLeft;
        }
        if (isAdjacent(touchX, end.x) && isAdjacent(touchY, start.y)) {
            return Handle.TopRight;
        }
        if (isAdjacent(touchX, end.x) && isAdjacent(touchY, end.y)) {
            return Handle.BottomRight;
        }
        if (start.x < touchX && touchX < end.x && start.y < touchY && touchY < end.y) {
            return Handle.InsideArea;
        }
        return null;
    }

    private enum Handle {TopLeft, BottomLeft, TopRight, BottomRight, InsideArea}

    public interface Listener {
        void onDoubleTap();
    }

}
