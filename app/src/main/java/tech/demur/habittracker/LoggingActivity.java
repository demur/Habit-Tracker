package tech.demur.habittracker;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

import tech.demur.habittracker.databinding.ActivityLoggingBinding;
import tech.demur.habittracker.model.Habit;
import tech.demur.habittracker.model.Period;
import tech.demur.habittracker.model.Record;
import tech.demur.habittracker.viewmodel.DbViewModel;

import static tech.demur.habittracker.utils.DateUtils.roundDateDay;
import static tech.demur.habittracker.utils.DateUtils.roundDateMonth;
import static tech.demur.habittracker.utils.DateUtils.roundDateWeek;

public class LoggingActivity extends AppCompatActivity {
    private static final String TAG = LoggingActivity.class.getSimpleName();
    ActivityLoggingBinding mBinding;
    private DbViewModel mViewModel;
    private Habit mHabit = null;
    private Record mRecord = null;
    private Period mPeriod = null;
    private Date startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_logging);
        mViewModel = new ViewModelProvider(this).get(DbViewModel.class);
        if (getIntent().hasExtra(MainActivity.EXTRA_HABIT_ID)
                && null != mViewModel.getHabitCopyById(getIntent().getStringExtra(MainActivity.EXTRA_HABIT_ID))) {
            mHabit = mViewModel.getHabitCopyById(getIntent().getStringExtra(MainActivity.EXTRA_HABIT_ID));
        } else if (getIntent().hasExtra(MainActivity.EXTRA_RECORD_ID)
                && null != mViewModel.getRecordCopyById(getIntent().getStringExtra(MainActivity.EXTRA_RECORD_ID))) {
            mRecord = mViewModel.getRecordCopyById(getIntent().getStringExtra(MainActivity.EXTRA_RECORD_ID));
            mPeriod = mRecord.parentPeriod;
            mHabit = mRecord.parentPeriod.parentHabit;
            setTitle("Edit Log Entry");
            mBinding.etValue.setText(mRecord.value);
            // TODO: Display Date while editing existent log entry
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_habit_found, Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            if (null != v) v.setGravity(Gravity.CENTER);
            toast.show();
            NavUtils.navigateUpFromSameTask(this);
        }
        mBinding.tvTitle.setText(LoggingActivity.this.getString(R.string.record_entry_title, mHabit.name));
        mBinding.tvValueExplained.setText(LoggingActivity.this.getString(R.string.record_value_type_label, mHabit.goal_type));
        mBinding.tvLabel.setText(mHabit.goal_label);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            logProgress();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logProgress() {
        if (null == mPeriod) {
            switch (mHabit.interval) {
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
            mPeriod = mViewModel.loadPeriodByStart(mHabit, startDate);
            if (null == mPeriod) {
                mPeriod = new Period(startDate, mHabit);
                mViewModel.createOrUpdatePeriod(mPeriod);
            }
        }
        if (null == mRecord) {
            mRecord = new Record();
            mRecord.parentPeriod = mViewModel.getPeriodById(mPeriod._id);
        } else {
            mRecord.lastEdited = new Date();
        }
        mRecord.value = mBinding.etValue.getText().toString();
        mRecord.notes = mBinding.etNotes.getText().toString();
        mViewModel.createOrUpdateRecord(mRecord);
        mViewModel.recalcPeriod(mPeriod);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}