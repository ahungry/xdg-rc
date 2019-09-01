# xdg-rc

A Clojure library designed to work with user specified
preferences/configs, that tend to honor $XDG_CONFIG_HOME but will also
work with the more traditional GNU/Linux ~/.foorc convention.

## Usage

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


## License

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
