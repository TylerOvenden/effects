# LEDDisplay makefile

include	makefile.rules

JAVA_FILES = \
	craigl/beans/displays/IntLEDDisplay.java \
	craigl/beans/displays/IntLEDDisplayBeanInfo.java \
	craigl/beans/displays/LEDDisplayBase.java \
	craigl/beans/displays/SevenSegmentDisplay.java \

DATAFILES = \
	craigl/beans/displays/LEDDisplayIcon16.gif \
	craigl/beans/displays/LEDDisplayIcon32.gif \

JARFILE= jars/displays.jar

# Create a JAR file with a suitable manifest.
$(JARFILE): $(CLASSFILES) $(DATAFILES)
	rm -rf manifest.tmp;
	echo "Name: craigl/beans/displays/IntLEDDisplay.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	jar cfm $(JARFILE) manifest.tmp craigl/beans/displays/*.class $(DATAFILES)

all: $(JARFILE)

clobber:
	rm -rf craigl/beans/displays/*.class
	rm -rf $(JARFILE)

