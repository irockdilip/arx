package org.deidentifier.arx.risk;

import java.util.Map;

import org.apache.commons.math3.special.Gamma;

/**
* This class implements the PitmanModel, for details see Hoshino, 2001
* @author Michael Schneider
* @version 1.0
*/

public class PitmanModel extends UniquenessModel {

    /**
     * number of equivalence classes of size one in the sample
     */
    private final int c1;

    /**
     * number of equivalence classes of size two in the sample
     */
    private final int c2;

    /**
     * Population model according to Pitman, 1996
     * @param pi sampling fraction
     * @param eqClasses Map containing the equivalence class sizes (as keys) of the data set and the corresponding frequency (as values)
     * e.g. if the key 2 has value 3 then there are 3 equivalence classes of size two.
     */
    public PitmanModel(final double pi, final Map<Integer, Integer> eqClasses) {
        super(pi, eqClasses);

        c1 = this.eqClasses.get(1);
        c2 = this.eqClasses.get(2);
    }

    @Override
    public double computeRisk() {
        return (computeUniquenessTotal() / N);
    }

    @Override
    public double computeUniquenessTotal() throws IllegalArgumentException {

        // initial guess
        final double c = (c1 * (c1 - 1)) / c2;
        final double thetaGuess = ((n * u * c) - (c1 * (n - 1) * ((2 * u) + c))) / (((2 * c1 * u) + (c1 * c)) - (n * c));
        final double alphaGuess = ((thetaGuess * (c1 - n)) + ((n - 1) * c1)) / (n * u);

        // apply Newton-Rhapson algorithm to solve the Maximum Likelihood Estimates
        final NewtonPitman pitmanNewton = new NewtonPitman(u, n, eqClasses);
        final double[] initialGuess = { thetaGuess, alphaGuess };
        final double[] output = pitmanNewton.getSolution(initialGuess);

        final double theta = output[0];
        final double alpha = output[1];
        double result;
        if (alpha != 0) {
            // result = ( (Gamma.gamma(theta + 1) / Gamma.gamma(theta + alpha)) * Math.pow(N, alpha) );
            result = Math.exp(Gamma.logGamma(theta + 1) - Gamma.logGamma(theta + alpha)) * Math.pow(N, alpha);
        } else {
            result = Double.NaN;
        }

        return result;
    }

}
