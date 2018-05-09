# High level makefile
# written by: Craig A. Lindley
# last update: 03/31/99

SUBDIRS	= \
	apps \
	craigl \
	frontpanels \


JAVA_FILES = 

include makefile.rules

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

