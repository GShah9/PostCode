package com.postcode;

/**
 * Created by Gautam on 28/02/2017.
 */
public class Postcode {
    private int row_id;
    private String postcode;
    private char[] postcodeChar;

    public Postcode(int row_id, String postcode) {
        this.row_id = row_id;
        this.postcode = postcode;
    }

    public Postcode(int row_id, char[] postcodeChar) {
        this.row_id = row_id;
        this.postcodeChar = postcodeChar;
    }

    public int getRowId() {
        return row_id;
    }

    public String getPostcode() {
        return postcode;
    }

    public char[] getPostcodeChar() {
        return postcodeChar;
    }

    public void setRow_id(int row_id) {
        this.row_id = row_id;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setPostcodeChar(char[] postcodeChar) {
        this.postcodeChar = postcodeChar;
    }
}
