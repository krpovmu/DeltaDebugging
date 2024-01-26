package alloy;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ddmin.AbstractDDPlus;
import dto.DataTransportObject;
import edu.mit.csail.sdg.ast.Browsable;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.Func;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class AlloyManager<E> {

	public static void main(String[] args) throws ArgumentParserException {

		ArgumentParser parser = ArgumentParsers.newFor("FileProcessor").build().defaultHelp(true).description("Process a file passed as argument.");
		parser.addArgument("-i", "--input").required(true).help("The file to process");
		parser.addArgument("-f", "--facts").action(Arguments.storeTrue()).help("Analize errors in the model related with facts");
		parser.addArgument("-p", "--predicates").action(Arguments.storeTrue()).help("Analize errors in the model related with predicates");

		try {
			Namespace res = parser.parseArgs(args);
			DataTransportObject dto = new DataTransportObject();
			dto.setFilePath(res.getString("input"));
			dto.setCoreType((res.getBoolean("facts")) ? "facts" : (res.getBoolean("predicates")) ? "predicates" : "choose something perejil");
			dto.setReporter();
			dto.setOptions();
			dto.setModule(dto.getReporter(), dto.getFilePath());
			dto.setCommand(dto.getModule());

			LinkedHashMap<String, Object> factsAndpredicates = new LinkedHashMap<String, Object>();
			AbstractDDPlus ddplus = new AbstractDDPlus();

			// System.out.println(dto.getFilePath());
			if (validateFile(dto.getFilePath())) {

				factsAndpredicates = getFactsOrPredicates(dto);
				// System.out.println(factsAndpredicates);

				if (!factsAndpredicates.isEmpty()) {
					for (Map.Entry<String, Object> element : factsAndpredicates.entrySet()) {
						List<Object> cores = new ArrayList<Object>();
						List<Object> valuesList = (List<Object>) element.getValue();

						if (!valuesList.isEmpty()) {
							cores = ddplus.dd(valuesList, dto);
						} else {
							System.out.println("Empty List Of " + element.getKey());
						}

						System.out.println("=============");
						System.out.println("\n" + printCore(cores));
						System.out.println("=============");

					}
				} else {
					System.out.println("There are not elements to analyze, have a good day");
				}
			} else {
				System.out.println("Invalid");
			}
		} catch (ArgumentParserException e) {
			parser.handleError(e);
		}
	}

	public static LinkedHashMap<String, Object> getFactsOrPredicates(DataTransportObject dto) {

		LinkedHashMap<String, Object> listElementsAlloyModel = new LinkedHashMap<String, Object>();

		if (dto.getCoreType().equals("facts")) {
			// Facts extraction
			List<Object> facts = new ArrayList<Object>();
			for (int i = 0; i < dto.getModule().getAllFacts().size(); i++) {
				List<? extends Browsable> listFacts = (dto.getModule().getAllFacts().get(i).b).getSubnodes();
				for (Browsable fact : listFacts) {
					facts.add(fact);
				}
			}
			// Add facts to the list
			listElementsAlloyModel.put("Facts", facts);
		} else if (dto.getCoreType().equals("predicates")) {
			// Predicates and Functions
			List<Object> functions = new ArrayList<Object>();
			List<Object> predicates = new ArrayList<Object>();
			for (int i = 0; i < dto.getModule().getAllFunc().size(); i++) {
				Func element = dto.getModule().getAllFunc().get(i);
				// The idea is ignore run commands, run commands are always null in the explain
				if (!(element.explain() == null)) {
					// This validation tell me if the element is a predicate
					if (element.isPred) {
						predicates.add(element);
					}
					// otherwise the element is a function
					else {
						functions.add(element);
					}
				}
			}
			listElementsAlloyModel.put("Predicates", predicates);
			// listElementsAlloyModel.put("Function", functions);
		}
		return listElementsAlloyModel;
	}

	public static String printCore(List<Object> core) {
		String result = "";
		for (Object e : core) {
			if (e.getClass().getSimpleName().equals("Func")) {
				Func elem = (Func) e;
				result += "Pred: " + elem.pos.toShortString() + ": ";
				result += elem.toString() + "\n";
			} else {
				Expr elem = (Expr) e;
				result += "Fact: " + elem.pos.toShortString() + ": ";
				result += elem.toString() + "\n";
			}
		}
		return result;
	}

	public static boolean validateFile(String filePath) {
		File file = new File(filePath);

		// Check if the file exists and is not a directory
		if (!file.exists() || file.isDirectory()) {
			System.out.println("File does not exist or is a directory.");
			return false;
		}
		// Check if the file is empty
		if (file.length() == 0) {
			System.out.println("File is empty.");
			return false;
		}
		return true;
	}
}
