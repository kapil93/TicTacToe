package kapil.tictactoe.game;

import android.support.annotation.IntDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kapil.tictactoe.Constants;

class Brain {
    private static Brain INSTANCE;

    private int[][] board = new int[3][3];

    private int rowOfResult;
    private int columnOfResult;

    private int depth;

    private @Constants.Sign int computerSign;
    private @Constants.Sign int playerSign;

    private OnProcessCompleteListener onProcessCompleteListener;

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final int DIAGONAL = 2;

    @IntDef({HORIZONTAL, VERTICAL, DIAGONAL})
    @interface DirectionOfWinLine {

    }

    // References used by isWin function.
    private int[] winSequence = new int[3];
    private int[] row = new int[3];
    private int[] column = new int[3];
    private int[] diagonal1 = new int[3];
    private int[] diagonal2 = new int[3];

    private Brain() {

    }

    static Brain getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Brain();
        }
        return INSTANCE;
    }

    void play(@Constants.Sign int sign) {
        if (onProcessCompleteListener == null) {
            return;
        }

        calculateNextMove(sign, depth);

        onProcessCompleteListener.onNextMoveCalculated(rowOfResult, columnOfResult);
    }

    private int calculateNextMove(@Constants.Sign int sign, int depth) {

        if (isWin(computerSign, false)) {
            return 10 - depth;
        } else if (isWin(playerSign, false)) {
            return depth - 10;
        }

        if (depth >= 9) {
            return 0;
        }

        List<Integer> scores = new ArrayList<>(), rowIndices = new ArrayList<>(), columnIndices = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = sign;
                    scores.add(calculateNextMove(getOppositeSign(sign), depth + 1));
                    rowIndices.add(i);
                    columnIndices.add(j);
                    board[i][j] = 0;
                }
            }
        }

        if (sign == computerSign) {
            int maxScore = -100;
            for (int i = 0; i < scores.size(); i++) {
                if (scores.get(i) > maxScore) {
                    maxScore = scores.get(i);
                }
            }
            return randomizeScore(maxScore, scores, rowIndices, columnIndices);

        } else {
            int minScore = 100;
            for (int i = 0; i < scores.size(); i++) {
                if (scores.get(i) < minScore) {
                    minScore = scores.get(i);
                }
            }
            return randomizeScore(minScore, scores, rowIndices, columnIndices);
        }
    }

    private int randomizeScore(int score, List<Integer> scores, List<Integer> rowIndices, List<Integer> columnIndices) {
        List<Integer> equalScoreIndices = new ArrayList<>();

        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) == score) {
                equalScoreIndices.add(i);
            }
        }

        Random rand = new Random();
        int randomIndex = equalScoreIndices.get(rand.nextInt(equalScoreIndices.size()));

        rowOfResult = rowIndices.get(randomIndex);
        columnOfResult = columnIndices.get(randomIndex);

        return score;
    }

    private boolean isWin(@Constants.Sign int sign, boolean notifyWinEnabled) {
        for (int i = 0; i < 3; i++) {
            winSequence[i] = sign;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                if (i == j) {
                    diagonal1[i] = board[i][j];
                }
                if ((i + j) == 2) {
                    diagonal2[i] = board[i][j];
                }

                row[j] = board[i][j];
                column[j] = board[j][i];
            }

            if (isEqual(row, winSequence)) {
                if (notifyWinEnabled) {
                    notifyWin(sign, HORIZONTAL, i + 1);
                }
                return true;
            } else if (isEqual(column, winSequence)) {
                if (notifyWinEnabled) {
                    notifyWin(sign, VERTICAL, i + 1);
                }
                return true;
            }
        }

        if (isEqual(diagonal1, winSequence)) {
            if (notifyWinEnabled) {
                notifyWin(sign, DIAGONAL, 1);
            }
            return true;
        } else if (isEqual(diagonal2, winSequence)) {
            if (notifyWinEnabled) {
                notifyWin(sign, DIAGONAL, 2);
            }
            return true;
        }

        return false;
    }

    private boolean isEqual(int[] x, int[] y) {
        for (int i = 0; i < 3; i++) {
            if (x[i] != y[i]) {
                return false;
            }
        }
        return true;
    }

    void analyzeBoard() {
        if (onProcessCompleteListener == null) {
            return;
        }

        if ((!isWin(Constants.CIRCLE, true)) && (!isWin(Constants.CROSS, true)) && (depth >= 9)) {
            onProcessCompleteListener.onGameDraw();
        }
    }

    private void notifyWin(@Constants.Sign int sign, @DirectionOfWinLine int direction, int index) {
        if (onProcessCompleteListener == null) {
            return;
        }

        @Constants.WinLinePosition int winLinePosition = Constants.NONE;

        switch (direction) {
            case HORIZONTAL:
                switch (index) {
                    case 1:
                        winLinePosition = Constants.ROW_1;
                        break;
                    case 2:
                        winLinePosition = Constants.ROW_2;
                        break;
                    case 3:
                        winLinePosition = Constants.ROW_3;
                        break;
                }
                break;
            case VERTICAL:
                switch (index) {
                    case 1:
                        winLinePosition = Constants.COLUMN_1;
                        break;
                    case 2:
                        winLinePosition = Constants.COLUMN_2;
                        break;
                    case 3:
                        winLinePosition = Constants.COLUMN_3;
                        break;
                }
                break;
            case DIAGONAL:
                switch (index) {
                    case 1:
                        winLinePosition = Constants.DIAGONAL_1;
                        break;
                    case 2:
                        winLinePosition = Constants.DIAGONAL_2;
                        break;
                }
                break;
        }

        onProcessCompleteListener.onGameWin(sign, winLinePosition);
    }

    void reset() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }
        depth = 0;
    }

    void setComputerSign(int computerSign) {
        this.computerSign = computerSign;
        playerSign = getOppositeSign(computerSign);
    }

    void updateBoard(@Constants.Sign int sign, int row, int column) {
        board[row][column] = sign;
        depth++;
    }

    private @Constants.Sign int getOppositeSign(@Constants.Sign int sign) {
        return sign == Constants.CIRCLE ? Constants.CROSS : Constants.CIRCLE;
    }

    void setOnProcessCompleteListener(OnProcessCompleteListener onProcessCompleteListener) {
        this.onProcessCompleteListener = onProcessCompleteListener;
    }

    interface OnProcessCompleteListener {

        void onNextMoveCalculated(int row, int column);

        void onGameWin(@Constants.Sign int sign, @Constants.WinLinePosition int winLinePosition);

        void onGameDraw();
    }

    void destroy() {
        INSTANCE = null;
    }
}