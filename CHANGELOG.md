# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Historical entries were reconstructed from the Git history between published tags.
Dates reflect the commits referenced by the tags on GitHub.

## [Unreleased]

### Added

- Interactive Bash script for Clojars deployment, with username and hidden token
  prompts.

## [0.3.6] - 2026-10-18

### Added

- Category APIs for listing, creating, reading, updating and deleting categories,
  and assigning or unassigning node categories.
- Deletion of version renditions.
- Constants for basic permission groups.
- Clojars deployment task and Git SCM metadata in the generated POM for cljdoc.
- SPDX license identifiers in project file headers.

### Changed

- Migrate development and packaging from Leiningen to Clojure CLI and tools.build.
- Update Clojure to 1.12.6, data.json to 2.5.2, Telemere to 1.4.0 and
  tools.build to 0.10.14.
- Configure CI with Java 21, unit tests and library JAR packaging.
- Update Alfresco Docker components, parameterize the PostgreSQL image and version,
  and increase Solr memory to 1.5 GB.
- Adapt the local cljdoc preview script to Clojure CLI and improve configuration
  and utility documentation.

### Fixed

- Route the two-argument `list-secondary-children` call to the secondary children
  endpoint.

## [0.3.5] - 2024-07-03

### Added

- APIs to create, list and retrieve node renditions and download their content.
- APIs to list and retrieve renditions of shared links and deleted nodes,
  including rendition content.
- APIs to create, list and retrieve version renditions and download their content.
- Reverting nodes to an earlier version.
- Retrieval of action parameter constraints.
- Deletion of node renditions.
- Integration tests for rendition and version operations.

### Fixed

- Pass query parameters when retrieving shared-link rendition content.
- Represent the shared-link creation request body as a vector.

## [0.3.4] - 2024-06-07

### Added

- Action APIs to list available actions, retrieve action definitions and node
  actions, and execute actions.
- Search request helper `make-query-body`.
- Integration tests and documentation for action execution and simplified search.

## [0.3.3] - 2024-05-30

### Added

- Query APIs to find nodes, sites and people.
- Additional fields in the search request model.
- Integration tests for the query APIs.

### Changed

- Update Clojure to 1.11.2 and Telemere to 1.0.0-beta14.
- Update the Docker search service to Alfresco Search Services 2.0.10.

### Fixed

- Read configuration using `clojure.edn/read-string`.

## [0.3.2] - 2024-05-18

### Added

- Listing audit entries for a node, with integration tests.
- Permission and access-status constants.

### Changed

- Mark model constants with `^:const` metadata.
- Extend the audit request models.

## [0.3.1] - 2024-05-15

### Added

- Audit APIs to list applications, read and update application information, list
  and retrieve entries, and delete individual or application-wide entries.
- Rating APIs to list, create, retrieve and delete ratings.
- Site container APIs and user/group site membership management, including
  membership request approval and rejection.
- Direct-access content URL requests.
- Site role and additional content model constants.
- Configuration loading helpers and additional integration test fixtures.

### Changed

- Replace Timbre logging with Telemere 1.0.0-beta5.
- Improve HTTP request and exception logging.
- Rework the Docker configuration and startup script.

### Fixed

- Improve REST exception handling.
- Correct site API documentation links.

## [0.3.0] - 2024-04-23

### Added

- Shared-link APIs to create, list, retrieve, delete and email links, and download
  shared content.
- Trashcan APIs to list, retrieve, restore and permanently delete nodes, and
  download deleted content.
- Version APIs to list version history, retrieve version information and content,
  and delete versions.
- Discovery API and model APIs to list and retrieve types and aspects.
- Preference listing and retrieval.
- Site membership and membership request APIs, plus node-tag listing and updates.
- Alfresco content model constants.
- Local cljdoc preview script and expanded API documentation and integration tests.

### Changed

- Reorganize API namespaces from `cral.alfresco` to `cral.api` and models to
  `cral.model`; update consumers' namespace references accordingly.
- Rename site functions referring to people to use membership terminology.
- Expand node creation, content retrieval and activity query models.
- Improve test assertions and fixture cleanup.
- Adjust Docker ports to coexist with another Alfresco Docker environment.

### Fixed

- Correct camel-case names in Alfresco content model constants.
- Pass query parameters when retrieving node content.
- Correct cljdoc formatting and links.

## [0.2.1] - 2024-03-29

### Added

- Repository readiness and liveness probes.
- Unit tests for shared test utilities.

### Fixed

- Normalize query parameters after removing nil values so key conversion is
  applied to a map.
- Improve Guest Home resolution in test helpers.

## [0.2.0] - 2024-03-28

### Added

- People APIs to list, create, retrieve and update people.
- Group APIs to list, create, retrieve, update and delete groups, and manage
  group memberships.
- Favorite APIs to list, create, retrieve and delete favorites.
- Activity listing and site membership request listing.
- Tag retrieval and updates.
- Integration tests and expanded endpoint documentation.

### Changed

- Update dependencies and the Docker environment to Alfresco 23.2.1.
- Reorganize request model records.

## [0.1.1] - 2023-06-11

### Added

- Download APIs to create, retrieve and delete download requests.
- Download request models and integration tests.

## [0.0.1] - 2023-06-10

### Added

- Initial Clojure client for the Alfresco public REST APIs.
- Ticket authentication and search APIs.
- Node metadata and content operations, copying, moving, locking, parent/child
  relationships and peer associations.
- Site, comment and tag operations.
- Request records, recursive JSON key conversion, query parameter normalization,
  optional response headers and REST error handling.
- Docker development environment, CI workflow and tests.
- GraalVM-oriented authentication encoding.

[Unreleased]: https://github.com/saidone75/CRAL/compare/v0.3.6...HEAD
[0.3.6]: https://github.com/saidone75/CRAL/compare/v0.3.5...v0.3.6
[0.3.5]: https://github.com/saidone75/CRAL/compare/v0.3.4...v0.3.5
[0.3.4]: https://github.com/saidone75/CRAL/compare/v0.3.3...v0.3.4
[0.3.3]: https://github.com/saidone75/CRAL/compare/v0.3.2...v0.3.3
[0.3.2]: https://github.com/saidone75/CRAL/compare/v0.3.1...v0.3.2
[0.3.1]: https://github.com/saidone75/CRAL/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/saidone75/CRAL/compare/v0.2.1...v0.3.0
[0.2.1]: https://github.com/saidone75/CRAL/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/saidone75/CRAL/compare/v0.1.1...v0.2.0
[0.1.1]: https://github.com/saidone75/CRAL/compare/v0.0.1...v0.1.1
[0.0.1]: https://github.com/saidone75/CRAL/releases/tag/v0.0.1
