// JMF2.0 File Base Class
// Written by: Craig A. Lindley
// Last Update: 06/02/99

package audiostuff.craigl.jmf20.devices;

//import java.awt.*;
import java.io.*;
import java.util.*;


public class JMFFileBase {

	// The package prefix for our packages. This prefix is registered
	// with package manager.
	public static final String PACKAGEPREFIX = "craigl.jmf20";
	public static final String DEMULTIPLEXER = 
		PACKAGEPREFIX + ".media.parser.audio.RawParser";

	// Inner classes for listening to states
    public class StateListener implements ControllerListener {

		public void controllerUpdate(ControllerEvent ce) {
			if (ce instanceof ControllerClosedEvent) {
				System.out.println("ControllerClosedEvent");

			}	else if (ce instanceof ControllerErrorEvent) {
				
				// Error occurred
				failure = true;
				String failMessage = ((ControllerErrorEvent) ce).getMessage();
				System.out.println(failMessage);

			}	else if (ce instanceof ControllerEvent) {
					synchronized (getStateLock()) {
						getStateLock().notifyAll();
					}
			}
		}
    }


    public Integer getStateLock() {
		return stateLock;
    }


	public synchronized boolean waitForState(Processor p, int state) {

		p.addControllerListener(new StateListener());
		failure = false;

		if (state == Processor.Configured)
			p.configure();
		else if (state == Processor.Realized)
			p.realize();

		while (p.getState() < state && !failure) {
			synchronized (getStateLock()) {
				try	{
					getStateLock().wait();
				}
				catch (InterruptedException ie) {
					return false;
				}
			}
		}
		return failure? false:true;
	}

	/**
	 * Register the specified package prefix with the package manager
	 *
	 * @param String prefix is the package prefix string to register
	 *
	 * @return boolean true if registration was successful
	 */
	public static boolean registerPackagePrefix(String prefix, boolean verbose) {

		// Get the vector of registered packages
		Vector packagePrefixes = PackageManager.getContentPrefixList();
		
		// Has prefix already been registered ?
		if (packagePrefixes.contains(prefix)) {
			if (verbose)
				System.out.println("Package prefix: " + prefix + " already registered");
			return false;
		}

		// Register new package prefix by appending the new prefix
		// to end of the package prefix list
		packagePrefixes.addElement(prefix);

		PackageManager.setContentPrefixList(packagePrefixes);

		// Save the changes to the package prefix list
		PackageManager.commitContentPrefixList();

		if (verbose)
			System.out.println("Package prefix: " + prefix + " registered");
		return true;
	}
	
	/**
	 * Register the specified protocol prefix with the package manager
	 *
	 * @param String prefix is the protocol prefix string to register
	 *
	 * @return boolean true if registration was successful
	 */
	public static boolean registerProtocolPrefix(String prefix, boolean verbose) {

		// Get the vector of registered packages
		Vector packagePrefixes = PackageManager.getProtocolPrefixList();
		
		// Has prefix already been registered ?
		if (packagePrefixes.contains(prefix)) {
			if (verbose)
				System.out.println("Protocol package prefix: " + prefix + " already registered");
			return false;
		}

		// Register new package prefix by appending the new prefix
		// to end of the package prefix list
		packagePrefixes.addElement(prefix);

		PackageManager.setProtocolPrefixList(packagePrefixes);

		// Save the changes to the package prefix list
		PackageManager.commitProtocolPrefixList();

		if (verbose)
			System.out.println("Protocol package prefix: " + prefix + " registered");
		return true;
	}
	
	/**
	 * Register a plug in with the plug in manager
	 *
	 * @param String className is the package name of the plug in to register
	 * @param Format [] inputFormats is the input formats supported by
	 * the plug in
	 * @param Format [] outputFormats is the output formats supported by
	 * the plug in
	 * @param int type is the type of the plug in see PlugInManager javadocs
	 * @boolean verbose if true causes status messages to be output
	 *
	 * @return boolean true if registration was successful
	 */
	public static boolean registerPlugIn(
								String className,
								Format [] inputFormats,
								Format [] outputFormats,
								int type, boolean verbose) {
		// Attempt registration
		boolean result = PlugInManager.addPlugIn(className, inputFormats,
												 outputFormats, type);
		if (!result) {
			if (verbose)
				System.out.println("Problem registering plug in: " + className);
			return false;
		}
		// Plug in registered successfully, commit the registry
		try {
			PlugInManager.commit();
			if (verbose)
				System.out.println("Plug in registration successful");
			return true;
		}
		catch(IOException e) {
			if (verbose)
				System.out.println("Problem registering plug in: " + className);
			return false;
		}
	}

	/**
	 * Register a demultiplexer plug in
	 *
	 * @param String className is the package name of the demultiplexer
	 * @boolean verbose if true causes status messages to be output
	 *
	 * @return boolean true if registration was successful
	 */
	public static boolean registerDemultiplexer(
								String className,
								boolean verbose) {
		
		Format [] inputFormats = new Format[1];
		inputFormats[0] = new ContentDescriptor("audio_raw");
		
		return registerPlugIn(className, inputFormats, null, 
							  PlugInManager.DEMULTIPLEXER, verbose);
	}

	// Class data
	private boolean failure = false;
	private Integer stateLock = new Integer(1);
}