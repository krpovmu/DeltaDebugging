package ddmin;

import java.util.ArrayList;
import java.util.List;

import alloy.IDDPlusTest;
import dto.DataTransportObject;
import test.DDPlusTest;

public class AbstractDDPlus {

//	private static final Random random = new Random();
	private static IDDPlusTest checkAlloy =  new DDPlusTest();
//	private static DDPlusTest checkClass = new DDPlusTest();
	public static final int PASS = 1;
	public static final int FAIL = -1;
	public static final int UNRESOLVED = 0;

	// Entry point for Algorithm 1
	public static List<Object> dd(List<Object> input, DataTransportObject dto) {
		return ddAux(input, new ArrayList<>(), dto);
	}

	// Auxiliary method for recursive calls in Algorithm 1
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
		} else if (testResultC1 == FAIL) {
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

	// Partition the list into two subsets
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

	// Entry point for Algorithm 2
	private static List<Object> ddPlus(List<Object> input, DataTransportObject dto) {
		return dd3(input, new ArrayList<>(), dto, 2);
	}

	// Method for Algorithm 2
	private static List<Object> dd3(List<Object> input, List<Object> r, DataTransportObject dto, int n) {
		if (input.size() == 1) {
			return input; // Found the problematic subset
		}

		List<List<Object>> subsets = partitionIntoNSubsets(input, n);
		List<Object> cPrime = new ArrayList<>();
		List<Object> rPrime = new ArrayList<>(r);
		int nPrime = Math.min(cPrime.size(), 2 * n);

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
				if (ti == PASS) {
					cPrime.addAll(ci);
				}
			}
		}

		if (!cPrime.isEmpty()) {
			return dd3(cPrime, rPrime, dto, nPrime); // Preference
		} else if (n < input.size()) {
			return dd3(input, r, dto, input.size()); // Try again
		}

		return new ArrayList<>(); // Nothing left
	}

	// Partition the list into n subsets for Algorithm 2
	private static List<List<Object>> partitionIntoNSubsets(List<Object> c, int n) {
		List<List<Object>> subsets = new ArrayList<>();
		int subsetSize = (int) Math.ceil(c.size() / (double) n);

		for (int i = 0; i < c.size(); i += subsetSize) {
			subsets.add(new ArrayList<>(c.subList(i, Math.min(c.size(), i + subsetSize))));
		}

		return subsets;
	}

//	private static String test(List<Object> c) {
//		int outcome = random.nextInt(3); // Randomly choose an outcome
//		switch (outcome) {
//		case 0:
//			return "d"; // Pass
//		case 1:
//			return "X"; // Fail
//		case 2:
//			return "?"; // Unresolved
//		default:
//			return "d";
//		}
//	}

//	public static void main(String[] args) {
//		// Example usage for both algorithms
//		List<Object> initialList = new ArrayList<>();
//		// Add elements to initialList
//		for (int i = 1; i <= 10; i++) {
//			initialList.add(i);
//		}
//
//		// Using Algorithm 1
//		System.out.println("Testing with Algorithm 1:");
//		List<Object> result1 = dd(initialList);
//		System.out.println("Result: " + result1);
//
//		// Resetting list for Algorithm 2
//		initialList.clear();
//		for (int i = 1; i <= 10; i++) {
//			initialList.add(i);
//		}
//
//		// Using Algorithm 2
//		System.out.println("Testing with Algorithm 2:");
//		List<Object> result2 = ddPlus(initialList);
//		System.out.println("Result: " + result2);
//	}
}
