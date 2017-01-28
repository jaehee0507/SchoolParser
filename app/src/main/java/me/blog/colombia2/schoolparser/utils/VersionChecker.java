package me.blog.colombia2.schoolparser.utils;

import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import java.io.*;
import me.blog.colombia2.schoolparser.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class VersionChecker {
    public void checkVersionAndDoUpdate() {
        new UpdateAsyncTask().execute();
    }

    class UpdateAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=me.blog.colombia2.schoolparser")
                    .timeout(10 * 1000)
                    .get();
                String version = doc.select("div[itemprop=\"softwareVersion\"").first().text();
                String updateDate = doc.select("div[itemprop=\"datePublished\"").first().text();
                if(!version.equals(MainActivity.instance.getPackageManager().getPackageInfo(MainActivity.instance.getPackageName(), 0).versionName)) {
                    return version + ";" + updateDate;
                }
            } catch(PackageManager.NameNotFoundException e) {
                return "package";
            } catch(IOException e) {
                return "internet";
            } catch(Exception e) {
                return "unknown";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.contains(";")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
                builder.setTitle("업데이트");
                builder.setMessage(result.split(";")[1] + "에 버전 " + result.split(";")[0] + "로 업데이트 되었습니다.");
                builder.setNegativeButton("취소", null);
                builder.setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=me.blog.colombia2.schoolparser"));
                            MainActivity.instance.startActivity(intent);
                        }
                    });
                builder.create().show();
            }
            super.onPostExecute(result);
        }
    }
}
