package kapil.tictactoe.game;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import kapil.tictactoe.Constants;
import kapil.tictactoe.R;

public class GameActivity extends AppCompatActivity implements BoardView.OnBoardClickListener {
    private Brain brain;

    private BoardView board;
    private FloatingActionButton fab;
    private TextView turnTextBox;

    private @Constants.Sign int player1Sign;
    private @Constants.Sign int player2Sign;
    private @Constants.Player int turn;
    private @Constants.GameMode int gameMode;

    private int numberOfTurns;
    private boolean playerWin, player2Win, computerWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();
        setClickListeners();

        brain = Brain.getInstance();

        getValues();

        initializeBoard();

        makeFirstMove();
    }

    private void initializeViews() {
        board = (BoardView) findViewById(R.id.board);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        turnTextBox = (TextView) findViewById(R.id.turn_text_box);
    }

    private void setClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                board.resetBoard();

                //noinspection WrongConstant
                turn = getIntent().getIntExtra("FIRST_TURN", Constants.PLAYER_1);

                initializeBoard();

                makeFirstMove();

                Snackbar snackbar = Snackbar.make(findViewById(R.id.fab), "Board Reset", Snackbar.LENGTH_SHORT);
                TextView sbText = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
                sbText.setTextColor(Color.parseColor("#d47e00"));
                snackbar.show();
            }
        });

        board.setOnBoardClickListener(this);
    }

    @SuppressWarnings("WrongConstant")
    private void getValues() {
        Intent intent = getIntent();

        gameMode = intent.getIntExtra("GAME_MODE", Constants.MULTI_PLAYER);
        player1Sign = intent.getIntExtra("PLAYER_1_SIGN", Constants.CIRCLE);
        player2Sign = intent.getIntExtra("PLAYER_2_SIGN", Constants.CROSS);
        turn = intent.getIntExtra("FIRST_TURN", Constants.PLAYER_1);

        if (gameMode == Constants.SINGLE_PLAYER) {
            brain.setComputerSign(player2Sign);
        }
    }

    private void initializeBoard() {
        playerWin = player2Win = computerWin = false;

        numberOfTurns = 0;

        switch (gameMode) {
            case Constants.SINGLE_PLAYER:
                turnTextBox.setText("Your Turn");
                break;
            case Constants.MULTI_PLAYER:
                turnTextBox.setText("Player 1 Turn");
                break;
        }

        brain.reset();
    }

    private void makeFirstMove() {
        if ((gameMode == Constants.SINGLE_PLAYER) && (turn == Constants.PLAYER_2)) {
            int row = randomize(), column = randomize();
            brain.updateBoard(getCurrentPlayerSign(), row, column);
            putSign(row, column);
            numberOfTurns++;
            toggleTurn();
            turnTextBox.setText("Your Turn");
        }
    }

    private int randomize() {
        Random rand = new Random();
        return rand.nextInt(3);
    }

    private @Constants.Sign int getCurrentPlayerSign() {
        return turn == Constants.PLAYER_1 ? player1Sign : player2Sign;
    }

    public void putSign(int row, int column) {
        board.addSignToBoard(getCurrentPlayerSign(), row, column);
    }

    @Override
    public void onBoardClick(BoardView board, int row, int column) {
        if (isAlreadyClicked(row, column)) {
            return;
        }

        numberOfTurns++;

        if (gameMode == Constants.MULTI_PLAYER) {
            switch (turn) {
                case Constants.PLAYER_1:
                    turnTextBox.setText("Player 2 Turn");
                    break;
                case Constants.PLAYER_2:
                    turnTextBox.setText("Player 1 Turn");
                    break;
            }
        }

        putSign(row, column);
        brain.updateBoard(getCurrentPlayerSign(), row, column);

        switch (gameMode) {
            case Constants.SINGLE_PLAYER:
                toggleTurn();

                brain.play(getCurrentPlayerSign(), numberOfTurns);

                brain.updateBoard(getCurrentPlayerSign(), brain.coord[0], brain.coord[1]);
                putSign(brain.coord[0], brain.coord[1]);
                numberOfTurns++;
                turnTextBox.setText("Your Turn");

                if (brain.isWin(getCurrentPlayerSign(), true)) {
                    computerWin = true;
                    turnTextBox.setText("");
                    Toast.makeText(GameActivity.this, "Computer Wins", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constants.MULTI_PLAYER:
                boolean isWin = brain.isWin(getCurrentPlayerSign(), true);

                if (isWin) {
                    switch (turn) {
                        case Constants.PLAYER_1:
                            playerWin = true;
                            turnTextBox.setText("");
                            Toast.makeText(GameActivity.this, "Player 1 Wins", Toast.LENGTH_SHORT).show();
                            break;
                        case Constants.PLAYER_2:
                            player2Win = true;
                            turnTextBox.setText("");
                            Toast.makeText(GameActivity.this, "Player 2 Wins", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                break;
        }

        if ((!(playerWin || player2Win || computerWin)) && (numberOfTurns >= 9)) {
            Toast.makeText(GameActivity.this, "Draw", Toast.LENGTH_SHORT).show();
            turnTextBox.setText("");
        }

        toggleTurn();
    }

    private boolean isAlreadyClicked(int row, int column) {
        List<SignData> signDataList = board.getSignDataList();

        for (int i = 0; i < signDataList.size(); i++) {
            SignData signData = signDataList.get(i);

            if ((signData.getRow() == row) && (signData.getColumn() == column)) {
                return true;
            }
        }

        return false;
    }

    private void toggleTurn() {
        turn = turn == Constants.PLAYER_1 ? Constants.PLAYER_2 : Constants.PLAYER_1;
    }
}
