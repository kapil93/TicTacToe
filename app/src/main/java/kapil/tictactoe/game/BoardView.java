package kapil.tictactoe.game;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import kapil.tictactoe.Constants;
import kapil.tictactoe.R;

/**
 *
 */

public class BoardView extends View implements GestureDetector.OnGestureListener {
    private static final int STROKE_WIDTH = 10;

    private float[] gridLinePoints;
    private Paint gridPaint;

    private PointF[][] centerPoints;
    private Paint signPaint;

    private List<SignData> signDataList;

    private @Constants.WinLinePosition int winLinePosition;
    private Paint winLinePaint;

    private GestureDetector clickDetector;
    private OnBoardClickListener onBoardClickListener;

    public BoardView(Context context) {
        super(context);
        init();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void init() {
        gridLinePoints = new float[16];

        centerPoints = new PointF[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                centerPoints[i][j] = new PointF();
            }
        }

        signDataList = new ArrayList<>();

        winLinePosition = Constants.NONE;

        gridPaint = new Paint();
        gridPaint.setColor(getContext().getResources().getColor(R.color.holo_green_dark, null));
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(dpToPx(STROKE_WIDTH));
        gridPaint.setStrokeCap(Paint.Cap.ROUND);

        signPaint = new Paint();
        signPaint.setColor(getContext().getResources().getColor(R.color.holo_orange_dark, null));
        signPaint.setAntiAlias(true);
        signPaint.setStyle(Paint.Style.STROKE);
        signPaint.setStrokeWidth(dpToPx(STROKE_WIDTH));
        signPaint.setStrokeCap(Paint.Cap.ROUND);

        winLinePaint = new Paint();
        winLinePaint.setColor(getContext().getResources().getColor(R.color.holo_red_dark, null));
        winLinePaint.setAntiAlias(true);
        winLinePaint.setStrokeWidth(dpToPx(STROKE_WIDTH));
        winLinePaint.setStrokeCap(Paint.Cap.ROUND);

        clickDetector = new GestureDetector(getContext(), this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        getLayoutParams().height = getMeasuredWidth();

        setGridLinePoints();
        setCenterPoints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawGrid(canvas);
        drawSigns(canvas);
        drawWinLine(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onBoardClickListener == null) {
            return super.onTouchEvent(event);
        } else {
            return clickDetector.onTouchEvent(event);
        }
    }

    private void setGridLinePoints() {
        int side = getMeasuredWidth();
        float padding = dpToPx(STROKE_WIDTH / 2f);

        gridLinePoints[0] = gridLinePoints[4] = gridLinePoints[9] = gridLinePoints[13] = padding;
        gridLinePoints[1] = gridLinePoints[3] = gridLinePoints[8] = gridLinePoints[10] = side / 3f;
        gridLinePoints[2] = gridLinePoints[6] = gridLinePoints[11] = gridLinePoints[15] = side - padding;
        gridLinePoints[5] = gridLinePoints[7] = gridLinePoints[12] = gridLinePoints[14] = (2 * side) / 3f;
    }

    private void setCenterPoints() {
        float a = getMeasuredWidth() / 6f;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                centerPoints[i][j].x = a + (j * (2 * a));
                centerPoints[i][j].y = a + (i * (2 * a));
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        canvas.drawLines(gridLinePoints, gridPaint);
    }

    private void drawSigns(Canvas canvas) {
        for (int i = 0; i < signDataList.size(); i++) {
            SignData signData = signDataList.get(i);

            switch (signData.getSign()) {
                case Constants.CIRCLE:
                    drawCircle(canvas, centerPoints[signData.getRow()][signData.getColumn()]);
                    break;
                case Constants.CROSS:
                    drawCross(canvas, centerPoints[signData.getRow()][signData.getColumn()]);
                    break;
                case Constants.EMPTY:
                    break;
            }
        }
    }

    private void drawCircle(Canvas canvas, PointF center) {
        canvas.drawCircle(center.x, center.y, (getMeasuredWidth() / 6f) - dpToPx(2 * STROKE_WIDTH), signPaint);
    }

    private void drawCross(Canvas canvas, PointF center) {
        float l = (getMeasuredWidth() / 6f) - dpToPx(2 * STROKE_WIDTH);

        canvas.drawLine(center.x - l, center.y - l, center.x + l, center.y + l, signPaint);
        canvas.drawLine(center.x - l, center.y + l, center.x + l, center.y - l, signPaint);
    }

    private void drawWinLine(Canvas canvas) {
        float a = getMeasuredWidth() / 6f;

        float padding = dpToPx(STROKE_WIDTH);

        switch (winLinePosition) {
            case Constants.NONE:
                break;
            case Constants.ROW_1:
                canvas.drawLine(padding, a, getMeasuredWidth() - padding, a, winLinePaint);
                break;
            case Constants.ROW_2:
                canvas.drawLine(padding, a + (2 * a), getMeasuredWidth() - padding, a + (2 * a), winLinePaint);
                break;
            case Constants.ROW_3:
                canvas.drawLine(padding, a + (4 * a), getMeasuredWidth() - padding, a + (4 * a), winLinePaint);
                break;
            case Constants.COLUMN_1:
                canvas.drawLine(a, padding, a, getMeasuredWidth() - padding, winLinePaint);
                break;
            case Constants.COLUMN_2:
                canvas.drawLine(a + (2 * a), padding, a + (2 * a), getMeasuredWidth() - padding, winLinePaint);
                break;
            case Constants.COLUMN_3:
                canvas.drawLine(a + (4 * a), padding, a + (4 * a), getMeasuredWidth() - padding, winLinePaint);
                break;
            case Constants.DIAGONAL_1:
                canvas.drawLine(padding, padding, getMeasuredWidth() - padding, getMeasuredWidth() - padding, winLinePaint);
                break;
            case Constants.DIAGONAL_2:
                canvas.drawLine(padding, getMeasuredWidth() - padding, getMeasuredWidth() - padding, padding, winLinePaint);
                break;
        }
    }

    void addSignToBoard(@Constants.Sign int sign, int row, int column) {
        SignData signData = new SignData();
        signData.setSign(sign);
        signData.setRow(row);
        signData.setColumn(column);

        signDataList.add(signData);

        invalidate();
    }

    void resetBoard() {
        signDataList.clear();
        winLinePosition = Constants.NONE;
        invalidate();
    }

    boolean isAlreadyClicked(int row, int column) {
        for (int i = 0; i < signDataList.size(); i++) {
            SignData signData = signDataList.get(i);

            if ((signData.getRow() == row) && (signData.getColumn() == column)) {
                return true;
            }
        }

        return false;
    }

    public void setWinLinePosition(@Constants.WinLinePosition int winLinePosition) {
        this.winLinePosition = winLinePosition;
        invalidate();
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        int row = detectIndexOfPartition(y);
        int column = detectIndexOfPartition(x);

        if ((row != -1) && (column != -1)) {
            onBoardClickListener.onBoardClick(BoardView.this, row, column);
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private int detectIndexOfPartition(float value) {
        float maxValue = getMeasuredWidth();
        float a = maxValue / 3;

        for (int i = 0; i < 3; i++) {
            if ((value >= (i * a)) && (value <= ((i + 1) * a))) {
                return i;
            }
        }

        return -1;
    }

    public void setOnBoardClickListener(OnBoardClickListener onBoardClickListener) {
        this.onBoardClickListener = onBoardClickListener;
    }

    interface OnBoardClickListener {
        void onBoardClick(BoardView board, int row, int column);
    }

    private class SignData {
        private @Constants.Sign int sign;
        private int row;
        private int column;

        @Constants.Sign int getSign() {
            return sign;
        }

        void setSign(@Constants.Sign int sign) {
            this.sign = sign;
        }

        int getRow() {
            return row;
        }

        void setRow(int row) {
            this.row = row;
        }

        int getColumn() {
            return column;
        }

        void setColumn(int column) {
            this.column = column;
        }
    }
}
