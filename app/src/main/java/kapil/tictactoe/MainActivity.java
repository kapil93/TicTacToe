package kapil.tictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static int playerSign, player2Sign, firstTurn, pageFlag;
    private static boolean manualMode;

    private TextView menuTitle;
    private ImageView imgUp;
    private ImageView imgDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menuTitle = (TextView) findViewById(R.id.menu_title);
        imgUp = (ImageView) findViewById(R.id.imgUp);
        imgDown = (ImageView) findViewById(R.id.imgDown);

        pageFlag = 0;

        menuTitle.setText("Select Mode");
        imgUp.setImageResource(R.drawable.single_player);
        imgDown.setImageResource(R.drawable.multi_player);

        imgUp.setOnClickListener(this);
        imgDown.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (pageFlag == 0) {

            if (id == R.id.imgUp) {
                manualMode = false;
            } else if (id == R.id.imgDown) {
                manualMode = true;
            }

            menuTitle.setText("Select Sign");
            imgUp.setImageResource(R.drawable.circle);
            imgDown.setImageResource(R.drawable.cross);
            pageFlag = 1;

        } else if (pageFlag == 1) {

            if (id == R.id.imgUp) {
                playerSign = 1;
                player2Sign = 2;
            } else if (id == R.id.imgDown) {
                playerSign = 2;
                player2Sign = 1;
            }

            if (manualMode) {
                firstTurn = playerSign;
                startActivity(new Intent(getApplicationContext(), GameActivity.class));
                finish();
            }

            if (!manualMode) {
                menuTitle.setText("Select First Turn");
                imgUp.setImageResource(R.drawable.user);
                imgDown.setImageResource(R.drawable.system);
                pageFlag = 2;
            }

        } else if (pageFlag == 2) {
            if (id == R.id.imgUp) {
                firstTurn = playerSign;
            } else if (id == R.id.imgDown) {
                firstTurn = player2Sign;
            }
            startActivity(new Intent(getApplicationContext(), GameActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (pageFlag != 0) {
            recreate();
        } else {
            super.onBackPressed();
        }
    }

    public static int getPlayerSign() {
        return playerSign;
    }

    public static int getPlayer2Sign() {
        return player2Sign;
    }

    public static boolean isManualMode() {
        return manualMode;
    }

    public static int getFirstTurn() {
        return firstTurn;
    }
}
