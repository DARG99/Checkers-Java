/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;
import piece.Piece;

/**
 *
 * @author Diogo
 */
public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    public static final int RED = 0;
    public static final int BLACK = 1;
    int currentColor = RED;

    //PIECES
    public static ArrayList<Piece> pieces = new ArrayList<Piece>();
    public static ArrayList<Piece> simPieces = new ArrayList<Piece>();
    Piece activeP;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        setPieces();
        copyPieces(pieces, simPieces);

    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {
        //RED
        pieces.add(new Piece(RED, 0, 5, false));
        pieces.add(new Piece(RED, 2, 5, false));
        pieces.add(new Piece(RED, 4, 5, false));
        pieces.add(new Piece(RED, 6, 5, false));
        pieces.add(new Piece(RED, 1, 6, false));
        pieces.add(new Piece(RED, 3, 6, false));
        pieces.add(new Piece(RED, 5, 6, false));
        pieces.add(new Piece(RED, 7, 6, false));
        pieces.add(new Piece(RED, 0, 7, false));
        pieces.add(new Piece(RED, 2, 7, false));
        pieces.add(new Piece(RED, 4, 7, false));
        pieces.add(new Piece(RED, 6, 7, false));

        //BLAC
        pieces.add(new Piece(BLACK, 1, 0, false));
        pieces.add(new Piece(BLACK, 3, 0, false));
        pieces.add(new Piece(BLACK, 5, 0, false));
        pieces.add(new Piece(BLACK, 7, 0, false));
        pieces.add(new Piece(BLACK, 0, 1, false));
        pieces.add(new Piece(BLACK, 2, 1, false));
        pieces.add(new Piece(BLACK, 4, 1, false));
        pieces.add(new Piece(BLACK, 6, 1, false));
        pieces.add(new Piece(BLACK, 1, 2, false));
        pieces.add(new Piece(BLACK, 3, 2, false));
        pieces.add(new Piece(BLACK, 5, 2, false));
        pieces.add(new Piece(BLACK, 7, 2, false));

    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

        }
    }

    private void update() {
        if (mouse.pressed) {
            if (activeP == null) {

                for (Piece piece : simPieces) {
                    if (piece.color == currentColor && piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                        activeP = piece;
                    }
                }
            } else {
                simulate();
            }
        }
        if (mouse.pressed == false) {
            if (activeP != null) {
                ArrayList<int[]> captureOptions = activeP.checkForCapturePiece(currentColor);

                if (!captureOptions.isEmpty()) {
                    for (int i = 0; i < captureOptions.size(); i++) {
                        int colNewPos = captureOptions.get(i)[0] + captureOptions.get(i)[2];
                        int rowNewPos = captureOptions.get(i)[1] + captureOptions.get(i)[3];
                        /*System.out.println("col: " + captureOptions.get(i)[0]);
                        System.out.println("row: " + captureOptions.get(i)[1]);
                        System.out.println("colDirection: " + captureOptions.get(i)[2]);
                        System.out.println("rowDirection: " + captureOptions.get(i)[3]);*/
                        
           
                        
                        if (activeP.getHittingPiece(rowNewPos, colNewPos) != null) {
                            System.out.println("here");
                            

                            if (activeP.getCol(activeP.x) == colNewPos && activeP.getRow(activeP.y) == rowNewPos) {

                                int index = activeP.findPieceIndex(captureOptions.get(i)[0], captureOptions.get(i)[1]);
                                simPieces.remove(index);
                                activeP.updatePosition();
                                activeP.setCrowned();
                                if (currentColor == RED) {
                                    currentColor = BLACK;
                                } else {
                                    currentColor = RED;

                                }
                                break;
                            }

                        }

                    }
                }

                if (activeP.canMove(activeP.getCol(activeP.x), activeP.getRow(activeP.y), currentColor)) {

                    activeP.updatePosition();
                    activeP.setCrowned();
                    //change player
                    activeP = null;
                    if (currentColor == RED) {
                        currentColor = BLACK;
                    } else {
                        currentColor = RED;

                    }

                } else {
                    activeP.col = activeP.preCol;
                    activeP.row = activeP.preRow;
                    activeP.updatePosition();
                    activeP = null;
                }

            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        board.draw(g2);

        for (Piece p : simPieces) {
            p.draw(g2);
        }
        if (activeP != null) {
            if (activeP.canMove(activeP.getCol(activeP.x), activeP.getRow(activeP.y), currentColor)) {
                g2.setColor(Color.white);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                activeP.draw(g2);
            }
//checar se pode dar o hightlight para capturar
            if (activeP.checkForCapturePiece(currentColor) != null) {
                for (int[] pieces : activeP.checkForCapturePiece(currentColor)) {

                    int col = pieces[0];
                    int row = pieces[1];
                    if (activeP.isWithinBounds(col + pieces[2], row + pieces[3])) {
                        g2.setColor(Color.white);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                        g2.fillRect(col * Board.SQUARE_SIZE, row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                        activeP.draw(g2);
                    }

                }
            }
        }

        //status messages
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        g2.drawString("Current Player: " + (currentColor == RED ? "Red" : "Black"), 700, 250);

    }

    private void simulate() {
        //mexer a peça
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;

        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);
        System.out.println("Simulated Position: (" + activeP.col + ", " + activeP.row + ")");
        activeP.checkForCapturePiece(currentColor);

    }

}
