// Potentiometer Wrapper Class
// Written by: Craig A. Lindley
// Last Update: 03/18/99

package craigl.beans.pots;

// This class extends Pot to provide for real values other than 0..100
public class RealValuedPot extends Pot {

    /**
	 * Class constructor
	 *
	 * @param double maxValue is the maximum value the pot should return at
	 * max rotation.
	 * @param double minValue is the minimum value the pot should return at
	 * min rotation.
	 */
    public RealValuedPot(double maxValue, double minValue) {

        this.minValue  = minValue;
        potGranularity = (maxValue - minValue) / 100.0;
    }

	/**
	 * Get the scaled value of the pot at its current position
	 *
	 * @return double scaled double value
	 */
    public double getRealValue() {

        return (getValue() * potGranularity) + minValue;
    }

	/**
	 * Set the current position of the pot to the scaled value
	 *
	 * @param double realValue is the scaled double value to set the pot to
	 */
    public void setRealValue(double realValue) {

		setValue((int)((realValue - minValue) / potGranularity));
	}

    // Private class data
    private double potGranularity;
    private double minValue;
}
