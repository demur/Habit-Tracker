package tech.demur.habittracker;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.demur.habittracker.adapter.HabitAdapter;
import tech.demur.habittracker.databinding.ActivityMainBinding;
import tech.demur.habittracker.model.Habit;
import tech.demur.habittracker.utils.RecyclerViewStateHelper;
import tech.demur.habittracker.utils.SwipeController;
import tech.demur.habittracker.utils.SwipeControllerActions;
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

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                Intent habitIntent = new Intent(MainActivity.this, HabitActivity.class);
                habitIntent.putExtra(EXTRA_HABIT_ID, mHabitAdapter.getHabitId(position));
                startActivity(habitIntent);
            }

            @Override
            public void onRightClicked(int position) {
                mViewModel.deleteHabitById(mHabitAdapter.getHabitId(position));
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mBinding.recyclerView);

        mBinding.recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    @Override
    public void onClick(Habit habit) {
        Intent detailIntent = new Intent(this, LoggingActivity.class);
        detailIntent.putExtra(EXTRA_HABIT_ID, habit._id);
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        getMenuInflater().inflate(R.menu.populate, menu);
        getMenuInflater().inflate(R.menu.clear, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(this, HabitActivity.class));
        } else if (item.getItemId() == R.id.action_populate) {
            mViewModel.populateDb();
        } else if (item.getItemId() == R.id.action_clear) {
            mViewModel.clearDb();
        } else if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    final Observer<List<Habit>> allHabitsObserver = new Observer<List<Habit>>() {
        @Override
        public void onChanged(List<Habit> habits) {
            mHabitAdapter.swapHabitList(habits);
        }
    };
}