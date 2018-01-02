package me.blog.colombia2.schoolparser.parser;


import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/*
 * VERSION 6
 * UPDATE 20150225
 *
 * @author Mir(whdghks913)
 *
 * Use : getDateNew, getKcalNew, getMealNew, getPeopleNew
 * Delete : getDate, getKcal, getMeal, getMonthMeal, getPeople
 */
public class MealLibrary {
    private static Source mSource;

    /**
     * getDateNew
     */
    public static String[] getDateNew(String CountryCode, String schulCode,
                                      String schulCrseScCode, String schulKndScCode, String schMmealScCode) {

        String[] date = new String[7];
        String url = "http://stu." + CountryCode
                + "/sts_sci_md01_001.do?schulCode=" + schulCode
                + "&schulCrseScCode=" + schulCrseScCode + "&schulKndScCode="
                + schulKndScCode + "&schMmealScCode=" + schMmealScCode;

        return getDateNewSub(date, url);
    }

    public static String[] getDateNew(String CountryCode, String schulCode,
                                      String schulCrseScCode, String schulKndScCode,
                                      String schMmealScCode, String year, String month, String day) {

        String[] date = new String[7];
        String url = "http://stu." + CountryCode
                + "/sts_sci_md01_001.do?schulCode=" + schulCode
                + "&schulCrseScCode=" + schulCrseScCode + "&schulKndScCode="
                + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day;

        return getDateNewSub(date, url);
    }

    private static String[] getDateNewSub(String[] date, String url) {
        try {
            mSource = new Source(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSource.fullSequentialParse();
        List<?> table = mSource.getAllElements("table");

        for (int i = 0; i < table.size(); i++) {
            if (((Element) table.get(i)).getAttributeValue("class").equals(
                    "tbl_type3")) {
                List<?> tr = ((Element) table.get(i)).getAllElements("tr");
                List<?> th = ((Element) tr.get(0)).getAllElements("th");

                for (int j = 0; j < 7; j++) {
                    date[j] = ((Element) th.get(j + 1)).getContent().toString();
                }

                break;
            }
        }

        return date;
    }

    /**
     * getKcalNew
     */
    public static String[] getKcalNew(String CountryCode, String schulCode,
                                      String schulCrseScCode, String schulKndScCode, String schMmealScCode) {
        String[] content = new String[7];
        String url = "http://stu." + CountryCode
                + "/sts_sci_md01_001.do?schulCode=" + schulCode
                + "&schulCrseScCode=" + schulCrseScCode + "&schulKndScCode="
                + schulKndScCode + "&schMmealScCode=" + schMmealScCode;

        return getKcalSubNew(content, url);
    }

    public static String[] getKcalNew(String CountryCode, String schulCode,
                                      String schulCrseScCode, String schulKndScCode,
                                      String schMmealScCode, String year, String month, String day) {
        String[] content = new String[7];
        String url = "http://stu." + CountryCode
                + "/sts_sci_md01_001.do?schulCode=" + schulCode
                + "&schulCrseScCode=" + schulCrseScCode + "&schulKndScCode="
                + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day;

        return getKcalSubNew(content, url);
    }

    private static String[] getKcalSubNew(String[] content, String url) {
        try {
            mSource = new Source(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSource.fullSequentialParse();
        List<?> table = mSource.getAllElements("table");

        for (int i = 0; i < table.size(); i++) {
            if (((Element) table.get(i)).getAttributeValue("class").equals(
                    "tbl_type3")) {
                List<?> tbody = ((Element) table.get(i))
                        .getAllElements("tbody");
                List<?> __tr = ((Element) tbody.get(0)).getAllElements("tr");
                List<?> __th = ((Element) __tr.get(16)).getAllElements("th");

                if (((Element) __th.get(0)).getContent().toString()
                        .equals("에너지(kcal)")) {
                    List<?> td = ((Element) __tr.get(16)).getAllElements("td");

                    for (int j = 0; j < 7; j++) {
                        content[j] = ((Element) td.get(j)).getContent()
                                .toString();
                    }

                    break;
                }

                for (int index = 0; index < content.length; index++) {
                    content[index] = null;
                }

                break;
            }
        }

        return content;
    }

    /**
     * getMealNew
     */
    public static String[] getMealNew(String CountryCode, String schulCode,
                                      String schulCrseScCode, String schulKndScCode, String schMmealScCode) {

        String[] content = new String[7];
        String url = "http://stu." + CountryCode
                + "/sts_sci_md01_001.do?schulCode=" + schulCode
                + "&schulCrseScCode=" + schulCrseScCode + "&schulKndScCode="
                + schulKndScCode + "&schMmealScCode=" + schMmealScCode;

        return getMealNewSub(content, url);
    }

    public static String[] getMealNew(String CountryCode, String schulCode,
                                      String schulCrseScCode, String schulKndScCode,
                                      String schMmealScCode, String year, String month, String day) {

        String[] content = new String[7];
        String url = "http://stu." + CountryCode
                + "/sts_sci_md01_001.do?schulCode=" + schulCode
                + "&schulCrseScCode=" + schulCrseScCode + "&schulKndScCode="
                + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day;

        return getMealNewSub(content, url);
    }

    private static String[] getMealNewSub(String[] content, String url) {
        try {
            mSource = new Source(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSource.fullSequentialParse();
        List<?> table = mSource.getAllElements("table");

        for (int i = 0; i < table.size(); i++) {
            if (((Element) table.get(i)).getAttributeValue("class").equals(
                    "tbl_type3")) {
                List<?> tbody = ((Element) table.get(i))
                        .getAllElements("tbody");
                List<?> tr = ((Element) tbody.get(0)).getAllElements("tr");
                List<?> title = ((Element) tr.get(2)).getAllElements("th");

                if (((Element) title.get(0)).getContent().toString()
                        .equals("식재료")) {
                    List<?> tdMeal = ((Element) tr.get(1)).getAllElements("td");

                    for (int j = 0; j < 7; j++) {
                        content[j] = ((Element) tdMeal.get(j)).getContent()
                                .toString();
                        content[j] = content[j].replace("<br />", "\n");
                    }

                    break;
                }

                for (int index = 0; index < content.length; index++) {
                    content[index] = null;
                }

                break;
            }
        }

        return content;
    }

    /**
     * getPeopleNew
     */
    public static String[] getPeopleNew(String CountryCode, String schulCode,
                                        String schulCrseScCode, String schulKndScCode, String schMmealScCode) {
        String[] content = new String[7];
        String url = "http://stu." + CountryCode
                + "/sts_sci_md01_001.do?schulCode=" + schulCode
                + "&schulCrseScCode=" + schulCrseScCode + "&schulKndScCode="
                + schulKndScCode + "&schMmealScCode=" + schMmealScCode;

        return getPeopleSubNew(content, url);
    }

    public static String[] getPeopleNew(String CountryCode, String schulCode,
                                        String schulCrseScCode, String schulKndScCode,
                                        String schMmealScCode, String year, String month, String day) {
        String[] content = new String[7];
        String url = "http://stu." + CountryCode
                + "/sts_sci_md01_001.do?schulCode=" + schulCode
                + "&schulCrseScCode=" + schulCrseScCode + "&schulKndScCode="
                + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day;

        return getPeopleSubNew(content, url);
    }

    private static String[] getPeopleSubNew(String[] content, String url) {
        try {
            mSource = new Source(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSource.fullSequentialParse();
        List<?> table = mSource.getAllElements("table");

        for (int i = 0; i < table.size(); i++) {
            if (((Element) table.get(i)).getAttributeValue("class").equals(
                    "tbl_type3")) {
                List<?> tbody = ((Element) table.get(i))
                        .getAllElements("tbody");
                List<?> __tr = ((Element) tbody.get(0)).getAllElements("tr");
                List<?> __th = ((Element) __tr.get(0)).getAllElements("th");

                if (((Element) __th.get(0)).getContent().toString()
                        .equals("급식인원")) {
                    List<?> td = ((Element) __tr.get(0)).getAllElements("td");

                    for (int j = 0; j < 7; j++) {
                        content[j] = ((Element) td.get(j)).getContent()
                                .toString();
                    }

                    break;
                }

                for (int index = 0; index < content.length; index++) {
                    content[index] = null;
                }

                break;
            }
        }

        return content;
    }

    /**
     * isMealCheck
     *
     * meal이 "", " ", null이면 false, 아니면 true
     */
    public static boolean isMealCheck(String meal) {
        return !("".equals(meal) || " ".equals(meal) || meal == null);
    }
}