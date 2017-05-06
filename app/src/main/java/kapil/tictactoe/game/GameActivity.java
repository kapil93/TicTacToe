package kapil.tictactoe.game;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import kapil.tictactoe.Constants;
import kapil.tictactoe.R;

public class GameActivity extends AppCompatActivity implements BoardView.OnBoardInteractionListener, Brain.OnProcessCompleteListener, View.OnClickListener {
    private Brain brain;

    private BoardView board;
    private FloatingActionButton resetButton;
    private TextView turnTextBox;

    private @Constants.Sign int player1Sign;
    private @Constants.Sign int player2Sign;
    private @Constants.Player int turn;
    private @Constants.GameMode int gameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();
        setClickListeners();

        brain = Brain.getInstance();
        brain.setOnProcessCompleteListener(this);

        getValues();
        setGameScreen();
        makeFirstMove();
    }

    private void initializeViews() {
        board = (BoardView) findViewById(R.id.board);
        resetButton = (FloatingActionButton) findViewById(R.id.reset_button);
        turnTextBox = (TextView) findViewById(R.id.turn_text_box);
    }

    private void setClickListeners() {
        resetButton.setOnClickListener(this);
        board.setOnBoardInteractionListener(this);
    }

    @SuppressWarnings("WrongConstant")
    private void getValues() {
        Intent intent = getIntent();

        gameMode = intent.getIntExtra("GAME_MODE", Constants.MULTI_PLAYER);
        player1Sign = intent.getIntExtra("PLAYER_1_SIGN", Constants.CIRCLE);
        player2Sign = intent.getIntExtra("PLAYER_2_SIGN", Constants.CROSS);
        turn = intent.getIntExtra("FIRST_TURN", Constants.PLAYER_1);
    }

    private void setGameScreen() {
        switch (gameMode) {
            case Constants.SINGLE_PLAYER:
                brain.setComputerSign(player2Sign);
                turnTextBox.setText(R.string.player_turn_prompt);
                break;
            case Constants.MULTI_PLAYER:
                turnTextBox.setText(R.string.player_1_turn_prompt);
                break;
        }
        brain.reset();
    }

    private void makeFirstMove() {
        if ((gameMode == Constants.SINGLE_PLAYER) && (turn == Constants.PLAYER_2)) {
            int row = randomize(), column = randomize();
            putSign(getCurrentPlayerSign(), row, column);
            toggleTurn();
            turnTextBox.setText(R.string.player_turn_prompt);
        }
    }

    private int randomize() {
        Random rand = new Random();
        return rand.nextInt(3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_button:
                board.resetBoard();
                board.setOnBoardInteractionListener(GameActivity.this);

                //noinspection WrongConstant
                turn = getIntent().getIntExtra("FIRST_TURN", Constants.PLAYER_1);

                setGameScreen();
                makeFirstMove();
                showBoardResetSnackBar();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showBoardResetSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.reset_button), "Board Reset", Snackbar.LENGTH_SHORT);
        TextView sbText = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        sbText.setTextColor(getResources().getColor(R.color.holo_orange_dark, null));
        snackbar.show();
    }

    @Override
    public void onBoardClick(BoardView board, int row, int column) {
        if (board.isAlreadyClicked(row, column)) {
            return;
        }

        putSign(getCurrentPlayerSign(), row, column);
    }

    @Override
    public void onSignAdded(@Constants.Sign int sign, int row, int column) {
        switch (gameMode) {
            case Constants.SINGLE_PLAYER:

                if (sign == player1Sign) {

                    toggleTurn();

                    brain.play(getCurrentPlayerSign());
                }

                break;

            case Constants.MULTI_PLAYER:

                switch (turn) {
                    case Constants.PLAYER_1:
                        turnTextBox.setText(R.string.player_2_turn_prompt);
                        break;
                    case Constants.PLAYER_2:
                        turnTextBox.setText(R.string.player_1_turn_prompt);
                        break;
                }

                toggleTurn();

                break;
        }

        brain.analyzeBoard();
    }

    private @Constants.Sign int getCurrentPlayerSign() {
        return turn == Constants.PLAYER_1 ? player1Sign : player2Sign;
    }

    public void putSign(@Constants.Sign int sign, int row, int column) {
        brain.updateBoard(sign, row, column);
        board.addSignToBoard(sign, row, column);
    }

    private void toggleTurn() {
        turn = turn == Constants.PLAYER_1 ? Constants.PLAYER_2 : Constants.PLAYER_1;
    }

    @Override
    public void onNextMoveCalculated(int row, int column) {
        putSign(player2Sign, row, column);
        turnTextBox.setText(R.string.player_turn_prompt);
        toggleTurn();
    }

    @Override
    public void onGameWin(@Constants.Sign int sign, @Constants.WinLinePosition int winLinePosition) {
        board.showWinLine(winLinePosition);

        turnTextBox.setText("");

        if (sign == player1Sign) {
            Toast.makeText(GameActivity.this, "Player 1 Wins", Toast.LENGTH_SHORT).show();
        } else if (sign == player2Sign) {
            switch (gameMode) {
                case Constants.SINGLE_PLAYER:
                    Toast.makeText(GameActivity.this, "Computer Wins", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MULTI_PLAYER:
                    Toast.makeText(GameActivity.this, "Player 2 Wins", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        board.setOnBoardInteractionListener(null);
    }

    @Override
    public void onGameDraw() {
        turnTextBox.setText("");
        Toast.makeText(GameActivity.this, "Draw", Toast.LENGTH_SHORT).show();
        board.setOnBoardInteractionListener(null);
    }

    @Override
    protected void onDestroy() {
        brain.destroy();
        brain = null;
        super.onDestroy();
    }
}