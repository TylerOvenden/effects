package audiostuff.craigl.scope;

public class TriggerFlag {
	
	public TriggerFlag() {
		
		value = false;
	}

	public boolean trigger() {
		
		return value;
	}

	public void triggered() {
		
		value = true;
	}
	
	public void resetTrigger() {

		value = false;
	}
	
	// Private class data
	private boolean value;
}
