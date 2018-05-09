# craigl packages makefile
# written by: Craig A. Lindley
# last update: 05/31/99

SUBDIRS	= \
	au \
	beans \
	compexp \
	filereader \
	filewriter \
	filters \
	jmf20 \
	osc	\
	pcmplayer \
	processors	\
	reverb \
	scope	\
	spectrumanalyzer \
	test \
	uiutils \
	utils \
	wave \
	winplayer \
	winrecorder \

JAVA_FILES = 

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

