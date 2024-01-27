package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alloy.IDDPlusTest;
import dto.DataTransportObject;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprList;
import edu.mit.csail.sdg.ast.Func;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;

public class DDPlusTest<E> implements IDDPlusTest {

	public static final int PASS = 1;
	public static final int FAIL = -1;
	public static final int UNRESOLVED = 0;

	public int check(List<Object> chunk, List<Object> input, DataTransportObject dto) {
		A4Solution ans = null;
		int result = FAIL;
		if (dto.getCoreType().equals("facts")) {
			try {
				Command cm = dto.getCommand().change(assemble((List<E>) chunk));
				ans = TranslateAlloyToKodkod.execute_command(dto.getReporter(), dto.getModule().getAllReachableSigs(), cm, dto.getOptions());
			} catch (Exception e) {
				result = UNRESOLVED;
			}
		} else if (dto.getCoreType().equals("predicates")) {
			try {
				String modelWithJustThePredicateToEvaluate = removePredicates((List<E>) chunk, (List<E>) input, dto.getFilePath());
				dto.setModulePred(dto.getReporter(), modelWithJustThePredicateToEvaluate);
				ans = TranslateAlloyToKodkod.execute_command(dto.getReporter(), dto.getModule().getAllReachableSigs(), dto.getModule().getAllCommands().get(0), dto.getOptions());
			} catch (Exception e) {
				result = UNRESOLVED;
			}
		} else {
			System.out.println("Invalid kind of core.");
		}
		if (ans == null) {
			result = UNRESOLVED;
		} else if (ans.satisfiable()) {
			result = PASS;
		}
		if (dto.isTrace()) {
			System.out.println(dto.printCoreAsNumber(chunk, result));
		}
		return result;
	}

	private List<E> difference(List<E> a, List<E> b) {
		List<E> result = new LinkedList<E>();
		result.addAll(a);
		result.removeAll(b);
		return result;
	}

	private Expr assemble(List<E> complement) throws IOException {
		List<E> cand = new ArrayList<E>(complement);
		ExprList el = ExprList.make(null, null, ExprList.Op.AND, (List<? extends Expr>) cand);
		return el;
	}

	public String removePredicates(List<E> partListPredicates, List<E> originalListPredicates, String pathModel) throws IOException {
		String originalModel = new String(Files.readAllBytes(Paths.get(pathModel)));
		StringBuilder modifiedModel = new StringBuilder();
		List<E> listPredicatesToDelete = difference(originalListPredicates, partListPredicates);
		String[] lines = originalModel.split("\n");
		for (String line : lines) {
			boolean lineIsPredicateToDelete = lineIsPredicateToDelete(listPredicatesToDelete, line);
			if (!lineIsPredicateToDelete) {
				modifiedModel.append(line).append("\n");
			}
		}
		return modifiedModel.toString();
	}

	public boolean lineIsPredicateToDelete(List<E> predicates, String line) {
		boolean isPredicateToDelete = false;
		for (int i = 0; i < predicates.size(); i++) {
			Func funcPredicate = (Func) predicates.get(i);
			String[] elementsNamePredicate = funcPredicate.label.toString().split("/");
			String namePredicate = elementsNamePredicate[elementsNamePredicate.length - 1];
			String regex = "\\bpred\\s+" + namePredicate + "\\s*\\(([^)]*)\\)\\s*\\{";
			// String regex = "\\bpred\\s+" + namePredicate + "\\s*\\(([^)]*)\\)\\s*\\{.*?\\}";
			Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				isPredicateToDelete = true;
				break;
			}
		}
		return isPredicateToDelete;
	}
}
