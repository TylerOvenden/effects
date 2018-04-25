package introduction;

public class Processor {
	int adjValue;
	Double previous;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public int getSamples(short [] buffer, int length) {
		int len = previous.getSamples(buffer,length);
		if(getByPass())
			return len;
		for(int i = 0; i < len; i++)
			buffer[i] = (short)(buffer[i] * adjValue);
		return len;
	}
	private boolean getByPass() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
