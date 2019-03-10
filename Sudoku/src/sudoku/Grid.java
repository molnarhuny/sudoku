package sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Grid {
    private File file;
    private int[][] m_board;

    
    public Grid() {
        file = null;
        m_board = new int[9][9];

        //Initialise
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                m_board[i][j] = 0;
            }
        }

    }

    /**
     * Copy constructor
     * 
     */
    public Grid(Grid original) {
        file = original.file;
        m_board = new int[9][9];

        //Copy board
        for (int i = 0; i < 9; i++) {
            System.arraycopy(original.m_board[i], 0, m_board[i], 0, 9);
        }
    }


    public int getCell(int i, int j) {
        return m_board[i][j];
    }

    public void setCell(int value, int row, int col) {
        m_board[row][col] = value;
    }


    public void setFile(File fi) {
        file = fi;
    }

    public File getFile() {
        return file;
    }

    /**
     * Loads Sudoku from a file
     */
    public void loadBoard() {
        FileReader fr = null;
        String line;
        int i;

        try {
            fr = new FileReader(file.getPath());
            BufferedReader bf = new BufferedReader(fr);
            try {
                i = 0;
                while ((line = bf.readLine()) != null) {
                    String[] num = line.split(" "); 

                    for (int j = 0; j < num.length; j++) {
                        m_board[i][j] = Integer.parseInt(num[j]);
                    }
                    i++;
                }
            } catch (IOException e1) {
                System.out.println("Error reading file:" + file.getName());
            }
        } catch (FileNotFoundException e2) {
            System.out.println("Error opening file: " + file.getName());
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e3) {
                System.out.println("Error closing file: " + file.getName());
            }
        }
    }

    public void cleanBoard() {
        //Iterate through the board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                m_board[i][j] = 0;
            }
        }
    }


    public boolean emptyBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (m_board[i][j] != 0) { 
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * repeated numbers
     */
    public boolean checkMatrix(int row, int col) {
        int i, j, central;
        boolean correct = false;

        central = m_board[row][col];
        i = row - 1;
        while (i <= row + 1 && correct) {
            j = col - 1;
            while (j <= col + 1 && correct) {
                if (i != row && j != col && central == m_board[i][j]) {
                    correct = true;
                }
                j++;
            }
            i++;
        }
        return correct;
    }

    /**
     * correct answer
     * 
     */
    public boolean checkBoard() {
        int value;


        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                value = m_board[i][j];

                if (value == 0) {
                    return false;
                }

                //Check for repeated numbers on the row
                for (int z = 0; z < 9; z++) {
                    if (j != z) {
                        if (value == m_board[i][z]) {
                            return false;
                        }
                    }
                }

                //Check for repeated numbers on the column
                for (int z = 0; z < 9; z++) {
                    if (i != z) {
                        if (value == m_board[z][j]) {
                            return false;
                        }
                    }
                }
            }
        }
        //check sub matrix
        for (int i = 1; i <= 7; i += 3) {
            for (int j = 1; j <= 7; j += 3) {
                if (checkMatrix(i, j)) {
                    return false;
                }
            }
        }

        return true;
    }
}
