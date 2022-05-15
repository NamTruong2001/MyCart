package edu.hanu.mycart;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatHelper {
    private static FormatHelper formatHelper;
    private Locale loc;
    private NumberFormat nf;

    private FormatHelper() {
        loc = Locale.getDefault();
        nf = NumberFormat.getCurrencyInstance(loc);
    }
    public static FormatHelper getFormatHelper() {
        if (formatHelper == null) {
            formatHelper = new FormatHelper();
        }
        return formatHelper;
    }
    public String format(int numb) {
        String money = nf.format(numb);
        return money;
    }
}
