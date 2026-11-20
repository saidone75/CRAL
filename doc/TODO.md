# TODO

Aggiornato al 2026-11-20. Solo attività e verifiche ancora aperte.

## Test e affidabilità

- [ ] **1. P2 — Consolidare le verifiche di polling e cleanup.** Il codice è
  già implementato in `test_utils.clj` e `fixtures.clj`. Restano regressioni su
  timeout, errori HTTP, stati terminali, setup parziali e pulizie che falliscono,
  usando scenari riproducibili sull’ambiente Alfresco reale.
  Verificare che tutte le pulizie vengano tentate e che i fallimenti originali
  restino visibili. Gli utenti temporanei sono disabilitati e restano nel
  repository: è un limite noto, non una cancellazione ancora da implementare.

- [ ] **2. P2 — Verificare l’esito completo della pipeline.** Il workflow
  `integration.yml` e gli script Docker esistono già, con isolamento, attese,
  timeout e log. Controllare un’esecuzione recente su `main`, compresi cleanup,
  artifact e risorse del runner. La verifica locale del 17 novembre riguarda
  i tre test di ricerca, non l’intera suite dopo tutte le modifiche. Eseguire
  anche `cral.pagination-test` contro Alfresco: l’helper è implementato, ma la
  verifica reale è ancora aperta.

## Contratto pubblico e serializzazione

- [ ] **3. P2 — Preservare le chiavi dei dati applicativi.** Le conversioni in
  `src/cral/utils/utils.clj` raggiungono anche `properties` e valori arbitrari:
  `custom:my-field` diventa `custom:myField`. Definire i confini della conversione
  con casi reali e prevedere compatibilità o opt-out prima di cambiarla.

- [ ] **4. P2 — Codificare i segmenti degli URL.** I wrapper interpolano ID e
  nomi con `format`, per esempio in `api/core/preferences.clj`. Verificare i
  caratteri ammessi e introdurre encoding condiviso per spazi, Unicode, `%`, `?`,
  `#` e `/`, preservando gli ID speciali ed evitando doppia codifica.

- [ ] **5. P2 — Validare la configurazione.** In `cral.config` verificare
  timeout, schema, host, porta e path, con errori comprensibili. Il ripristino
  della configurazione nelle fixture e la documentazione di `configure` sono
  già sistemati; non occorre rifarli.

## Ambiente di sviluppo, qualità e release

- [ ] **6. P2 — Allineare gli script di sviluppo.** Risolvere i percorsi
  rispetto ad `alfresco.sh`/`alfresco.bat`, propagare gli errori e uniformare
  Compose v2 e `docker/.env`. Gli script `integration-tests.*` gestiscono già
  percorsi e volumi isolati. Documentare la migrazione dei dati tra major di PostgreSQL.

- [ ] **7. P2 — Rendere affidabile l’avvio dello stack di sviluppo.** Aggiungere
  attesa con scadenza e diagnostica in `alfresco.sh`. Chiarire i riferimenti a
  `transform-router` e `shared-file-store` nel Compose principale, che non li
  definisce. Lo stack di integrazione usa già ATS locale e controlli di readiness:
  riutilizzare quella soluzione dove appropriato, senza aggiungere servizi inutili.

- [ ] **8. P2 — Misurare copertura e introdurre analisi statica.** Produrre
  un report ripetibile e configurare lint/formatting, iniziando da asserzioni
  inefficaci e import inutilizzati. Evitare un reformat globale. Non dichiarare
  una percentuale di copertura prima di averla misurata.

- [ ] **9. P2 — Aggiungere controlli prima del deploy.** `build/deploy`,
  `deploy.sh` e la procedura manuale sono presenti. Verificare working tree,
  coerenza versione/tag e contenuto JAR/POM prima della pubblicazione; rendere
  i controlli eseguibili anche senza credenziali e senza pubblicare.

- [ ] **10. P2 — Verificare la compatibilità dichiarata.** Documentare le
  versioni Java/Alfresco effettivamente provate e aggiungere uno smoke test
  GraalVM per caricamento namespace, JSON e chiamate a un Alfresco reale. Nel repository manca una
  verifica automatica della compatibilità GraalVM citata nel README.

## Proposte opzionali e attività rimandate

- [ ] **11. P3 — Redazione dei log, rimandata.** Conservare il dettaglio utile
  di richieste, risposte ed eccezioni, oscurando password, ticket e token nelle
  sole copie per logging. Le precedenti implementazioni sono state annullate;
  definire prima comportamento ed esempi attesi. I log integrali sono ancora
  presenti e il livello predefinito è `:info`.

- [ ] **12. P3 — Helper pubblici per operazioni asincrone.** Valutare attese
  per download e rendition con scadenza e stati terminali. Il polling dei test
  esiste già; non trasferirlo automaticamente nell’API pubblica né aggiungere
  retry alle scritture senza una politica di idempotenza.

- [ ] **13. P3 — Configurazione per client.** Solo se serve usare più repository
  nello stesso processo, introdurre un contesto esplicito preservando le chiamate
  attuali. L’atom globale non è un difetto nell’uso con un solo server.
