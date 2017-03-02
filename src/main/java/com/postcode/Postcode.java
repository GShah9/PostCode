package com.postcode;

/**
 * Created by Gautam on 28/02/2017.
 *
 * This class is a wrapper to identify row values from csv file consisting of columns 'row_id' and
 * 'postcode'
 */
public class Postcode {
    private int row_id;
    private String postcode;

    /*
     * @param row_id is row ID data from import_data.csv file
     * @param postcode is row postcode data from import_data.csv file
     */
    public Postcode(int row_id, String postcode) {
        this.row_id = row_id;
        this.postcode = postcode;
    }

    /*
     * @return returns the current identified row_id
     */
    public int getRowId() {
        return row_id;
    }

    /*
     * @return returns the current identified po
     */
    public String getPostcode() {
        return postcode;
    }

    /*
     * @param row_id is column row data from import_data.csv file
     */
    public void setRow_id(int row_id) {
        this.row_id = row_id;
    }

    /*
     * @param postcode is column row data from import_data.csv file
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
