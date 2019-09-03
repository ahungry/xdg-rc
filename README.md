# xdg-rc

[![Clojars Project](https://img.shields.io/clojars/v/ahungry/xdg-rc.svg)](https://clojars.org/ahungry/xdg-rc)
[![cljdoc badge](https://cljdoc.org/badge/ahungry/xdg-rc)](https://cljdoc.org/d/ahungry/xdg-rc)

A Clojure library designed to work with user specified
preferences/configs, that tend to honor $XDG_CONFIG_HOME but will also
work with the more traditional GNU/Linux ~/.foorc convention.

# Installation

Just add into your project.clj or deps:

[![Clojars Project](http://clojars.org/ahungry/xdg-rc/latest-version.svg)](http://clojars.org/ahungry/xdg-rc)

Then when you want to use it, import it as:

```clojure
(ns your-package
  (:require [xdg-rc.core :as xdg-rc]))
```

# Usage

## Getting the proper config directory

You can get the Freedesktop.org
(https://specifications.freedesktop.org/basedir-spec/latest/ar01s03.html)
compatible XDG_CONFIG_HOME as such:

```clojure
(get-xdg-config-home)
```

which will return a string result of the user's XDG_CONFIG_HOME,
defaulting to ~/.config if not defined.

For a user convenience, you can get the proper home directory for your
system, as such:

```clojure
(with-xdg "your-system-name")
```

which will tend to evaluate to: ~/.config/your-system-name

You can also use this per-file as follows:

```clojure
(with-xdg "your-system-name" "some-file")
```

and receive: ~/.config/your-system-name/some-file

## Simple RC file usage

You can have your Clojure system create/define user configs similar to
the following:

```clojure
(make-xdg-rc-file! "your-system-name" "Hello from xdg")
;; Will create a file in ~/.config/your-system-name/your-system-namerc

(make-classic-rc-file! "your-system-name" "Hello from classic")
;; Will create a file in ~/.your-system-namerc

(get-rc-file "your-system-name")
;; Will evaluate to "Hello from xdg"

(get-rc-file "your-system-name" :classic)
;; Will evaluate to "Hello from classic"
```

The get-rc-file command will try to load from both locations, with
precedence given to XDG by default, unless the :classic keyword is
given as the second argument (in which case it flips the precedence).

## Generating a config structure/tree

You can also generate many config files for your project, as follows:

```clojure
(make-configs! "your-system-name"
  [{:filename "first-file" :content "Some content"}
   {:filename "second-file" :content "your other content"}
   {:filename "third-file" :content "your last content"}])
```

This will create (honoring XDG_CONFIG_HOME) a structure similar to
this:

```sh
your-system-name
├── first-file
├── second-file
└── third-file
```

You can then view what files are present in the future, with:

```clojure
(ls "your-system-name")
```

which will return a seq of the filenames that exist.

## Useful macros for general extensability

This package also provides a macro, with-directory, which will allow
evaluation of a form with the current user.dir changed to the
directory specified by DIR.

```clojure
xdg-rc.core> (with-directory "/tmp" (spit "over-here" "found me"))
nil
xdg-rc.core> (slurp "/tmp/over-here")
"found me"
xdg-rc.core> (slurp "over-here")
Execution error (FileNotFoundException) at java.io.FileInputStream/open0 (FileInputStream.java:-2).
over-here (No such file or directory)
```

This is very usable with the following XDG based calls:

```clojure
(with-directory (xdg-cache-dir "some-system") (ls "."))
(with-directory (xdg-config-dir "some-system") (ls "."))
(with-directory (xdg-data-dir "some-system") (ls "."))
(with-directory (xdg-bin-dir "some-system") (ls "."))
```

# License

Copyright © 2019 Matthew Carter <m@ahungry.com>

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
