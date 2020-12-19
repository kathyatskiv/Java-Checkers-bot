import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    private static int[][] board =
            {{1,0,1,0,1,0,1,0},
             {0,1,0,1,0,1,0,1},
             {1,0,1,0,1,0,1,0},
             {0,0,0,0,0,0,0,0},
             {0,0,0,0,0,0,0,0},
             {0,2,0,2,0,2,0,2},
             {2,0,2,0,2,0,2,0},
             {0,2,0,2,0,2,0,2}};

    private static void zeroBoard(){
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                board[i][j] = 0;
    }

    private static void setPosition(String color, int pos){
        int posi = 0;
        int posj = 0;

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if(positions[i][j] == pos){
                    posi = i;
                    posj = j;
                    break;
                }

        board[posi][posj] = color == "RED" ? 1 : 2;
    }

    private static final int[][] positions =
            {{4,0,3,0,2,0,1,0},
             {0,8,0,7,0,6,0,5},
             {12,0,11,0,10,0,9,0},
             {0,16,0,15,0,14,0,13},
             {20,0,19,0,18,0,17,0},
             {0,24,0,23,0,22,0,21},
             {28,0,27,0,25,0,25,0},
             {0,32,0,31,0,30,0,29}};

    public static void main(String[] args) throws IOException, InterruptedException {
        Connect connection = new Connect("team1");

        GameInfoResponse.GIData curdata;
        do{
           curdata = connection.getInfo();
           zeroBoard();
            for(GameInfoResponse.Tile tile : curdata.board){
                setPosition(tile.color,tile.position);
            }

//            System.out.println("MY COLOR:"+(Connect.color.length()==curdata.whose_turn.length()));
//            System.out.println("WHOSE TURN:"+curdata.whose_turn);
//            System.out.println("MY TURN: "+ curdata.whose_turn==Connect.color);

           if(curdata.whose_turn.length() == Connect.color.length())
               connection.sendMove(Connect.token,makeMove(Connect.color == "RED" ? 1 : 2));

            TimeUnit.SECONDS.sleep(10);
        } while (curdata.winner == null);

    }

    private static Move makeMove(int color){
        if(color == 1){
            for(int i = 0; i < 8; i++)
                for(int j = 0; j < 8; j++){
                    if(board[i][j] == 0 || board[i][j] == 2) continue;

                    //check for beat
                    try
                    {
                        if(board[i+1][j+1] == 2 && board[i+2][j+2] == 0)
                            return new Move(positions[i][j], positions[i+2][j+2]);
                        if(board[i+1][j-1] == 2 && board[i+2][j-2] == 0)
                            return new Move(positions[i][j], positions[i+2][j-2]);
                        if(board[i-1][j+1] == 2 && board[i-2][j+2] == 0)
                            return new Move(positions[i][j], positions[i-2][j+2]);
                        if(board[i-1][j-1] == 2 && board[i-2][j-2] == 0)
                            return new Move(positions[i][j], positions[i-2][j-2]);
                    } catch (ArrayIndexOutOfBoundsException aioobe){}


                    //move
                    try{
                        if(board[i+1][j+1] == 0)
                            return new Move(positions[i][j], positions[i+1][j+1]);
                        if(board[i+1][j-1] == 0)
                            return new Move(positions[i][j], positions[i+1][j-1]);
                    } catch (ArrayIndexOutOfBoundsException aioobe){}
                }
        }
        else {
            for(int i = 0; i < 8; i++)
                for(int j = 0; j < 8; j++){
                    if(board[i][j] == 0 || board[i][j] == 1) continue;

                    //check for beat
                    try
                    {
                        if(board[i+1][j+1] == 1 && board[i+2][j+2] == 0)
                            return new Move(positions[i][j], positions[i+2][j+2]);
                        if(board[i+1][j-1] == 1 && board[i+2][j-2] == 0)
                            return new Move(positions[i][j], positions[i+2][j-2]);
                        if(board[i-1][j+1] == 1 && board[i-2][j+2] == 0)
                            return new Move(positions[i][j], positions[i-2][j+2]);
                        if(board[i-1][j-1] == 1 && board[i-2][j-2] == 0)
                            return new Move(positions[i][j], positions[i-2][j-2]);
                    } catch (ArrayIndexOutOfBoundsException aioobe){}


                    //move
                    try{
                        if(board[i-1][j+1] == 0)
                            return new Move(positions[i][j], positions[i+1][j+1]);
                        if(board[i-1][j-1] == 0)
                            return new Move(positions[i][j], positions[i+1][j-1]);
                    } catch (ArrayIndexOutOfBoundsException aioobe){}
                }
        }
        return new Move(1,2);
    }


}
