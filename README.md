# yada-datomic-todomvc

An implementation of the Todo Backend using Clojure, the yada web library, and
Datomic.

## Features

- [Datomic](http://www.datomic.com/)
- [Bidirectional URI routing](https://github.com/juxt/bidi)
- [Component](https://github.com/stuartsierra/component)
- [yada](https://github.com/juxt/yada)

Have a look in `project.clj` for a more complete list of the dependencies. This
project was tested in an environment running Java version 1.8.0_152.

## Usage

After cloning the repo, you can run the project from the REPL or doing a
`cider-jack-in` in Emacs.

```
cd yada-datomic
lein repl
```

Once the REPL starts, which may take a few minutes if you're running the project
for the first time, type the following commands:

```
user> (dev)
dev> (go)
```

The server will start and listen on [http://localhost:8000](http://localhost:8000).

For development, you can also seed the database with some initial dummy data by
running:

```
dev> (seed)
```

## API Resources

#### Create a Todo

Method:   POST
Endpoint: `/api/todos`
Example:

```
curl -i -H 'Content-Type:application/x-www-form-urlencoded' \
     -X POST -d "title=Get+some+green+eggs+and+ham&completed=false" \
     http://localhost:8000/api/todos
```

#### List Todos

Method:   GET
Endpoint: `/api/todos/<uuid>`
Example:

```
curl -i -H "Accept:application/json" -H 'Content-Type:application/json' \
     -X GET http://localhost:8000/api/todos
```

#### Update a Todo

Method:   POST
Endpoint: `/api/todos/<uuid>`
Example:

```
curl -i -H 'Content-Type:application/x-www-form-urlencoded' \
     -X PUT -d "title=Get+some+green+eggs+and+ham&completed=true" \
     http://localhost:8000/api/todos/<uuid>
```

## References

- [Bidirectional URI routing](https://github.com/juxt/bidi)
- [Component](https://github.com/stuartsierra/component)
- [Datomic Clojure API](http://docs.datomic.com/clojure/)
- [datomic-schema](https://github.com/Yuppiechef/datomic-schema)
- [Schema](https://github.com/plumatic/schema)
- [Timbre](https://github.com/ptaoussanis/timbre)
- [yada](https://github.com/juxt/yada)

## Acknowledgements

I would like to thank my friend, Patrick Galvin, for getting me started and
interested with Clojure and Datomic.

## License

Copyright © 2017 Albert Chan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
