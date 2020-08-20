package tech.demur.habittracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.demur.habittracker.adapter.HabitAdapter;
import tech.demur.habittracker.databinding.ActivityMainBinding;
import tech.demur.habittracker.model.Habit;
import tech.demur.habittracker.utils.RecyclerViewStateHelper;
import tech.demur.habittracker.utils.SwipeController;
import tech.demur.habittracker.viewmodel.DbViewModel;

public class MainActivity extends AppCompatActivity implements HabitAdapter.HabitAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_HABIT_ID = "habit_id";
    public static final String EXTRA_RECORD_ID = "record_id";
    ActivityMainBinding mBinding;
    private RecyclerViewStateHelper rvHelper = new RecyclerViewStateHelper();
    private DbViewModel mViewModel;
    private RecyclerView.LayoutManager mLayoutManager;
    private HabitAdapter mHabitAdapter;
    SwipeController swipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setRvHelper(rvHelper);
        mViewModel = new ViewModelProvider(this).get(DbViewModel.class);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(mLayoutManager);
        mHabitAdapter = new HabitAdapter(this, this);
        mBinding.recyclerView.setAdapter(mHabitAdapter);
        mViewModel.getLiveHabits().observe(MainActivity.this, allHabitsObserver);
    }

    @Override
    public void onClick(Habit habit) {
    }

    final Observer<List<Habit>> allHabitsObserver = new Observer<List<Habit>>() {
        @Override
        public void onChanged(List<Habit> habits) {
            mHabitAdapter.swapHabitList(habits);
        }
    };
}