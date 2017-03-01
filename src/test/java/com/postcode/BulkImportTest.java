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
    public void recordSizeIs4() {
        Assert.assertEquals(records.size(), 4);
    }

    @Test
    public void recordValues() {
        Map<Integer, String> map1 = new HashMap<Integer, String>();
        map1.put(905529, "LE14 3QB");
        map1.put(1064397, "MK12 5EY");
        map1.put(1995262, "W6 8EX");
        map1.put(803671, "IP20 9DL");

        Assert.assertEquals(records, map1);
    }

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
