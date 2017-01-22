package me.blog.colombia2.schoolparser;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.utils.*;

public class AttachmentsActivity extends AppCompatActivity {
    protected HashMap<RelativeLayout, String> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachments);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.attach_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        views = new HashMap<>();

        ArrayList<FileData> files = SharedConstants.ATTACHMENTS;
        LinearLayout layout = (LinearLayout) findViewById(R.id.content_layout);
        for(FileData attach : files) {
            RelativeLayout checkbox = getCheckBoxLayout(attach);
            views.put(checkbox, attach.getHyperLink());
            layout.addView(checkbox);
            View view = new View(this);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(
                                                                   TypedValue.COMPLEX_UNIT_DIP,
                                                                   1,
                                                                   getResources().getDisplayMetrics())));
            view.setBackgroundColor(Color.rgb(200, 200, 200));
            layout.addView(view);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set<RelativeLayout> keys = views.keySet();
                    for(RelativeLayout layout : keys) {
                        final CheckBox checkbox = (CheckBox) layout.findViewById(0);
                        if(!checkbox.isChecked())
                            continue;

                        final View progress = layout.findViewById(1);
                        final String value = views.get(layout);
                        try {
                            FileDownloader downloader = new FileDownloader(value, "/sdcard/Download/" + checkbox.getText());
                            downloader.setFileDownloadListener(new FileDownloader.FileDownloadListener() {
                                    private int fileSize;

                                    @Override
                                    public void onDownloadStart(int fileSize) {
                                        this.fileSize = fileSize;
                                    }

                                    @Override
                                    public void onDownloading(int currentBytes) {
                                        final float prog = (float) currentBytes / (float) fileSize;
                                        runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (prog * checkbox.getWidth()), (int) TypedValue.applyDimension(
                                                                                                                             TypedValue.COMPLEX_UNIT_DIP,
                                                                                                                             3.2f,
                                                                                                                             getResources().getDisplayMetrics()));
                                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                                    progress.setLayoutParams(params);
                                                }
                                            });
                                    }

                                    @Override
                                    public void onDownloadError(Exception err) {
                                        
                                    }

                                    @Override
                                    public void onDownloadComplete(final File result) {
                                        runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, (int) TypedValue.applyDimension(
                                                                                                                             TypedValue.COMPLEX_UNIT_DIP,
                                                                                                                             3.2f,
                                                                                                                             getResources().getDisplayMetrics()));
                                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                                    progress.setLayoutParams(params);

                                                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                    Intent toLaunch = new Intent();
                                                    toLaunch.setAction(Intent.ACTION_VIEW);
                                                    toLaunch.setDataAndType(Uri.fromFile(result), MimeTypeMap.getSingleton().getMimeTypeFromExtension(result.getName().substring(result.getName().lastIndexOf("."), result.getName().length())));
                                                    PendingIntent pendingIntent = PendingIntent.getActivity(AttachmentsActivity.this, 0, toLaunch, 0);
                                                    Notification.Builder builder = new Notification.Builder(AttachmentsActivity.this);

                                                    builder.setAutoCancel(true);
                                                    builder.setContentTitle(result.getName());               
                                                    builder.setContentText("눌러서 열기");
                                                    builder.setSmallIcon(R.drawable.ic_file_download_white_24dp);
                                                    builder.setContentIntent(pendingIntent);
                                                    builder.build();

                                                    Notification myNotication = builder.getNotification();
                                                    manager.notify((int) System.currentTimeMillis(), myNotication);

                                                    Toast.makeText(AttachmentsActivity.this, "/sdcard/Download/" + checkbox.getText() + "에 다운로드됨", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    }
                                });
                            downloader.start();
                        } catch(Exception e) {

                        }
                    }
                }
            });
    }

    private RelativeLayout getCheckBoxLayout(FileData attach) {
        RelativeLayout layout = new RelativeLayout(this);
        View progress = new View(this);
        progress.setId(1);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, (int) TypedValue.applyDimension(
                                                                                 TypedValue.COMPLEX_UNIT_DIP,
                                                                                 3.2f,
                                                                                 getResources().getDisplayMetrics()));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        progress.setBackgroundColor(Color.parseColor("#2196F3"));
        progress.setLayoutParams(params);
        layout.addView(progress);

        CheckBox checkbox = new CheckBox(this);
        checkbox.setId(0);
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(Color.argb(20, 0, 0, 0)));
        states.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(Color.argb(0, 0, 0, 0)));
        states.addState(new int[]{}, new ColorDrawable(Color.argb(0, 0, 0, 0)));
        checkbox.setBackgroundDrawable(states);
        checkbox.setText(attach.getTitle());
        checkbox.setGravity(Gravity.LEFT | Gravity.CENTER);
        checkbox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(
                                                                   TypedValue.COMPLEX_UNIT_DIP,
                                                                   60,
                                                                   getResources().getDisplayMetrics())));
        layout.addView(checkbox);
        return layout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attachments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.select_all) {
            Set<RelativeLayout> keys = views.keySet();
            for(RelativeLayout layout : keys) {
                CheckBox checkbox = (CheckBox) layout.findViewById(0);
                checkbox.setChecked(true);
            }
            return true;
        }
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
