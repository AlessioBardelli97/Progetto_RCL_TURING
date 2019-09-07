ECHO OFf

ECHO Si eliminano eventuali processi client o server ancora attivi da esecuzioni precedenti.
taskkill /IM java.exe /F

ECHO Si eliminano eventuali documenti creati da esecuzioni precedenti.
rd /S /Q ..\Server\Prova_1
rd /S /Q ..\Server\Prova_2
rd /S /Q ..\Client\Prova_1
rd /S /Q ..\Client\Prova_2

ECHO QUESTO TEST HA LO SCOPO DI MOSTRARE IL FUNZIONAMENTO DI BASE DELL'APPLICAZIONE TURING.

cd ..\Server
ECHO Il server va in esecuzione.
START /B java MainClassTuringServer > ..\Log\Test1\output_server1.txt 2>&1
TIMEOUT 2

cd ..\Client
ECHO Il client 1 va in esecuzione.
START /B java MainClassTuringClient < ..\ClientInput\input1.txt > ..\Log\Test1\output_client_1.txt 2>&1
TIMEOUT 1

ECHO Il client 2 va in esecuzione.
START /B java MainClassTuringClient < ..\ClientInput\input2.txt > ..\Log\Test1\output_client_2.txt 2>&1

ECHO Esecuzione terminata. 
ECHO I vari messaggi del server si trovano in Log\Test1\output_server_1.txt 
ECHO I vari messaggi del client si trovano in Log\Test1\output_client_*.txt

cd ..\Test