build:
	javac -classpath .:classes:/opt/pi4j/lib/'*' -d . DoorModule.java


run:
	sudo java -classpath .:classes:/opt/pi4j/lib/'*' DoorModule


clean:
	rm *.class