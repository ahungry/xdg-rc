# xdg-rc

A Clojure library designed to work with user specified
preferences/configs, that tend to honor $XDG_CONFIG_HOME but will also
work with the more traditional GNU/Linux ~/.foorc convention.

# Usage

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
