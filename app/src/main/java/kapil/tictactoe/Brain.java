package kapil.tictactoe;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Brain {

    public static int[][] board = new int[3][3];
    public static int[] coord = new int[2];

    public static int play(int turn, int depth) {

        if (isWin(GameActivity.getComputerSign(), false)) {
            return 10 - depth;
        } else if (isWin((GameActivity.getComputerSign() % 2) + 1, false)) {
            return depth - 10;
        }

        if (depth >= 9) {
            return 0;
        }

        List<Integer> scores = new ArrayList<>(), xCoords = new ArrayList<>(), yCoords = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = turn;
                    scores.add(play((turn % 2) + 1, depth + 1));
                    xCoords.add(i);
                    yCoords.add(j);
                    board[i][j] = 0;
                }
            }
        }

        if (turn == GameActivity.getComputerSign()) {
            int maxScore = -100;
            for (int i = 0; i < scores.size(); i++) {
                if (scores.get(i) > maxScore) {
                    maxScore = scores.get(i);
                }
            }
            return randomizeScore(maxScore, scores, xCoords, yCoords);

        } else if (turn != GameActivity.getComputerSign()) {
            int minScore = 100;
            for (int i = 0; i < scores.size(); i++) {
                if (scores.get(i) < minScore) {
                    minScore = scores.get(i);
                }
            }
            return randomizeScore(minScore, scores, xCoords, yCoords);
        }

        return 0;
    }

    private static int randomizeScore(int score, List<Integer> scores, List<Integer> xCoords, List<Integer> yCoords) {
        List<Integer> equalScoreIndices = new ArrayList<>();

        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) == score) {
                equalScoreIndices.add(i);
            }
        }

        Random rand = new Random();
        int randomIndex = equalScoreIndices.get(rand.nextInt(equalScoreIndices.size()));

        coord[0] = xCoords.get(randomIndex);
        coord[1] = yCoords.get(randomIndex);

        return score;
    }

    public static Boolean isWin(int turn, boolean flag) {
        int[] winSeq = new int[3], row = new int[3], col = new int[3], diag1 = new int[3], diag2 = new int[3];

        for (int i = 0; i < 3; i++) {
            winSeq[i] = turn;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                if (i == j) {
                    diag1[i] = board[i][j];
                }
                if ((i + j) == 2) {
                    diag2[i] = board[i][j];
                }

                row[j] = board[i][j];
                col[j] = board[j][i];
            }

            if (isEqual(row, winSeq)) {
                if (flag) {
                    putLine(0, i);
                }
                return true;
            } else if (isEqual(col, winSeq)) {
                if (flag) {
                    putLine(1, i);
                }
                return true;
            }
        }

        if (isEqual(diag1, winSeq)) {
            if (flag) {
                putLine(2, 0);
            }
            return true;
        } else if (isEqual(diag2, winSeq)) {
            if (flag) {
                putLine(2, 1);
            }
            return true;
        }

        return false;
    }

    private static boolean isEqual(int[] x, int[] y) {
        for (int i = 0; i < 3; i++) {
            if (x[i] != y[i]) {
                return false;
            }
        }
        return true;
    }

    private static void putLine(int direction, int index) {
        ImageView line = GameActivity.getLine();
        if ((direction == 0) && (index == 0)) {
            line.setBackgroundResource(R.drawable.row1);
        } else if ((direction == 0) && (index == 1)) {
            line.setBackgroundResource(R.drawable.row2);
        } else if ((direction == 0) && (index == 2)) {
            line.setBackgroundResource(R.drawable.row3);
        } else if ((direction == 1) && (index == 0)) {
            line.setBackgroundResource(R.drawable.col1);
        } else if ((direction == 1) && (index == 1)) {
            line.setBackgroundResource(R.drawable.col2);
        } else if ((direction == 1) && (index == 2)) {
            line.setBackgroundResource(R.drawable.col3);
        } else if ((direction == 2) && (index == 0)) {
            line.setBackgroundResource(R.drawable.diag1);
        } else if ((direction == 2) && (index == 1)) {
            line.setBackgroundResource(R.drawable.diag2);
        }
    }
}