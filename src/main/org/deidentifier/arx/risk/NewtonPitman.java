package org.deidentifier.arx.risk;

import java.util.Map;

/**
* This class implements Newton Raphson algorithm for the Pitman Model to obtain results for the
* Maximum Likelihood estimation. For further details see Hoshino, 2001 
* @author Michael Schneider
* @version 1.0
*/

public class NewtonPitman extends NewtonRaphsonAlgorithm {

    /** The number of equivalence class sizes (keys) and corresponding frequency (values) */
    private final Map<Integer, Integer> eqClasses;

    /** The total number of entries in our sample data set */
    private final double                n;

    /** The total number of equivalence classes in the sample data set */
    private final double                u;

    /**
     * Creates an instance of the Newton-Raphson Algorithm to determine the Maximum Likelihood Estimator for the Pitman Model
     * @param u total number of entries in the sample data set
     * @param n size of sample
     * @param eqClasses The number of equivalence class sizes (keys) and corresponding frequency (values) 
     */
    public NewtonPitman(final double u, final double n, final Map<Integer, Integer> eqClasses) {
        this.u = u;
        this.n = n;
        this.eqClasses = eqClasses;
    }

    /**
     * The method for computing the first derivatives of the object functions evaluated at the iterated
     * solutions.
     * @param iteratedSolution the iterated vector of solutions.
     * @return the first derivatives of the object functions evaluated at the
     *         iterated solutions.
     */
    @Override
    public double[][] firstDerivativeMatrix(final double[] iteratedSolution) {

        final double[][] result = new double[iteratedSolution.length][iteratedSolution.length];
        double temp1 = 0, temp2 = 0, temp3 = 0;

        // compute d^2L/(dtheta)^2
        for (int i = 1; i < u; i++) {
            temp1 += (1 / ((iteratedSolution[0] + (i * iteratedSolution[1])) * (iteratedSolution[0] + (i * iteratedSolution[1]))));
        }

        for (final Map.Entry<Integer, Integer> entry : eqClasses.entrySet()) {
            temp2 += (1 / ((iteratedSolution[0] + entry.getKey()) * (iteratedSolution[0] + entry.getKey())));
        }
        result[0][0] = temp2 - temp1;

        // compute d^2L/(d alpha)^2
        temp1 = 0;
        temp2 = 0;
        temp3 = 0;
        for (int i = 1; i < u; i++) {
            temp1 += ((i * i) / ((iteratedSolution[0] + (i * iteratedSolution[1])) * (iteratedSolution[0] + (i * iteratedSolution[1]))));
        }

        for (final Map.Entry<Integer, Integer> entry : eqClasses.entrySet()) {
            temp3 = 0;
            if (entry.getKey() != 1) {
                for (int j = 1; j < entry.getKey(); j++) {
                    temp3 += (1 / ((j - iteratedSolution[1]) * (j - iteratedSolution[1])));
                }
                temp2 += entry.getValue() * temp3;
            }
        }
        result[1][1] = 0 - temp1 - temp2;

        // compute d^2L/(d theta d alpha)
        temp1 = 0;
        temp2 = 0;
        temp3 = 0;
        for (int i = 1; i < u; i++) {
            temp1 += (i / (((i * iteratedSolution[1]) + iteratedSolution[0]) * ((i * iteratedSolution[1]) + iteratedSolution[0])));
        }
        result[0][1] = 0 - temp1;
        result[1][0] = 0 - temp1;

        return result;
    }

    /**
     * The method for computing the object functions evaluated at the iterated solutions.
     * @param iteratedSolution the iterated vector of solutions.
     * @return the object functions evaluated at the iterated solutions.
     */
    @Override
    public double[] objectFunctionVector(final double[] iteratedSolution) {
        // theta is at iteratedSolution[0], alpha at [1]
        final double[] result = new double[iteratedSolution.length];
        double temp1 = 0, temp2 = 0, temp3 = 0;

        // compute theta
        for (int i = 1; i < u; i++) {
            temp1 += (1 / (iteratedSolution[0] + (i * iteratedSolution[1])));
        }
        for (int i = 1; i < n; i++) {
            temp2 += (1 / (iteratedSolution[0] + i));
        }
        result[0] = temp1 - temp2;

        // compute alpha
        temp1 = 0;
        temp2 = 0;
        temp3 = 0;
        for (int i = 1; i < u; i++) {
            temp1 += (i / (iteratedSolution[0] + (i * iteratedSolution[1])));
        }
        for (final Map.Entry<Integer, Integer> entry : eqClasses.entrySet()) {
            temp3 = 0;
            if (entry.getKey() != 1) {
                for (int j = 1; j < entry.getKey(); j++) {
                    temp3 += (1 / (j - iteratedSolution[1]));
                }
                temp2 += entry.getValue() * temp3;
            }
        }
        result[1] = temp1 - temp2;

        return result;
    }

}
