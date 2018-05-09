# Audio Applications Makefile
# written by: Craig A. Lindley
# last update: 02/14/99

SUBDIRS	= \
	guitartuner \
	phrasesampler \

JAVA_FILES = \


include ..\Makefile.rules

CLASSES	= \
	$(JAVA_FILES:.java=.class)

all:	$(CLASSES)

	for i in $(SUBDIRS); do \
		(cd $$i; pwd; make all); \
	done

	
clobber:	
	
	for i in $(SUBDIRS); do \
		(cd $$i; pwd; make clobber); \
	done

