# Postcode Validator

Total number of days to implement the project - 3 days (with brakes in between and research work involved)

This program is built on `Java 8` and is dependant on Stream API.
It also requires `maven 3.3.9+`

###### Reason to choose Java:
I was recently working on Java platform and have more number of years of experience (4 years) compare to Python.
Well aware of standard Java APIs.

###### Other benefits include:
 - It is flexible, portable, widely used, can run on remote machines
 - Latest Java 8 uses lambda functions and has capability of using filters

### Running the Project

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
`import_data.csv` file needs to be in resources dir at (file needs to be untared):
```sh
$ src/main/resources/import_data.csv
or
$ src/test/resources/import_data.csv
```

sample data for test `import_data.csv` file:
```sh
row_id,postcode
905529,LE14 3QB
1064397,MK12 5EY
1995262,W6 8EX
803671,IP20 9DL
122334,XXX XXXX
```