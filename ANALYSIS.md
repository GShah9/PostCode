# Analysis

This document highlights about Part 3 - Performance engineering

Till commit code `05da55d`, marks a point at which only invalid postcodes would be populated in `failed_validation.csv` file (in root DIR of this project)

##### Steps taken to optimise the performance:
 - Initially used HashMap as it would enhance the performance while sorting and adding values
 - commit `ac8435c` has got an attempt to use charArray (char[]). But it was futile. This was in relation to reducing memory footprint
 - commit `356600e` has got additional option to perform the conversion using ArrayList (what java stream supports).
 This also provides total execution time taken to read the original `import_data.csv` file,
 processing the data for validation,
 saving in respective files (valid/invalid UK postcodes) and
 use of distinct filter to ignore duplicate postcodes (1 found)



### Few points considered while attempting to optimise the code:
 - Deal with post-codes without space (None were found on manual check)
 - Use of distinct filter on postcode column to ignore duplicates. This can be disabled by commenting lines `91` and `112` in `BulkImport.java` file
 - Sorting items while reading from buffer (using ArrayList implementation) / during postcode validation (using TreeMap implementation)


### Observations:
 - Tired to use various other Regex mentioned on following StackOverflow discussion post: http://stackoverflow.com/questions/164979/uk-postcode-regex-comprehensive
   But it did not cater most of the edge cases for validating Part 1
 - Used the original Regex shared with the Task.
 - ArrayList method implementation 'wall' time is quicker compared to TreeMap (SortedMap) implementation for `small sets of data`
 - Noticeable performance degradation was observed while processing 2 million records from csv file provided when ArrayList was used
 - There was not much difference while using TreeMap (SortedMap) equivalent variant even while applying filter to ignore duplicate postcodes

# Conclusion:
The fastest code to execute the validation is by using option (2) TreeMap.
