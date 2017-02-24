package me.blog.colombia2.schoolparser;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.tab.*;
import me.blog.colombia2.schoolparser.utils.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected ArticlePageFragment fragment;
    protected ArrayList<PhotoData> photoData;
    protected boolean loading;

    public PhotoAdapter(ArticlePageFragment fragment, ArrayList<PhotoData> photoData) {
        this.fragment = fragment;
        this.photoData = photoData;
        this.loading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p1, int view_type) {
        if(view_type == 0) {
            return new PhotoViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.view_photo_card, p1, false));
        } else {
            return new LoadMoreHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.view_load_more, p1, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return photoData.get(position) != null ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder a, final int position) {
        final PhotoData photo = photoData.get(position);

        if(a instanceof PhotoViewHolder) {
            final PhotoViewHolder holder = (PhotoViewHolder) a;
            holder.title.setText(photo.getTitle());
            
            holder.imageWeb.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });
            holder.imageWeb.getSettings().setLoadWithOverviewMode(true);
            holder.imageWeb.getSettings().setUseWideViewPort(true);
            holder.imageWeb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            holder.imageWeb.setBackgroundColor(Color.TRANSPARENT);
            holder.imageWeb.loadDataWithBaseURL(null, "<html><img src=\""+photo.getPreview()+"\" width=\"400\" height=\"300\"/></html>", "text/html", "utf-8", null);
            
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.instance, ArticleActivity.class);
                    i.putExtra("url", photo.getHyperLink());
                    MainActivity.instance.startActivity(i);
                }
            });
            
            holder.attachments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AttachmentsAsyncTask(photo).execute();
                    }
                });
        } else if(a instanceof LoadMoreHolder) {
            final LoadMoreHolder holder = (LoadMoreHolder) a;

            if(loading) {
                holder.loadMore.setVisibility(View.INVISIBLE);
                holder.loading.setVisibility(View.VISIBLE);
            } else {
                holder.loading.setVisibility(View.INVISIBLE);
                holder.loadMore.setVisibility(View.VISIBLE);
            }

            holder.loadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        loading = true;
                        holder.loadMore.setVisibility(View.INVISIBLE);
                        holder.loading.setVisibility(View.VISIBLE);

                        new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final int orgin_size = photoData.size();
                                    try {
                                        final ListParser parser = fragment.parser;
                                        parser.setCurrentPage(parser.getCurrentPage() + 1)
                                            .connect().get();
                                        final ArrayList<PhotoData> arr = parser.getPhotoList();
                                        photoData.addAll(arr);

                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyItemRangeInserted(orgin_size, arr.size());
                                                    loading = false;
                                                    photoData.remove(orgin_size - 1);
                                                    notifyItemRemoved(orgin_size - 1);
                                                    if(parser.getMaxPage() > parser.getCurrentPage()) {
                                                        photoData.add(null);
                                                        notifyItemInserted(photoData.size());
                                                    }
                                                }
                                            });
                                    } catch(IOException e) {
                                        e.printStackTrace();
                                        loading = false;
                                        photoData.remove(orgin_size-1);
                                        notifyItemRemoved(orgin_size-1);
                                        if(fragment.parser.getMaxPage() > fragment.parser.getCurrentPage()) {
                                            photoData.add(null);
                                            notifyItemInserted(photoData.size());
                                        }
                                        ErrorDisplayer.showInternetError(v);
                                    }
                                }
                            }).start();
                    }
                });
        }
    }

    @Override
    public int getItemCount() {
        return this.photoData.size();
    }


    class PhotoViewHolder extends RecyclerView.ViewHolder {
        protected WebView imageWeb;
        protected TextView title;
        protected Button attachments;
        protected CardView card;

        public PhotoViewHolder(View itemView) {
            super(itemView);

            imageWeb = (WebView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            attachments = (Button) itemView.findViewById(R.id.attachments);
            card = (CardView) itemView;
        }
    }
    
    class AttachmentsAsyncTask extends AsyncTask<String, String, ArrayList<FileData>> {
        private PhotoData photo;

        public AttachmentsAsyncTask(PhotoData photo) {
            this.photo = photo;
        }

        @Override
        protected void onPreExecute() {
            
        }

        @Override
        protected ArrayList<FileData> doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(photo.getHyperLink())
                                    .timeout(10 * 1000)
                                    .get();
                ArrayList<FileData> result = new ArrayList<>();
                Elements attachments = doc.select("tr > td > p > a");
                for(Element e : attachments) {
                    String hyperLink = e.attr("href");
                    String title = e.attr("title").replace(" 첨부파일 다운받기", "");
                    result.add(new FileData(title, SharedConstants.SCHOOL_URL + hyperLink));
                }
                return result;
            } catch(IOException e) {
                return new ArrayList<FileData>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<FileData> result) {
            if(result.size() > 0) {
                Intent i = new Intent(MainActivity.instance, AttachmentsActivity.class);
                i.putExtra("attachments", result);
                MainActivity.instance.startActivity(i);
            } else if(result.size() == 0) {
                //Nothing to do
            }

            super.onPostExecute(result);
        }
    }
}
