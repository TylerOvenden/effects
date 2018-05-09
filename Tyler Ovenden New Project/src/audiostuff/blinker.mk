# Blinker makefile

include	makefile.rules

JAVA_FILES = \
	craigl/beans/blinker/Blinker.java

DATAFILES = 

JARFILE = jars/blinker.jar

# Create a JAR file with a suitable manifest.
$(JARFILE): $(CLASSFILES) $(DATAFILES)
	rm -rf manifest.tmp;
	echo "Name: craigl/beans/blinker/Blinker.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	jar cfm $(JARFILE) manifest.tmp craigl/beans/blinker/*.class $(DATAFILES)

all: $(JARFILE)

clobber:
	rm -rf craigl/beans/blinker/*.class
	rm -rf $(JARFILE)

