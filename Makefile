build-jar:
	./gradlew jar

install: build-jar
	bash ./deployment.sh
