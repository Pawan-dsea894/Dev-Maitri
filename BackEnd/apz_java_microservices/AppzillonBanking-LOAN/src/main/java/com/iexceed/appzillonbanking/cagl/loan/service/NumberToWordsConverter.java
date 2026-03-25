package com.iexceed.appzillonbanking.cagl.loan.service;
import java.text.DecimalFormat;

public class NumberToWordsConverter {

	private static final String[] tensNames = {
	        "", " ten", " twenty", " thirty", " forty", " fifty", 
	        " sixty", " seventy", " eighty", " ninety"
	    };

	    private static final String[] numNames = {
	        "", " one", " two", " three", " four", " five", 
	        " six", " seven", " eight", " nine", " ten", " eleven", 
	        " twelve", " thirteen", " fourteen", " fifteen", 
	        " sixteen", " seventeen", " eighteen", " nineteen"
	    };

	    private NumberToWordsConverter() {}

	    private static String convertLessThanOneThousand(int number) {
	        String current;

	        if (number % 100 < 20) {
	            current = numNames[number % 100];
	            number /= 100;
	        } else {
	            current = numNames[number % 10];
	            number /= 10;

	            current = tensNames[number % 10] + current;
	            number /= 10;
	        }
	        if (number == 0) return current;
	        return numNames[number] + " hundred" + current;
	    }

	    public static String convert(long number) {
	        // 0 to 999 999 999 999
	    	if (number == 0) { return "ZERO"; }

	        StringBuilder result = new StringBuilder();

	        long crore = number / 10000000;
	        number %= 10000000;

	        long lakh = number / 100000;
	        number %= 100000;

	        long thousand = number / 1000;
	        number %= 1000;

	        long remainder = number;

	        if (crore > 0) {
	            result.append(convertLessThanOneThousand((int) crore)).append(" crore ");
	        }

	        if (lakh > 0) {
	            result.append(convertLessThanOneThousand((int) lakh)).append(" lakh ");
	        }

	        if (thousand > 0) {
	            result.append(convertLessThanOneThousand((int) thousand)).append(" thousand ");
	        }

	        if (remainder > 0) {
	            result.append(convertLessThanOneThousand((int) remainder));
	        }

	        return result.toString()
	                .trim()
	                .replaceAll("\\s+", " ")
	                .toUpperCase() + " ONLY";
	    }
}
