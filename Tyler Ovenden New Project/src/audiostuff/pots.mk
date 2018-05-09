# Potentiometer(s) makefile

include	makefile.rules

JAVA_FILES = \
	craigl/beans/pots/Pot.java \
	craigl/beans/pots/PotBase.java \
	craigl/beans/pots/PotBeanInfo.java \
	craigl/beans/pots/SlidePot.java \
	craigl/beans/pots/SlidePotBeanInfo.java

DATAFILES = \
	craigl/beans/pots/PotIcon16.gif \
	craigl/beans/pots/PotIcon32.gif \
	craigl/beans/pots/SlidePotIcon16.gif \
	craigl/beans/pots/SlidePotIcon32.gif

JARFILE= jars/pots.jar

# Create a JAR file with a suitable manifest.
$(JARFILE): $(CLASSFILES) $(DATAFILES)
	rm -rf manifest.tmp;
	echo "Name: craigl/beans/pots/Pot.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	echo "Name: craigl/beans/pots/SlidePot.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	jar cfm $(JARFILE) manifest.tmp craigl/beans/pots/*.class $(DATAFILES)

all: $(JARFILE)

clobber:
	rm -rf craigl/beans/pots/*.class
	rm -rf $(JARFILE)

