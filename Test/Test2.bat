ECHO OFf

ECHO Si eliminano eventuali processi client o server ancora attivi da esecuzioni precedenti.
taskkill /IM java.exe /F

ECHO Si eliminano eventuali documenti creati da esecuzioni precedenti.
rd /S /Q ..\Server\Prova_1
rd /S /Q ..\Server\Prova_2
rd /S /Q ..\Client\Prova_1
rd /S /Q ..\Client\Prova_2

ECHO QUESTO TEST HA LO SCOPO DI MOSTRARE IL COMPORTAMENTO DELL'APPLICAZIONE TURING,
ECHO NEL GESTIRE SITUAZIONI DI ERRORE.

cd ..\Server
ECHO Il server va in esecuzione.
START /B java MainClassTuringServer > ..\Log\Test2\output_server2.txt 2>&1
TIMEOUT 2

cd ..\Client
ECHO Il client 3 va in esecuzione.
START /B java MainClassTuringClient < ..\ClientInput\input3.txt > ..\Log\Test2\output_client_3.txt 2>&1

ECHO Esecuzione terminata. 
ECHO I vari messaggi del server si trovano in Log\Test2\output_server_2.txt 
ECHO I vari messaggi del client si trovano in Log\Test2\output_client_3.txt

cd ..\Test