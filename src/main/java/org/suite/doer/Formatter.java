package org.suite.doer;

import org.parser.Operator;
import org.parser.Operator.Assoc;
import org.suite.doer.TermParser.TermOp;
import org.suite.node.Atom;
import org.suite.node.Int;
import org.suite.node.Node;
import org.suite.node.Reference;
import org.suite.node.Str;
import org.suite.node.Tree;

public class Formatter {

	private Operator operators[];

	public Formatter(Operator operators[]) {
		this.operators = operators;
	}

	public static String display(Node node) {
		return new Formatter(TermOp.values()).format(node, false);
	}

	public static String dump(Node node) {
		return new Formatter(TermOp.values()).format(node, true);
	}

	private String format(Node node, boolean dump) {
		StringBuilder sb = new StringBuilder();
		format(node, 0, dump, sb);
		return sb.toString();
	}

	/**
	 * Converts a node to its string representation.
	 * 
	 * @param node
	 *            Node to be converted.
	 * @param parentPrec
	 *            Minimum operator precedence without adding parentheses.
	 * @param dump
	 *            If specified as true, the output would be machine-parsable.
	 * @param sb
	 *            Buffer to hold output.
	 */
	private void format(Node node, int parentPrec, boolean dump,
			StringBuilder sb) {
		node = node.finalNode();

		if (node instanceof Int)
			sb.append(((Int) node).getNumber());
		else if (node instanceof Atom) {
			String s = ((Atom) node).getName();
			s = dump ? quoteAtomIfRequired(s) : s;
			sb.append(s);
		} else if (node instanceof Str) {
			String s = ((Str) node).getValue();
			s = dump ? quote(s, "\"") : s;
			sb.append(s);
		} else if (node instanceof Tree) {
			Tree tree = (Tree) node;
			Operator operator = tree.getOperator();
			int ourPrec = operator.getPrecedence();
			boolean needParentheses = (ourPrec <= parentPrec);

			int leftPrec = ourPrec, rightPrec = ourPrec;
			if (operator.getAssoc() == Assoc.LEFT)
				rightPrec--;
			else if (operator.getAssoc() == Assoc.RIGHT)
				leftPrec--;

			if (needParentheses)
				sb.append('(');

			format(tree.getLeft(), leftPrec, dump, sb);

			String name = operator.getName();
			sb.append(name);
			if (!name.endsWith(" "))
				sb.append(' ');

			format(tree.getRight(), rightPrec, dump, sb);

			if (needParentheses)
				sb.append(')');
		} else if (node instanceof Reference)
			sb.append(Generalizer.variablePrefix + ((Reference) node).getId());
		else
			sb.append(node.getClass().getSimpleName() + '@'
					+ Integer.toHexString(node.hashCode()));
	}

	public String quoteAtomIfRequired(String s) {
		if (!s.isEmpty()) {
			boolean quote = false;
			if (s.indexOf('\'') != -1)
				quote = true;

			for (Operator operator : operators)
				if (s.contains(operator.getName()))
					quote = true;

			if (quote)
				s = quote(s.replace("%", "%%"), "'");
		} else
			s = "()";
		return s;
	}

	public String quote(String s, String quote) {
		s = s.replace(quote, quote + quote);
		s = s.replace("%", "%%");
		return quote + s + quote;
	}

}
