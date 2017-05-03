package kapil.tictactoe.selection;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import kapil.tictactoe.Constants;
import kapil.tictactoe.R;
import kapil.tictactoe.game.GameActivity;

public class SelectionActivity extends AppCompatActivity implements SelectionFragment.OnValueSelectedListener {
    private SelectionFragment modeSelectionFragment;
    private SelectionFragment signSelectionFragment;
    private SelectionFragment turnSelectionFragment;

    private @Constants.Sign int player1Sign;
    private @Constants.Sign int player2Sign;
    private @Constants.Sign int firstTurn;
    private @Constants.GameMode int gameMode;

    private boolean backFromGameActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        modeSelectionFragment = SelectionFragment.newInstance(getResources().getString(
                R.string.mode_selection_text), R.drawable.single_player, R.drawable.multi_player);

        signSelectionFragment = SelectionFragment.newInstance(getResources().getString(
                R.string.sign_selection_text), R.drawable.circle, R.drawable.cross);

        turnSelectionFragment = SelectionFragment.newInstance(getResources().getString(
                R.string.turn_selection_text), R.drawable.user, R.drawable.system);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, modeSelectionFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (backFromGameActivity) {
            setFragmentTransitionAnimationEnabled(false);

            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStack();

            backFromGameActivity = false;
        }
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();

        setFragmentTransitionAnimationEnabled(true);
    }

    private void setFragmentTransitionAnimationEnabled(boolean enabled) {
        modeSelectionFragment.setTransitionAnimationEnabled(enabled);
        signSelectionFragment.setTransitionAnimationEnabled(enabled);
        turnSelectionFragment.setTransitionAnimationEnabled(enabled);
    }

    @Override
    public void onValueSelected(Fragment fragment, @SelectionFragment.ButtonType int buttonType) {
        if (fragment == modeSelectionFragment) {

            switch (buttonType) {
                case SelectionFragment.TOP_BUTTON:
                    gameMode = Constants.SINGLE_PLAYER;
                    break;
                case SelectionFragment.BOTTOM_BUTTON:
                    gameMode = Constants.MULTI_PLAYER;
                    break;
            }

            replaceFragment(signSelectionFragment);

        } else if (fragment == signSelectionFragment) {

            switch (buttonType) {
                case SelectionFragment.TOP_BUTTON:
                    player1Sign = Constants.CIRCLE;
                    player2Sign = Constants.CROSS;
                    break;
                case SelectionFragment.BOTTOM_BUTTON:
                    player1Sign = Constants.CROSS;
                    player2Sign = Constants.CIRCLE;
                    break;
            }

            if (gameMode == Constants.MULTI_PLAYER) {
                firstTurn = player1Sign;
                startGameActivity();
            } else {
                replaceFragment(turnSelectionFragment);
            }

        } else if (fragment == turnSelectionFragment) {

            switch (buttonType) {
                case SelectionFragment.TOP_BUTTON:
                    firstTurn = player1Sign;
                    break;
                case SelectionFragment.BOTTOM_BUTTON:
                    firstTurn = player2Sign;
                    break;
            }

            startGameActivity();
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left, R.anim.fade_out, R.anim.fade_in, R.anim.exit_to_left)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void startGameActivity() {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("PLAYER_1_SIGN", player1Sign);
        intent.putExtra("PLAYER_2_SIGN", player2Sign);
        intent.putExtra("FIRST_TURN", firstTurn);
        intent.putExtra("GAME_MODE", gameMode);
        startActivity(intent);

        backFromGameActivity = true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
