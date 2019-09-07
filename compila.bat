ECHO OFF

ECHO Compilazione del client.
cd .\Client
javac *.java

ECHO Compilazione del server.
cd ..\Server
javac *.java

cd ..\