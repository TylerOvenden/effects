# beans directory makefile
# written by: Craig A. Lindley
# last update: 03/21/99

SUBDIRS	= \
	blinker \
	buttons \
	datagen \
	displays \
	leds \
	meters \
	pots	\

JAVA_FILES = 

include ..\..\Makefile.rules

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

