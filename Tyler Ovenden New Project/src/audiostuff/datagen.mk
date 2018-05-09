# DataGen makefile

include	makefile.rules

JAVA_FILES = \
	craigl/beans/datagen/DataGen.java

DATAFILES = 

JARFILE = jars/datagen.jar

# Create a JAR file with a suitable manifest.
$(JARFILE): $(CLASSFILES) $(DATAFILES)
	rm -rf manifest.tmp;
	echo "Name: craigl/beans/datagen/DataGen.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	jar cfm $(JARFILE) manifest.tmp craigl/beans/datagen/*.class $(DATAFILES)

all: $(JARFILE)

clobber:
	rm -rf craigl/beans/datagen/*.class
	rm -rf $(JARFILE)

