package sudoku;


public class Main {

    public static void main(String[] args) {
        Grid grid = new Grid();

        Interface sud = new Interface(grid);
        sud.setVisible(true);
    }

}
