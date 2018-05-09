# Digital Filters Makefile
# written by: Craig A. Lindley
# last update: 06/28/99

SUBDIRS	= \

JAVA_FILES = \
	IIRFilterBase.java \
	IIRFilterDesignBase.java \
	IIRBandpassFilter.java \
	IIRBandpassFilterDesign.java \
	IIRHighpassFilter.java \
	IIRHighpassFilterDesign.java \
	IIRLowpassFilter.java \
	IIRLowpassFilterDesign.java \

include ..\..\Makefile.rules

CLASSES	= \
	$(JAVA_FILES:.java=.class)

all:	$(CLASSES)
	@echo done

	
clobber:	
	rm -f *.class 


