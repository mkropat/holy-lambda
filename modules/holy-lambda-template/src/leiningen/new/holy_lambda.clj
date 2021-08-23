(ns leiningen.new.holy-lambda
  (:require
   [leiningen.new.templates :refer [renderer
                                    project-name
                                    name-to-path
                                    ->files
                                    multi-segment
                                    sanitize-ns]]
   [clojure.string :as string] [leiningen.core.main :as main])
  (:import
   [java.util UUID]))

(def render (renderer "holy-lambda"))

(defn holy-lambda
  [name]
  (let [uuid (subs (string/replace (.toString (UUID/randomUUID)) #"-" "") 0 6)
        pname (project-name name)
        main-ns (string/replace (multi-segment (sanitize-ns name)) #".core" "")
        data {:name (project-name name)
              :nested-dirs (name-to-path main-ns)
              :main-ns main-ns
              :sanitized (name-to-path name)}
        render* #(render % data)]
    (main/info "Generating new project based on holy-lambda. Make sure that you have babashka tool installed, `docker` running and AWS account properly configured via `aws configure`.
If you're using AWS Vault make sure to set `HL_NO_PROFILE=1` environment variable.

First steps in new project:
- 1. Run bb hl:sync to sync the project with dockerized version of holy-lambda
- 2. Run bb tasks to get full list of tasks")

    (->files data
             ["src/{{nested-dirs}}/core.cljc" (render* "core.cljc")]
             ["resources/native-agents-payloads/1.edn" (render* "1.edn")]
             ["README.md" (render* "README.md")]
             ["bb.edn" (render* "bb.edn")]
             ["Dockerfile" (render* "Dockerfile")]
             ["deps.edn" (render* "deps.edn")]
             ["template.yml" (render* "template.yml")]
             [".editorconfig" (render* "editorconfig")]
             [".gitignore" (render* "gitignore")])))
