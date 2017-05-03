package kapil.tictactoe;

import android.support.annotation.IntDef;

/**
 * Created by witworks on 04/05/17.
 */

public interface Constants {
    int CIRCLE = 1;
    int CROSS = 2;

    @IntDef({CIRCLE, CROSS})
    @interface Sign {

    }

    int PLAYER_1 = 0;
    int PLAYER_2 = 1;

    @IntDef({PLAYER_1, PLAYER_2})
    @interface Player {

    }

    int SINGLE_PLAYER = 0;
    int MULTI_PLAYER = 1;

    @IntDef({SINGLE_PLAYER, MULTI_PLAYER})
    @interface GameMode {

    }
}
