package kapil.tictactoe.game;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import kapil.tictactoe.Constants;
import kapil.tictactoe.R;

/**
 *
 */

public class BoardView extends View implements GestureDetector.OnGestureListener, ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    private static final int STROKE_WIDTH = 10;
    private static final int SWEEPER_WIDTH = 20;

    private float[] gridLinePoints;
    private Paint gridPaint;

    private PointF[][] centerPoints;
    private Paint signPaint;

    private List<SignData> signDataList;

    private @Constants.WinLinePosition int winLinePosition;
    private Paint winLinePaint;

    private GestureDetector clickDetector;
    private OnBoardInteractionListener onBoardInteractionListener;

    private ValueAnimator clickAnimator;
    private ValueAnimator winLineAnimator;
    private ValueAnimator resetAnimator;

    private float signRadius;
    private float winLineLength;
    private float sweeperStartPosition;

    private Paint sweeperPaint;
    private int[] sweeperColors;
    private float[] sweeperStops;

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

        clickAnimator = new ValueAnimator();
        clickAnimator.setDuration(150);
        clickAnimator.setInterpolator(new DecelerateInterpolator());
        clickAnimator.addUpdateListener(this);
        clickAnimator.addListener(this);

        winLineAnimator = new ValueAnimator();
        winLineAnimator.setDuration(150);
        winLineAnimator.setInterpolator(new DecelerateInterpolator());
        winLineAnimator.addUpdateListener(this);
        winLineAnimator.addListener(this);

        resetAnimator = new ValueAnimator();
        resetAnimator.setDuration(1000);
        resetAnimator.setInterpolator(new AccelerateInterpolator());
        resetAnimator.addUpdateListener(this);
        resetAnimator.addListener(this);

        sweeperPaint = new Paint();
        sweeperPaint.setAntiAlias(true);
        sweeperPaint.setStyle(Paint.Style.FILL);

        sweeperColors = new int[3];
        sweeperColors[0] = Color.parseColor("#0000DDFF");
        sweeperColors[1] = Color.parseColor("#FF00DDFF");
        sweeperColors[2] = Color.parseColor("#0000DDFF");

        sweeperStops = new float[3];
        sweeperStops[0] = 0;
        sweeperStops[1] = 0.5f;
        sweeperStops[2] = 1;

        setLayerType(LAYER_TYPE_SOFTWARE, sweeperPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        getLayoutParams().height = getMeasuredWidth();

        setGridLinePoints();
        setCenterPoints();
        setAnimationValues();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawGrid(canvas);
        super.onDraw(canvas);

        if (resetAnimator.isRunning()) {
            canvas.clipRect(0, sweeperStartPosition, getMeasuredWidth(), getMeasuredWidth());

            setSweeperGradient();
            canvas.drawRect(0, sweeperStartPosition, getMeasuredWidth(), sweeperStartPosition + dpToPx(SWEEPER_WIDTH), sweeperPaint);
        }

        drawSigns(canvas);
        drawWinLine(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((onBoardInteractionListener == null) || (clickAnimator.isRunning()) || (isFlagSet())) {
            return super.onTouchEvent(event);
        } else {
            return clickDetector.onTouchEvent(event);
        }
    }

    private boolean isFlagSet() {
        for (SignData signData : signDataList) {
            if (signData.isAnimationFlag()) {
                return true;
            }
        }
        return false;
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

    private void setAnimationValues() {
        clickAnimator.setFloatValues(0, (getMeasuredWidth() / 6f) - dpToPx(2 * STROKE_WIDTH));
        winLineAnimator.setFloatValues(0, getMeasuredWidth());
        resetAnimator.setFloatValues(-dpToPx(SWEEPER_WIDTH), getMeasuredWidth());
    }

    private void setSweeperGradient() {
        float axis = sweeperStartPosition + (dpToPx(SWEEPER_WIDTH / 2f));

        LinearGradient horizontalGradient = new LinearGradient(0, axis, getMeasuredWidth(), axis, sweeperColors, sweeperStops, Shader.TileMode.CLAMP);
        LinearGradient verticalGradient = new LinearGradient(getMeasuredWidth() / 2f, sweeperStartPosition, getMeasuredWidth() / 2f, sweeperStartPosition + dpToPx(SWEEPER_WIDTH), sweeperColors, sweeperStops, Shader.TileMode.CLAMP);

        ComposeShader shader = new ComposeShader(horizontalGradient, verticalGradient, PorterDuff.Mode.MULTIPLY);

        sweeperPaint.setShader(shader);
    }

    private void drawGrid(Canvas canvas) {
        canvas.drawLines(gridLinePoints, gridPaint);
    }

    private void drawSigns(Canvas canvas) {
        for (int i = 0; i < signDataList.size(); i++) {
            SignData signData = signDataList.get(i);

            switch (signData.getSign()) {
                case Constants.CIRCLE:
                    drawCircle(canvas, centerPoints[signData.getRow()][signData.getColumn()], signData.isAnimationFlag());
                    break;
                case Constants.CROSS:
                    drawCross(canvas, centerPoints[signData.getRow()][signData.getColumn()], signData.isAnimationFlag());
                    break;
                case Constants.EMPTY:
                    break;
            }
        }
    }

    private void drawCircle(Canvas canvas, PointF center, boolean animationFlag) {
        float radius = animationFlag ? signRadius : (getMeasuredWidth() / 6f) - dpToPx(2 * STROKE_WIDTH);

        canvas.drawCircle(center.x, center.y, radius, signPaint);
    }

    private void drawCross(Canvas canvas, PointF center, boolean animationFlag) {
        float l = animationFlag ? signRadius : (getMeasuredWidth() / 6f) - dpToPx(2 * STROKE_WIDTH);

        canvas.drawLine(center.x - l, center.y - l, center.x + l, center.y + l, signPaint);
        canvas.drawLine(center.x - l, center.y + l, center.x + l, center.y - l, signPaint);
    }

    private void drawWinLine(Canvas canvas) {
        float length = winLineLength;

        float a = getMeasuredWidth() / 6f;

        float padding = dpToPx(STROKE_WIDTH);

        switch (winLinePosition) {
            case Constants.NONE:
                break;
            case Constants.ROW_1:
                canvas.drawLine(padding, a, length - padding, a, winLinePaint);
                break;
            case Constants.ROW_2:
                canvas.drawLine(padding, a + (2 * a), length - padding, a + (2 * a), winLinePaint);
                break;
            case Constants.ROW_3:
                canvas.drawLine(padding, a + (4 * a), length - padding, a + (4 * a), winLinePaint);
                break;
            case Constants.COLUMN_1:
                canvas.drawLine(a, padding, a, length - padding, winLinePaint);
                break;
            case Constants.COLUMN_2:
                canvas.drawLine(a + (2 * a), padding, a + (2 * a), length - padding, winLinePaint);
                break;
            case Constants.COLUMN_3:
                canvas.drawLine(a + (4 * a), padding, a + (4 * a), length - padding, winLinePaint);
                break;
            case Constants.DIAGONAL_1:
                canvas.drawLine(padding, padding, length - padding, length - padding, winLinePaint);
                break;
            case Constants.DIAGONAL_2:
                canvas.drawLine(getMeasuredWidth() - padding, padding, padding + getMeasuredWidth() - length, length - padding, winLinePaint);
                break;
        }
    }

    void addSignToBoard(@Constants.Sign int sign, int row, int column) {
        SignData signData = new SignData();
        signData.setSign(sign);
        signData.setRow(row);
        signData.setColumn(column);
        signData.setAnimationFlag(true);

        signDataList.add(signData);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clickAnimator.start();
            }
        }, 50);
    }

    void resetBoard() {
        if (!resetAnimator.isRunning()) {
            resetAnimator.start();
        }
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

    public void showWinLine(@Constants.WinLinePosition int winLinePosition) {
        this.winLinePosition = winLinePosition;

        winLineAnimator.start();
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
            onBoardInteractionListener.onBoardClick(BoardView.this, row, column);
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

    public void setOnBoardInteractionListener(OnBoardInteractionListener onBoardInteractionListener) {
        this.onBoardInteractionListener = onBoardInteractionListener;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (animation == clickAnimator) {
            signRadius = (float) animation.getAnimatedValue();
        } else if (animation == winLineAnimator) {
            winLineLength = (float) animation.getAnimatedValue();
        } else if (animation == resetAnimator) {
            sweeperStartPosition = (float) animation.getAnimatedValue();
        }
        invalidate();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animation == clickAnimator) {
            SignData signData = signDataList.get(signDataList.size() - 1);
            signData.setAnimationFlag(false);
            if (onBoardInteractionListener != null) {
                onBoardInteractionListener.onSignAdded(signData.getSign(), signData.getRow(), signData.getColumn());
            }
            signRadius = 0;
        } else if (animation == resetAnimator) {
            signDataList.clear();
            winLinePosition = Constants.NONE;
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    interface OnBoardInteractionListener {

        void onBoardClick(BoardView board, int row, int column);

        void onSignAdded(@Constants.Sign int sign, int row, int column);
    }

    private class SignData {
        private @Constants.Sign int sign;
        private int row;
        private int column;
        private boolean animationFlag;

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

        boolean isAnimationFlag() {
            return animationFlag;
        }

        void setAnimationFlag(boolean animationFlag) {
            this.animationFlag = animationFlag;
        }
    }
}
