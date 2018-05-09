// Slide Pot Wrapper Class
// Written by: Craig A. Lindley
// Last Update: 03/18/99

package craigl.beans.pots;

// This class extends SlidePot to provide for real values other than
// 0..100
public class RealValuedSlidePot extends SlidePot {

    /**
	 * Class constructor
	 *
	 * @param int length is the length of the slide pot in pixels
	 * @param int width is the width of the slide pot in pixels
	 * @param String caption is the label to associate with this pot
 	 * @param double maxValue is the maximum value the pot should return at
	 * max rotation.
	 * @param double minValue is the minimum value the pot should return at
	 * min rotation.
	 */
    public RealValuedSlidePot(int length, int width, String caption, 
							  double maxValue, double minValue) {

		super(length, width, caption, 0);

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
