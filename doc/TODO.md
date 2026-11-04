# TODO

Aggiornato al 2026-11-03 dopo il completamento del primo blocco operativo.
I percorsi riportati sono relativi alla radice del repository. Priorità basate su impatto,
certezza del problema e costo della correzione, non sul numero di funzionalità.

- **P1:** difetti confermati o controlli essenziali per evitare risultati ingannevoli.
- **P2:** consolidamento e verifiche di contratto; una verifica può produrre solo
  test e documentazione, senza richiedere modifiche all’API.
- **P3:** estensioni opzionali da motivare con un caso d’uso.

Nessuna voce P0: non è stato accertato un incidente in produzione.
Clojure CLI/tools.build, deploy Clojars, timeout HTTP e streaming sono già
implementati. Non vanno rifatti. La suite di integrazione non è stata rieseguita
per questa revisione; il precedente esito positivo non dimostra la copertura
completa. Dopo le correzioni: 4 test unitari passati, namespace di test caricati
e costruttori degli esempi verificati; integrazione da rieseguire.

Verifiche locali iniziali, prima delle correzioni (senza Alfresco):

- L’asserzione con `=` a un solo argomento restituisce `true` anche con status 404.
- `ex-response` solleva una nuova eccezione con body `nil`, vuoto o HTML.
- La chiave `custom:my-field` viene trasformata in `custom:myField` anche sotto `properties`.
- `(configure {})` restituisce `nil`, non la mappa di configurazione.

## P1 — Correzioni confermate e affidabilità dei test

- [x] **1. Correggere le asserzioni inefficaci.** Corretti 16 confronti in audit,
  nodes, versions, shared_links, sites, comments e tags, su status, contenuto e ID.
  Verificata l’assenza di uguaglianze unarie nell’intera suite e il rifiuto degli
  status 404/500 nei confronti di successo.

- [x] **2. Rendere robusta la gestione delle risposte.** Gestiti body vuoti,
  HTML/testo, JSON malformato e stream di errore senza nascondere l’errore HTTP
  originale. Il messaggio usa `errorKey`, poi `briefSummary`, poi il messaggio
  dell’eccezione. Gli stream di errore vengono chiusi anche in caso di fallimento;
  byte array e stream di successo restano invariati. Verificati i casi con test
  locali, anche attraverso `call-rest`. Gli errori senza status HTTP mantengono
  il fallback a 500; un nuovo schema degli errori di rete resta una decisione
  di compatibilità separata.

- [x] **3. Correggere gli esempi e i riferimenti del README.** Sistemati namespace,
  costruttori Ticket/QueryBody, configurazione, esempi dei nodi e percorso del JAR.
  Rimossa la dichiarazione di copertura non misurata. README e CHANGELOG aggiornati
  con descrizioni sintetiche; esempi remoti da verificare contro Alfresco.

- [ ] **4. Centralizzare il polling con una scadenza.** Sostituire i cicli senza
  limite in `actions_test`, `downloads_test`, `groups_test`, `renditions_test`,
  `shared_links_test`, `trashcan_test` e `versions_test` con un helper in
  `test/cral/test_utils.clj`: timeout, intervallo, ultimo risultato nel messaggio
  di errore e arresto sugli stati terminali di fallimento.
  Nei test delle rendition condivise attendere effettivamente `CREATED` e non
  uscire senza asserzioni quando la lista è popolata ma il lavoro è ancora in corso.

- [ ] **5. Garantire cleanup e ripristino dello stato.** Usare fixture o `try/finally`
  per nodi, utenti, siti, gruppi, ticket e file temporanei; ripristinare anche lo
  stato precedente delle applicazioni audit. Il cleanup attuale è spesso in fondo
  al test e viene saltato se il setup o una chiamata sollevano un'eccezione.
  Le normali asserzioni `is` fallite non interrompono il test: il problema riguarda eccezioni, attese indefinite e setup parziali.

- [ ] **6. Eliminare la dipendenza da dati casuali o preesistenti.** Rivedere
  `rand-nth` e selezioni del primo elemento in preferenze, categorie, audit,
  azioni e contenitori dei siti con fixture dedicate o identificatori espliciti.
  Riprodurre il 404 intermittente di `get-preference-test` prima di attribuirlo
  al server; includere il caso di lista vuota e identificatori con caratteri speciali.
  I test audit devono generare e cancellare le proprie entry, non entry arbitrarie.
  Non eliminare ogni uso di `first`: resta corretto per una lista creata e controllata dal test. La causa del 404 delle preferenze non è ancora accertata.

- [ ] **7. Aggiungere test locali del contratto HTTP.** Coprire `call-rest`,
  `ok-response`, `ex-response`, autenticazione, query param, header e wrapper
  rappresentativi con metodi HTTP sostituiti e, dove serve, un server locale.
  Verificare timeout globali/per chiamata, byte array/stream, chiusura degli stream
  di errore e download binari identici all'originale. `utils_test.clj` copre ora
  conversione delle chiavi e gestione delle risposte; resta da estendere la
  verifica ad autenticazione, opzioni e wrapper rappresentativi.

## P2 — Consolidamento di sviluppo e rilascio

Per la CI completa servono polling limitato, cleanup e ambiente isolato.
Le correzioni locali e gli esempi possono procedere indipendentemente.

- [ ] **8. Separare unit test e integrazione con comandi documentati.** Introdurre
  alias, directory o metadata espliciti in `deps.edn`; il comando unitario deve
  scoprire tutti i test locali senza mantenere una lista manuale in CI.

- [ ] **9. Ripristinare la configurazione nelle fixture e chiarirne il contratto.**
  `test/cral/fixtures.clj` modifica `cral.config/alfresco` e le radici di
  `user`/`password` senza ripristinarle. Salvare e ripristinare lo stato con
  `finally`. Il README ora distingue la chiamata a `configure` dalla lettura
  dell’atom. Restano da validare timeout e path con test mirati. Un client multi-repository è invece un’estensione opzionale (P3).

- [ ] **10. Uniformare le opzioni delle funzioni pubbliche.** Per esempio
  `delete-rating` in `api/core/ratings.clj` non accetta `opts`, diversamente dagli
  altri wrapper: verificare tutte le arità e rendere coerenti override dei timeout,
  header restituiti e opzioni di download.

- [ ] **11. Automatizzare la disponibilità dei servizi.** In
  `docker/docker-compose.yml` e `alfresco.sh` aggiungere healthcheck, attesa con
  scadenza e diagnostica su PostgreSQL, Alfresco e trasformazioni. Verificare la
  configurazione di `transform-router` e `shared-file-store`: sono referenziati
  nelle proprietà ma non definiti nel Compose attuale; chiarire il percorso di
  trasformazione effettivamente usato prima di aggiungere servizi.

- [ ] **12. Rendere gli script portabili e gli ambienti isolabili.** Risolvere i
  percorsi rispetto allo script, propagare gli errori e allineare `alfresco.bat`
  a Compose v2 e all'uso di `docker/.env`. Permettere un ambiente di test con
  volumi dedicati, eliminabile senza coinvolgere i volumi di sviluppo; documentare
  che gli aggiornamenti major di PostgreSQL richiedono migrazione dei dati.

- [ ] **13. Aggiungere una CI di integrazione riproducibile.** Dopo la fase 2,
  avviare l'ambiente dedicato, attendere i servizi, eseguire la suite con timeout
  globale, salvare log e risultati e garantire lo stop anche in caso di errore.
  Valutare inizialmente esecuzione manuale o pianificata per il costo dello stack.

- [ ] **14. Misurare la copertura e introdurre analisi statica.** La CI attuale
  esegue solo `cral.utils-test` e crea il JAR. La dichiarazione non misurata
  «100% test coverage» è stata rimossa dal README. Aggiungere un report
  riproducibile e configurare lint/formatting
  con attenzione alle asserzioni inefficaci e agli import inutilizzati.
  Iniziare con un controllo mirato delle asserzioni, senza un reformat globale.

- [ ] **15. Automatizzare i controlli prima della pubblicazione.** `deploy` e
  `deploy.sh` esistono già: aggiungere controlli su working tree, versione/tag
  e validazione del JAR/POM, mantenendo i controlli locali eseguibili senza
  pubblicare. Documentare la sequenza di release e le modifiche incompatibili.
  La sequenza manuale di release è già documentata: aggiungere soltanto i controlli mancanti, senza riscrivere il flusso esistente.

- [ ] **16. Rendere verificabile la compatibilità dichiarata.** Documentare le
  versioni di Java e Alfresco effettivamente provate e aggiungere uno smoke test
  GraalVM per caricamento namespace, JSON e HTTP. La dichiarazione GraalVM nel
  README oggi non è accompagnata da una verifica automatica nel repository.

## P2 — Verifiche prima di cambiare il contratto pubblico

Riprodurre i casi problematici e verificare lo schema Alfresco supportato;
non trattare queste voci come autorizzazione a introdurre breaking change.

- [ ] **17. Definire quali chiavi JSON convertire.** Le conversioni in
  `src/cral/utils/utils.clj` sono ricorsive anche dentro mappe di proprietà e
  valori arbitrari delle preferenze. Verificare la conservazione di nomi custom,
  prefissi Alfresco, acronimi e chiavi con trattini: convertire i campi del
  protocollo senza alterare involontariamente i dati applicativi.
  La conversione ricorsiva è documentata: prima di cambiarla, definire una modalità compatibile o un opt-out. Il fatto che modifichi le chiavi è certo; l’impatto sui modelli Alfresco va verificato con casi reali.

- [ ] **18. Codificare gli identificatori nei segmenti URL.** I wrapper interpolano
  direttamente nomi e ID con `format`, per esempio in `api/core/preferences.clj`.
  Introdurre una funzione condivisa per i segmenti, con test per spazi, Unicode,
  `%`, `?`, `#` e `/`, evitando doppia codifica e preservando gli ID speciali.
  L’interpolazione senza encoding è confermata; verificare quali caratteri siano ammessi per ciascun identificatore prima di modificare tutti gli endpoint.

- [ ] **19. Definire la semantica dei campi assenti e `nil`.** I costruttori
  `map->...` producono record con campi `nil`, serializzati come `null` dai wrapper.
  Confrontare mappe e record per creazione e aggiornamento, distinguendo campo
  omesso e cancellazione esplicita; non rimuovere indiscriminatamente i `nil`.
  È un comportamento da caratterizzare, non una prova che ogni record attuale generi richieste errate.

- [ ] **20. Verificare il modello di ordinamento della ricerca.** In
  `src/cral/model/search.clj`, `QueryBody.sort` e `make-query-body` indicano un
  singolo `Sort`; i test coprono solo query semplici. Confrontare il payload con
  lo schema della versione Alfresco supportata e aggiungere casi con paging e
  ordinamenti multipli. È una verifica di contratto, non un bug già riprodotto.
  Il record `Company` mancante è invece un miglioramento ergonomico P3: il campo
  `company` accetta già una mappa.

## P3 — Estensioni opzionali

- [ ] **21. Aggiungere un helper di paginazione opzionale.** Riutilizzare
  `skip-count`, `max-items` e `has-more-items` delle risposte senza modificare
  i wrapper a pagina singola. Definire valutazione lazy o riduzione, propagazione
  degli errori e condizioni di arresto; testare pagine vuote e più pagine.

- [ ] **22. Valutare helper per operazioni asincrone.** Solo dopo aver consolidato
  il polling dei test, offrire attese esplicite per download e rendition con
  scadenza e gestione degli stati di errore. Evitare retry automatici delle
  operazioni di scrittura senza una politica esplicita di idempotenza.

- [ ] **23. Valutare un client con configurazione esplicita per repository.**
  L’atom globale è una scelta attuale, non un bug nell’uso con un solo server.
  Introdurre un contesto per client soltanto se serve usare più repository nello
  stesso processo; preservare le chiamate esistenti.
- [ ] **24. Valutare un record `Company`.** I TODO in `model/core.clj` sono reali,
  ma la mappa già accettata permette di inviare i dati: nessun blocco funzionale
  dimostrato. Implementarlo se migliora esempi e discoverability.

## Ordine operativo suggerito

1. **Completato:** correggere asserzioni e documentazione palesemente errate.
2. Aggiungere regressioni per errori HTTP, poi correggere il trasporto.
3. Limitare il polling, assicurare cleanup e rendere deterministici i dati dei test.
4. Separare le suite e automatizzare l’integrazione su un ambiente dedicato.
5. Affrontare le verifiche di serializzazione e ricerca con test mirati;
   scegliere le estensioni P3 in base alle esigenze effettive.
