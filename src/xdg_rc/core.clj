(ns xdg-rc.core)

;; https://specifications.freedesktop.org/basedir-spec/latest/ar01s03.html

(defn get-xdg-config-home
  "Get the XDG_CONFIG_HOME or HOME/.config if its not defined."
  []
  (or (System/getenv "XDG_CONFIG_HOME")
      (str (System/getProperty "user.home") "/.config")))

(def get-config-home get-xdg-config-home)

(defn get-rc-file-raw
  "Read a file if it exist."
  []
  (let [defaults (read-string (slurp "../conf/default-rc"))
        home-rc (format "%s/.insectariumrc" (System/getProperty "user.home"))
        xdg-rc (format "%s/insectarium/insectariumrc" (get-config-home))]
    (conj
     defaults
     (if (.exists (clojure.java.io/file home-rc))
       (read-string (slurp home-rc)))
     (if (.exists (clojure.java.io/file xdg-rc))
       (read-string (slurp xdg-rc))))))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
