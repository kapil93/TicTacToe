package kapil.tictactoe;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private static ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, line;
    private TextView turnTitle;

    private static int playerSign, player2Sign, computerSign, turn, numberOfTurns;
    private static boolean manualMode, playerWin, player2Win, computerWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();
        setClickListeners();

        getValues();

        initializeBoard();

        if (turn == computerSign) {
            int row = randomize(), column = randomize();
            Brain.board[row][column] = turn;
            putSign(row, column);
            numberOfTurns++;
            turn = (turn % 2) + 1;
            turnTitle.setText("Your Turn");
        }
    }

    private int randomize() {
        Random rand = new Random();
        return rand.nextInt(3);
    }

    private void initializeViews() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        turnTitle = (TextView) findViewById(R.id.turn);
        line = (ImageView) findViewById(R.id.line);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        img5 = (ImageView) findViewById(R.id.img5);
        img6 = (ImageView) findViewById(R.id.img6);
        img7 = (ImageView) findViewById(R.id.img7);
        img8 = (ImageView) findViewById(R.id.img8);
        img9 = (ImageView) findViewById(R.id.img9);
    }

    private void getValues() {
        manualMode = MainActivity.isManualMode();
        playerSign = MainActivity.getPlayerSign();

        if (manualMode) {
            player2Sign = MainActivity.getPlayer2Sign();
            computerSign = 0;
        } else {
            computerSign = MainActivity.getPlayer2Sign();
        }

        turn = MainActivity.getFirstTurn();
    }

    private void initializeBoard() {
        playerWin = player2Win = computerWin = false;

        numberOfTurns = 0;

        if (manualMode) {
            turnTitle.setText("Player 1 Turn");
        } else {
            turnTitle.setText("Your Turn");
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Brain.board[i][j] = 0;
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.fab), "Board Reset", Snackbar.LENGTH_SHORT);
        TextView sbText = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        sbText.setTextColor(Color.parseColor("#d47e00"));
        snackbar.show();
    }

    private void setClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);
        img7.setOnClickListener(this);
        img8.setOnClickListener(this);
        img9.setOnClickListener(this);
    }

    private void unSetClickListeners() {
        img1.setClickable(false);
        img2.setClickable(false);
        img3.setClickable(false);
        img4.setClickable(false);
        img5.setClickable(false);
        img6.setClickable(false);
        img7.setClickable(false);
        img8.setClickable(false);
        img9.setClickable(false);
    }

    @Override
    public void onClick(View view) {
        numberOfTurns++;

        if (manualMode) {
            if (turn == player2Sign) {
                turnTitle.setText("Player 1 Turn");
            } else {
                turnTitle.setText("Player 2 Turn");
            }
        }

        switch (view.getId()) {
            case R.id.img1:
                putSign(0, 0);
                Brain.board[0][0] = turn;
                break;

            case R.id.img2:
                putSign(0, 1);
                Brain.board[0][1] = turn;
                break;

            case R.id.img3:
                putSign(0, 2);
                Brain.board[0][2] = turn;
                break;

            case R.id.img4:
                putSign(1, 0);
                Brain.board[1][0] = turn;
                break;

            case R.id.img5:
                putSign(1, 1);
                Brain.board[1][1] = turn;
                break;

            case R.id.img6:
                putSign(1, 2);
                Brain.board[1][2] = turn;
                break;

            case R.id.img7:
                putSign(2, 0);
                Brain.board[2][0] = turn;
                break;

            case R.id.img8:
                putSign(2, 1);
                Brain.board[2][1] = turn;
                break;

            case R.id.img9:
                putSign(2, 2);
                Brain.board[2][2] = turn;
                break;
        }

        if (manualMode) {
            boolean isWin = Brain.isWin(turn, true);

            if (isWin) {
                unSetClickListeners();

                if (turn == playerSign) {
                    playerWin = true;
                    turnTitle.setText("");
                    Toast.makeText(GameActivity.this, "Player 1 Wins", Toast.LENGTH_SHORT).show();
                } else if (turn == player2Sign) {
                    player2Win = true;
                    turnTitle.setText("");
                    Toast.makeText(GameActivity.this, "Player 2 Wins", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            turn = (turn % 2) + 1;

            Brain.play(turn, numberOfTurns);

            Brain.board[Brain.coord[0]][Brain.coord[1]] = turn;
            putSign(Brain.coord[0], Brain.coord[1]);
            numberOfTurns++;
            turnTitle.setText("Your Turn");

            if (Brain.isWin(turn, true)) {
                unSetClickListeners();
                computerWin = true;
                turnTitle.setText("");
                Toast.makeText(GameActivity.this, "Computer Wins", Toast.LENGTH_SHORT).show();
            }
        }

        if ((!(playerWin || player2Win || computerWin)) && (numberOfTurns >= 9)) {
            Toast.makeText(GameActivity.this, "Draw", Toast.LENGTH_SHORT).show();
            turnTitle.setText("");
        }

        turn = (turn % 2) + 1;
    }

    public static void putSign(int r, int c) {

        if ((r == 0) && (c == 0)) {
            if (turn == 1) {
                img1.setImageResource(R.drawable.circle);
            } else {
                img1.setImageResource(R.drawable.cross);
            }
            img1.setClickable(false);
        } else if ((r == 0) && (c == 1)) {
            if (turn == 1) {
                img2.setImageResource(R.drawable.circle);
            } else {
                img2.setImageResource(R.drawable.cross);
            }
            img2.setClickable(false);
        } else if ((r == 0) && (c == 2)) {
            if (turn == 1) {
                img3.setImageResource(R.drawable.circle);
            } else {
                img3.setImageResource(R.drawable.cross);
            }
            img3.setClickable(false);
        } else if ((r == 1) && (c == 0)) {
            if (turn == 1) {
                img4.setImageResource(R.drawable.circle);
            } else {
                img4.setImageResource(R.drawable.cross);
            }
            img4.setClickable(false);
        } else if ((r == 1) && (c == 1)) {
            if (turn == 1) {
                img5.setImageResource(R.drawable.circle);
            } else {
                img5.setImageResource(R.drawable.cross);
            }
            img5.setClickable(false);
        } else if ((r == 1) && (c == 2)) {
            if (turn == 1) {
                img6.setImageResource(R.drawable.circle);
            } else {
                img6.setImageResource(R.drawable.cross);
            }
            img6.setClickable(false);
        } else if ((r == 2) && (c == 0)) {
            if (turn == 1) {
                img7.setImageResource(R.drawable.circle);
            } else {
                img7.setImageResource(R.drawable.cross);
            }
            img7.setClickable(false);
        } else if ((r == 2) && (c == 1)) {
            if (turn == 1) {
                img8.setImageResource(R.drawable.circle);
            } else {
                img8.setImageResource(R.drawable.cross);
            }
            img8.setClickable(false);
        } else if ((r == 2) && (c == 2)) {
            if (turn == 1) {
                img9.setImageResource(R.drawable.circle);
            } else {
                img9.setImageResource(R.drawable.cross);
            }
            img9.setClickable(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public static int getComputerSign() {
        return computerSign;
    }

    public static ImageView getLine() {
        return line;
    }

}
