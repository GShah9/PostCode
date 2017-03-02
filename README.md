# Postcode Validator

This program is built on `Java 8` and is dependant on Stream API.
It also requires `maven 3.3.9+`

The code can be compiled and executed using maven via commandline or latest java supported IDE.

Following line is used to compile the code (using command-line):
```sh
$ cd PostCode 
$ mvn clean install
```

To run the tests:
```sh
$ mvn test
```

To run the main program to read csv file and create respective valid/invalid files: 
```sh
$ mvn exec:java
```
**You would need to enter number '1' or '2' to perform the task**

#### Note:
`import_data.csv` file needs to be in resources dir at:
```sh
$ src/main/resources/import_data.csv
or
$ src/test/resources/import_data.csv
```
