import java.io.IOException;
import java.util.FormatFlagsConversionMismatchException;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int TIME = 10;

    public static void main(String[] args) throws IOException, InterruptedException {
        Connect connection = new Connect("team2");

        GameInfoResponse.GIData curdata;

        do{
            curdata = connection.getInfo();
            if(curdata.status.equals("Game is over") || curdata.winner != null) break;

            if(curdata.whose_turn.equals(Connect.color) && curdata.winner == null){
                Movement curmv = new Movement(Connect.color, connection);

                try {
                    connection.sendMove(Connect.token, curmv.makeMove());
                } catch (Exception e) { continue;}

            }
            else TimeUnit.SECONDS.sleep(1);

        } while (curdata.winner == null);

    }


}
