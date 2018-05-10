// JMF Controller Adapter Class
// from JMF manual
// Last Update: 08/02/98

package audiostuff.craigl.pcmplayer;

import javax.media.*;

public class ControllerAdapter implements ControllerListener {

	public void cachingControl(CachingControlEvent e) {}
	public void controllerClosed(ControllerClosedEvent e) {}
	public void controllerError(ControllerErrorEvent e) {}
	public void connectionError(ConnectionErrorEvent e) {}
	public void internalError(InternalErrorEvent e) {}
	public void resourceUnavailable(ResourceUnavailableEvent e) {}
	public void durationUpdate(DurationUpdateEvent e) {}
	public void mediaTimeSet(MediaTimeSetEvent e) {}
	public void rateChange(RateChangeEvent e) {}
	public void stopTimeChange(StopTimeChangeEvent e) {}
	public void transition(TransitionEvent e) {}
	public void prefetchComplete(PrefetchCompleteEvent e) {}
	public void realizeComplete(RealizeCompleteEvent e) {}
	public void start(StartEvent e) {}
	public void stop(StopEvent e) {}
	public void dataStarved(DataStarvedEvent e) {}
	public void deallocate(DeallocateEvent e) {}
	public void endOfMedia(EndOfMediaEvent e) {}
	public void restarting(RestartingEvent e) {}
	public void stopAtTime(StopAtTimeEvent e) {}
	public void stopByRequest(StopByRequestEvent e) {}
	
	// Main dispatching function. Subclasses should not need to
	// override this method, but instead subclass only the 
	// individual event methods listed above that they need.

	public void controllerUpdate(ControllerEvent e) {

		if (e instanceof CachingControlEvent) { 
			cachingControl((CachingControlEvent) e);

		}	else if (e instanceof ControllerClosedEvent) {
			controllerClosed((ControllerClosedEvent) e);

			if (e instanceof ControllerErrorEvent) {
				controllerError((ControllerErrorEvent) e);

				if (e instanceof ConnectionErrorEvent) {
					connectionError((ConnectionErrorEvent) e);
				
				}	else if (e instanceof InternalErrorEvent) {
					internalError((InternalErrorEvent) e);
				
				}	else if (e instanceof ResourceUnavailableEvent) {
					resourceUnavailable((ResourceUnavailableEvent) e);
				}
			}
		
		}	else if (e instanceof DurationUpdateEvent) {
			durationUpdate((DurationUpdateEvent) e);

		}	else if (e instanceof MediaTimeSetEvent) {
			mediaTimeSet((MediaTimeSetEvent) e);

		}	else if (e instanceof RateChangeEvent) {
			rateChange((RateChangeEvent) e);

		}	else if (e instanceof StopTimeChangeEvent) {
			stopTimeChange((StopTimeChangeEvent) e);

		}	else if (e instanceof TransitionEvent) {
			transition((TransitionEvent) e);

			if (e instanceof PrefetchCompleteEvent) {
				prefetchComplete((PrefetchCompleteEvent) e);

			}	else if (e instanceof RealizeCompleteEvent) {
				realizeComplete((RealizeCompleteEvent) e);

			}	else if (e instanceof StartEvent) {
				start((StartEvent) e);

			}	else if (e instanceof StopEvent) {
				stop((StopEvent) e);

				if (e instanceof DataStarvedEvent) {
					dataStarved((DataStarvedEvent) e);

				}	else if (e instanceof DeallocateEvent) {
					deallocate((DeallocateEvent) e);
					
				}	else if (e instanceof EndOfMediaEvent) {
					endOfMedia((EndOfMediaEvent) e);

				}	else if (e instanceof RestartingEvent) {
					restarting((RestartingEvent) e);

				}	else if (e instanceof StopAtTimeEvent) {
					stopAtTime((StopAtTimeEvent) e);

				}	else if (e instanceof StopByRequestEvent) {
					stopByRequest((StopByRequestEvent) e);
				}
			}
		}
	}
}

