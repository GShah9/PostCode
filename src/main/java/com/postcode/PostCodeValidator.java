package com.postcode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gautam on 17/02/2017.
 * Instructions:
 * https://gist.github.com/edhiley/5da612c93e31c7e60355
 *
 * This class is for validating postcodes, regex used (postCodePattern) here is for validating UK postcodes.
 */

public class PostCodeValidator {
    /*
     *  Used postcode regex from the verbose format provided in tasking
     */
    private final static String postCodePattern = "(GIR\\s0AA)|((([A-PR-UWYZ][0-9][0-9]?)|(([A-PR-UWYZ][A-HK-Y][0-9](?<!(BR|FY|HA|HD|HG|HR|HS|HX|JE|LD|SM|SR|WC|WN|ZE)[0-9])[0-9])|([A-PR-UWYZ][A-HK-Y](?<!AB|LL|SO)[0-9])|(WC[0-9][A-Z])|(([A-PR-UWYZ][0-9][A-HJKPSTUW])|([A-PR-UWYZ][A-HK-Y][0-9][ABEHMNPRVWXY]))))\\s[0-9][ABD-HJLNP-UW-Z]{2})";
    private final static Pattern pattern = Pattern.compile(postCodePattern, Pattern.CASE_INSENSITIVE);

    /*
     *  This code is to cater Part 1 of Postcode validation
     *  link - https://gist.github.com/edhiley/5da612c93e31c7e60355#part-1---postcode-validation
     *
     *  This method helps in validating UK postcode. It expects single postcode at a time.
     *  @param text is the CharSequence for checking against the regex pattern
     *  @return boolean which suggests that the supplied CharSequence matches the pattern or not
     */
    public static boolean isPostCode(CharSequence text) {
        if (text == null)
            return false;
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}
