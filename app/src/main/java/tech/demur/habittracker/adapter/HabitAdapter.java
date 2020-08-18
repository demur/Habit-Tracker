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