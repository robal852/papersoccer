import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class PaperSoccerServer {
    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(58901)) {
            System.out.println("Paper Soccer Server is Running...");
            var pool = Executors.newFixedThreadPool(200);
            while (true) {
                Game game = new Game();
                pool.execute(game.new Player(listener.accept(), '1'));
                pool.execute(game.new Player(listener.accept(), '2'));
            }
        }
    }
}


class Game {
    Player currentPlayer;
    GameBoard gameBorad;
    BallPosition currentBallPosition;

    Game() {
        gameBorad = new GameBoard();
        currentBallPosition = new BallPosition(5, 7);
    }

    class BallPosition {
        int X, Y;

        BallPosition(int x, int y) {
            X = x;
            Y = y;
        }
    }

    public synchronized void move(int direction, Player player) {
        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } else if (player.opponent == null) {
            throw new IllegalStateException("You don't have an opponent yet");
        } else {
            switch (direction) {
                case 0:
                    if (gameBorad.board[currentBallPosition.X][currentBallPosition.Y].N) {
                        gameBorad.board[currentBallPosition.X][currentBallPosition.Y].N = false;
                        gameBorad.updatePositionState(currentBallPosition.X, currentBallPosition.Y);
                        gameBorad.board[currentBallPosition.X][currentBallPosition.Y - 1].S = false;
                        currentBallPosition.Y -= 1;//zmieniam pozycje piłki
                    } else {
                        throw new IllegalStateException("Line already occupied");
                    }
                    break;
                case 1:
                    if (gameBorad.board[currentBallPosition.X][currentBallPosition.Y].NE) {
                        gameBorad.board[currentBallPosition.X][currentBallPosition.Y].NE = false;
                        gameBorad.updatePositionState(currentBallPosition.X, currentBallPosition.Y);
                        gameBorad.board[currentBallPosition.X + 1][currentBallPosition.Y - 1].SW = false;
                        currentBallPosition.X += 1;
                        currentBallPosition.Y -= 1;
                    } else {
                        throw new IllegalStateException("Line already occupied");
                    }
                    break;
                case 2:
                    if (gameBorad.board[currentBallPosition.X][currentBallPosition.Y].E) {
                        gameBorad.board[currentBallPosition.X][currentBallPosition.Y].E = false;
                        gameBorad.updatePositionState(currentBallPosition.X, currentBallPosition.Y);
                        gameBorad.board[currentBallPosition.X + 1][currentBallPosition.Y].W = false;
                        currentBallPosition.X += 1;
                    } else {
                        throw new IllegalStateException("Line already occupied");
                    }
                    break;
                case 3:
                    if (gameBorad.board[currentBallPosition.X][currentBallPosition.Y].ES) {
                        gameBorad.board[currentBallPosition.X][currentBallPosition.Y].ES = false; //zmieniam ze juz ruszylem w gore
                        gameBorad.updatePositionState(currentBallPosition.X, currentBallPosition.Y);
                        gameBorad.board[currentBallPosition.X + 1][currentBallPosition.Y + 1].WN = false; //zmieniam ze juz w dol nie moge
                        currentBallPosition.X += 1;//zmieniam pozycje piłki
                        currentBallPosition.Y += 1;//zmieniam pozycje piłki
                    } else {
                        throw new IllegalStateException("Line already occupied");
                    }
                    break;
                case 4:
                    if (gameBorad.board[currentBallPosition.X][currentBallPosition.Y].S) {
                        gameBorad.board[currentBallPosition.X][currentBallPosition.Y].S = false;
                        gameBorad.updatePositionState(currentBallPosition.X, currentBallPosition.Y);
                        gameBorad.board[currentBallPosition.X][currentBallPosition.Y + 1].N = false;
                        currentBallPosition.Y += 1;
                    } else {
                        throw new IllegalStateException("Line already occupied");
                    }
                    break;
                case 5:
                    if (gameBorad.board[currentBallPosition.X][currentBallPosition.Y].SW) {
                        gameBorad.board[currentBallPosition.X][currentBallPosition.Y].SW = false;
                        gameBorad.updatePositionState(currentBallPosition.X, currentBallPosition.Y);
                        gameBorad.board[currentBallPosition.X - 1][currentBallPosition.Y + 1].NE = false;
                        currentBallPosition.X -= 1;
                        currentBallPosition.Y += 1;
                    } else {
                        throw new IllegalStateException("Line already occupied");
                    }
                    break;
                case 6:
                    if (gameBorad.board[currentBallPosition.X][currentBallPosition.Y].W) {
                        gameBorad.board[currentBallPosition.X][currentBallPosition.Y].W = false;
                        gameBorad.updatePositionState(currentBallPosition.X, currentBallPosition.Y);
                        gameBorad.board[currentBallPosition.X - 1][currentBallPosition.Y].E = false;
                        currentBallPosition.X -= 1;
                    } else {
                        throw new IllegalStateException("Line already occupied");
                    }
                    break;
                case 7:
                    if (gameBorad.board[currentBallPosition.X][currentBallPosition.Y].WN) {
                        gameBorad.board[currentBallPosition.X][currentBallPosition.Y].WN = false;
                        gameBorad.updatePositionState(currentBallPosition.X, currentBallPosition.Y);
                        gameBorad.board[currentBallPosition.X - 1][currentBallPosition.Y - 1].ES = false;
                        currentBallPosition.X -= 1;
                        currentBallPosition.Y -= 1;
                    } else {
                        throw new IllegalStateException("Line already occupied");
                    }
                    break;
            }
        }
        if (gameBorad.board[currentBallPosition.X][currentBallPosition.Y].positionState != 2) { // nie mozna juz odbic wiec zmieniam gracza
            currentPlayer = currentPlayer.opponent;
        }
        gameBorad.updatePositionState(currentBallPosition.X, currentBallPosition.Y);
    }

    class Player implements Runnable {
        char mark;
        Player opponent;
        Socket socket;
        Scanner input;
        PrintWriter output;

        public Player(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
        }

        @Override
        public void run() {
            try {
                setup();
                processCommands();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (opponent != null && opponent.output != null) {
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        private void setup() throws IOException {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("WELCOME " + mark);
            if (mark == '1') {
                currentPlayer = this;
                output.println("MESSAGE Waiting for opponent to connect");
            } else {
                opponent = currentPlayer;
                opponent.opponent = this;
                opponent.output.println("MESSAGE Your move");
            }
        }

        private void processCommands() {
            while (input.hasNextLine()) {
                var command = input.nextLine();
                if (command.startsWith("QUIT")) {
                    return;
                } else if (command.startsWith("MOVE")) {
                    processMoveCommand(Integer.parseInt(command.substring(5)));
                }
            }
        }

        private void processMoveCommand(int direction) {
            try {
                move(direction, this);
                output.println("VALID_MOVE " + direction);//zwracam temu co zagral gdzie zagral
                opponent.output.println("OPPONENT_MOVED " + direction);
                if (hasWinner()) {
                    if (currentBallPosition.Y == 1) {
                        output.println("VICTORY");
                        opponent.output.println("DEFEAT");
                    } else {
                        output.println("DEFEAT");
                        opponent.output.println("VICTORY");
                    }
                }
            } catch (IllegalStateException e) {
                output.println("MESSAGE " + e.getMessage());
            }
        }
    }


    public boolean hasWinner() {
        return gameBorad.board[currentBallPosition.X][currentBallPosition.Y].positionState == 0 || gameBorad.board[currentBallPosition.X][currentBallPosition.Y].positionState == 1;
    }


    class GameBoard {
        BallPositions[][] board;

        class BallPositions {
            boolean N, NE, E, ES, S, SW, W, WN;
            int positionState; //0 - przegranko, 1 - wygranko, 2 - mozna odbic, 3 - nie mozna odbic

            BallPositions(int x, int y) {

                if (x == 0 || y == 0 || x == 10 || y == 14 || ((y == 1 || y == 13) && (x == 1 || x == 2 || x == 3 || x == 7 || x == 8 || x == 9))) { //poza boiskiem
                    N = false;
                    NE = false;
                    E = false;
                    ES = false;
                    S = false;
                    SW = false;
                    W = false;
                    WN = false;
                    positionState = 3;
                } else if (x == 1 && y == 2) {//lewy gorny naroznik
                    N = false;
                    NE = false;
                    E = false;
                    ES = true;
                    S = false;
                    SW = false;
                    W = false;
                    WN = false;
                    positionState = 2;
                } else if (x == 4 && y == 1) { //i w bramce gornej
                    N = false;
                    NE = false;
                    E = false;
                    ES = true;
                    S = false;
                    SW = false;
                    W = false;
                    WN = false;
                    positionState = 1;
                } else if (x == 1 && y == 12) { //lewy dolny naroznik i
                    N = false;
                    NE = true;
                    E = false;
                    ES = false;
                    S = false;
                    SW = false;
                    W = false;
                    WN = false;
                    positionState = 2;
                } else if ((x == 4 && y == 13)) {//w bramce dolnej
                    N = false;
                    NE = true;
                    E = false;
                    ES = false;
                    S = false;
                    SW = false;
                    W = false;
                    WN = false;
                    positionState = 0;
                } else if (x == 1) { //lewa sciana
                    N = false;
                    NE = true;
                    E = true;
                    ES = true;
                    S = false;
                    SW = false;
                    W = false;
                    WN = false;
                    positionState = 2;
                } else if ((x == 9 && y == 2) || (x == 6 && y == 1)) { //gorne prawe narozniki
                    N = false;
                    NE = false;
                    E = false;
                    ES = false;
                    S = false;
                    SW = true;
                    W = false;
                    WN = false;
                    positionState = 2;
                } else if (((x == 9 && y == 12) || (x == 6 && y == 13))) { //dolne prawe narozniki
                    N = false;
                    NE = false;
                    E = false;
                    ES = false;
                    S = false;
                    SW = false;
                    W = false;
                    WN = true;
                    positionState = 2;
                } else if (x == 9) { //prawa sciana
                    N = false;
                    NE = false;
                    E = false;
                    ES = false;
                    S = false;
                    SW = true;
                    W = true;
                    WN = true;
                    positionState = 2;
                } else if (x == 5 && y == 1) {// w bramce u gory
                    N = false;
                    NE = false;
                    E = false;
                    ES = true;
                    S = true;
                    SW = true;
                    W = false;
                    WN = false;
                    positionState = 1;
                } else if ((y == 2 && (x == 2 || x == 3 || x == 7 || x == 8))) {// na scianie u gory
                    N = false;
                    NE = false;
                    E = false;
                    ES = true;
                    S = true;
                    SW = true;
                    W = false;
                    WN = false;
                    positionState = 2;
                } else if (x == 5 && y == 13) {//w bramce na dole
                    N = true;
                    NE = true;
                    E = false;
                    ES = false;
                    S = false;
                    SW = false;
                    W = false;
                    WN = true;
                    positionState = 0;
                } else if (y == 12 && (x == 2 || x == 3 || x == 7 || x == 8)) { // na scianie na dole
                    N = true;
                    NE = true;
                    E = false;
                    ES = false;
                    S = false;
                    SW = false;
                    W = false;
                    WN = true;
                    positionState = 2;
                } else if (x == 4 && y == 2) {//kąt bramki w polu
                    N = false;
                    NE = true;
                    E = true;
                    ES = true;
                    S = true;
                    SW = true;
                    W = false;
                    WN = false;
                    positionState = 2;
                } else if (x == 6 && y == 2) {
                    N = false;
                    NE = false;
                    E = false;
                    ES = true;
                    S = true;
                    SW = true;
                    W = true;
                    WN = true;
                    positionState = 2;
                } else if (x == 4 && y == 12) {
                    N = true;
                    NE = true;
                    E = true;
                    ES = true;
                    S = false;
                    SW = false;
                    W = false;
                    WN = true;
                    positionState = 2;
                } else if (x == 6 && y == 12) {
                    N = true;
                    NE = true;
                    E = false;
                    ES = false;
                    S = false;
                    SW = true;
                    W = true;
                    WN = true;
                    positionState = 2;
                } else {//w polu
                    N = true;
                    NE = true;
                    E = true;
                    ES = true;
                    S = true;
                    SW = true;
                    W = true;
                    WN = true;
                    positionState = 3;
                }
            }
        }

        void updatePositionState(int x, int y) {
            if (board[x][y].positionState == 2) {
                if (!(board[x][y].N || board[x][y].NE || board[x][y].E || board[x][y].ES || board[x][y].S || board[x][y].SW || board[x][y].W || board[x][y].WN)) {
                    board[x][y].positionState = 0;//przegranko
                }
            } else if (board[x][y].positionState == 3) {
                if ((board[x][y].N || board[x][y].NE || board[x][y].E || board[x][y].ES || board[x][y].S || board[x][y].SW || board[x][y].W || board[x][y].WN)) {
                    board[x][y].positionState = 2;//jesli ktorykolwiek prawdziwy to juz moge tedy odbijac
                }
            }
        }

        GameBoard() {
            board = new BallPositions[11][15];
            for (int x = 0; x < 11; x++) {
                for (int y = 0; y < 15; y++) {
                    board[x][y] = new BallPositions(x, y);
                }
            }
        }
    }
}

/*
   0 1 2 3 4 5 6 7 8 9 10
 0.                      .
 1         |   |
 2   |               |
 3   |               |
 4   |               |
 5   |               |
 6   |       .       |
 7   |               |
 8   |               |
 9   |               |
10   |               |
11   |               |
12         |   |
13.                      .


                7    0   1

directions:     6   .    2

                5    4   3

 */