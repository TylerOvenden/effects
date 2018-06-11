package introduction;

//jdk1.3  
import javax.sound.sampled.*;  
import java.util.Random;  
import javax.swing.*;    
import java.awt.event.*;  

public class SoundUtils2 {  

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
  int steps= (int)(Math.random()*16)+5;

  
  int s = (int)(Math.random()*steps);
  int u = (int)(Math.random()*12)+5;
  int v = (int)(Math.random()*3900)+800;

  int t = (int)(Math.random()*250);

  for (int j=0; j < u; j++) {  
    step = steps;  
    for(int i=0; i < v; i++) {  
      buf[0] = ((i%step > s) ? 35 : (byte)0);  

      if(i%250 == t) step += (int)(Math.random()*5)+1;;  
      sdl.write(buf,0,1);  
    }  
    Thread.sleep(200);  
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
  int u = (int)(Math.random()*12)+6;
  int b = (int)(Math.random()*1000)+1000;
  int t = (int)(Math.random()*80);
  int s = (int)(Math.random()*25);
  Random random = new Random();
  System.out.println(random.nextBoolean());
  
  for (int j=0; j < u; j++) {  
    step = 25;  
    for(int i=0; i < b; i++) {  
      if(i < 600) {  
        buf[0] = ((i%step > 3) ? 30 : (byte)0);  
        if(i%25 == s) {
        	if(random.nextBoolean() == false)
        	step++;  
        	else{
        	step--;	
        	}
        }	
      }  
      else {  
        buf[0] = ((i%step > 5) ? 30 : (byte)0);  
        if(i%50 == t) {
        	if(random.nextBoolean() == false)
        	step--;  
        	else{
        	step++;	
        	}
        }
   
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
  Random r = new Random();  
  boolean silence = true;  
  for (int i=0 ; i < 8000 ; i++) {  
    while(r.nextInt() % 10 != 0) {  
        buf[0] =   
          silence ? 0 :   
            (byte)r.nextInt(  
                (int)(1 + 4 * (1 + Math.cos(i  
                    * Math.PI / 8000))));  
        i++;  
        sdl.write(buf,0,1);  
    }  
    silence = !silence;  
}  
  sdl.drain();  
  sdl.stop();  
  sdl.close();  
}  
/*
 * for(int i = 0; i<8;i++) {
SoundUtils2.warp(10);  
  Thread.sleep(1000);  }
  //SoundUtils2.bang();  
 * 
 * 
 * 
 *  JButton c=new JButton("Click to test");  
    c.setBounds(250,100,95,30);  
    c.addActionListener(new ActionListener(){  
public void actionPerformed1(ActionEvent e){  
	for(int j = 0; j<8;j++) {
		try {
			SoundUtils2.laser(10);
		} catch (LineUnavailableException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		  try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  }
		  //SoundUtils2.bang();  
        }  
 */

public static void main(String[] args) {  
    JFrame f=new JFrame("test");  
   
  
    JButton b=new JButton("warp");  
    b.setBounds(50,100,95,30);  
    b.addActionListener(new ActionListener(){  
public void actionPerformed(ActionEvent e){  
	for(int i = 0; i<8;i++) {
		try {
			SoundUtils2.warp(10);
		} catch (LineUnavailableException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		  try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  }
		  //SoundUtils2.bang();  
        }  

    });  
    
    f.add(b);
    f.setSize(400,400);  
    f.setLayout(null);  
    f.setVisible(true);   
}  
} 
