# Analysis

This document highlights about Part 3 - Performance engineering

Till commit code `05da55d`, marks a point at which only invalid postcodes would be populated in `failed_validation.csv` file (in root DIR of this project)

##### Steps taken to optimise the performance
 - Initially used HashMap as it would enhance the performance while sorting and adding values.
 - commit `ac8435c` has got an attempt to use charArray (char[]). But it was futile. This was in relation to reducing memory footprint. It resulted in taking more time and more memory.
 - commit `356600e` has got additional option to perform the conversion using ArrayList (what java stream supports).
 This also provides total execution time taken to read the original `import_data.csv` file,
 processing the data for validation,
 saving in respective files (valid/invalid UK postcodes) and
 use of distinct filter to ignore duplicate postcodes (1 found). But this is now disabled.
 - commit `38334ac` has got more in depth time logs for identifying obvious bottlenecks with using particular data structure.



### Few points considered while attempting to optimise the code
 - Deal with post-codes without space
 - Use of different regex logic. reference link - http://stackoverflow.com/questions/164979/uk-postcode-regex-comprehensive
 - Use of distinct filter on postcode column to ignore duplicates.
 - Sorting row_id column -> while reading from buffer (using ArrayList implementation) / during postcode validation (using TreeMap implementation)
 - Remove extra code coverage which is no longer used or place where it can be referenced in one line instead of creating additional variable(s)


### Observations
 - None of postcodes without space were found on manual check
 - Tired to use various other Regex mentioned on following StackOverflow discussion post: http://stackoverflow.com/questions/164979/uk-postcode-regex-comprehensive
   But it did not cater most of the edge cases for validating Part 1 task
 - Used the original Regex shared with the Task
 - Distinct filter has been disabled on lines `141` for readRecords() method and `167` for readRecordsList() method in `BulkImport.java` file
 - ArrayList method implementation 'wall' time is quicker compared to TreeMap (SortedMap) implementation for `small sets of test data`
 - Noticeable performance degradation was observed while processing 2 million records from csv file provided when ArrayList was used
 - Individual time logs can point to each process's bottleneck
 - There was a noticeable performance improvement using TreeMap (SortedMap) compared to ArrayList  data structure
 - More fine tuning was achieved by disabling filter to ignore duplicate postcodes (distinct). The memory footprint reduced and so did the execution time
 - Use of filter to look for duplicate postcodes v/s data in `import_data.csv` file, it is not worth using that filter

# Conclusion
The fastest code to execute the validation is by using option (2) TreeMap with filter to check for duplicates disabled



# Next steps
- Reduce number of lines of code further (i.e. modularise common code into a separate method)

- Rely on third-party library to perform postcode validation if regex is not mandatory (http://postcodes.io/ or MaxMind for offline usage)
    - This helps in verifying the data from third party sources
    - Flexibility to use other medium other than use of Regex
    - Gives power to tap on geo location data
    - Can help in identifying residential or business related postcodes

- Extensive code coverage in test cases to make most of the application
    - Use of third-party tools like Apache JMeter, Grinder etc for load testing
    - Find bottlenecks in the performance in relation to maximum time spent - File I/Os or Java memory
    - Add test cases for opening final valid/invalid csv files and checking the ordering of that data
    - Add support for enabling validation from/to other sources and can be extended to be used as an API service module

