import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Movement {
    /* ----- CURRENT GAME BOARD ----- */
    private int[][] board =
            {{1,0,1,0,1,0,1,0},
                    {0,1,0,1,0,1,0,1},
                    {1,0,1,0,1,0,1,0},
                    {0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0},
                    {0,-1,0,-1,0,-1,0,-1},
                    {-1,0,-1,0,-1,0,-1,0},
                    {0,-1,0,-1,0,-1,0,-1}};

    /* ----- SEREVER GAME POSITIONS ----- */
    private final int[][] positions =
            {{4,0,3,0,2,0,1,0},
                    {0,8,0,7,0,6,0,5},
                    {12,0,11,0,10,0,9,0},
                    {0,16,0,15,0,14,0,13},
                    {20,0,19,0,18,0,17,0},
                    {0,24,0,23,0,22,0,21},
                    {28,0,27,0,26,0,25,0},
                    {0,32,0,31,0,30,0,29}};

    private static final int SKIP = 25;
    private static final int SKIP_ON_NEXT = 20;
    private static final int KING_ON_NEXT = 40;
    private static final int SAFE_SAFE = 5;
    private static final int SAFE_UNSAFE = -40;
    private static final int UNSAFE_SAFE = 40;
    private static final int UNSAFE_UNSAFE = -40;
    private static final int SAFE = 3;
    private static final int UNSAFE = -5;
    private static final int KING_MULTIPLIER = 3;

    private static final int RED = 1;
    private static final int BLACK = -1;

    private int color;
    private Connect connection;
    private GameInfoResponse.GIData data;
    ArrayList<int[]> strategy;
    public static Set<Integer> kings = new LinkedHashSet<Integer>();

    Movement(String color, Connect connection) throws IOException {
        this.color = color.equals("RED") ? 1 : -1;
        this.connection = connection;
        this.data = connection.getInfo();

        zeroBoard();
        for(GameInfoResponse.Tile tile : data.board){
            setPosition(tile.color.length() == 3 ? 1 : -1, tile.position);
        }

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if(kings.contains(positions[i][j]) && board[i][j] != this.color)
                    kings.remove(positions[i][j]);

        System.out.println("KINGS " + kings);
    }

    public Move makeMove() throws IOException {
        ArrayList<Move> gm = getMoves();

        if(data.last_move != null)
            if((data.last_move.player.equals("RED") && color == RED) || (data.last_move.player.equals("BLACK") && color == BLACK)){
                int[][] lm = data.last_move.last_moves;
                int from = lm[lm.length-1][1];

                for(Move move: gm){
                    if(move.from != from) gm.remove(move);
                }
            }
        System.out.println(gm);

        ArrayList<Move> bestmoves = addWeights(gm);
        System.out.println(bestmoves);

        int rm = ((int) (Math.random() * bestmoves.size())) % bestmoves.size() ;
        Move move = bestmoves.get(rm);
        System.out.println("MOVE: " + move);

        if(color == -1){
            if(move.to < 5) kings.add(move.to);
        }
        else if (move.to > 28) kings.add(move.to);

        if(kings.contains(move.from)){
            kings.remove(move.from);
            kings.add(move.to);
        }
        return move;
    }



    private ArrayList<Move> addWeights(ArrayList<Move> moves){
        int bestWeight = -10000;
        ArrayList<Move> bestmoves = new ArrayList<>();

        for(Move move : moves){
            getMoveWeight(move);
            if(bestWeight < move.getWeight()){
                bestWeight = move.getWeight();
            }

            System.out.println(move + " w: " + move.getWeight());
        }

        for(Move move : moves)
            if(move.getWeight() == bestWeight)
                bestmoves.add(move);

        return bestmoves;
    }

    private void getMoveWeight(Move m) {

        int start = m.from, end = m.to;
        m.setWeight(getSafetyWeight());

        // Make the move
        int si = 0; int sj = 0; int ei = 0; int ej = 0;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++){
                if(positions[i][j] == start){
                    si = i;
                    sj = j;
                }
                if(positions[i][j] == end){
                    ei = i;
                    ej = j;
                }
            }

        int[][] b = new int[8][8];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 7; j++)
                b[i][j] = board[i][j];


        if(Math.abs(si-ei) == 2){
            m.changeWeight(SKIP);
            board[ei][ej] = color;
            board[si][sj] = 0;
            board[Math.abs(si-ei)][Math.abs(sj-ej)] = 0;

            if (ei + color*2 < 8 && ei + color*2 >=0 && ej < 6 && board[ei + color][ej + 1] == -color && board[ei + color*2][ej + 2] == 0)
                m.changeWeight(SKIP_ON_NEXT);
            if (ei + color*2 < 8 && ei + color*2 >=0 && ej > 1 && board[ei + color][ej - 1] == -color && board[ei + color*2][ej - 2] == 0)
                m.changeWeight(SKIP_ON_NEXT);

            if(kings.contains(positions[si][sj])){
                if (ei - color*2 < 8 && ei - color*2 >=0 && ej < 6 && board[ei - color][ej + 1] == -color && board[ei - color*2][ej + 2] == 0)
                    m.changeWeight(SKIP_ON_NEXT);
                if (ei - color*2 < 8 && ei - color*2 >=0 && ej > 1 && board[ei - color][ej - 1] == -color && board[ei - color*2][ej - 2] == 0)
                    m.changeWeight(SKIP_ON_NEXT);
            }
        }
        else {
            board[ei][ej] = color;
            board[si][sj] = 0;
        }

        boolean safeBefore = isSafe(si,sj);
        boolean safeAfter = isSafe(ei,ej);
        boolean isKing = kings.contains(positions[si][sj]);

        if (safeBefore && safeAfter) {
            m.changeWeight(SAFE_SAFE);
        } else if (!safeBefore && safeAfter) {
            m.changeWeight(UNSAFE_SAFE);
        } else if (safeBefore && !safeAfter) {
            System.out.println("EAT KING " + isKing);
            m.changeWeight(SAFE_UNSAFE * (isKing ? KING_MULTIPLIER : 1));
        } else {
            m.changeWeight(UNSAFE_UNSAFE * (isKing ? KING_MULTIPLIER : 1));
        }

        if(color == -1){
            if(ei < 5) {
                m.changeWeight(KING_ON_NEXT);
            }
        }
        else{
            if (ei > 28)m.changeWeight(KING_ON_NEXT);
        }

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 7; j++)
                board[i][j] = b[i][j];

    }

    private int getSafetyWeight() {

        int weight = 0;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0 || board[i][j] != color) continue;

                if(isSafe(i,j))
                    weight += SAFE;
                else
                    weight += UNSAFE;
            }


        return weight;
    }

    private boolean isSafe(int i, int j){
        if(i+color < 8 && i+color >= 0 && j < 7 && board[i+color][j+1] == -color) return false;
        if(i+color < 8 && i+color >= 0 && j > 0 && board[i+color][j-1] == -color) return false;

        if(kings.contains(positions[i][j])){
            if(i-color < 8 && i-color >= 0 && j < 7 && board[i-color][j+1] == -color) return false;
            if(i-color < 8 && i-color >= 0 && j > 0 && board[i-color][j-1] == -color) return false;
        }

        return true;
    }

    private ArrayList<Move> getMoves(){
        ArrayList<Move> moves = getSkips();
        if (!moves.isEmpty()) return moves;

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++){
                if(board[i][j] != color) continue;


                //move
                if(i + color < 8 && i + color >= 0 && j < 7 && board[i+color][j+1] == 0)
                    moves.add(new Move(positions[i][j], positions[i+color][j+1]));
                if(i + color < 8 && i + color >= 0 && j > 0 && board[i+color][j-1] == 0)
                    moves.add(new Move(positions[i][j], positions[i+color][j-1]));

                if(kings.contains(positions[i][j])){
                    if(i - color < 8 && i - color >= 0 && j < 7 && board[i-color][j+1] == 0)
                        moves.add(new Move(positions[i][j], positions[i-color][j+1]));
                    if(i - color < 8 && i - color >= 0 && j > 0 && board[i-color][j-1] == 0)
                        moves.add(new Move(positions[i][j], positions[i-color][j-1]));
                }

            }
        return moves;
    }

    private ArrayList<Move> getSkips() {
        ArrayList<Move> skips = new ArrayList<Move>();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != color) continue;


                //check for beat
                if (i + color*2 < 8 && i + color*2 >= 0  && j < 6 && board[i + color][j + 1] == -color && board[i + color*2][j + 2] == 0)
                    skips.add(new Move(positions[i][j],positions[i+color*2][j+2]));
                if (i + color*2 < 8 && i + color*2 >= 0 && j > 1 && board[i + color][j - 1] == -color && board[i + color*2][j - 2] == 0)
                    skips.add(new Move(positions[i][j],positions[i+color*2][j-2]));


                if(kings.contains(positions[i][j])){
                    if (i - color*2 < 8 && i - color*2 >= 0  && j < 6 && board[i - color][j + 1] == -color && board[i - color*2][j + 2] == 0)
                        skips.add(new Move(positions[i][j],positions[i-color*2][j+2]));
                    if (i - color*2 < 8 && i - color*2 >= 0 && j > 1 && board[i - color][j - 1] == -color && board[i - color*2][j - 2] == 0)
                        skips.add(new Move(positions[i][j],positions[i-color*2][j-2]));
                }

            }
        return skips;
    }

    private void zeroBoard(){
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                board[i][j] = 0;
    }

    private void setPosition(int color, int pos){
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if(positions[i][j] == pos){
                    board[i][j] = color;
                    return;
                }

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if(kings.contains(positions[i][j]) && board[i][j] != color)
                    kings.remove(positions[i][j]);
    }

}