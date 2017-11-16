package solver.model;

public enum Status {

    BLOCK_MINE_EXPLODED(-3),
    BLOCK_CLOSED(-1),
    BLOCK_FLAG(-2),
    BLOCK_0(0),
    BLOCK_1(1),
    BLOCK_2(2),
    BLOCK_3(3),
    BLOCK_4(4),
    BLOCK_5(5),
    BLOCK_6(6),
    BLOCK_7(7),
    BLOCK_8(8),
    BLOCK_9(9);

    private int val;

    Status(int val) {
        this.val = val;
    }

    @Override
    public String toString() {

        switch (val) {
            case -3:
                return("O");
            case -2:
                return("X");
            case -1:
                return("+");
            case 0:
                return(".");
            default:
                return String.valueOf(val);
        }
    }



    public int getVal() {
        return val;
    }
}
