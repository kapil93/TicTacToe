package kapil.tictactoe.game;

import kapil.tictactoe.Constants;

/**
 *
 */

class SignData {
    private @Constants.Sign int sign;
    private int row;
    private int column;

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
}
