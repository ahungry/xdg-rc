(ns xdg-rc.core
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [clojure.spec.test.alpha :as stest]
   )
  (:import [jnr.posix LazyPOSIX POSIXHandler POSIXFactory])
  (:gen-class)
  )

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

(defn- -exists [x]
  (.exists (clojure.java.io/file x)))

(defn- exists [s]
  (-exists (with-xdg s)))

(defn- make-file-if-not-exists!
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

(defn- slurp-if-exists [filename]
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
  "List the files for the DIR that exist."
  [dir]
  (->> (clojure.java.io/file dir)
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

;; Some functions that work off File are fine with just modifying user.dir
;; Others that work off of the FileStream items need the POSIX call.
;; Some odd thing happens though - if we change back to the original dir
;; too quickly, the call acts as if it never happened (the chdir)
(defmacro with-directory
  "Run a command with directory DIR changed during form evaluation.
  While some calls in Clojure/Java work off of the `user.dir' property,
  others require changing the active directory with the POSIX interface chdir.

  POTENTIAL UNSAFE TIMING ISSUE: Evaluation of a form that relies on chdir will not
  work properly if the chdir of POSIX is reset too quickly.  Therefore this thread
  will only reset the original chdir POSIX call 1 second after evaluation of the form.

  For a more granular control, use the explicit function based variant `change-directory`
  and `restore-directory`."
  [dir & r]
  `(let [current-directory# (System/getProperty "user.dir")]
     (try
       (System/setProperty "user.dir" ~dir)
       (doto (POSIXFactory/getPOSIX) (.chdir ~dir))
       (let [result# ~@r]
         (System/setProperty "user.dir" current-directory#)
         (future
           (Thread/sleep 1e3)
           (doto (POSIXFactory/getPOSIX) (.chdir current-directory#)))
         result#)
       (catch Exception e#
         (System/setProperty "user.dir" current-directory#)
         (doto (POSIXFactory/getPOSIX) (.chdir current-directory#))
         (throw e#)
         ))))

(def original-user-dir (atom (System/getProperty "user.dir")))

(defn restore-directory
  "To be used after `change-directory' to restore the last directory."
  []
  (doto (POSIXFactory/getPOSIX) (.chdir @original-user-dir))
  (System/setProperty "user.dir" @original-user-dir))

(defn change-directory
  "Change the active directory.  Use `restore-directory' to go back to the prior defaults."
  [dir]
  (reset! original-user-dir (System/getProperty "user.dir"))
  (doto (POSIXFactory/getPOSIX) (.chdir dir))
  (System/setProperty "user.dir" dir))

(defn get-xdg-dir
  "Get the XDG_CONFIG_HOME or HOME/.config if its not defined."
  [env alt]
  (or (System/getenv env)
      (str (get-home) alt)))

(defn get-xdg-cache-dir [] (get-xdg-dir "XDG_CACHE_HOME" "/.cache"))
(defn get-xdg-config-dir [] (get-xdg-dir "XDG_CONFIG_HOME" "/.config"))
(defn get-xdg-data-dir [] (get-xdg-dir "XDG_DATA_HOME" "/.local/share"))
(defn get-xdg-bin-dir [] (get-xdg-dir "XDG_BIN_HOME" "/.local/bin"))

(defn with-dir [f]
  (fn
    ([s] (str (f) "/" s))
    ([s s2] (str (f) "/" s "/" s2))))

(defn xdg-cache-dir
  "Returns the proper XDG_CACHE_HOME for SYSTEM."
  [system]
  ((with-dir get-xdg-cache-dir) system))

(defn xdg-config-dir
  "Returns the proper XDG_CONFIG_HOME for SYSTEM."
  [system]
  ((with-dir get-xdg-config-dir) system))

(defn xdg-data-dir
  "Returns the proper XDG_DATA_HOME for SYSTEM."
  [system]
  ((with-dir get-xdg-data-dir) system))

(defn xdg-bin-dir
  "Returns the proper XDG_BIN_HOME for SYSTEM."
  [system]
  ((with-dir get-xdg-bin-dir) system))
