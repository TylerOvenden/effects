REM Run the Javadoc utility over all audio code

c:
cd \audiostuff
mkdir html

javadoc -d \audiostuff\html @javadocs.files

