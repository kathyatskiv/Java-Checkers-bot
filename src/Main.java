import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Connect connection = new Connect("team2");

        GameInfoResponse.GIData curdata  = connection.getInfo();
    }
}
