# Utils Package Makefile
# written by: Craig A. Lindley
# last update: 04/25/99

JAVA_FILES = \
	AbstractAudio.java \
	AbstractDecoderIF.java \
	AudioFileDecoder.java \
	Assert.java \
	AudioConstants.java \
	AudioUIIF.java \
	ConvertDataInputStream.java \
	ConvertDataOutputStream.java \
	DecodeG711MuLaw.java \
	DecodePcm16LESigned.java \
	DecodePcm16BESigned.java \
	DecodePcm8Signed.java \
	DecodePcm8UnSigned.java \
	FileWriterIF.java \
	LinkedListVector.java \
	MyInt.java \
	NegotiationCompleteIF.java \
	ReadCompleteIF.java \

include ..\..\Makefile.rules

CLASSES	= \
	$(JAVA_FILES:.java=.class)

all:	$(CLASSES)
	@echo done

clobber:
	rm -f *.class 


