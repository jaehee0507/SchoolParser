package me.blog.colombia2.schoolparser;

import android.content.*;
import android.graphics.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.tab.*;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected ArticlePageFragment fragment;
    protected ArrayList<ScheduleData> scheduleData;

    public ScheduleAdapter(ArticlePageFragment fragment, ArrayList<ScheduleData> scheduleData) {
        this.fragment = fragment;
        this.scheduleData = scheduleData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p1, int view_type) {
        if(view_type == 0)
            return new ScheduleViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.view_schedule, p1, false));
        else {
            return new MonthSelectViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.view_month_select, p1, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder a, final int position) {
        final ScheduleData schedule = scheduleData.get(position);

        if(a instanceof ScheduleViewHolder) {
            ScheduleViewHolder holder = (ScheduleViewHolder) a;
            if(schedule.getData().isEmpty()) {
                holder.card.setCardBackgroundColor(Color.parseColor("#DDDDDD"));
                holder.message.setVisibility(View.GONE);
            } else {
                holder.card.setCardBackgroundColor(holder.card.getContext().getResources().getColor(R.color.cardview_light_background));
                holder.message.setVisibility(View.VISIBLE);
            }
            holder.date.setText(schedule.getYear() + "년 " + (schedule.getMonth() + 1) + "월 " + schedule.getDate() + "일 " + schedule.getDay());
            holder.message.setText(schedule.getData());
        } else if(a instanceof MonthSelectViewHolder) {
            MonthSelectViewHolder holder = (MonthSelectViewHolder) a;
            holder.fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout layout = new LinearLayout(fragment.getContext());
                        layout.setGravity(Gravity.CENTER);
                        layout.setOrientation(LinearLayout.HORIZONTAL);
                        final NumberPicker yearPicker = new NumberPicker(fragment.getContext());
                        yearPicker.setMaxValue(2100);
                        yearPicker.setMinValue(1970);
                        TextView text = new TextView(fragment.getContext());
                        text.setText("    ");
                        final NumberPicker monthPicker = new NumberPicker(fragment.getContext());
                        monthPicker.setMaxValue(12);
                        monthPicker.setMinValue(1);
                        if(((String) fragment.getTab().getTag()).equals("")) {
                            yearPicker.setValue(Calendar.getInstance().get(Calendar.YEAR));
                            monthPicker.setValue(Calendar.getInstance().get(Calendar.MONTH)+1);
                        } else {
                            yearPicker.setValue(Integer.parseInt(((String) fragment.getTab().getTag()).split(";")[0]));
                            monthPicker.setValue(Integer.parseInt(((String) fragment.getTab().getTag()).split(";")[1])+1);
                        }
                        layout.addView(yearPicker);
                        layout.addView(text);
                        layout.addView(monthPicker);
                        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
                        builder.setTitle("년도/월 선택");
                        builder.setView(layout);
                        builder.setPositiveButton("확인", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface a, int p) {
                                fragment.getTab().setTag(yearPicker.getValue()+";"+(monthPicker.getValue()-1));
                                fragment.task();
                            }
                        });
                        builder.setNegativeButton("취소", null);
                        builder.show();
                    }
                });
        }
    }

    @Override
    public int getItemCount() {
        return this.scheduleData.size();
    }


    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        protected TextView date;
        protected TextView message;
        protected CardView card;

        public ScheduleViewHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.date);
            message = (TextView) itemView.findViewById(R.id.message);
            card = (CardView) itemView;
        }
    }

    class MonthSelectViewHolder extends RecyclerView.ViewHolder {
        protected FloatingActionButton fab;

        public MonthSelectViewHolder(View itemView) {
            super(itemView);

            fab = (FloatingActionButton) itemView.findViewById(R.id.fab);
        }
    }
}
