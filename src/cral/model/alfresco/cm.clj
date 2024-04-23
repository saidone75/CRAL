;  CRAL
;  Copyright (C) 2023-2024 Saidone
;
;  This program is free software: you can redistribute it and/or modify
;  it under the terms of the GNU General Public License as published by
;  the Free Software Foundation, either version 3 of the License, or
;  (at your option) any later version.
;
;  This program is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;  GNU General Public License for more details.
;
;  You should have received a copy of the GNU General Public License
;  along with this program.  If not, see <http://www.gnu.org/licenses/>.

; generated with https://saidone.org/#/cm-clj

(ns cral.model.alfresco.cm)

(def cm-uri "http://www.alfresco.org/model/content/1.0")
(def cm-prefix "cm")
(def rn-uri "http://www.alfresco.org/model/rendition/1.0")
(def rn-prefix "rn")
(def exif-uri "http://www.alfresco.org/model/exif/1.0")
(def exif-prefix "exif")
(def audio-uri "http://www.alfresco.org/model/audio/1.0")
(def audio-prefix "audio")
(def webdav-uri "http://www.alfresco.org/model/webdav/1.0")
(def webdav-prefix "webdav")
(def type-cmobject-localname "cmobject")
(def type-cmobject (keyword (format "%s:%s" cm-prefix type-cmobject-localname)))
(def type-folder-localname "folder")
(def type-folder (keyword (format "%s:%s" cm-prefix type-folder-localname)))
(def type-content-localname "content")
(def type-content (keyword (format "%s:%s" cm-prefix type-content-localname)))
(def type-dictionary-model-localname "dictionary-model")
(def type-dictionary-model (keyword (format "%s:%s" cm-prefix type-dictionary-model-localname)))
(def type-link-localname "link")
(def type-link (keyword (format "%s:%s" cm-prefix type-link-localname)))
(def type-savedquery-localname "savedquery")
(def type-savedquery (keyword (format "%s:%s" cm-prefix type-savedquery-localname)))
(def type-systemfolder-localname "systemfolder")
(def type-systemfolder (keyword (format "%s:%s" cm-prefix type-systemfolder-localname)))
(def type-authority-localname "authority")
(def type-authority (keyword (format "%s:%s" cm-prefix type-authority-localname)))
(def type-person-localname "person")
(def type-person (keyword (format "%s:%s" cm-prefix type-person-localname)))
(def type-authority-container-localname "authority-container")
(def type-authority-container (keyword (format "%s:%s" cm-prefix type-authority-container-localname)))
(def type-zone-localname "zone")
(def type-zone (keyword (format "%s:%s" cm-prefix type-zone-localname)))
(def type-category_root-localname "category_root")
(def type-category_root (keyword (format "%s:%s" cm-prefix type-category_root-localname)))
(def type-category-localname "category")
(def type-category (keyword (format "%s:%s" cm-prefix type-category-localname)))
(def type-ml-root-localname "ml-root")
(def type-ml-root (keyword (format "%s:%s" cm-prefix type-ml-root-localname)))
(def type-ml-container-localname "ml-container")
(def type-ml-container (keyword (format "%s:%s" cm-prefix type-ml-container-localname)))
(def type-rating-localname "rating")
(def type-rating (keyword (format "%s:%s" cm-prefix type-rating-localname)))
(def type-failed-thumbnail-localname "failed-thumbnail")
(def type-failed-thumbnail (keyword (format "%s:%s" cm-prefix type-failed-thumbnail-localname)))
(def type-thumbnail-localname "thumbnail")
(def type-thumbnail (keyword (format "%s:%s" cm-prefix type-thumbnail-localname)))
(def asp-titled-localname "titled")
(def asp-titled (keyword (format "%s:%s" cm-prefix asp-titled-localname)))
(def asp-auditable-localname "auditable")
(def asp-auditable (keyword (format "%s:%s" cm-prefix asp-auditable-localname)))
(def asp-person-disabled-localname "person-disabled")
(def asp-person-disabled (keyword (format "%s:%s" cm-prefix asp-person-disabled-localname)))
(def asp-annullable-localname "annullable")
(def asp-annullable (keyword (format "%s:%s" cm-prefix asp-annullable-localname)))
(def asp-transformable-localname "transformable")
(def asp-transformable (keyword (format "%s:%s" cm-prefix asp-transformable-localname)))
(def asp-templatable-localname "templatable")
(def asp-templatable (keyword (format "%s:%s" cm-prefix asp-templatable-localname)))
(def asp-webscriptable-localname "webscriptable")
(def asp-webscriptable (keyword (format "%s:%s" cm-prefix asp-webscriptable-localname)))
(def asp-projectsummary-localname "projectsummary")
(def asp-projectsummary (keyword (format "%s:%s" cm-prefix asp-projectsummary-localname)))
(def asp-complianceable-localname "complianceable")
(def asp-complianceable (keyword (format "%s:%s" cm-prefix asp-complianceable-localname)))
(def asp-ownable-localname "ownable")
(def asp-ownable (keyword (format "%s:%s" cm-prefix asp-ownable-localname)))
(def asp-author-localname "author")
(def asp-author (keyword (format "%s:%s" cm-prefix asp-author-localname)))
(def asp-dublincore-localname "dublincore")
(def asp-dublincore (keyword (format "%s:%s" cm-prefix asp-dublincore-localname)))
(def asp-basable-localname "basable")
(def asp-basable (keyword (format "%s:%s" cm-prefix asp-basable-localname)))
(def asp-partable-localname "partable")
(def asp-partable (keyword (format "%s:%s" cm-prefix asp-partable-localname)))
(def asp-referencing-localname "referencing")
(def asp-referencing (keyword (format "%s:%s" cm-prefix asp-referencing-localname)))
(def asp-replaceable-localname "replaceable")
(def asp-replaceable (keyword (format "%s:%s" cm-prefix asp-replaceable-localname)))
(def asp-effectivity-localname "effectivity")
(def asp-effectivity (keyword (format "%s:%s" cm-prefix asp-effectivity-localname)))
(def asp-summarizable-localname "summarizable")
(def asp-summarizable (keyword (format "%s:%s" cm-prefix asp-summarizable-localname)))
(def asp-countable-localname "countable")
(def asp-countable (keyword (format "%s:%s" cm-prefix asp-countable-localname)))
(def asp-copiedfrom-localname "copiedfrom")
(def asp-copiedfrom (keyword (format "%s:%s" cm-prefix asp-copiedfrom-localname)))
(def asp-workingcopy-localname "workingcopy")
(def asp-workingcopy (keyword (format "%s:%s" cm-prefix asp-workingcopy-localname)))
(def asp-checked-out-localname "checked-out")
(def asp-checked-out (keyword (format "%s:%s" cm-prefix asp-checked-out-localname)))
(def asp-cmis-created-checked-out-localname "cmis-created-checked-out")
(def asp-cmis-created-checked-out (keyword (format "%s:%s" cm-prefix asp-cmis-created-checked-out-localname)))
(def asp-versionable-localname "versionable")
(def asp-versionable (keyword (format "%s:%s" cm-prefix asp-versionable-localname)))
(def asp-lockable-localname "lockable")
(def asp-lockable (keyword (format "%s:%s" cm-prefix asp-lockable-localname)))
(def asp-subscribable-localname "subscribable")
(def asp-subscribable (keyword (format "%s:%s" cm-prefix asp-subscribable-localname)))
(def asp-classifiable-localname "classifiable")
(def asp-classifiable (keyword (format "%s:%s" cm-prefix asp-classifiable-localname)))
(def asp-generalclassifiable-localname "generalclassifiable")
(def asp-generalclassifiable (keyword (format "%s:%s" cm-prefix asp-generalclassifiable-localname)))
(def asp-taggable-localname "taggable")
(def asp-taggable (keyword (format "%s:%s" cm-prefix asp-taggable-localname)))
(def asp-tagscope-localname "tagscope")
(def asp-tagscope (keyword (format "%s:%s" cm-prefix asp-tagscope-localname)))
(def asp-rateable-localname "rateable")
(def asp-rateable (keyword (format "%s:%s" cm-prefix asp-rateable-localname)))
(def asp-likes-rating-scheme-rollups-localname "likes-rating-scheme-rollups")
(def asp-likes-rating-scheme-rollups (keyword (format "%s:%s" cm-prefix asp-likes-rating-scheme-rollups-localname)))
(def asp-five-star-rating-scheme-rollups-localname "five-star-rating-scheme-rollups")
(def asp-five-star-rating-scheme-rollups (keyword (format "%s:%s" cm-prefix asp-five-star-rating-scheme-rollups-localname)))
(def asp-attachable-localname "attachable")
(def asp-attachable (keyword (format "%s:%s" cm-prefix asp-attachable-localname)))
(def asp-emailed-localname "emailed")
(def asp-emailed (keyword (format "%s:%s" cm-prefix asp-emailed-localname)))
(def asp-referencesnode-localname "referencesnode")
(def asp-referencesnode (keyword (format "%s:%s" cm-prefix asp-referencesnode-localname)))
(def asp-ml-document-localname "ml-document")
(def asp-ml-document (keyword (format "%s:%s" cm-prefix asp-ml-document-localname)))
(def asp-ml-empty-translation-localname "ml-empty-translation")
(def asp-ml-empty-translation (keyword (format "%s:%s" cm-prefix asp-ml-empty-translation-localname)))
(def asp-store-selector-localname "store-selector")
(def asp-store-selector (keyword (format "%s:%s" cm-prefix asp-store-selector-localname)))
(def asp-preferences-localname "preferences")
(def asp-preferences (keyword (format "%s:%s" cm-prefix asp-preferences-localname)))
(def asp-syndication-localname "syndication")
(def asp-syndication (keyword (format "%s:%s" cm-prefix asp-syndication-localname)))
(def asp-geographic-localname "geographic")
(def asp-geographic (keyword (format "%s:%s" cm-prefix asp-geographic-localname)))
(def asp-rendition-localname "rendition")
(def asp-rendition (keyword (format "%s:%s" rn-prefix asp-rendition-localname)))
(def asp-rendition2-localname "rendition2")
(def asp-rendition2 (keyword (format "%s:%s" rn-prefix asp-rendition2-localname)))
(def asp-hidden-rendition-localname "hidden-rendition")
(def asp-hidden-rendition (keyword (format "%s:%s" rn-prefix asp-hidden-rendition-localname)))
(def asp-visible-rendition-localname "visible-rendition")
(def asp-visible-rendition (keyword (format "%s:%s" rn-prefix asp-visible-rendition-localname)))
(def asp-renditioned-localname "renditioned")
(def asp-renditioned (keyword (format "%s:%s" rn-prefix asp-renditioned-localname)))
(def asp-prevent-renditions-localname "prevent-renditions")
(def asp-prevent-renditions (keyword (format "%s:%s" rn-prefix asp-prevent-renditions-localname)))
(def asp-failed-thumbnail-source-localname "failed-thumbnail-source")
(def asp-failed-thumbnail-source (keyword (format "%s:%s" cm-prefix asp-failed-thumbnail-source-localname)))
(def asp-thumbnail-modification-localname "thumbnail-modification")
(def asp-thumbnail-modification (keyword (format "%s:%s" cm-prefix asp-thumbnail-modification-localname)))
(def asp-exif-localname "exif")
(def asp-exif (keyword (format "%s:%s" exif-prefix asp-exif-localname)))
(def asp-audio-localname "audio")
(def asp-audio (keyword (format "%s:%s" audio-prefix asp-audio-localname)))
(def asp-index-control-localname "index-control")
(def asp-index-control (keyword (format "%s:%s" cm-prefix asp-index-control-localname)))
(def asp-object-localname "object")
(def asp-object (keyword (format "%s:%s" webdav-prefix asp-object-localname)))
(def asp-localizable-localname "localizable")
(def asp-localizable (keyword (format "%s:%s" cm-prefix asp-localizable-localname)))
(def asp-translatable-localname "translatable")
(def asp-translatable (keyword (format "%s:%s" cm-prefix asp-translatable-localname)))
(def asp-thumbnailed-localname "thumbnailed")
(def asp-thumbnailed (keyword (format "%s:%s" cm-prefix asp-thumbnailed-localname)))
(def assoc-formats-localname "formats")
(def assoc-formats (keyword (format "%s:%s" cm-prefix assoc-formats-localname)))
(def assoc-basis-localname "basis")
(def assoc-basis (keyword (format "%s:%s" cm-prefix assoc-basis-localname)))
(def assoc-parts-localname "parts")
(def assoc-parts (keyword (format "%s:%s" cm-prefix assoc-parts-localname)))
(def assoc-references-localname "references")
(def assoc-references (keyword (format "%s:%s" cm-prefix assoc-references-localname)))
(def assoc-replaces-localname "replaces")
(def assoc-replaces (keyword (format "%s:%s" cm-prefix assoc-replaces-localname)))
(def assoc-original-localname "original")
(def assoc-original (keyword (format "%s:%s" cm-prefix assoc-original-localname)))
(def assoc-workingcopylink-localname "workingcopylink")
(def assoc-workingcopylink (keyword (format "%s:%s" cm-prefix assoc-workingcopylink-localname)))
(def assoc-subscribed-by-localname "subscribed-by")
(def assoc-subscribed-by (keyword (format "%s:%s" cm-prefix assoc-subscribed-by-localname)))
(def assoc-ratings-localname "ratings")
(def assoc-ratings (keyword (format "%s:%s" cm-prefix assoc-ratings-localname)))
(def assoc-attachments-localname "attachments")
(def assoc-attachments (keyword (format "%s:%s" cm-prefix assoc-attachments-localname)))
(def assoc-preference-image-localname "preference-image")
(def assoc-preference-image (keyword (format "%s:%s" cm-prefix assoc-preference-image-localname)))
(def assoc-rendition-localname "rendition")
(def assoc-rendition (keyword (format "%s:%s" rn-prefix assoc-rendition-localname)))
(def assoc-failed-thumbnail-localname "failed-thumbnail")
(def assoc-failed-thumbnail (keyword (format "%s:%s" cm-prefix assoc-failed-thumbnail-localname)))
(def assoc-translations-localname "translations")
(def assoc-translations (keyword (format "%s:%s" cm-prefix assoc-translations-localname)))
(def assoc-contains-localname "contains")
(def assoc-contains (keyword (format "%s:%s" cm-prefix assoc-contains-localname)))
(def assoc-avatar-localname "avatar")
(def assoc-avatar (keyword (format "%s:%s" cm-prefix assoc-avatar-localname)))
(def assoc-member-localname "member")
(def assoc-member (keyword (format "%s:%s" cm-prefix assoc-member-localname)))
(def assoc-in-zone-localname "in-zone")
(def assoc-in-zone (keyword (format "%s:%s" cm-prefix assoc-in-zone-localname)))
(def assoc-categories-localname "categories")
(def assoc-categories (keyword (format "%s:%s" cm-prefix assoc-categories-localname)))
(def assoc-subcategories-localname "subcategories")
(def assoc-subcategories (keyword (format "%s:%s" cm-prefix assoc-subcategories-localname)))
(def assoc-ml-container-localname "ml-container")
(def assoc-ml-container (keyword (format "%s:%s" cm-prefix assoc-ml-container-localname)))
(def assoc-ml-child-localname "ml-child")
(def assoc-ml-child (keyword (format "%s:%s" cm-prefix assoc-ml-child-localname)))
(def prop-title-localname "title")
(def prop-title (keyword (format "%s:%s" cm-prefix prop-title-localname)))
(def prop-description-localname "description")
(def prop-description (keyword (format "%s:%s" cm-prefix prop-description-localname)))
(def prop-created-localname "created")
(def prop-created (keyword (format "%s:%s" cm-prefix prop-created-localname)))
(def prop-creator-localname "creator")
(def prop-creator (keyword (format "%s:%s" cm-prefix prop-creator-localname)))
(def prop-modified-localname "modified")
(def prop-modified (keyword (format "%s:%s" cm-prefix prop-modified-localname)))
(def prop-modifier-localname "modifier")
(def prop-modifier (keyword (format "%s:%s" cm-prefix prop-modifier-localname)))
(def prop-accessed-localname "accessed")
(def prop-accessed (keyword (format "%s:%s" cm-prefix prop-accessed-localname)))
(def prop-template-localname "template")
(def prop-template (keyword (format "%s:%s" cm-prefix prop-template-localname)))
(def prop-webscript-localname "webscript")
(def prop-webscript (keyword (format "%s:%s" cm-prefix prop-webscript-localname)))
(def prop-summary-webscript-localname "summary-webscript")
(def prop-summary-webscript (keyword (format "%s:%s" cm-prefix prop-summary-webscript-localname)))
(def prop-remove-after-localname "remove-after")
(def prop-remove-after (keyword (format "%s:%s" cm-prefix prop-remove-after-localname)))
(def prop-owner-localname "owner")
(def prop-owner (keyword (format "%s:%s" cm-prefix prop-owner-localname)))
(def prop-author-localname "author")
(def prop-author (keyword (format "%s:%s" cm-prefix prop-author-localname)))
(def prop-publisher-localname "publisher")
(def prop-publisher (keyword (format "%s:%s" cm-prefix prop-publisher-localname)))
(def prop-contributor-localname "contributor")
(def prop-contributor (keyword (format "%s:%s" cm-prefix prop-contributor-localname)))
(def prop-type-localname "type")
(def prop-type (keyword (format "%s:%s" cm-prefix prop-type-localname)))
(def prop-identifier-localname "identifier")
(def prop-identifier (keyword (format "%s:%s" cm-prefix prop-identifier-localname)))
(def prop-dcsource-localname "dcsource")
(def prop-dcsource (keyword (format "%s:%s" cm-prefix prop-dcsource-localname)))
(def prop-coverage-localname "coverage")
(def prop-coverage (keyword (format "%s:%s" cm-prefix prop-coverage-localname)))
(def prop-rights-localname "rights")
(def prop-rights (keyword (format "%s:%s" cm-prefix prop-rights-localname)))
(def prop-subject-localname "subject")
(def prop-subject (keyword (format "%s:%s" cm-prefix prop-subject-localname)))
(def prop-from-localname "from")
(def prop-from (keyword (format "%s:%s" cm-prefix prop-from-localname)))
(def prop-to-localname "to")
(def prop-to (keyword (format "%s:%s" cm-prefix prop-to-localname)))
(def prop-summary-localname "summary")
(def prop-summary (keyword (format "%s:%s" cm-prefix prop-summary-localname)))
(def prop-hits-localname "hits")
(def prop-hits (keyword (format "%s:%s" cm-prefix prop-hits-localname)))
(def prop-counter-localname "counter")
(def prop-counter (keyword (format "%s:%s" cm-prefix prop-counter-localname)))
(def prop-working-copy-owner-localname "working-copy-owner")
(def prop-working-copy-owner (keyword (format "%s:%s" cm-prefix prop-working-copy-owner-localname)))
(def prop-working-copy-mode-localname "working-copy-mode")
(def prop-working-copy-mode (keyword (format "%s:%s" cm-prefix prop-working-copy-mode-localname)))
(def prop-working-copy-label-localname "working-copy-label")
(def prop-working-copy-label (keyword (format "%s:%s" cm-prefix prop-working-copy-label-localname)))
(def prop-version-label-localname "version-label")
(def prop-version-label (keyword (format "%s:%s" cm-prefix prop-version-label-localname)))
(def prop-version-type-localname "version-type")
(def prop-version-type (keyword (format "%s:%s" cm-prefix prop-version-type-localname)))
(def prop-initial-version-localname "initial-version")
(def prop-initial-version (keyword (format "%s:%s" cm-prefix prop-initial-version-localname)))
(def prop-auto-version-localname "auto-version")
(def prop-auto-version (keyword (format "%s:%s" cm-prefix prop-auto-version-localname)))
(def prop-auto-version-on-update-props-localname "auto-version-on-update-props")
(def prop-auto-version-on-update-props (keyword (format "%s:%s" cm-prefix prop-auto-version-on-update-props-localname)))
(def prop-lock-owner-localname "lock-owner")
(def prop-lock-owner (keyword (format "%s:%s" cm-prefix prop-lock-owner-localname)))
(def prop-lock-type-localname "lock-type")
(def prop-lock-type (keyword (format "%s:%s" cm-prefix prop-lock-type-localname)))
(def prop-lock-lifetime-localname "lock-lifetime")
(def prop-lock-lifetime (keyword (format "%s:%s" cm-prefix prop-lock-lifetime-localname)))
(def prop-expiry-date-localname "expiry-date")
(def prop-expiry-date (keyword (format "%s:%s" cm-prefix prop-expiry-date-localname)))
(def prop-lock-is-deep-localname "lock-is-deep")
(def prop-lock-is-deep (keyword (format "%s:%s" cm-prefix prop-lock-is-deep-localname)))
(def prop-lock-additional-info-localname "lock-additional-info")
(def prop-lock-additional-info (keyword (format "%s:%s" cm-prefix prop-lock-additional-info-localname)))
(def prop-categories-localname "categories")
(def prop-categories (keyword (format "%s:%s" cm-prefix prop-categories-localname)))
(def prop-taggable-localname "taggable")
(def prop-taggable (keyword (format "%s:%s" cm-prefix prop-taggable-localname)))
(def prop-tag-scope-cache-localname "tag-scope-cache")
(def prop-tag-scope-cache (keyword (format "%s:%s" cm-prefix prop-tag-scope-cache-localname)))
(def prop-tag-scope-summary-localname "tag-scope-summary")
(def prop-tag-scope-summary (keyword (format "%s:%s" cm-prefix prop-tag-scope-summary-localname)))
(def prop-likes-rating-scheme-count-localname "likes-rating-scheme-count")
(def prop-likes-rating-scheme-count (keyword (format "%s:%s" cm-prefix prop-likes-rating-scheme-count-localname)))
(def prop-likes-rating-scheme-total-localname "likes-rating-scheme-total")
(def prop-likes-rating-scheme-total (keyword (format "%s:%s" cm-prefix prop-likes-rating-scheme-total-localname)))
(def prop-five-star-rating-scheme-count-localname "five-star-rating-scheme-count")
(def prop-five-star-rating-scheme-count (keyword (format "%s:%s" cm-prefix prop-five-star-rating-scheme-count-localname)))
(def prop-five-star-rating-scheme-total-localname "five-star-rating-scheme-total")
(def prop-five-star-rating-scheme-total (keyword (format "%s:%s" cm-prefix prop-five-star-rating-scheme-total-localname)))
(def prop-originator-localname "originator")
(def prop-originator (keyword (format "%s:%s" cm-prefix prop-originator-localname)))
(def prop-addressee-localname "addressee")
(def prop-addressee (keyword (format "%s:%s" cm-prefix prop-addressee-localname)))
(def prop-addressees-localname "addressees")
(def prop-addressees (keyword (format "%s:%s" cm-prefix prop-addressees-localname)))
(def prop-subjectline-localname "subjectline")
(def prop-subjectline (keyword (format "%s:%s" cm-prefix prop-subjectline-localname)))
(def prop-sentdate-localname "sentdate")
(def prop-sentdate (keyword (format "%s:%s" cm-prefix prop-sentdate-localname)))
(def prop-noderef-localname "noderef")
(def prop-noderef (keyword (format "%s:%s" cm-prefix prop-noderef-localname)))
(def prop-store-name-localname "store-name")
(def prop-store-name (keyword (format "%s:%s" cm-prefix prop-store-name-localname)))
(def prop-preference-values-localname "preference-values")
(def prop-preference-values (keyword (format "%s:%s" cm-prefix prop-preference-values-localname)))
(def prop-published-localname "published")
(def prop-published (keyword (format "%s:%s" cm-prefix prop-published-localname)))
(def prop-updated-localname "updated")
(def prop-updated (keyword (format "%s:%s" cm-prefix prop-updated-localname)))
(def prop-latitude-localname "latitude")
(def prop-latitude (keyword (format "%s:%s" cm-prefix prop-latitude-localname)))
(def prop-longitude-localname "longitude")
(def prop-longitude (keyword (format "%s:%s" cm-prefix prop-longitude-localname)))
(def prop-content-url-hash-code-localname "content-url-hash-code")
(def prop-content-url-hash-code (keyword (format "%s:%s" rn-prefix prop-content-url-hash-code-localname)))
(def prop-last-thumbnail-modification-localname "last-thumbnail-modification")
(def prop-last-thumbnail-modification (keyword (format "%s:%s" cm-prefix prop-last-thumbnail-modification-localname)))
(def prop-date-time-original-localname "date-time-original")
(def prop-date-time-original (keyword (format "%s:%s" exif-prefix prop-date-time-original-localname)))
(def prop-pixel-xdimension-localname "pixel-xdimension")
(def prop-pixel-xdimension (keyword (format "%s:%s" exif-prefix prop-pixel-xdimension-localname)))
(def prop-pixel-ydimension-localname "pixel-ydimension")
(def prop-pixel-ydimension (keyword (format "%s:%s" exif-prefix prop-pixel-ydimension-localname)))
(def prop-exposure-time-localname "exposure-time")
(def prop-exposure-time (keyword (format "%s:%s" exif-prefix prop-exposure-time-localname)))
(def prop-f-number-localname "f-number")
(def prop-f-number (keyword (format "%s:%s" exif-prefix prop-f-number-localname)))
(def prop-flash-localname "flash")
(def prop-flash (keyword (format "%s:%s" exif-prefix prop-flash-localname)))
(def prop-focal-length-localname "focal-length")
(def prop-focal-length (keyword (format "%s:%s" exif-prefix prop-focal-length-localname)))
(def prop-iso-speed-ratings-localname "iso-speed-ratings")
(def prop-iso-speed-ratings (keyword (format "%s:%s" exif-prefix prop-iso-speed-ratings-localname)))
(def prop-manufacturer-localname "manufacturer")
(def prop-manufacturer (keyword (format "%s:%s" exif-prefix prop-manufacturer-localname)))
(def prop-model-localname "model")
(def prop-model (keyword (format "%s:%s" exif-prefix prop-model-localname)))
(def prop-software-localname "software")
(def prop-software (keyword (format "%s:%s" exif-prefix prop-software-localname)))
(def prop-orientation-localname "orientation")
(def prop-orientation (keyword (format "%s:%s" exif-prefix prop-orientation-localname)))
(def prop-x-resolution-localname "x-resolution")
(def prop-x-resolution (keyword (format "%s:%s" exif-prefix prop-x-resolution-localname)))
(def prop-y-resolution-localname "y-resolution")
(def prop-y-resolution (keyword (format "%s:%s" exif-prefix prop-y-resolution-localname)))
(def prop-resolution-unit-localname "resolution-unit")
(def prop-resolution-unit (keyword (format "%s:%s" exif-prefix prop-resolution-unit-localname)))
(def prop-album-localname "album")
(def prop-album (keyword (format "%s:%s" audio-prefix prop-album-localname)))
(def prop-artist-localname "artist")
(def prop-artist (keyword (format "%s:%s" audio-prefix prop-artist-localname)))
(def prop-composer-localname "composer")
(def prop-composer (keyword (format "%s:%s" audio-prefix prop-composer-localname)))
(def prop-engineer-localname "engineer")
(def prop-engineer (keyword (format "%s:%s" audio-prefix prop-engineer-localname)))
(def prop-genre-localname "genre")
(def prop-genre (keyword (format "%s:%s" audio-prefix prop-genre-localname)))
(def prop-track-number-localname "track-number")
(def prop-track-number (keyword (format "%s:%s" audio-prefix prop-track-number-localname)))
(def prop-release-date-localname "release-date")
(def prop-release-date (keyword (format "%s:%s" audio-prefix prop-release-date-localname)))
(def prop-sample-rate-localname "sample-rate")
(def prop-sample-rate (keyword (format "%s:%s" audio-prefix prop-sample-rate-localname)))
(def prop-sample-type-localname "sample-type")
(def prop-sample-type (keyword (format "%s:%s" audio-prefix prop-sample-type-localname)))
(def prop-channel-type-localname "channel-type")
(def prop-channel-type (keyword (format "%s:%s" audio-prefix prop-channel-type-localname)))
(def prop-compressor-localname "compressor")
(def prop-compressor (keyword (format "%s:%s" audio-prefix prop-compressor-localname)))
(def prop-is-indexed-localname "is-indexed")
(def prop-is-indexed (keyword (format "%s:%s" cm-prefix prop-is-indexed-localname)))
(def prop-is-content-indexed-localname "is-content-indexed")
(def prop-is-content-indexed (keyword (format "%s:%s" cm-prefix prop-is-content-indexed-localname)))
(def prop-deadproperties-localname "deadproperties")
(def prop-deadproperties (keyword (format "%s:%s" webdav-prefix prop-deadproperties-localname)))
(def prop-locale-localname "locale")
(def prop-locale (keyword (format "%s:%s" cm-prefix prop-locale-localname)))
(def prop-automatic-update-localname "automatic-update")
(def prop-automatic-update (keyword (format "%s:%s" cm-prefix prop-automatic-update-localname)))
(def prop-name-localname "name")
(def prop-name (keyword (format "%s:%s" cm-prefix prop-name-localname)))
(def prop-content-localname "content")
(def prop-content (keyword (format "%s:%s" cm-prefix prop-content-localname)))
(def prop-model-name-localname "model-name")
(def prop-model-name (keyword (format "%s:%s" cm-prefix prop-model-name-localname)))
(def prop-model-description-localname "model-description")
(def prop-model-description (keyword (format "%s:%s" cm-prefix prop-model-description-localname)))
(def prop-model-author-localname "model-author")
(def prop-model-author (keyword (format "%s:%s" cm-prefix prop-model-author-localname)))
(def prop-model-published-date-localname "model-published-date")
(def prop-model-published-date (keyword (format "%s:%s" cm-prefix prop-model-published-date-localname)))
(def prop-model-version-localname "model-version")
(def prop-model-version (keyword (format "%s:%s" cm-prefix prop-model-version-localname)))
(def prop-model-active-localname "model-active")
(def prop-model-active (keyword (format "%s:%s" cm-prefix prop-model-active-localname)))
(def prop-destination-localname "destination")
(def prop-destination (keyword (format "%s:%s" cm-prefix prop-destination-localname)))
(def prop-user-name-localname "user-name")
(def prop-user-name (keyword (format "%s:%s" cm-prefix prop-user-name-localname)))
(def prop-home-folder-localname "home-folder")
(def prop-home-folder (keyword (format "%s:%s" cm-prefix prop-home-folder-localname)))
(def prop-first-name-localname "first-name")
(def prop-first-name (keyword (format "%s:%s" cm-prefix prop-first-name-localname)))
(def prop-last-name-localname "last-name")
(def prop-last-name (keyword (format "%s:%s" cm-prefix prop-last-name-localname)))
(def prop-middle-name-localname "middle-name")
(def prop-middle-name (keyword (format "%s:%s" cm-prefix prop-middle-name-localname)))
(def prop-email-localname "email")
(def prop-email (keyword (format "%s:%s" cm-prefix prop-email-localname)))
(def prop-organization-id-localname "organization-id")
(def prop-organization-id (keyword (format "%s:%s" cm-prefix prop-organization-id-localname)))
(def prop-home-folder-provider-localname "home-folder-provider")
(def prop-home-folder-provider (keyword (format "%s:%s" cm-prefix prop-home-folder-provider-localname)))
(def prop-default-home-folder-path-localname "default-home-folder-path")
(def prop-default-home-folder-path (keyword (format "%s:%s" cm-prefix prop-default-home-folder-path-localname)))
(def prop-presence-provider-localname "presence-provider")
(def prop-presence-provider (keyword (format "%s:%s" cm-prefix prop-presence-provider-localname)))
(def prop-presence-username-localname "presence-username")
(def prop-presence-username (keyword (format "%s:%s" cm-prefix prop-presence-username-localname)))
(def prop-organization-localname "organization")
(def prop-organization (keyword (format "%s:%s" cm-prefix prop-organization-localname)))
(def prop-jobtitle-localname "jobtitle")
(def prop-jobtitle (keyword (format "%s:%s" cm-prefix prop-jobtitle-localname)))
(def prop-location-localname "location")
(def prop-location (keyword (format "%s:%s" cm-prefix prop-location-localname)))
(def prop-persondescription-localname "persondescription")
(def prop-persondescription (keyword (format "%s:%s" cm-prefix prop-persondescription-localname)))
(def prop-telephone-localname "telephone")
(def prop-telephone (keyword (format "%s:%s" cm-prefix prop-telephone-localname)))
(def prop-mobile-localname "mobile")
(def prop-mobile (keyword (format "%s:%s" cm-prefix prop-mobile-localname)))
(def prop-companyaddress1-localname "companyaddress1")
(def prop-companyaddress1 (keyword (format "%s:%s" cm-prefix prop-companyaddress1-localname)))
(def prop-companyaddress2-localname "companyaddress2")
(def prop-companyaddress2 (keyword (format "%s:%s" cm-prefix prop-companyaddress2-localname)))
(def prop-companyaddress3-localname "companyaddress3")
(def prop-companyaddress3 (keyword (format "%s:%s" cm-prefix prop-companyaddress3-localname)))
(def prop-companypostcode-localname "companypostcode")
(def prop-companypostcode (keyword (format "%s:%s" cm-prefix prop-companypostcode-localname)))
(def prop-companytelephone-localname "companytelephone")
(def prop-companytelephone (keyword (format "%s:%s" cm-prefix prop-companytelephone-localname)))
(def prop-companyfax-localname "companyfax")
(def prop-companyfax (keyword (format "%s:%s" cm-prefix prop-companyfax-localname)))
(def prop-companyemail-localname "companyemail")
(def prop-companyemail (keyword (format "%s:%s" cm-prefix prop-companyemail-localname)))
(def prop-skype-localname "skype")
(def prop-skype (keyword (format "%s:%s" cm-prefix prop-skype-localname)))
(def prop-instantmsg-localname "instantmsg")
(def prop-instantmsg (keyword (format "%s:%s" cm-prefix prop-instantmsg-localname)))
(def prop-user-status-localname "user-status")
(def prop-user-status (keyword (format "%s:%s" cm-prefix prop-user-status-localname)))
(def prop-user-status-time-localname "user-status-time")
(def prop-user-status-time (keyword (format "%s:%s" cm-prefix prop-user-status-time-localname)))
(def prop-googleusername-localname "googleusername")
(def prop-googleusername (keyword (format "%s:%s" cm-prefix prop-googleusername-localname)))
(def prop-email-feed-disabled-localname "email-feed-disabled")
(def prop-email-feed-disabled (keyword (format "%s:%s" cm-prefix prop-email-feed-disabled-localname)))
(def prop-subscriptions-private-localname "subscriptions-private")
(def prop-subscriptions-private (keyword (format "%s:%s" cm-prefix prop-subscriptions-private-localname)))
(def prop-email-feed-id-localname "email-feed-id")
(def prop-email-feed-id (keyword (format "%s:%s" cm-prefix prop-email-feed-id-localname)))
(def prop-size-current-localname "size-current")
(def prop-size-current (keyword (format "%s:%s" cm-prefix prop-size-current-localname)))
(def prop-size-quota-localname "size-quota")
(def prop-size-quota (keyword (format "%s:%s" cm-prefix prop-size-quota-localname)))
(def prop-authority-name-localname "authority-name")
(def prop-authority-name (keyword (format "%s:%s" cm-prefix prop-authority-name-localname)))
(def prop-authority-display-name-localname "authority-display-name")
(def prop-authority-display-name (keyword (format "%s:%s" cm-prefix prop-authority-display-name-localname)))
(def prop-rating-score-localname "rating-score")
(def prop-rating-score (keyword (format "%s:%s" cm-prefix prop-rating-score-localname)))
(def prop-rating-scheme-localname "rating-scheme")
(def prop-rating-scheme (keyword (format "%s:%s" cm-prefix prop-rating-scheme-localname)))
(def prop-rated-at-localname "rated-at")
(def prop-rated-at (keyword (format "%s:%s" cm-prefix prop-rated-at-localname)))
(def prop-failure-count-localname "failure-count")
(def prop-failure-count (keyword (format "%s:%s" cm-prefix prop-failure-count-localname)))
(def prop-failed-thumbnail-time-localname "failed-thumbnail-time")
(def prop-failed-thumbnail-time (keyword (format "%s:%s" cm-prefix prop-failed-thumbnail-time-localname)))
(def prop-thumbnail-name-localname "thumbnail-name")
(def prop-thumbnail-name (keyword (format "%s:%s" cm-prefix prop-thumbnail-name-localname)))
(def prop-content-property-name-localname "content-property-name")
(def prop-content-property-name (keyword (format "%s:%s" cm-prefix prop-content-property-name-localname)))
