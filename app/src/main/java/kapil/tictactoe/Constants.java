package kapil.tictactoe;

import android.support.annotation.IntDef;

/**
 * Created by witworks on 04/05/17.
 */

public interface Constants {
    int EMPTY = 0;
    int CIRCLE = 1;
    int CROSS = 2;

    @IntDef({EMPTY, CIRCLE, CROSS})
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

    int NONE = 0;
    int ROW_1 = 1;
    int ROW_2 = 2;
    int ROW_3 = 3;
    int COLUMN_1 = 4;
    int COLUMN_2 = 5;
    int COLUMN_3 = 6;
    int DIAGONAL_1 = 7;
    int DIAGONAL_2 = 8;

    @IntDef({NONE, ROW_1, ROW_2, ROW_3, COLUMN_1, COLUMN_2, COLUMN_3, DIAGONAL_1, DIAGONAL_2})
    @interface WinLinePosition {

    }
}
