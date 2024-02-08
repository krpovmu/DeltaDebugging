package alloy;

import java.util.List;

import dto.DataTransportObject;

/**
 * Defines the interface for testing subsets of an Alloy model to determine their satisfiability.
 * Implementations of this interface are used by the delta debugging algorithms to perform
 * checks on partitions of the model, aiding in the identification of minimal unsatisfiable subsets.
 */
public interface IDDPlusTest {
	
    /**
     * Performs a check on specified subsets of an Alloy model to assess their satisfiability.
     * This method is a key part of the delta debugging process, allowing for the evaluation
     * of different parts of the model to isolate the minimal unsatisfiable subset.
     *
     * @param c A list of objects representing a subset of the model elements to be checked.
     * @param i Another list of objects representing a different subset of model elements to be checked.
     * @param dto The DataTransportObject containing the current context and configurations for the analysis.
     * @return An integer indicating the result of the check: positive for pass, negative for fail, 
     *         and zero for unresolved or indeterminate cases.
     */
	int check(List<Object> c, List<Object> i, DataTransportObject dto);
}
