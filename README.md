- tenerci scritto ogni giorno cosa facciamo come se stessimo facendo dei "daily scrum" far finta di avere 1 o 2 sprint, poi è da capire cosa bisogna dire in qugli sprint.
- usare gradle per buildare il progetto.
- usare git ovviamente, ma come vogliono loro
- prima di scrivere il codice dobbiamo scrivere i test possibili per la prossima feature che va creata
- con i casi corretti in cui deve fallire e i casi in cui invece deve funzionare, alla fine del test rifattorizza (N.B. i test vanno definiti con @Test sopra e sono semplici funzioni java)
- se branchiamo nello sviluppo, le integrazioni devono essere molto frequenti e la verifica deve essere automatica, piccoli cambiamenti spesso integrati, usa github ci/cd
- scegliere un giorno ogni tot in cui si refactorizza
- segui le code smells per capire se c'è refactoring da fare
- per capire la qualità del codice si usano coupling (tra le classi, vogliamo basso) e cohesion (interno ad una classe, vogliamo alta),
- una classe deve seguire i principi S.O.L.I.D. (in pratica dare poche (una sola) cosa da fare agli oggetti e quella responsabilità deve essere trattata dalla classe, una volta che scrivi la classe non modificare quello che hai gia fatto ma fai in modo che tu possa espandere in futuro, non renderla dipendente da altre classi piu semplici)
- scrivi codice "semplice", aka non duplicare e scrivi codice chiaro per capire subito cosa fa la classe
- una volta scritto il codice, durante il refactoring, riscrivi codice seguendo design pattern, ma non seguirli troppo se no rischi over engeneering
- se vedi un code smell puoi deodorizzarlo secondo alcuni principi (pacco di slide 10 e 11)
- ci sono tool automatici per aiutare ad analizzare il codice scritto e pulirlo
- per testare usare anche copie semplificate di varie classi. vedi ultime slides

riassunto del riassunto, segui quello che dice
https://refactoring.guru/


-ogni singola parte del gioco deve essere un oggetto a se stante per rispettare il concetto di SingleResponsibility

exPosition

DEVI FARE UNA CLASSE DI TUTTO TUTTO

board con nxn squares che possono contenere una stone
squares possono essere bianchi o neri quindi potrebbe essere utile isolarli, ma potrebbe essere una funzione della board dato che serve solo per analizzare gli escort
stone che tiene se bianco o nero o non piazzata
la posizione è uno stato separato penso

avremo un interfaccia che decide le game rules per permettere polimorfismo

il game engine prendera le game rules dall'interfaca e le applica 

per selezionare le regole si usa una rule factory e un enumerate con le varie regole disponibili per creare l'oggetto delle regole
