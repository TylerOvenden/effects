// Slide Pot Wrapper Class
// Written by: Craig A. Lindley
// Last Update: 03/18/99

package audiostuff.craigl.beans.pots;

// This class extends SlidePot to provide for integer values other than
// 0..100
public class IntValuedSlidePot extends SlidePot {

    /**
	 * Class constructor
	 *
	 * @param int length is the length of the slide pot in pixels
	 * @param int width is the width of the slide pot in pixels
	 * @param String caption is the label to associate with this pot
 	 * @param int maxValue is the maximum value the pot should return at
	 * max rotation.
	 * @param int minValue is the minimum value the pot should return at
	 * min rotation.
	 */
    public IntValuedSlidePot(int length, int width, String caption, 
							 int maxValue, int minValue) {

		super(length, width, caption, 0);
		
		this.minValue  = minValue;
        potGranularity = ((double) maxValue - minValue) / 100.0;
    }

	/**
	 * Get the scaled value of the pot at its current position
	 *
	 * @return int scaled int value
	 */
    public int getIntValue() {

        return (int)((getValue() * potGranularity) + minValue);
    }

	/**
	 * Set the current position of the pot to the scaled value
	 *
	 * @param int realValue is the scaled int value to set the pot to
	 */
    public void setIntValue(int realValue) {

		setValue((int)((realValue - minValue) / potGranularity));
	}

    // Private class data
    private double potGranularity;
    private int minValue;
}
