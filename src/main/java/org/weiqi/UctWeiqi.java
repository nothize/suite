package org.weiqi;

import java.util.List;
import java.util.Random;

import org.weiqi.Weiqi.Occupation;

public class UctWeiqi {

	private final static Random random = new Random();

	public static class Visitor implements UctVisitor<Coordinate> {
		protected Board board;
		protected Occupation nextPlayer;

		public Visitor(Board board, Occupation nextPlayer) {
			this.board = board;
			this.nextPlayer = nextPlayer;
		}

		@Override
		public UctVisitor<Coordinate> cloneVisitor() {
			return new Visitor(new Board(board), nextPlayer);
		}

		@Override
		public Iterable<Coordinate> elaborateMoves() {
			return board.findAllMoves(nextPlayer);
		}

		@Override
		public void playMove(Coordinate c) {
			board.move(c, nextPlayer);
			nextPlayer = nextPlayer.opponent();
		}

		@Override
		public boolean evaluateRandomOutcome() {
			Occupation winner = null;
			Occupation player = nextPlayer;

			// Move until someone cannot move anymore
			while (winner == null) {
				List<Coordinate> moves = board.findAllMoves(nextPlayer);

				if (!moves.isEmpty()) {
					Coordinate c = moves.get(random.nextInt(moves.size()));
					board.move(c, nextPlayer);
					nextPlayer = nextPlayer.opponent();
				} else
					winner = nextPlayer.opponent();
			}

			return player == winner;
		}
	}

}
