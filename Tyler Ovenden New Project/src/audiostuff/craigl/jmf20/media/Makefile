# jmf20 packages makefile
# written by: Craig A. Lindley
# last update: 06/06/99

SUBDIRS	= \
	datasink \
	parser \
	protocol \

JAVA_FILES = 

include ..\..\..\Makefile.rules

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

