(defproject ahungry/xdg-rc "0.0.3"
  :description "Library for working with XDG_CONFIG_HOME or other RC locations."
  :url "http://github.com/ahungry/xdg-rc"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.github.jnr/jnr-posix "3.0.45"]]
  :repl-options {:init-ns xdg-rc.core})
