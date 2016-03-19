package me.antonle.algs4j.puzzle;

import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Class that is able to find the solution for a given {@link Board}.
 * <p>
 * To implement the A* algorithm, you must use MinPQ for the priority queue(s).
 *
 * @see edu.princeton.cs.algs4.MinPQ
 * <p>
 * Corner cases.
 * The constructor should throw a java.lang.NullPointerException if passed a null argument.
 */
public class Solver {

    private List<Board> solutionBoards = new ArrayList<>();
    private boolean solved;


    /**
     * Find a solution to the initial board (using the A* algorithm)
     * */
    public Solver(Board initial) {
        MinPQ<SolverStep> prioritizedSteps = new MinPQ<>(new SolverStepComparator());
        prioritizedSteps.insert(new SolverStep(initial, 0, null));

        MinPQ<SolverStep> prioritizedStepsTwin = new MinPQ<>(new SolverStepComparator());
        prioritizedStepsTwin.insert(new SolverStep(initial.twin(), 0, null));

        SolverStep step;
        while (!(prioritizedSteps.min().getBoard().isGoal() || prioritizedStepsTwin.min().getBoard().isGoal())) {
            step = prioritizedSteps.delMin();
            for (Board neighbor : step.getBoard().neighbors()) {
                if (!isAlreadyInSolutionPath(step, neighbor)) {
                    prioritizedSteps.insert(new SolverStep(neighbor, step.getMoves() + 1, step));
                }
            }

            SolverStep stepTwin = prioritizedStepsTwin.delMin();
            for (Board neighbor : stepTwin.getBoard().neighbors()) {
                if (!isAlreadyInSolutionPath(stepTwin, neighbor)) {
                    prioritizedStepsTwin.insert(new SolverStep(neighbor, stepTwin.getMoves() + 1, stepTwin));
                }
            }
        }
        step = prioritizedSteps.delMin();
        solved = step.getBoard().isGoal();

        solutionBoards.add(step.getBoard());
        while ((step = step.getPreviousStep()) != null) {
            solutionBoards.add(0, step.getBoard());
        }
    }

    private boolean isAlreadyInSolutionPath(SolverStep lastStep, Board board) {
        SolverStep previousStep = lastStep;
        while ((previousStep = previousStep.getPreviousStep()) != null) {
            if (previousStep.getBoard().equals(board)) {
                return true;
            }
        }
        return false;
    }

    /**
     * is the initial board solvable?
     * */
    public boolean isSolvable() {
        return solved;
    }

    /**
     * Min number of moves to solve initial board; -1 if unsolvable
     * */
    public int moves() {
        int moves;
        if (isSolvable()) {
            moves = solutionBoards.size() - 1;
        } else {
            moves = -1;
        }
        return moves;
    }

    /**
     * Sequence of boards in a shortest solution; null if unsolvable
     * */
    public Iterable<Board> solution() {
        Iterable<Board> iterable;
        if (isSolvable()) {
            iterable = SolutionIterator::new;
        } else {
            iterable = null;
        }
        return iterable;
    }

    private class SolutionIterator implements Iterator<Board> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < solutionBoards.size();
        }

        @Override
        public Board next() {
            return solutionBoards.get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("It is not supported to remove a board from the solution.");
        }
    }

    private static class SolverStepComparator implements Comparator<SolverStep> {

        @Override
        public int compare(SolverStep step1, SolverStep step2) {
            return step1.getPriority() - step2.getPriority();
        }
    }

    private static class SolverStep {

        private int moves;
        private Board board;
        private SolverStep previousStep;

        private SolverStep(Board board, int moves, SolverStep previousStep) {
            this.board = board;
            this.moves = moves;
            this.previousStep = previousStep;
        }

        public int getMoves() {
            return moves;
        }

        public int getPriority() {
            return board.manhattan() + moves;
        }

        public Board getBoard() {
            return board;
        }

        public SolverStep getPreviousStep() {
            return previousStep;
        }
    }
}