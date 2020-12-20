import java.io.IOException;
import java.util.FormatFlagsConversionMismatchException;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int TIME = 10;

//    private static int[][] board =
//            {{1,0,1,0,1,0,1,0},
//             {0,1,0,1,0,1,0,1},
//                    {1,0,1,0,1,0,1,0},
//                    {0,0,0,0,0,0,0,0},
//                    {0,0,0,0,0,0,0,0},
//                    {0,-1,0,-1,0,-1,0,-1},
//                    {-1,0,-1,0,-1,0,-1,0},
//                    {0,-1,0,-1,0,-1,0,-1}};
//
//    private static void zeroBoard(){
//        for(int i = 0; i < 8; i++)
//            for(int j = 0; j < 8; j++)
//                board[i][j] = 0;
//    }
//
//    private static void setPosition(int color, int pos){
//        for(int i = 0; i < 8; i++)
//            for(int j = 0; j < 8; j++)
//                if(positions[i][j] == pos){
//                    board[i][j] = color;
//                    return;
//                }
//    }
//
//    private static final int[][] positions =
//            {{4,0,3,0,2,0,1,0},
//             {0,8,0,7,0,6,0,5},
//             {12,0,11,0,10,0,9,0},
//             {0,16,0,15,0,14,0,13},
//             {20,0,19,0,18,0,17,0},
//             {0,24,0,23,0,22,0,21},
//             {28,0,27,0,26,0,25,0},
//             {0,32,0,31,0,30,0,29}};

    public static void main(String[] args) throws IOException, InterruptedException {
        Connect connection = new Connect("team1");

        GameInfoResponse.GIData curdata;

        do{
           curdata = connection.getInfo();
           if(curdata.winner != null) break;

           if(curdata.whose_turn.equals(Connect.color)){
               Movement curmv = new Movement(Connect.color, connection);
               connection.sendMove(Connect.token, curmv.makeMove());
           }
           else TimeUnit.SECONDS.sleep(1);
//           zeroBoard();
//            for(GameInfoResponse.Tile tile : curdata.board){
//                setPosition(tile.color.length() == 3 ? 1 : -1, tile.position);
//            }
//
//            for(int i = 0; i < 8; i++){
//                for(int j = 0; j < 7; j++)
//                    System.out.print(board[i][j]);
//                System.out.println(board[i][7]);
//            }

//           if(curdata.whose_turn.length() == Connect.color.length()){
//               Move mv = makeMove(Connect.color.length() == 3 ? 1 : 2);
//               System.out.println("MOVE:" + mv);
//               connection.sendMove(Connect.token,mv);
//           }
        } while (curdata.winner == null);

    }

//    private static Move makeMove(int color){
//        System.out.println("COLOR " + color);
//        if(color == 1){
//            for(int i = 0; i < 8; i++)
//                for(int j = 0; j < 8; j++){
//                    if(board[i][j] == 0 || board[i][j] == 2) continue;
//
//                    System.out.println(i + " " + j + " " + board[i][j]);
//
//                    //check for beat
//                    if(i < 6 && j < 6 && board[i+1][j+1] == 2 && board[i+2][j+2] == 0)
//                        return new Move(positions[i][j], positions[i+2][j+2]);
//                    if(i < 6 && j > 1 && board[i+1][j-1] == 2 && board[i+2][j-2] == 0)
//                        return new Move(positions[i][j], positions[i+2][j-2]);
//
//                }
//            for(int i = 0; i < 8; i++)
//                for(int j = 0; j < 8; j++){
//                    if(board[i][j] == 0 || board[i][j] == 2) continue;
//
//                    System.out.println(i + " " + j + " " + board[i][j]);
//
//                    //move
//                    if(i < 7 && j < 7 && board[i+1][j+1] == 0)
//                        return new Move(positions[i][j], positions[i+1][j+1]);
//                    if(i < 7 && j > 0 && board[i+1][j-1] == 0)
//                        return new Move(positions[i][j], positions[i+1][j-1]);
//
//                }
//        }
//        else {
//            for(int i = 0; i < 8; i++)
//                for(int j = 0; j < 8; j++){
//                    if(board[i][j] == 0 || board[i][j] == 1) continue;
//
//                    System.out.println(i + " " + j + " " + positions[i][j]);
//                    //check for beat
//                    if(i > 1 && j < 6 && board[i-1][j+1] == 1 && board[i-2][j+2] == 0)
//                        return new Move(positions[i][j], positions[i-2][j+2]);
//                    if(i > 1 && j > 1 && board[i-1][j-1] == 1 && board[i-2][j-2] == 0)
//                        return new Move(positions[i][j], positions[i-2][j-2]);
//
//                }
//            for(int i = 0; i < 8; i++)
//                for(int j = 0; j < 8; j++){
//                    if(board[i][j] == 0 || board[i][j] == 1) continue;
//
//                    System.out.println(i + " " + j + " " + positions[i][j]);
//
//                    //move
//                    if(i > 0 && j < 7 && board[i-1][j+1] == 0)
//                        return new Move(positions[i][j], positions[i-1][j+1]);
//                    if(i > 0 && j > 0 && board[i-1][j-1] == 0)
//                        return new Move(positions[i][j], positions[i-1][j-1]);
//
//                }
//
//        }
//        return new Move(9,13);
//    }


}
