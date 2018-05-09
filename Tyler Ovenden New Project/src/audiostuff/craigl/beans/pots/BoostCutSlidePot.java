// Special Slide Pot Wrapper Class
// Written by: Craig A. Lindley
// Last Update: 03/18/99

package craigl.beans.pots;

// This class extends Pot to provide a specialized boost/cut pot for
// the graphic equalizer processor.

public class BoostCutSlidePot extends SlidePot {

	/**
	 * Class constructor
	 *
	 * @param int length is the length in pixels of the slide pot
	 * @param int width is the width in pixels of the slide pot
	 * @param String caption is the label to be associated with the pot
	 * @param int minDBGain is the min value of gain in dB
	 * @param int maxDBGain is the max value of gain in dB
	 * NOTE min and max values are usually the same only different in sign
	 */
	public BoostCutSlidePot(int length, int width,
							String caption, 
							int minDBGain, int maxDBGain) {

        super(length, width, caption, 50);
		
		this.minDBGain = minDBGain;

		potGranularity = ((double) maxDBGain - minDBGain) / POTRANGE;
	}

	/**
	 * Return the value of the gain at the current pot setting
	 *
	 * @return double gain
	 */
    public double getGain() {

		// Get the pots current value 0..100
		int potValue = getValue();

		double db = (potValue * potGranularity) + minDBGain;

		double gain = Math.pow(10, db / 20.0);

		if (gain >= 1.0)
			return gain;
		else
			return -gain;
    }

    // Private class data
    private double minDBGain;
	private double potGranularity;
}
