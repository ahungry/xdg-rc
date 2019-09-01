(ns xdg-rc.core
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [clojure.spec.test.alpha :as stest]
   ))

;; https://specifications.freedesktop.org/basedir-spec/latest/ar01s03.html

(defn get-home [] (System/getProperty "user.home"))

(defn get-xdg-config-home
  "Get the XDG_CONFIG_HOME or HOME/.config if its not defined."
  []
  (or (System/getenv "XDG_CONFIG_HOME")
      (str (get-home) "/.config")))

(defn with-home
  "Will prefix with the home."
  ([s] (str (get-home) "/" s))
  ([s s2] (str (with-home s) "/" s2)))

(defn with-xdg
  "Will prefix with the XDG home."
  ([s] (str (get-xdg-config-home) "/" s))
  ([s s2] (str (with-xdg s) "/" s2)))

(defn -exists [x]
  (.exists (clojure.java.io/file x)))

(defn exists [s]
  (-exists (with-xdg s)))

(defn make-file-if-not-exists!
  "Create a file unless it already exists."
  ([filename] (make-file-if-not-exists! filename ""))
  ([filename content]
   (when-not (-exists filename)
     (clojure.java.io/make-parents filename)
     (spit filename content))))

(defn make-config! [system {:keys [filename content]}]
  (make-file-if-not-exists! (with-xdg system filename) content))

(defn make-configs!
  "Given a SYSTEM name, will ensure the directories/files exist."
  [system ms]
  (clojure.java.io/make-parents (with-xdg system ".gitkeep"))
  (map (partial make-config! system) ms))

(defn slurp-if-exists [filename]
  (when (-exists filename)
    (slurp filename)))

(defn make-xdg-rc-filename [system]
  (str system "rc"))

(defn make-classic-rc-filename [system]
  (str "." system "rc"))

(defn make-xdg-rc-filename-path [system]
  (with-xdg system (make-xdg-rc-filename system)))

(defn make-xdg-rc-file!
  "Just use a single config (runcom) type file."
  [system content]
  (make-config! system {:filename (make-xdg-rc-filename system) :content content}))

(defn get-xdg-rc-file [system]
  (let [filename (make-xdg-rc-filename-path system)]
    (slurp-if-exists filename)))

(defn make-classic-rc-file!
  "Use the old style home directory location for RC file."
  [system content]
  (let [filename (with-home (make-classic-rc-filename system))]
    (make-file-if-not-exists! filename content)))

(defn get-classic-rc-file [system]
  (let [filename (with-home (make-classic-rc-filename system))]
    (slurp-if-exists filename)))

(defn ls
  "List the files for SYSTEM that exist."
  [system]
  (->> (clojure.java.io/file (with-xdg system))
       file-seq
       (filter -exists)))

(defn get-rc-file
  "Load the user RC file for SYSTEM, with preference to XDG by default.
  Pass in the optional second argument :classic to flip priority."
  ([system] (get-rc-file system :xdg))
  ([system preference]
   (let [classic-rc (get-classic-rc-file system)
         xdg-rc (get-xdg-rc-file system)]
     (if (= preference :classic)
       (or classic-rc xdg-rc)
       (or xdg-rc classic-rc)))))
