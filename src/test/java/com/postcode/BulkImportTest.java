package com.postcode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gautam on 27/02/2017.
 *
 * This Test class performs tests on loading import_data.csv test file,
 * converting to a Map and checking it's values against known format
 * It also checks for known header format, size of header columns, size of rows
 *
 * Todo:
 * - Add other test coverage for dealing with List records
 * - Add test coverage on reading valid and invalid files created after the process is complete
 *   - Check if the right files were created
 *   - What happens if either list/map is empty (valid/invalid), does it create empty file (should it do that?)
 *   - Are the records sorted based on row_id in ascending format
 *   - Are there any missing records, compare total valid+invalid data rows against import_data.csv data rows
 */
public class BulkImportTest {

    BulkImport csvReader;
    List<String> header;
    Map<Integer, String> records;

    @Before
    public void initObjects() {
        csvReader = createCsvReader();
        records = csvReader.readRecords();
        header = csvReader.getHeader();
    }

    @Test
    public void headerSizeIsTwo() {
        Assert.assertEquals(header.size(), 2);
    }

    @Test
    public void headerColumnValues() {
        Assert.assertTrue(header.contains("row_id"));
        Assert.assertTrue(header.contains("postcode"));
    }

    @Test
    public void recordSizeIs5() {
        Assert.assertEquals(records.size(), 5);
    }

    @Test
    public void compareRecordValuesFromCSV() {
        Map<Integer, String> map1 = new HashMap<Integer, String>();
        map1.put(905529, "LE14 3QB");
        map1.put(1064397, "MK12 5EY");
        map1.put(1995262, "W6 8EX");
        map1.put(803671, "IP20 9DL");
        map1.put(122334,"XXX XXXX");

        Assert.assertEquals(records, map1);
    }

    /*
     * This method is for initialising BulkImport object and loading file that needs to be tested with
     */
    private BulkImport createCsvReader() {
        try {
            File file = new File("src/test/resources/import_data.csv");
            Path path = Paths.get(file.getAbsolutePath());
            Reader reader = Files.newBufferedReader(
                    path, Charset.forName("UTF-8"));
            return new BulkImport(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


}
