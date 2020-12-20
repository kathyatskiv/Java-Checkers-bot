import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.Base64;


public class Connect {
    public static String color;
    public static String token;

    Connect(String name) throws IOException {
        ServerConnectionResponse.SCRData staticdata = connectToServer(name);
        color = staticdata.color;
        token = staticdata.token;

    }
    public MoveResponse sendMove(String token, Move move) throws IOException {
        HttpURLConnection connection = ((HttpURLConnection) new URL(url + "move").openConnection());
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json; " + charset);
        connection.setDoOutput(true);
        String auth_header_value = Base64.getEncoder().encodeToString(("Token " + token).getBytes());
        connection.setRequestProperty("Authorization", "Token " + token);
        String stringified = String.format("{\n    \"move\": %s\n}", move.toString());
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = stringified.getBytes(charset);
            os.write(input, 0, input.length);
        }

        if(connection.getResponseCode() == 400){
            System.out.println("Error 400");
        }

        InputStream response = connection.getInputStream();
        Reader reader = new InputStreamReader(response, charset);
        MoveResponse result = new Gson().fromJson(reader, MoveResponse.class);

        System.out.println(result.status);
        System.out.println(result.data);
        return result;
    }

    private static String url = "http://0.0.0.0:8081/";
    private static String charset = "UTF-8";

    public ServerConnectionResponse.SCRData connectToServer(String team_name) throws IOException, ConnectException {
        String query = "team_name=" + URLEncoder.encode(team_name, charset);

        URLConnection connection = new URL(url + "game?" + query).openConnection();
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

        OutputStream output = connection.getOutputStream();
        output.write(query.getBytes(charset));

        InputStream response = connection.getInputStream();
        Reader reader = new InputStreamReader(response, charset);
        ServerConnectionResponse result = new Gson().fromJson(reader, ServerConnectionResponse.class);
        System.out.println("Status: " + result.status);
        System.out.println("Color: " + result.data.color);
        System.out.println("Token: " + result.data.token);
        return result.data;
    }

    public GameInfoResponse.GIData getInfo() throws IOException {
        String query = "game";
        URLConnection connection = new URL(url + query).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        InputStream response = connection.getInputStream();
        Reader reader = new InputStreamReader(response, charset);
        GameInfoResponse result = new Gson().fromJson(reader, GameInfoResponse.class);

        System.out.println(result.data.status);
        System.out.println("Whose turn: " + result.data.whose_turn);
        System.out.println("Winner: " + result.data.winner);
        System.out.println("Time: " + result.data.available_time);
        try { System.out.println("Last player: " + result.data.last_move.player); } catch (NullPointerException e){}
        try { System.out.println("Last moves: " + result.data.last_move.moves); } catch (NullPointerException e){}

//		for(GameInfoResponse.Tile tile : result.data.board)
//			System.out.println("(Pos: "+tile.position+", Color: "+tile.color+")");

        return result.data;
    }

}

class ServerConnectionResponse {
    String status;
    SCRData data;

    class SCRData {
        String color;
        String token;
    }
}

class GameInfoResponse{
    GIData data;

    class GIData {
        String status;
        String whose_turn;
        String winner;
        String available_time;
        LastMove last_move;
        Tile[] board;
    }

    class Tile{
        int position;
        String color;
    }

    class LastMove {
        String player;
        int[][] moves;
    }
}

class MoveResponse{
    String status;
    String data;
}

