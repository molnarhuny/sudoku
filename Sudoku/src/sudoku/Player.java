package sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Player {

    HashMap<Cell, ArrayList<Integer>> map = new HashMap<>();    // map linking variables(cells) to domains
    ArrayList<Pair> qArray = new ArrayList<>(); // Q contais a set of arcs (pairs of (cell, domain)
    boolean ac3 = false; //Boolean indicating if AC3 has been executed
    int recursiveCalls = 0; // variable to measure the ammount of recursive calls 

//////////////////////////////////////////////////////////////////////////////////
    // map methods
//////////////////////////////////////////////////////////////////////////////////  
    private void fillMap(Grid table) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (table.getCell(i, j) == 0) {
                    Cell c = new Cell(i, j);
                    ArrayList<Integer> elements = fillDomain();
                    map.put(c, elements);
                } else {
                    Cell c = new Cell(i, j);
                    ArrayList<Integer> elements = new ArrayList<>();
                    elements.add(table.getCell(i, j));
                    map.put(c, elements);
                }
            }
        }
    }

    private ArrayList<Integer> fillDomain() {  // fills the domain with the values 1-9
        ArrayList<Integer> elements = new ArrayList<>();

        for (int i = 1; i <= 9; i++) {
            elements.add(i);
        }

        return elements;
    }

    private void printDomains() {    //prints the domains
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print("Domain Cell (" + i + "," + j + "): ");
                Cell cel = new Cell(i,j);
                ArrayList<Integer> domain = map.get(cel);
                for (int z = 0; z < domain.size(); z++) {
                    System.out.print(domain.get(z) + " ");
                }
                System.out.print("\n");
            }
        }
    }

//////////////////////////////////////////////////////////////////////////////////
    // AC3 methods
////////////////////////////////////////////////////////////////////////////////// 
    private void fillQ() { // fills Q in the first iteration of the algorithm
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell cell = new Cell(i, j);
                addToQ(cell, true);
            }
        }
    }

    private boolean existsInQ(Pair pair) { 
        for (Pair p : qArray) {
            if (p.equals(pair)) {
                return true;
            }
        }
        return false;
    }

    private void addToQ(Cell c, boolean fill) { 
        addRow(c, fill);                        
        addColumn(c, fill);                     
        addMatrix(c, fill);
    }

    private void addRow(Cell c, boolean fill) {
        for (int i = 0; i < 9; i++) {
            if (i != c.col) {
                Pair pair = null;
                Cell newCell = new Cell(c.row, i);
                if (fill) {
                    pair = new Pair(c, newCell);
                } else {
                    pair = new Pair(newCell, c);
                }
                if (!existsInQ(pair)) {
                    qArray.add(pair);
                }
            }
        }
    }

    private void addColumn(Cell c, boolean fill) {
        for (int i = 0; i < 9; i++) {
            if (i != c.row) {
                Pair pair = null;
                Cell newCell = new Cell(i, c.col);
                if (fill) {
                    pair = new Pair(c, newCell);
                } else {
                    pair = new Pair(newCell, c);
                }
                if (!existsInQ(pair)) {
                    qArray.add(pair);
                }
            }
        }
    }

    private void addMatrix(Cell c, boolean fill) {
        float x1Calc = 3 * (c.row / 3);
        float y1Calc = 3 * (c.col / 3);
        int x1 = Math.round(x1Calc);
        int y1 = Math.round(y1Calc);
        int x2 = x1 + 2;
        int y2 = y1 + 2;

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (x != c.row && y != c.col) {
                    Pair pair = null;
                    Cell newCell = new Cell(x, y);
                    if (fill) {
                        pair = new Pair(c, newCell);
                    } else {
                        pair = new Pair(newCell, c);
                    }
                    if (!existsInQ(pair)) {
                        qArray.add(pair);
                    }
                }
            }
        }
    }

    public boolean AC3(Grid board) {
        ac3 = true; // set to true to indicate AC3 has been executed
        boolean changed;
        boolean solutionExists = true;
        fillMap(board); 
        fillQ();
        while (qArray.size() > 0 && solutionExists) { 
            Pair pair = qArray.get(0);
            qArray.remove(pair);
            changed = false;
            ArrayList<Integer> valuesCell1 = map.get(pair.left());
            ArrayList<Integer> valuesCell2 = map.get(pair.right());

            for (int i = valuesCell1.size() - 1; i >= 0; i--) { // for each vk from Dk
                if (deleteValue(valuesCell2, valuesCell1.get(i))) { // if (vk, Dm) doesnt satisfy the constraint
                    valuesCell1.remove(valuesCell1.get(i)); // delete (vk, Dk)
                    changed = true;
                }
            }

            if (valuesCell1.isEmpty()) {    // if the domain is empty, there is no solution
                solutionExists = false;
            }

            if (changed) {
                map.put(pair.left(), valuesCell1); // if we deleted values, we update the domain of the cell  
                addToQ(pair.left(), false); // add new restrictions to Q
            }
        }
        printDomains();

        if (!solutionExists) {
            System.out.println("There is no solution for this problem");
            return false;
        }

        return true;
    }

    // searches for the number given as a parameter(number from first cell)
    // in the domain of the second cell, if it finds a different number
    // returns false

    private boolean deleteValue(ArrayList<Integer> values, int number) {
        for (int value : values) {
            if (value != number) {
                return false;
            }
        }

        return true;
    }

//////////////////////////////////////////////////////////////////////////////////
    // Backtrack methods
//////////////////////////////////////////////////////////////////////////////////      
    private boolean backtrack(Grid board, Cell cell) {
        recursiveCalls++;
        if (endOfGrid(board)) {
            return true;
        }

        ArrayList<Integer> valuesCell = map.get(cell); // get the domain of the cell on which im operating 
        int value = 0;

        for (int i = 0; i < valuesCell.size(); i++) { 
            value = valuesCell.get(i);                  
            board.setCell(value, cell.row, cell.col);  
            if (isValid(board, cell, value)) {            
                if (backtrack(board, cell.nextCell(board))) { 
                    return true;
                }
            }
        }
        board.setCell(0, cell.row, cell.col);  // restores the cell value

        return false;
    }

    private boolean endOfGrid(Grid table) { // returns true if the whole grid is full
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (table.getCell(i, j) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(Grid table, Cell cell, int value) { // Checks if the inserted value is correct  
        for (int i = 0; i < 9; i++) { // check for repeated values in the row
            if (i != cell.col) {
                if (table.getCell(cell.row, i) == value) {
                    return false;
                }
            }
        }

        for (int j = 0; j < 9; j++) { // check for repeated values in the column
            if (j != cell.row) {
                if (table.getCell(j, cell.col) == value) {
                    return false;
                }
            }
        }

        if (!checkMatrix(table, cell, value)) {
            return false;
        }

        return true;
    }

    private boolean checkMatrix(Grid board, Cell cell, int value) { // check for repeated values on a 3x3 matrix
        float x1Calc = 3 * (cell.row / 3);
        float y1Calc = 3 * (cell.col / 3);
        int x1 = Math.round(x1Calc);
        int y1 = Math.round(y1Calc);
        int x2 = x1 + 2;
        int y2 = y1 + 2;

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (x != cell.row && y != cell.col) {
                    if (board.getCell(x, y) == value) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

//////////////////////////////////////////////////////////////////////////////////
    // Forward Checking
////////////////////////////////////////////////////////////////////////////////// 
    public boolean forwardChecking(Grid board, Cell cell) { 
        recursiveCalls++;
        if (endOfGrid(board)) { 
            return true;
        }

        DeleteQueue delQueue = new DeleteQueue();
        delQueue.fcAddToDelete(cell);   // fill the queue with posible variables (row, col & matrix) that must update their domain
        ArrayList<Integer> valuesCell = map.get(cell); // save the domain of the variable on which im operating
        int value = 0;

        for (int i = 0; i < valuesCell.size(); i++) { 
            value = valuesCell.get(i);
            delQueue.executeDeletion(value, map); 
            if (delQueue.checkForEmptyDomains(map)) { 
                delQueue.restoreDomains(value, map); // If any domain is empty, restore all domains
            } else {
                ArrayList<Integer> newDomain = new ArrayList<>();
                newDomain.add(value);
                map.put(cell, newDomain);                           
                board.setCell(value, cell.row, cell.col);             
                if (forwardChecking(board, cell.nextCell(board))) { 
                    return true;
                } else {
                    delQueue.restoreDomains(value, map);         
                    map.put(cell, valuesCell);       
                }
            }
        }
        board.setCell(0, cell.row, cell.col);  

        return false;
    }

//////////////////////////////////////////////////////////////////////////////////
    // Interface Calls
//////////////////////////////////////////////////////////////////////////////////    
    public boolean runBC(Grid board) {
        if (!ac3) // if AC3 did not run, I load the map
        {
            fillMap(board);
        }
        Cell cell = new Cell();

        long time1 = System.nanoTime();
        backtrack(board, cell.nextCell(board)); // call it with the first empty cell
        long time2 = System.nanoTime();
        long timeSpent = time2 - time1;
        System.out.println("Time elapsed(Backtracking): " + TimeUnit.NANOSECONDS.toMillis(timeSpent) + "ms");
        System.out.println("Recursive calls: " + recursiveCalls);

        return true;
    }

    public boolean runAC(Grid board) {
        long time1 = System.nanoTime();
        boolean solutionExists = AC3(board);
        long time2 = System.nanoTime();
        long timeSpent = time2 - time1;
        System.out.println("Time elapsed(AC3): " + TimeUnit.NANOSECONDS.toMillis(timeSpent) + "ms");

        return solutionExists;
    }

    public boolean executeFC(Grid table) {
        if (!ac3) // if AC3 did not run
        {
            fillMap(table);
        }
        Cell cell = new Cell();

        long time1 = System.nanoTime();
        forwardChecking(table, cell.nextCell(table)); // call it with the first empty cell
        long time2 = System.nanoTime();
        long timeSpent = time2 - time1;
        System.out.println("Time elapsed(Forward Checking): " + TimeUnit.NANOSECONDS.toMillis(timeSpent) + "ms");
        System.out.println("Recursive calls: " + recursiveCalls);

        return true;
    }
}
