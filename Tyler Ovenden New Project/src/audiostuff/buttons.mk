# Button(s) makefile

include	makefile.rules

JAVA_FILES = \
	craigl/beans/buttons/Button.java \
	craigl/beans/buttons/SquareButton.java \
	craigl/beans/buttons/SquareButtonBeanInfo.java \
	craigl/beans/buttons/RoundButton.java \
	craigl/beans/buttons/RoundButtonBeanInfo.java \
	craigl/beans/buttons/ToggleSwitchButton.java \
	craigl/beans/buttons/ToggleSwitchButtonBeanInfo.java

DATAFILES = \
	craigl/beans/buttons/SquareButtonIcon16.gif \
	craigl/beans/buttons/SquareButtonIcon32.gif \
	craigl/beans/buttons/RoundButtonIcon16.gif \
	craigl/beans/buttons/RoundButtonIcon32.gif \
	craigl/beans/buttons/ToggleSwitchButtonIcon16.gif \
	craigl/beans/buttons/ToggleSwitchButtonIcon32.gif

JARFILE= jars/buttons.jar

# Create a JAR file with a suitable manifest.
$(JARFILE): $(CLASSFILES) $(DATAFILES)
	rm -rf manifest.tmp;
	echo "Name: craigl/beans/buttons/SquareButton.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	echo "Name: craigl/beans/buttons/RoundButton.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	echo "Name: craigl/beans/buttons/ToggleSwitchButton.class" >> manifest.tmp;
	echo "Java-Bean: True" >> manifest.tmp;
	echo "" >> manifest.tmp;
	jar cfm $(JARFILE) manifest.tmp craigl/beans/buttons/*.class $(DATAFILES)

all: $(JARFILE)

clobber:
	rm -rf craigl/beans/buttons/*.class
	rm -rf $(JARFILE)

