# LED(s) makefile

include 	makefile.rules

JAVA_FILES = \
	craigl/beans/leds/LEDBase.java \
	craigl/beans/leds/RoundLED.java \
	craigl/beans/leds/SquareLED.java \

DATAFILES = \

JARFILE= jars/leds.jar

# Create a JAR file with a suitable manifest.
$(JARFILE): $(CLASSFILES) $(DATAFILES)
	rm -rf manifest.tmp;
	echo "Name: craigl/beans/leds/RoundLED.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	echo "Name: craigl/beans/leds/SquareLED.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	jar cfm $(JARFILE) manifest.tmp craigl/beans/leds/*.class $(DATAFILES)

all: $(JARFILE)

clobber:
	rm -rf craigl/beans/leds/*.class
	rm -rf $(JARFILE)

