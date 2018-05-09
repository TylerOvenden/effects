# Meter Bean Makefile
# Written by: Craig A. Lindley
# Last Update: 07/03/98

include	makefile.rules

JAVA_FILES = \
	craigl/beans/meters/Meter.java \
	craigl/beans/meters/LEDMeter.java \
	craigl/beans/meters/AnalogMeter.java \
	craigl/beans/meters/RoundLEDMeter.java \

DATAFILES = \

JARFILE= jars/meters.jar

# Create a JAR file with a suitable manifest.
$(JARFILE): $(CLASSFILES) $(DATAFILES)
	rm -rf manifest.tmp;
	echo "Name: craigl/beans/meters/RoundLEDMeter.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	echo "Name: craigl/beans/meters/LEDMeter.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	echo "Name: craigl/beans/meters/AnalogMeter.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	jar cfm $(JARFILE) manifest.tmp craigl/beans/meters/*.class $(DATAFILES)

all: $(JARFILE)

clobber:
	rm -rf craigl/beans/meters/*.class
	rm -rf $(JARFILE)

