package ddmin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import alloy.IDDPlusTest;
import dto.DataTransportObject;
import test.DDPlusTest;

/**
 * Implements delta debugging algorithms to identify minimal unsatisfiable subsets (MUS)
 * within Alloy models. This abstract class provides the foundation for executing the delta
 * debugging process, utilizing the IDDPlusTest interface for testing subsets of the model.
 */
public class AbstractDDPlus {

	private static IDDPlusTest checkAlloy = new DDPlusTest();
	public static final int PASS = 1;
	public static final int FAIL = -1;
	public static final int UNRESOLVED = 0;

    /**
     * Starts the delta debugging algorithm by partitioning the input set and
     * recursively narrowing down to identify the minimal unsatisfiable subset.
     *
     * @param input The list of objects (model elements) to be debugged.
     * @param dto The DataTransportObject containing configuration and state for the debugging session.
     * @return A list of objects representing the minimal unsatisfiable subset.
     */
	public static List<Object> dd(List<Object> input, DataTransportObject dto) {
		return ddAux(input, new ArrayList<>(), dto);
	}

    /**
     * Auxiliary method for recursive delta debugging calls. It splits the input set, tests subsets,
     * and combines results to find the minimal unsatisfiable subset.
     *
     * @param input The current subset of objects to test.
     * @param r A list of objects that have been identified as relevant to the unsatisfiability.
     * @param dto The DataTransportObject providing context and settings for the test.
     * @return A list of objects representing a (more) minimal unsatisfiable subset.
     */
	private static List<Object> ddAux(List<Object> input, List<Object> r, DataTransportObject dto) {
		if (input.size() == 1) {
			return input; // Found the problematic subset
		}

		List<List<Object>> subsets = partitionIntoTwoSubsets(input);
		List<Object> c1 = subsets.get(0);
		List<Object> c2 = subsets.get(1);

		List<Object> rUnionC1 = new ArrayList<>(r);
		rUnionC1.addAll(c1);

		List<Object> rUnionC2 = new ArrayList<>(r);
		rUnionC2.addAll(c2);

		int testResultC1 = checkAlloy.check(rUnionC1, input, dto);
		int testResultC2 = checkAlloy.check(rUnionC2, input, dto);

		if (testResultC1 == FAIL) {
			return ddAux(c1, r, dto);
		} else if (testResultC2 == FAIL) {
			return ddAux(c2, r, dto);
		} else if (testResultC1 == UNRESOLVED || testResultC2 == UNRESOLVED) {
			// Transition to Algorithm 2 if an unresolved case is encountered
			return ddPlus(input, dto);
		} else {
			List<Object> result = new ArrayList<>(ddAux(c1, rUnionC2, dto));
			result.addAll(ddAux(c2, rUnionC1, dto));
			return result;
		}
	}


    /**
     * Partitions the given list into two approximately equal subsets.
     *
     * @param c The list of objects to partition.
     * @return A list containing two lists, each a subset of the original list.
     */
	private static List<List<Object>> partitionIntoTwoSubsets(List<Object> c) {
		List<Object> c1 = new ArrayList<>();
		List<Object> c2 = new ArrayList<>();

		int i = 0;
		for (Object element : c) {
			if (i < c.size() / 2) {
				c1.add(element);
			} else {
				c2.add(element);
			}
			i++;
		}

		List<List<Object>> subsets = new ArrayList<>();
		subsets.add(c1);
		subsets.add(c2);

		return subsets;
	}


    /**
     * Enhanced delta debugging algorithm that handles unresolved cases by increasing
     * granularity and retesting subsets.
     *
     * @param input The list of objects (model elements) to be debugged.
     * @param dto The DataTransportObject containing configuration and state for the debugging session.
     * @return A list of objects representing the minimal unsatisfiable subset, considering unresolved cases.
     */
	private static List<Object> ddPlus(List<Object> input, DataTransportObject dto) {
		return dd3(input, new ArrayList<>(), dto, 2);
	}

	/**
	 * Performs a refined delta debugging algorithm to identify the minimal unsatisfiable subset (MUS)
	 * within the given set of constraints or elements. The `dd3` method may implement an advanced version
	 * of delta debugging that optimizes the search for MUS by iteratively partitioning the input set
	 * and evaluating subsets for satisfiability. This method is designed to handle complex cases with
	 * higher efficiency, possibly incorporating strategies to deal with dependencies or to accelerate
	 * the convergence towards the MUS.
	 * 
	 * @param input A List of Objects representing the current subset of elements or constraints to be analyzed.
	 *              This set is part of the Alloy model under investigation for unsatisfiability.
	 * @param r A List of Objects that have been determined to be relevant in the context of the current
	 *          debugging iteration. This list helps in refining the search and focusing on promising subsets.
	 * @param dto The DataTransportObject that carries configuration settings, model information, and provides
	 *            a mechanism for tracking and logging the analysis process. It may include options that affect
	 *            the debugging strategy or the interpretation of results.
	 * @param n An integer that represents the current depth or iteration in the recursive debugging process.
	 *          It is used to control the recursion and may influence the partitioning strategy or termination
	 *          conditions of the algorithm.
	 * @return A List of Objects that represents the minimal unsatisfiable subset (MUS) found within the input set.
	 *         This subset is the smallest collection of elements or constraints that, when considered together,
	 *         render the Alloy model unsatisfiable.
	 * @throws DebuggingException if an error occurs during the debugging process. This may include errors related
	 *         to model manipulation, evaluation of satisfiability, or issues arising from the configuration
	 *         settings provided through the DataTransportObject.
	 */
	private static List<Object> dd3(List<Object> input, List<Object> r, DataTransportObject dto, int n) {
		if (input.size() == 1) {
			return input; // Found the problematic subset
		}

		List<List<Object>> subsets = partitionIntoNSubsets(input, n);
		List<Object> cPrime = new ArrayList<>();
		List<Object> rPrime = new ArrayList<>(r);
		// int nPrime = Math.min(cPrime.size(), 2 * n);
		int nPrime;

		for (List<Object> ci : subsets) {
			List<Object> ciUnionR = new ArrayList<>(ci);
			ciUnionR.addAll(r);

			int ti = checkAlloy.check(ciUnionR, input, dto);

			if (ti == FAIL) {
				return dd3(ci, r, dto, 2); // Found in ci
			} else if (ti == UNRESOLVED) {
				List<Object> zi = new ArrayList<>(input);
				zi.removeAll(ci);
				zi.addAll(r);
				int tiPrime = checkAlloy.check(zi, input, dto);
				if (tiPrime == PASS) {
					cPrime.addAll(ci);
				}
			}
		}

		if (!cPrime.isEmpty()) {
			nPrime = Math.min(cPrime.size(), 2 * n);
			rPrime.addAll(cPrime);
			List<Object> complementcPrime = difference(input, cPrime);
			return dd3(complementcPrime, rPrime, dto, nPrime); // Preference
		} else if (n < input.size()) {
			return dd3(input, r, dto, input.size()); // Try again
		}

		return new ArrayList<>(); // Nothing left
	}

    /**
     * Partitions the given list into n subsets for use in the ddPlus algorithm.
     *
     * @param c The list of objects to partition.
     * @param n The desired number of subsets.
     * @return A list of lists, where each inner list is a subset of the original list.
     */
	private static List<List<Object>> partitionIntoNSubsets(List<Object> c, int n) {
		List<List<Object>> subsets = new ArrayList<>();
		int subsetSize = (int) Math.ceil(c.size() / (double) n);

		for (int i = 0; i < c.size(); i += subsetSize) {
			subsets.add(new ArrayList<>(c.subList(i, Math.min(c.size(), i + subsetSize))));
		}

		return subsets;
	}

    /**
     * Computes the difference between two lists of objects.
     *
     * @param a The first list.
     * @param b The second list, whose elements will be removed from the first list.
     * @return A list containing all elements of the first list that are not in the second list.
     */
	private static List<Object> difference(List<Object> a, List<Object> b) {
		List<Object> result = new LinkedList<Object>();
		result.addAll(a);
		result.removeAll(b);
		return result;
	}
}
