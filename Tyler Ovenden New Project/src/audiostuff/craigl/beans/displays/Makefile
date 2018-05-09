# Display Package Makefile
# written by: Craig A. Lindley
# last update: 03/21/99

JAVA_FILES = \
	DisplayDemo.java \
	IntLEDDisplay.java \
	IntLEDDisplayBeanInfo.java \
	LEDDisplayBase.java \
	ReadoutLabel.java \
	SevenSegmentDisplay.java \

include ..\..\..\Makefile.rules

CLASSES	= \
	$(JAVA_FILES:.java=.class)

all:	$(CLASSES)
	@echo done

clobber:
	rm -f *.class 


