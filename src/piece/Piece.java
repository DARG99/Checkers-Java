/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package piece;

import checkers2d.Board;
import checkers2d.GamePanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author Diogo
 */
public class Piece {

    public BufferedImage image;

    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    boolean isCrowned;

    public Piece(int color, int col, int row, boolean isCrowned) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
        image = getImage(color);
        this.isCrowned = isCrowned;

    }

    public BufferedImage getImage(int color) {

        if (color == GamePanel.RED) {
            try {
                image = ImageIO.read(getClass().getResourceAsStream(("/res/VERMELHA.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                image = ImageIO.read(getClass().getResourceAsStream(("/res/PRETA.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return image;
    }

    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public void updatePosition() {
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);

    }

    public void setCrowned() {
        int row = this.getRow(y);

        if (this.color == GamePanel.RED && row == 0) {
            try {
                this.image = ImageIO.read(getClass().getResourceAsStream(("/res/VERMELHA_QUEEN.png")));
                this.isCrowned = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (this.color == GamePanel.BLACK && row == 7) {
            try {
                this.image = ImageIO.read(getClass().getResourceAsStream(("/res/PRETA_QUEEN.png")));
                this.isCrowned = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean canMove(int targetCol, int targetRow, int currentPlayer) {
        Piece hittingPiece = getHittingPiece(targetCol, targetRow);

        if (isWithinBounds(targetCol, targetRow)) {
            if (currentPlayer == 0) {
                if (hittingPiece != null) {

                    return false;
                }

                if (targetRow - preRow == -1 && Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {
                    return true;
                }

            } else {
                if (hittingPiece != null) {

                    return false;
                }
                if (targetRow - preRow == 1 && Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isWithinBounds(int targetCol, int targetRow) {
        return (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7);

    }

    public Piece getHittingPiece(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }

        return null;

    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

    public int findPieceIndex(int targetCol, int targetRow) {
        for (int i = 0; i < GamePanel.simPieces.size(); i++) {
            Piece piece = GamePanel.simPieces.get(i);
            if (piece.col == targetCol && piece.row == targetRow) {
                return i;  // Return the index of the found piece
            }
        }
        return -1;  // Return -1 if the piece is not found
    }

    /*public boolean checkForCapturePiece(int targetCol, int targetRow, int currentColor) {
        Piece hittingPiece = getHittingPiece(targetCol, targetRow);
        if (this.isCrowned && hittingPiece != null) {

            if (Math.abs(this.preRow - targetRow) == Math.abs(this.preCol - targetCol)) {
                int captureCol = 2 * targetCol - preCol;
                int captureRow = 2 * targetRow - preRow;
                if (isWithinBounds(captureCol, captureRow)
                        && getHittingPiece(captureCol, captureRow) == null && hittingPiece != null && hittingPiece.color != currentColor) {

                    return true;
                }
            }

        } else {
            if (Math.abs(this.row - targetRow) + Math.abs(this.col - targetCol) < 2) {
                int captureCol = 2 * targetCol - preCol;
                int captureRow = 2 * targetRow - preRow;
                if (isWithinBounds(captureCol, captureRow)
                        && getHittingPiece(captureCol, captureRow) == null && hittingPiece != null && hittingPiece.color != currentColor) {

                    return true;

                }
            }

        }
        return false;

    }*/
    public ArrayList<int[]> checkForCapturePiece(int currentColor) {
        ArrayList<int[]> positionsPieces = new ArrayList<>();
        int[][] directions = {
            {1, 1},
            {-1, 1},
            {1, -1},
            {-1, -1}};
        if (this.isCrowned) {
            for (int i = 0; i < directions.length; i++) {
                int colPosition = this.preCol + directions[i][0];
                int rowPosition = this.preRow + directions[i][1];
                while (isWithinBounds(colPosition, rowPosition)) {
                    Piece piece = getHittingPiece(colPosition, rowPosition);
                    if (piece != null && piece.color != this.color) {
                        int[] positions = {colPosition, rowPosition, directions[i][0], directions[i][1]};
                        positionsPieces.add(positions);
                        break;

                    }
                    colPosition += directions[i][0];
                    rowPosition += directions[i][1];

                }

            }

        } else {
            if (this.color == 0) {
                for (int i = 2; i < directions.length; i++) {

                    int colPosition = this.preCol + directions[i][0];
                    int rowPosition = this.preRow + directions[i][1];
                    if (isWithinBounds(colPosition, rowPosition)) {
                        Piece piece = getHittingPiece(colPosition, rowPosition);
                        if (piece != null && piece.color != this.color ) {
                            int[] positions = {colPosition, rowPosition, directions[i][0], directions[i][1]};
                            positionsPieces.add(positions);

                        }
                        colPosition += directions[i][0];
                        rowPosition += directions[i][1];
                    }

                }

            } else {
                for (int i = 0; i < 2; i++) {
                    int colPosition = this.preCol + directions[i][0];
                    int rowPosition = this.preRow + directions[i][1];
                    if (isWithinBounds(colPosition, rowPosition)) {
                        Piece piece = getHittingPiece(colPosition, rowPosition);
                        if (piece != null && piece.color != this.color) {
                            int[] positions = {colPosition, rowPosition, directions[i][0], directions[i][1]};
                            positionsPieces.add(positions);

                        }
                        colPosition += directions[i][0];
                        rowPosition += directions[i][1];

                    }
                }
            }

        }
        return positionsPieces;

    }

}
