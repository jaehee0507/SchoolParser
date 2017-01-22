package me.blog.colombia2.schoolparser;

import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.tab.*;

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
            PhotoViewHolder holder = (PhotoViewHolder) a;
            holder.title.setText(photo.getTitle());
            holder.image.setImageBitmap(photo.getPreview());
            holder.card.setPreventCornerOverlap(false);
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
                    public void onClick(View v) {
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
        protected ImageView image;
        protected TextView title;
        protected CardView card;

        public PhotoViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            card = (CardView) itemView;
        }
    }
}
