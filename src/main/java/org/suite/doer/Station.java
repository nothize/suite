package org.suite.doer;

import org.suite.node.Node;

/**
 * A node that is actually a runnable (with some parameters). When the prover
 * reaches here it would call the run() function.
 */
public abstract class Station extends Node {

	public abstract boolean run();

}
