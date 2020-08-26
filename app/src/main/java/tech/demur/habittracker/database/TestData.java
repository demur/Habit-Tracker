package tech.demur.habittracker.database;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import tech.demur.habittracker.model.Habit;
import tech.demur.habittracker.model.Period;
import tech.demur.habittracker.model.Record;
import tech.demur.habittracker.viewmodel.DbViewModel;

import static tech.demur.habittracker.utils.DateUtils.roundDateDay;
import static tech.demur.habittracker.utils.DateUtils.roundDateWeek;

public class TestData {
    public static void populateDb(DbViewModel viewModel) {
        Habit tmpHabit = new Habit("Water plants", "week", "500..600", "Number", "ml", "in range");
        Date tmpDate = new Date();
        Calendar clndr = new GregorianCalendar();
        clndr.setTime(tmpDate);
        clndr.set(Calendar.HOUR_OF_DAY, 0);
        clndr.set(Calendar.MINUTE, 0);
        clndr.set(Calendar.SECOND, 0);
        clndr.set(Calendar.MILLISECOND, 0);
        clndr.add(Calendar.MONTH, -2);
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);

        Period tmpPeriod = new Period(roundDateWeek(clndr.getTime()), tmpHabit);
        viewModel.createOrUpdatePeriod(tmpPeriod);
        Record tmpRecord = new Record("100", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        tmpRecord = new Record("50", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        tmpRecord = new Record("300", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        viewModel.recalcPeriod(tmpPeriod._id);

        clndr.add(Calendar.WEEK_OF_YEAR, 1);
        tmpPeriod = new Period(roundDateWeek(clndr.getTime()), tmpHabit);
        viewModel.createOrUpdatePeriod(tmpPeriod);
        tmpRecord = new Record("600", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        tmpRecord = new Record("50", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        tmpRecord = new Record("300", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        viewModel.recalcPeriod(tmpPeriod._id);

        tmpPeriod = new Period(roundDateWeek(new Date()), tmpHabit);
        viewModel.createOrUpdatePeriod(tmpPeriod);
        tmpRecord = new Record("75", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        tmpRecord = new Record("10", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        tmpRecord = new Record("25", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        viewModel.recalcPeriod(tmpPeriod._id);


        tmpHabit = new Habit("Drink less coffee", "day", "2", "Number", "cups", "≤");
        tmpDate = new Date();
        clndr = new GregorianCalendar();
        clndr.setTime(tmpDate);
        clndr.set(Calendar.HOUR_OF_DAY, 0);
        clndr.set(Calendar.MINUTE, 0);
        clndr.set(Calendar.SECOND, 0);
        clndr.set(Calendar.MILLISECOND, 0);
        clndr.add(Calendar.DAY_OF_MONTH, -5);
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);

        tmpPeriod = new Period(roundDateDay(clndr.getTime()), tmpHabit);
        viewModel.createOrUpdatePeriod(tmpPeriod);
        tmpRecord = new Record("1", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        tmpRecord = new Record("2", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        viewModel.recalcPeriod(tmpPeriod._id);

        clndr.add(Calendar.DAY_OF_MONTH, 2);
        tmpPeriod = new Period(roundDateDay(clndr.getTime()), tmpHabit);
        viewModel.createOrUpdatePeriod(tmpPeriod);
        tmpRecord = new Record("0", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        viewModel.recalcPeriod(tmpPeriod._id);

        tmpPeriod = new Period(roundDateDay(new Date()), tmpHabit);
        viewModel.createOrUpdatePeriod(tmpPeriod);
        tmpRecord = new Record("1", tmpPeriod);
        viewModel.createOrUpdateRecord(tmpRecord);
        viewModel.recalcPeriod(tmpPeriod._id);


        tmpHabit = new Habit("Meditate", "week", "2", "Number", "times", "≥");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);


        tmpHabit = new Habit("Read", "day", "5", "Number", "pages", "≥");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);


        tmpHabit = new Habit("Exercise", "week", "3", "Number", "times", "≥");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);


        tmpHabit = new Habit("Walk outside", "week", "4", "Number", "minutes", "≥");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);


        tmpHabit = new Habit("No soda", "week", "2", "Number", "cups", "≤");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);


        tmpHabit = new Habit("Publish blog post", "month", "4", "Number", "posts", "≥");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);


        tmpHabit = new Habit("Call mom", "week", "1", "Number", "times", "≥");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);


        tmpHabit = new Habit("Deep clean the house", "month", "1", "Number", "times", "≥");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);


        tmpHabit = new Habit("Put all dishes right away", "day", "Yes", "Yes/No", "", "=");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);


        tmpHabit = new Habit("Do the laundry", "week", "1", "Number", "times", "≥");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);


        tmpHabit = new Habit("Take medication", "day", "1", "Number", "times", "=");
        tmpHabit.created = clndr.getTime();
        viewModel.createOrUpdateHabit(tmpHabit);
    }
}