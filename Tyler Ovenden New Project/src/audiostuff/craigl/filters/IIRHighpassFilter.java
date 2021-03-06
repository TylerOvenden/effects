// IIRHighpassFilter Class
// Written by: Craig A. Lindley
// Last Update: 09/03/98

package audiostuff.craigl.filters;

// Optimized IIR highpass filter used as shelving EQ

public class IIRHighpassFilter extends IIRFilterBase {

	// IIRHighpassFilter class constructor
	// alpha, beta and gamma are precalculated filter coefficients
	// that are passed into this filter element.
	public IIRHighpassFilter(double alpha, double beta, double gamma) {

		super(alpha, beta, gamma);
	}

	// Filter coefficients can also be extracted by passing in 
	// design object.
	public IIRHighpassFilter(IIRHighpassFilterDesign fd) {

		super(fd);
	}

	// Run the filter algorithm
	public void doFilter(short [] inBuffer, double [] outBuffer,
						 int length) {

		for (int index=0; index < length; index++) {

			// Fetch sample
			inArray[iIndex] = (double) inBuffer[index];
			
			// Do indices maintainance
			jIndex = iIndex - 2;
			if (jIndex < 0) jIndex += HISTORYSIZE;
			kIndex = iIndex - 1;
			if (kIndex < 0) kIndex += HISTORYSIZE;

			// Run the highpass difference equation
			double out = outArray[iIndex] = 
				2.0 * 
				(alpha * (inArray[iIndex] - (2 * inArray[kIndex]) + inArray[jIndex]) +
				 gamma * outArray[kIndex] -
				 beta  * outArray[jIndex]);
			
			outBuffer[index] += amplitudeAdj * out;

			iIndex = (iIndex + 1) % HISTORYSIZE;
		}
	}
}

