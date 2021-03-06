package introduction;

import javax.sound.sampled.*;
import java.util.Random;

public class SoundUtils1 {

  public static float SAMPLE_RATE = 8000f;


  public static void laser(int repeat)
    throws LineUnavailableException, InterruptedException
  {
    AudioFormat af =
      new AudioFormat(
          SAMPLE_RATE, // sampleRate
          8,           // sampleSizeInBits
          1,           // channels
          true,        // signed
          false);      // bigEndian
    SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
    sdl.open(af);
    sdl.start();

    byte[] buf = new byte[1];
    int step;

    for (int j=0; j < repeat; j++) {
      step = 10;
      for(int i=0; i < 2000; i++) {
        buf[0] = ((i%step > 0) ? 32 : (byte)0);

        if(i%250 == 0) step += 2;
        sdl.write(buf,0,1);
      }
      Thread.sleep(100);
    }
    sdl.drain();
    sdl.stop();
    sdl.close(); 
  }

  public static void warp(int repeat)
		    throws LineUnavailableException, InterruptedException
		  {
		    AudioFormat af =
		      new AudioFormat(
		          SAMPLE_RATE, // sampleRate
		          8,           // sampleSizeInBits
		          1,           // channels
		          true,        // signed
		          false);      // bigEndian
		    SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
		    sdl.open(af);
		    sdl.start();

		    byte[] buf = new byte[1];
		    int step;
		    int r = (int)(Math.random()*9100)+900;
		    int s = (int)(Math.random()*25);
		    int t = (int)(Math.random()*50);
		    

		    for (int j=0; j < 100; j++) {
		      step = 1000;
		      for(int i=0; i < 100; i++) {
		        if(i < 1) {
		          buf[0] = ((i%step > 0) ? 64 : (byte)0);
		          if(i%25 == s) step--;
		        }
		        else {
		          buf[0] = ((i%step > 0) ? 64 : (byte)0);
		          if(i%50 == t) step++;
		        }
		        sdl.write(buf,0,1);
		      }
		    }
		    sdl.drain();
		    sdl.stop();
		    sdl.close();
		  }

		  public static void bang()
		    throws LineUnavailableException, InterruptedException
		  {
		    AudioFormat af =
		      new AudioFormat(
		          SAMPLE_RATE, // sampleRate
		          8,           // sampleSizeInBits
		          1,           // channels
		          true,        // signed
		          false);      // bigEndian
		    SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
		    sdl.open(af);
		    sdl.start();

		    byte[] buf = new byte[1];
		    Random r = new Random(9);
		    int s = (int)(Math.random()*8);
		    int t = (int)(Math.random()*10);
		    boolean silence = true;
		    for (int i=0 ; i < 8000 ; i++) {
		    //for (int i=0 ; i < 8000 ; i++) {
		      while(r.nextInt() % 15 == s || r.nextInt() % 13 != t) {
		          buf[0] =
		            silence ? 0 :
		              (byte)Math.abs(r.nextInt() %
		                  (int)(1. + 50. * 100* (8. + Math.cos(((double)i)
		                      * Math.PI / 10000.))));
		          i++;
		          sdl.write(buf,0,1);
		      }
		      silence = !silence;
		  }
		    sdl.drain();
		    sdl.stop();
		    sdl.close();
		  }

  public static void main(String[] args) throws Exception {
	 SoundUtils1.warp(10);	  
	 //SoundUtils1.laser(10);

	  
	  /* Thread.sleep(1000);
    SoundUtils1.warp(10);
    Thread.sleep(1000);
    SoundUtils1.bang(); */
	 // SoundUtils1.bang();
  }
}
