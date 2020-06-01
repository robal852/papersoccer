import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.*;


public class PaperSoccerClient {
    private JFrame frame = new JFrame("Paper Soccer");
    JLabel messageLabel = new JLabel("...");
    private Socket socket;
    private Scanner in;
    public static PrintWriter out;
    public static Point ballPosition = new Point(225, 325);//na poczatku pilka tu


    public PaperSoccerClient(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, 58901);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);

        JPanelWithBackground panel = new JPanelWithBackground("tloPilka.png");
        panel.addMouseListener(panel);
        frame.getContentPane().add(panel);//ladnie rysowanko
    }

    public void play() throws Exception {
        try {
            String response = in.nextLine();
            char mark = response.charAt(8);
            frame.setTitle("Paper Soccer: Player " + mark);
            while (in.hasNextLine()) {
                response = in.nextLine();
                if (response.startsWith("VALID_MOVE")) {
                    int youMoved = Integer.parseInt(response.substring(11)); //tutaj tez potrzebuje info ktory moj ruch byl valid!!! zebym wiedzial gdzie przestawic pilke!!
                    // System.out.println(youMoved);
                    int x1 = ballPosition.x;
                    int y1 = ballPosition.y;
                    int x2 = 0;
                    int y2 = 0;
                    switch (youMoved) {
                        case 0:
                            x2 = ballPosition.x;
                            y2 = ballPosition.y - 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.y -= 50;
                            break;
                        case 1:
                            x2 = ballPosition.x + 50;
                            y2 = ballPosition.y - 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x += 50;
                            ballPosition.y -= 50;
                            break;
                        case 2:
                            x2 = ballPosition.x + 50;
                            y2 = ballPosition.y;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x += 50;
                            break;
                        case 3:
                            x2 = ballPosition.x + 50;
                            y2 = ballPosition.y + 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x += 50;
                            ballPosition.y += 50;
                            break;
                        case 4:
                            x2 = ballPosition.x;
                            y2 = ballPosition.y + 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.y += 50;
                            break;
                        case 5:
                            x2 = ballPosition.x - 50;
                            y2 = ballPosition.y + 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x -= 50;
                            ballPosition.y += 50;
                            break;
                        case 6:
                            x2 = ballPosition.x - 50;
                            y2 = ballPosition.y;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x -= 50;
                            break;
                        case 7:
                            x2 = ballPosition.x - 50;
                            y2 = ballPosition.y - 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x -= 50;
                            ballPosition.y -= 50;
                            break;
                    }
                    messageLabel.setText("Valid move, please wait");
//                    currentSquare.setText(mark);
//                    currentSquare.repaint();
                } else if (response.startsWith("OPPONENT_MOVED")) {
                    int opponentMoved = Integer.parseInt(response.substring(15));
                    int x1 = ballPosition.x;
                    int y1 = ballPosition.y;
                    int x2 = 0;
                    int y2 = 0;
                    switch (opponentMoved) {
                        case 0:
                            x2 = ballPosition.x;
                            y2 = ballPosition.y - 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.y -= 50;
                            break;
                        case 1:
                            x2 = ballPosition.x + 50;
                            y2 = ballPosition.y - 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x += 50;
                            ballPosition.y -= 50;
                            break;
                        case 2:
                            x2 = ballPosition.x + 50;
                            y2 = ballPosition.y;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x += 50;
                            break;
                        case 3:
                            x2 = ballPosition.x + 50;
                            y2 = ballPosition.y + 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x += 50;
                            ballPosition.y += 50;
                            break;
                        case 4:
                            x2 = ballPosition.x;
                            y2 = ballPosition.y + 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.y += 50;
                            break;
                        case 5:
                            x2 = ballPosition.x - 50;
                            y2 = ballPosition.y + 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x -= 50;
                            ballPosition.y += 50;
                            break;
                        case 6:
                            x2 = ballPosition.x - 50;
                            y2 = ballPosition.y;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x -= 50;
                            break;
                        case 7:
                            x2 = ballPosition.x - 50;
                            y2 = ballPosition.y - 50;
                            JPanelWithBackground.lines.addLast(new JPanelWithBackground.Line(x1, y1, x2, y2, Color.BLACK));
                            frame.repaint();
                            ballPosition.x -= 50;
                            ballPosition.y -= 50;
                            break;
                    }


                    //  System.out.println(opponentMoved);

                    messageLabel.setText("Opponent moved, your turn");
                } else if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                } else if (response.startsWith("VICTORY")) {
                    JOptionPane.showMessageDialog(frame, "Winner Winner");
                    break;
                } else if (response.startsWith("DEFEAT")) {
                    JOptionPane.showMessageDialog(frame, "Sorry you lost");
                    break;
                } else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                    JOptionPane.showMessageDialog(frame, "Other player left");
                    break;
                }
            }
            out.println("QUIT");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
            frame.dispose();
        }
    }


    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            System.err.println("Pass the server IP as the sole command line argument");
//            return;
//        }
//        PaperSoccerClient client = new PaperSoccerClient(args[0]);
        PaperSoccerClient client = new PaperSoccerClient("192.168.1.33");//moj adres

        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setSize(450, 650);
        client.frame.setVisible(true);
        client.frame.setResizable(false);
        client.play();
    }
}


class JPanelWithBackground extends JPanel implements MouseListener {
    private Image backgroundImage;

    public JPanelWithBackground(String fileName) throws IOException {
        backgroundImage = ImageIO.read(new File(fileName));
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image.
        g.drawImage(backgroundImage, 0, 0, this);

        for (Line line : lines) {
            g.drawLine(line.x1, line.y1, line.x2, line.y2);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // System.out.println("here was a click ! ");                //tylko gdzie ustawie pozycje pilki? jak serwer odpoowie?
        Point newBallPosition = e.getPoint();
        int xdif = PaperSoccerClient.ballPosition.x - newBallPosition.x;
        int ydif = PaperSoccerClient.ballPosition.y - newBallPosition.y;
        if (ydif > 40 && xdif > -40 && xdif < 40) {
            PaperSoccerClient.out.println("MOVE " + 0);
        } else if (ydif > 40 && xdif < -40) {
            PaperSoccerClient.out.println("MOVE " + 1);
        } else if (xdif < -40 && ydif < 40 && ydif > -40) {
            PaperSoccerClient.out.println("MOVE " + 2);
        } else if (xdif < -40 && ydif < -40) {
            PaperSoccerClient.out.println("MOVE " + 3);
        } else if (ydif < -40 && xdif > -40 && xdif < 40) {                                 //tu obliczyc roznice klikniecia a pozycji pilki!!
            PaperSoccerClient.out.println("MOVE " + 4);
        } else if (xdif > 40 && ydif < -40) {
            PaperSoccerClient.out.println("MOVE " + 5);
        } else if (xdif > 40 && ydif > -40 && ydif < 40) {
            PaperSoccerClient.out.println("MOVE " + 6);
        } else if (xdif > 40 && ydif > 40) {
            PaperSoccerClient.out.println("MOVE " + 7);
        }
        // System.out.println(e.getPoint());//gites point mi mowi co i jak i gdzie  //teraz w zaleznosoci od tego gdzie mam pointa to taka liczbe dam
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public static class Line {
        final int x1;
        final int y1;
        final int x2;
        final int y2;
        final Color color;

        public Line(int x1, int y1, int x2, int y2, Color color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
    }

    public static LinkedList<Line> lines = new LinkedList<Line>();


}
