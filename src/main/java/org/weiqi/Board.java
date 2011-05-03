package org.weiqi;

import java.util.Set;

import org.util.Util;
import org.weiqi.Weiqi.Array;
import org.weiqi.Weiqi.Occupation;

public class Board extends Array<Occupation> {

	public Board() {
		for (Coordinate c : Coordinate.all())
			set(c, Occupation.EMPTY);
	}

	public Board(Board board) {
		super(board);
	}

	/**
	 * Plays a move on the Weiqi board.
	 * 
	 * This method do not take use of GroupAnalysis for performance reasons.
	 */
	public void move(Coordinate c, Occupation player) {
		Occupation current = get(c);
		Occupation opponent = player.opponent();

		if (current == Occupation.EMPTY) {
			set(c, player);

			for (Coordinate neighbour : c.neighbours())
				if (neighbour.isWithinBoard() && get(neighbour) == opponent)
					killIfDead(neighbour);

			Board b1 = new Board(this);

			if (killIfDead(c)) {
				UserInterface.display(b1);
				System.out.println();
				UserInterface.display(this);
				System.out.println(player + " " + c);
				set(c, player);
				killIfDead(c);
				throw new RuntimeException("Cannot perform suicide move");
			}
		} else
			throw new RuntimeException("Cannot move on occupied position");
	}

	private boolean killIfDead(Coordinate c) {
		boolean isKilled = !hasBreath(c, get(c));

		if (isKilled)
			for (Coordinate c1 : findGroup(c))
				set(c1, Occupation.EMPTY);

		return isKilled;
	}

	private boolean hasBreath(Coordinate c, Occupation player) {
		if (c.isWithinBoard()) {
			Occupation current = get(c);

			if (current == player) {
				set(c, null); // Do not re-count
				boolean hasBreath = false;

				for (Coordinate neighbour : c.neighbours())
					if (hasBreath(neighbour, player)) {
						hasBreath = true;
						break;
					}

				set(c, current); // Set it back
				return hasBreath;
			} else
				return current == Occupation.EMPTY;
		} else
			return false;
	}

	private Set<Coordinate> findGroup(Coordinate c) {
		Set<Coordinate> group = Util.createHashSet();
		findGroup(c, get(c), group);
		return group;
	}

	private void findGroup(Coordinate c, Occupation color, Set<Coordinate> group) {
		if (c.isWithinBoard() && get(c) == color && group.add(c))
			for (Coordinate neighbour : c.neighbours())
				findGroup(neighbour, color, group);
	}

	/**
	 * Plays a move on the Weiqi board. Uses group analysis which is slower.
	 */
	public void move1(Coordinate c, Occupation player) {
		Occupation current = get(c);
		Occupation opponent = player.opponent();

		if (current == Occupation.EMPTY) {
			set(c, player);
			GroupAnalysis ga = new GroupAnalysis(this);

			for (Coordinate neighbour : c.neighbours())
				if (neighbour.isWithinBoard() && get(neighbour) == opponent)
					killIfDead1(ga, neighbour);

			if (killIfDead1(ga, c))
				throw new RuntimeException("Cannot perform suicide move");
		} else
			throw new RuntimeException("Cannot move on occupied position");
	}

	private boolean killIfDead1(GroupAnalysis ga, Coordinate c) {
		Integer groupId = ga.getGroupId(c);
		boolean isKilled = ga.getNumberOfBreathes(groupId) == 0;

		if (isKilled)
			for (Coordinate c1 : ga.getCoords(groupId))
				set(c1, Occupation.EMPTY);

		return isKilled;
	}

}
