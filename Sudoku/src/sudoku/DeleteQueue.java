package sudoku;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteQueue {
        public ArrayList<Cell> checkDelete = new ArrayList<>(); // 
        public ArrayList<Cell> deletedList = new ArrayList<>(); //

        public void fcAddToDelete(Cell c) {
            fcAddRow(c);
            fcAddColumn(c);
            fcAddMatrix(c);
        }

        private void fcAddRow(Cell c) {
            for (int i = 0; i < 9; i++) {
                if (i != c.col) {
                    Cell newCell = new Cell(c.row, i);
                    checkDelete.add(newCell);
                }
            }
        }

        private void fcAddColumn(Cell c) {
            for (int i = 0; i < 9; i++) {
                if (i != c.row) {
                    Cell newCell = new Cell(i, c.col);
                    checkDelete.add(newCell);
                }
            }
        }

        private void fcAddMatrix(Cell c) {
            float x1Calc = 3 * (c.row / 3);
            float y1Calc = 3 * (c.col / 3);
            int x1 = Math.round(x1Calc);
            int y1 = Math.round(y1Calc);
            int x2 = x1 + 2;
            int y2 = y1 + 2;

            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    if (x != c.row && y != c.col) {
                        Cell newCell = new Cell(x, y);
                        checkDelete.add(newCell);
                    }
                }
            }

        }

        public void executeDeletion(int value, HashMap<Cell, ArrayList<Integer>> map) { 
            for (Cell c : checkDelete) {
                updateDomain(c, value, map);
            }
        }

        private boolean fcDeleteValue(ArrayList<Integer> values, int number) { 
            for (int value : values) {
                if (value == number) {
                    return true;
                }
            }

            return false;
        }

        private void updateDomain(Cell c, int value, HashMap<Cell, ArrayList<Integer>> map) { 
            ArrayList<Integer> dom = map.get(c);
            if (fcDeleteValue(dom, value)) {
                deletedList.add(c);
                dom.remove(Integer.valueOf(value));
                map.put(c, dom);
            }
        }

        public boolean checkForEmptyDomains(HashMap<Cell, ArrayList<Integer>> map) {
            for (Cell c : deletedList) {                                             
                if (map.get(c).isEmpty()) {
                    return true;
                }
            }

            return false;
        }

        public void restoreDomains(int value, HashMap<Cell, ArrayList<Integer>> map) { // restore domains of the deleted variables
            for (Cell c : deletedList) {
                ArrayList<Integer> dom = map.get(c);
                dom.add(value);
                map.put(c, dom);
            }
            deletedList.clear();
        }
}
