# TODO

Aggiornato al 2026-11-17 sul commit `a15db21`. Solo attività ancora aperte;
percorsi relativi alla radice del repository. Numerazione riorganizzata.

Priorità: **P1** affidabilità e regressioni; **P2** consolidamento;
**P3** proposte opzionali o rimandate. Le verifiche di contratto richiedono
prima un caso riproducibile e una scelta di compatibilità.

## Test e affidabilità

- [ ] **1. P1 — Rendere deterministici i dati di integrazione.** Eliminare le
  selezioni casuali in `audit_test.clj`, `categories_test.clj`, `actions_test.clj`
  e `sites_test.clj`, usando risorse dedicate o identificatori espliciti.
  I test audit devono generare e cancellare le proprie entry. Conservare `first`
  quando la lista è controllata dal test. Le preferenze sono già state sistemate.

- [ ] **2. P1 — Coprire il contratto HTTP con regressioni locali.** Verificare
  `call-rest`, `ok-response`, `ex-response`, autenticazione, query param, header,
  timeout globali/per chiamata e wrapper rappresentativi. Includere errori vuoti,
  non JSON e stream, chiusura delle risorse e download byte-array/stream.
  `utils_test.clj` contiene attualmente solo quattro test di conversione: le
  verifiche locali svolte durante le modifiche non sono tutte conservate in CI.
  Aggiungere anche verifiche del payload JSON di ricerca con paging e sort;
  il relativo test contro Alfresco esiste già in `search_test.clj`.

- [ ] **3. P2 — Separare unit test e integrazione.** Definire alias, directory
  o metadata in `deps.edn`, con comandi documentati e discovery automatica.
  La CI veloce deve eseguire tutti i test locali, non un namespace fissato a mano.

- [ ] **4. P2 — Consolidare le verifiche di polling e cleanup.** Il codice è
  già implementato in `test_utils.clj` e `fixtures.clj`. Restano regressioni su
  timeout, errori HTTP, stati terminali, setup parziali e pulizie che falliscono.
  Verificare che tutte le pulizie vengano tentate e che i fallimenti originali
  restino visibili. Gli utenti temporanei sono disabilitati e restano nel
  repository: è un limite noto, non una cancellazione ancora da implementare.

- [ ] **5. P2 — Verificare l’esito completo della pipeline.** Il workflow
  `integration.yml` e gli script Docker esistono già, con isolamento, attese,
  timeout e log. Controllare un’esecuzione recente su `main`, compresi cleanup,
  artifact e risorse del runner. La verifica locale del 17 novembre riguarda
  i tre test di ricerca, non l’intera suite dopo tutte le modifiche.

## Contratto pubblico e serializzazione

- [ ] **6. P2 — Preservare le chiavi dei dati applicativi.** Le conversioni in
  `src/cral/utils/utils.clj` raggiungono anche `properties` e valori arbitrari:
  `custom:my-field` diventa `custom:myField`. Definire i confini della conversione
  con casi reali e prevedere compatibilità o opt-out prima di cambiarla.

- [ ] **7. P2 — Chiarire campi assenti e `nil`.** I record serializzano anche
  i campi non valorizzati come `null`. Confrontare mappe e record nelle operazioni
  di creazione e aggiornamento, distinguendo omissione e cancellazione esplicita.
  Non rimuovere indiscriminatamente i `nil`: l’effetto dipende dall’endpoint.

- [ ] **8. P2 — Codificare i segmenti degli URL.** I wrapper interpolano ID e
  nomi con `format`, per esempio in `api/core/preferences.clj`. Verificare i
  caratteri ammessi e introdurre encoding condiviso per spazi, Unicode, `%`, `?`,
  `#` e `/`, preservando gli ID speciali ed evitando doppia codifica.

- [ ] **9. P2 — Validare la configurazione.** In `cral.config` verificare
  timeout, schema, host, porta e path, con errori comprensibili. Il ripristino
  della configurazione nelle fixture e la documentazione di `configure` sono
  già sistemati; non occorre rifarli.

## Ambiente di sviluppo, qualità e release

- [ ] **10. P2 — Allineare gli script di sviluppo.** Risolvere i percorsi
  rispetto ad `alfresco.sh`/`alfresco.bat`, propagare gli errori e uniformare
  Compose v2 e `docker/.env`. Gli script `integration-tests.*` gestiscono già
  percorsi e volumi isolati. Documentare la migrazione dei dati tra major di PostgreSQL.

- [ ] **11. P2 — Rendere affidabile l’avvio dello stack di sviluppo.** Aggiungere
  attesa con scadenza e diagnostica in `alfresco.sh`. Chiarire i riferimenti a
  `transform-router` e `shared-file-store` nel Compose principale, che non li
  definisce. Lo stack di integrazione usa già ATS locale e controlli di readiness:
  riutilizzare quella soluzione dove appropriato, senza aggiungere servizi inutili.

- [ ] **12. P2 — Misurare copertura e introdurre analisi statica.** Produrre
  un report ripetibile e configurare lint/formatting, iniziando da asserzioni
  inefficaci e import inutilizzati. Evitare un reformat globale. Non dichiarare
  una percentuale di copertura prima di averla misurata.

- [ ] **13. P2 — Aggiungere controlli prima del deploy.** `build/deploy`,
  `deploy.sh` e la procedura manuale sono presenti. Verificare working tree,
  coerenza versione/tag e contenuto JAR/POM prima della pubblicazione; rendere
  i controlli eseguibili anche senza credenziali e senza pubblicare.

- [ ] **14. P2 — Verificare la compatibilità dichiarata.** Documentare le
  versioni Java/Alfresco effettivamente provate e aggiungere uno smoke test
  GraalVM per caricamento namespace, JSON e HTTP. Nel repository manca una
  verifica automatica della compatibilità GraalVM citata nel README.

## Proposte opzionali e attività rimandate

- [ ] **15. P3 — Redazione dei log, rimandata.** Conservare il dettaglio utile
  di richieste, risposte ed eccezioni, oscurando password, ticket e token nelle
  sole copie per logging. Le precedenti implementazioni sono state annullate;
  definire prima comportamento ed esempi attesi. I log integrali sono ancora
  presenti e il livello predefinito è `:info`.

- [ ] **16. P3 — Helper di paginazione.** Se emerge un caso d’uso, offrire
  iterazione lazy o riduzione su più pagine, con propagazione degli errori e
  condizioni di arresto, mantenendo i wrapper a pagina singola.

- [ ] **17. P3 — Helper pubblici per operazioni asincrone.** Valutare attese
  per download e rendition con scadenza e stati terminali. Il polling dei test
  esiste già; non trasferirlo automaticamente nell’API pubblica né aggiungere
  retry alle scritture senza una politica di idempotenza.

- [ ] **18. P3 — Configurazione per client.** Solo se serve usare più repository
  nello stesso processo, introdurre un contesto esplicito preservando le chiamate
  attuali. L’atom globale non è un difetto nell’uso con un solo server.

- [ ] **19. P3 — Record `Company`.** I TODO in `model/core.clj` sono ancora
  presenti, ma il campo accetta già una mappa. Aggiungere il record solo se
  migliora l’uso della libreria; nessun blocco funzionale dimostrato.
