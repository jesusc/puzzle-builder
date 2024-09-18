
# Puzzle builder

A system to build and generate puzzles automatically. It is build on top of a UML and OCL model build with USE.

To execute the web application:

```bash
cd web
mvn jetty:run
```

To try the user interface open this URL in your browser  `http://0.0.0.0:8080/gamedev`. By default, there is a simple puzzle about cars and colors (Puzzle description). You can write OCL expressions in the Additional constraints box. Click on `Generate game` (and wait a bit) to obtain a playable game in the right hand side.
