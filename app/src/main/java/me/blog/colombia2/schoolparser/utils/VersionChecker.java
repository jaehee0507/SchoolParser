package me.blog.colombia2.schoolparser.utils;

import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import android.preference.*;

public class VersionChecker {
    public void checkVersionAndDoUpdate() {
        new UpdateAsyncTask().execute();
    }

    class UpdateAsyncTask extends AsyncTask<String, String, String[]> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String[] doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=me.blog.colombia2.schoolparser")
                    .timeout(10 * 1000)
                    .get();
                String version = doc.select("div[itemprop=\"softwareVersion\"").first().text();
                String updateDate = doc.select("div[itemprop=\"datePublished\"").first().text();
                Elements updateContent = doc.select("div[class=\"recent-change\"");
                StringBuilder updateContentBuilder = new StringBuilder();
                for(Element e : updateContent) {
                    updateContentBuilder.append(e.text()).append("\n");
                }
                if(!version.equals(MainActivity.instance.getPackageManager().getPackageInfo(MainActivity.instance.getPackageName(), 0).versionName)) {
                    return new String[]{version, updateDate, updateContentBuilder.toString()};
                }
            } catch(PackageManager.NameNotFoundException e) {
                return new String[]{"package"};
            } catch(IOException e) {
                return new String[]{"internet"};
            } catch(Exception e) {
                return new String[]{"unknown"};
            }
            return new String[]{""};
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(Arrays.asList(result).size() > 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
                builder.setTitle("업데이트");
                builder.setMessage(result[1] + "에 버전 " + result[0] + "로 업데이트 되었습니다.\n \n" + result[2]);
                builder.setNegativeButton("취소", null);
                builder.setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=me.blog.colombia2.schoolparser"));
                            MainActivity.instance.startActivity(intent);
                        }
                    });
                builder.setNeutralButton("다시 보지 않기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int i) {
                            SharedPreferences.Editor pref = MainActivity.instance.getSharedPreferences("appData", MainActivity.MODE_PRIVATE).edit();
                            pref.putBoolean("autoUpdate", false);
                            pref.commit();
                        }
                    });
                builder.create().show();
            }
            super.onPostExecute(result);
        }
    }
}
