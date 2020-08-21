package tech.demur.habittracker;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import tech.demur.habittracker.databinding.ActivityHabitBinding;
import tech.demur.habittracker.model.Habit;
import tech.demur.habittracker.viewmodel.DbViewModel;

public class HabitActivity extends AppCompatActivity {
    private static final String TAG = HabitActivity.class.getSimpleName();
    ActivityHabitBinding mBinding;
    private DbViewModel mViewModel;
    private Habit mHabit = null;
    public static final List<String> spinnerTypeArray = Arrays.asList("Number", "Time", "Yes/No");
    public static final List<String> spinnerIntervalArray = Arrays.asList("day", "week", "month");
    public static final List<String> spinnerConditionArray = Arrays.asList("=", "≠", "≥", "≤");
    public static final List<String> spinnerConditionBooleanArray = Arrays.asList("=", "≠");
    public static final List<String> spinnerConditionRangeArray = Arrays.asList("in range", "not in range");
    private ArrayAdapter<String> adapterType;
    private ArrayAdapter<String> adapterInterval;
    private ArrayAdapter<String> adapterCondition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_habit);
        mViewModel = new ViewModelProvider(this).get(DbViewModel.class);

        initSpinners();
        initSwitch();

        if (getIntent().hasExtra(MainActivity.EXTRA_HABIT_ID)) {
            mHabit = mViewModel.getHabitCopyById(getIntent().getStringExtra(MainActivity.EXTRA_HABIT_ID));
            if (null == mHabit) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_habit_found, Toast.LENGTH_LONG);
                TextView v = toast.getView().findViewById(android.R.id.message);
                if (null != v) v.setGravity(Gravity.CENTER);
                toast.show();
                NavUtils.navigateUpFromSameTask(this);
            }
            setTitle("Edit Habit");
            mBinding.etName.setText(mHabit.name);
            mBinding.etUnit.setText(mHabit.goal_label);
            mBinding.sInterval.setSelection(spinnerIntervalArray.indexOf(mHabit.interval));
            switch (mHabit.goal_type) {
                case "Yes/No":
                    mBinding.sType.setSelection(spinnerTypeArray.indexOf("Yes/No"));
                    mBinding.switchRange.setChecked(false);
                    mBinding.switchRange.setClickable(false);
                    adapterCondition.clear();
                    adapterCondition.addAll(spinnerConditionBooleanArray);
                    adapterCondition.notifyDataSetChanged();
                    mBinding.sCondition.setSelection(spinnerConditionBooleanArray.indexOf(mHabit.goal_condition));
                    mBinding.etValueStart.setText(mHabit.goal);
                    break;
                case "Time":
                case "Number":
                default:
                    if (mHabit.goal_type.equalsIgnoreCase("Time")) {
                        mBinding.sType.setSelection(spinnerTypeArray.indexOf("Time"));
                    } else {
                        mBinding.sType.setSelection(spinnerTypeArray.indexOf("Number"));
                    }
                    mBinding.switchRange.setClickable(true);
                    adapterCondition.clear();
                    if (mHabit.goal.contains("..")) {
                        mBinding.switchRange.setChecked(true);
                        String[] parts = mHabit.goal.split("\\.\\.");
                        mBinding.etValueStart.setText(parts[0]);
                        mBinding.etValueEnd.setText(parts[1]);
                        adapterCondition.addAll(spinnerConditionRangeArray);
                        adapterCondition.notifyDataSetChanged();
                        mBinding.sCondition.setSelection(spinnerConditionRangeArray.indexOf(mHabit.goal_condition));
                    } else {
                        mBinding.etValueStart.setText(mHabit.goal);
                        adapterCondition.addAll(spinnerConditionArray);
                        adapterCondition.notifyDataSetChanged();
                        mBinding.sCondition.setSelection(spinnerConditionArray.indexOf(mHabit.goal_condition));
                    }
            }
        }
    }

    private void initSpinners() {
        adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        adapterType.addAll(spinnerTypeArray);
        adapterInterval = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        adapterInterval.addAll(spinnerIntervalArray);
        adapterCondition = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        adapterCondition.addAll(spinnerConditionArray);

        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterInterval.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.sType.setAdapter(adapterType);
        mBinding.sInterval.setAdapter(adapterInterval);
        mBinding.sCondition.setAdapter(adapterCondition);
        mBinding.sType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerTypeArray.get(position).equalsIgnoreCase("Yes/No")) {
                    if (mBinding.switchRange.isChecked()) {
                        mBinding.switchRange.setChecked(false);
                    } else {
                        adapterCondition.clear();
                        adapterCondition.addAll(spinnerConditionBooleanArray);
                        adapterCondition.notifyDataSetChanged();
                    }
                    mBinding.switchRange.setClickable(false);
                } else {
                    mBinding.switchRange.setClickable(true);
                    if (adapterCondition.getCount() != spinnerConditionArray.size() && !mBinding.switchRange.isChecked()) {
                        adapterCondition.clear();
                        adapterCondition.addAll(spinnerConditionArray);
                        adapterCondition.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initSwitch() {
        mBinding.switchRange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapterCondition.clear();
                if (isChecked) {
                    mBinding.tvDots.setVisibility(View.VISIBLE);
                    mBinding.etValueEnd.setVisibility(View.VISIBLE);
                    adapterCondition.addAll(spinnerConditionRangeArray);
                } else {
                    mBinding.tvDots.setVisibility(View.INVISIBLE);
                    mBinding.etValueEnd.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onCheckedChanged: " + mBinding.sType.getSelectedItem().toString());
                    if (mBinding.sType.getSelectedItem().toString().equalsIgnoreCase("Yes/No")) {
                        adapterCondition.addAll(spinnerConditionBooleanArray);
                    } else {
                        adapterCondition.addAll(spinnerConditionArray);
                    }
                }
                adapterCondition.notifyDataSetChanged();
            }
        });
    }

    public void saveHabit() {
        if (null != mHabit) {
            mHabit.lastEdited = new Date();
        } else {
            mHabit = new Habit();
        }
        mHabit.name = mBinding.etName.getText().toString();
        mHabit.goal_label = mBinding.etUnit.getText().toString();
        mHabit.goal = mBinding.etValueStart.getText().toString();
        if (mBinding.switchRange.isChecked()) {
            mHabit.goal += ".." + mBinding.etValueEnd.getText().toString();
        }
        mHabit.goal_type = mBinding.sType.getSelectedItem().toString();
        mHabit.goal_condition = mBinding.sCondition.getSelectedItem().toString();
        mHabit.interval = mBinding.sInterval.getSelectedItem().toString();
        mViewModel.createOrUpdateHabit(mHabit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveHabit();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}