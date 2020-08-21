package tech.demur.habittracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tech.demur.habittracker.R;
import tech.demur.habittracker.databinding.HabitRvItemBinding;
import tech.demur.habittracker.model.Habit;
import tech.demur.habittracker.model.Period;

import static tech.demur.habittracker.utils.DateUtils.roundDateDay;
import static tech.demur.habittracker.utils.DateUtils.roundDateMonth;
import static tech.demur.habittracker.utils.DateUtils.roundDateWeek;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitAdapterViewHolder> {
    private final Context mContext;
    private final HabitAdapterOnClickHandler mClickHandler;
    private List<Habit> habitList;

    public HabitAdapter(@NonNull Context context, HabitAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
        setHasStableIds(false);
    }

    @NonNull
    @Override
    public HabitAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HabitRvItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.habit_rv_item, parent, false);
        itemBinding.getRoot().setFocusable(true);
        return new HabitAdapterViewHolder(itemBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final HabitAdapterViewHolder holder, int position) {
        final Habit theHabit = habitList.get(position);
        holder.binding.tvName.setText(theHabit.name);
        Map<String, String> intervalMap = new HashMap<String, String>();
        intervalMap.put("day", "daily");
        intervalMap.put("week", "weekly");
        intervalMap.put("month", "monthly");
        if (theHabit.goal_type.equalsIgnoreCase("Number")) {
            holder.binding.tvIntervalLabel.setText(intervalMap.get(theHabit.interval) + ", " + theHabit.goal_label);
        } else {
            holder.binding.tvIntervalLabel.setText(intervalMap.get(theHabit.interval));
        }
        Date startDate;
        switch (theHabit.interval) {
            case "month":
                startDate = roundDateMonth(new Date());
                break;
            case "week":
                startDate = roundDateWeek(new Date());
                break;
            case "day":
            default:
                startDate = roundDateDay(new Date());
        }
        Period curPeriod = null;
        if (null != theHabit.periods && theHabit.periods.size() > 0) {
            for (Period period : theHabit.periods) {
                if (period.periodStart.equals(startDate)) {
                    curPeriod = period;
                    break;
                }
            }
        }
        if (null != curPeriod && curPeriod.achieved) {
            holder.binding.tvStatusIcon.setText("☑");
            holder.binding.tvStatusIcon.setTextColor(Color.rgb(11, 102, 35));
        } else {
            holder.binding.tvStatusIcon.setText("☐");
            holder.binding.tvStatusIcon.setTextColor(Color.rgb(204, 139, 6));
        }
        Map<String, String> statusMap = new HashMap<String, String>();
        statusMap.put("day", "today");
        statusMap.put("week", "this week");
        statusMap.put("month", "this month");
        if (null != curPeriod && theHabit.goal_type.equalsIgnoreCase("Number") && !curPeriod.total.isEmpty()) {
            holder.binding.tvStatus.setText(statusMap.get(theHabit.interval) + "\n" + curPeriod.total + "/" + theHabit.goal);
        } else {
            holder.binding.tvStatus.setText(statusMap.get(theHabit.interval));
        }
    }

    @Override
    public int getItemCount() {
        if (null == habitList) return 0;
        return habitList.size();
    }

    // As Habit._id is String, default getItemId() is used for now
    /*@Override
    public long getItemId(int position) {
        return habitList.get(position).getId();
    }*/

    public String getHabitId(int position) {
        return habitList.get(position)._id;
    }

    public void swapHabitList(List<Habit> habitList) {
        this.habitList = habitList;
        notifyDataSetChanged();
    }

    public interface HabitAdapterOnClickHandler {
        void onClick(Habit habit);
    }

    class HabitAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final HabitRvItemBinding binding;

        HabitAdapterViewHolder(HabitRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (null != mClickHandler) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mClickHandler.onClick(habitList.get(position));
                }
            }
        }
    }
}