// Assert Class
// Written by: Craig A. Lindley
// Last Update: 08/01/98

// A simple assertion mechanism for asserting validity of arguments

package craigl.utils;

public class Assert {

	public static void notFalse(boolean b)
		throws IllegalArgumentException {

		if(b == false) 
			throw new IllegalArgumentException("boolean expression false");
	}

	public static void isTrue(boolean b) {
		
		notFalse(b); 
	}

	public static void isFalse(boolean b) 
		throws IllegalArgumentException {
		
		if(b != false)
			throw new IllegalArgumentException("boolean expression true");
	}

	public static void notNull(Object obj) 
		throws IllegalArgumentException {

        if(obj == null) 
            throw new IllegalArgumentException("null argument");
    }
}
