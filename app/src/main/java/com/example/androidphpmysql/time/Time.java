package com.example.androidphpmysql.time;

import static com.example.androidphpmysql.R.string.no_free_date;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.reservedservice.CalendarActivity;
import com.example.androidphpmysql.reservation.ReservationActivity;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Time {
    private static final long ONE_MINUTE_IN_MILLIS = 60000;
    private final int subServiceId, duration, minDuration;
    private final Context context;
    private final Activity activity;
    private final ArrayList<Integer> workdays;
    private final ArrayList<ArrayList<String>> freeTime;
    private final List<WorkTimeListItem> workTimeListItems;
    private final SimpleDateFormat simpleDateFormat, simpleDateFormatWithoutHour, simpleDateFormatWithoutDate;
    private List<AvailableTimeListItem> availableTimeListItems;
    private final List<ExcludingTimeListItem> excludingTimeListItems;
    private final List<ExcludingTimeListItem> excludingTimeOnDay;
    private JSONArray reservedTime, freeHours;

    public Time(int subServiceId, int duration, int minDuration, Context context, Activity activity) {
        this.subServiceId = subServiceId;
        this.duration = duration;
        this.minDuration = minDuration;
        this.context = context;
        this.activity = activity;
        this.workdays = new ArrayList<>();
        this.freeTime = new ArrayList<>();
        this.workTimeListItems = new ArrayList<>();
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        this.simpleDateFormatWithoutHour = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.simpleDateFormatWithoutDate = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.availableTimeListItems = new ArrayList<>();
        this.excludingTimeListItems = new ArrayList<>();
        this.excludingTimeOnDay = new ArrayList<>();
        this.reservedTime = new JSONArray();
        this.freeHours = new JSONArray();
    }

    public void loadWorkTime() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_WORK_TIME + "?sub_service=" + subServiceId, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("work_time");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    int workday = object.getInt("day_id");
                    WorkTimeListItem workTimeListItem = new WorkTimeListItem(object.getInt("work_time_id"), object.getInt("day_id"), object.getString("time_start"), object.getString("time_end"));
                    workdays.add(workday);
                    workTimeListItems.add(workTimeListItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show());
        RequestHandler requestHandler = new RequestHandler(context);
        requestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void loadWorkTimeByService(int serviceId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_WORK_TIME + "?service=" + serviceId, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("work_time");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    int workday = object.getInt("day_id");
                    WorkTimeListItem workTimeListItem = new WorkTimeListItem(object.getInt("work_time_id"), object.getInt("day_id"), object.getString("time_start"), object.getString("time_end"));
                    workdays.add(workday);
                    workTimeListItems.add(workTimeListItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show());
        RequestHandler requestHandler = new RequestHandler(context);
        requestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void loadFreeTime() {
        Calendar today = Calendar.getInstance();
        String todayDate = simpleDateFormatWithoutHour.format(today.getTime());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_FREE_TIME + "?sub_service=" + subServiceId + "&date=" + todayDate + "&all_day=1", response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("free_time");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String freeStart = object.getString("date_time_start");
                    String freeEnd = object.getString("date_time_end");
                    ArrayList<String> free = new ArrayList<>();
                    free.add(freeStart);
                    free.add(freeEnd);
                    freeTime.add(free);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show());
        RequestHandler requestHandler = new RequestHandler(context);
        requestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void loadFreeTimeByService(int serviceId, boolean calendarActivityBool) {
        Calendar today = Calendar.getInstance();
        String todayDate = simpleDateFormatWithoutHour.format(today.getTime());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_FREE_TIME + "?service=" + serviceId + "&date=" + todayDate + "&all_day=1", response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("free_time");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String freeStart = object.getString("date_time_start");
                    String freeEnd = object.getString("date_time_end");
                    ArrayList<String> free = new ArrayList<>();
                    free.add(freeStart);
                    free.add(freeEnd);
                    freeTime.add(free);
                }
                if (calendarActivityBool) {
                    CalendarActivity.getInstance().showDatePicker();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show());
        RequestHandler requestHandler = new RequestHandler(context);
        requestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public Calendar[] getWeekdays() {
        ArrayList<Calendar> weekdays = new ArrayList<>();
        Calendar day = Calendar.getInstance();
        for (int i = 0; i < 365; i++) {
            if (workdays.contains(day.get(Calendar.DAY_OF_WEEK) - 1)) {
                Calendar d = (Calendar) day.clone();
                weekdays.add(d);
            }
            day.add(Calendar.DATE, 1);
        }
        int size = weekdays.size();
        return weekdays.toArray(new Calendar[size]);
    }

    public Calendar[] getFreeDays() {
        ArrayList<Calendar> free = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            freeTime.forEach((item) -> {
                Calendar freeStart = Calendar.getInstance();
                Calendar freeEnd = Calendar.getInstance();
                try {
                    freeStart.setTime(Objects.requireNonNull(simpleDateFormat.parse(item.get(0))));
                    freeEnd.setTime(Objects.requireNonNull(simpleDateFormat.parse(item.get(1))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                for (String date = simpleDateFormat.format(freeStart.getTime()); freeStart.before(freeEnd); freeStart.add(Calendar.DATE, 1), date = simpleDateFormat.format(freeStart.getTime())) {
                    Calendar tmp = Calendar.getInstance();
                    try {
                        tmp.setTime(Objects.requireNonNull(simpleDateFormat.parse(date)));
                        free.add(tmp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        int sizeFree = free.size();
        return free.toArray(new Calendar[sizeFree]);
    }

    public void loadReservedTime(String date, int day, int option) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_WORK_TIME + "?sub_service=" + subServiceId + "&date=" + date, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                reservedTime = jsonObject.getJSONArray("reserved_services");
                loadFreeHours(date, day, option);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show());
        RequestHandler requestHandler = new RequestHandler(context);
        requestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void loadFreeHours(String date, int day, int option) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_FREE_TIME + "?sub_service=" + subServiceId + "&date=" + date + "&all_day=0", response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                freeHours = jsonObject.getJSONArray("free_time");
                switch (option) {
                    case 0:
                        createAvailableTimeList(date, day);
                        break;
                    case 1:
                        finishGenerate(date, day);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show());
        RequestHandler requestHandler = new RequestHandler(context);
        requestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void createAvailableTimeList(String date, int day) {
        availableTimeListItems = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < workTimeListItems.size(); i++) {
            if (workTimeListItems.get(i).getDay() == day) {
                Calendar startTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                try {
                    startTime.setTime(Objects.requireNonNull(simpleDateFormat.parse(date + " " + workTimeListItems.get(i).getTimeStart())));
                    endTime.setTime(Objects.requireNonNull(simpleDateFormat.parse(date + " " + workTimeListItems.get(i).getTimeEnd())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long timeInMills = startTime.getTimeInMillis();
                while (timeInMills <= endTime.getTimeInMillis() - (duration * ONE_MINUTE_IN_MILLIS)) {
                    Date dateReserved = checkReservedTime(timeInMills);
                    Date dateFree = checkFreeHours(timeInMills);
                    if (dateReserved == null) {
                        if (dateFree == null) {
                            AvailableTimeListItem availableTimeListItem = new AvailableTimeListItem(simpleDateFormatWithoutDate.format(startTime.getTime()));
                            availableTimeListItems.add(availableTimeListItem);
                            Date newDate = new Date(timeInMills + (minDuration * ONE_MINUTE_IN_MILLIS));
                            startTime.setTime(newDate);
                        } else {
                            startTime.setTime(dateFree);
                        }
                    } else {
                        if (dateFree == null) {
                            startTime.setTime(dateReserved);
                        } else {
                            Calendar calReserved = Calendar.getInstance();
                            calReserved.setTime(dateReserved);
                            Calendar calFree = Calendar.getInstance();
                            calFree.setTime(dateFree);
                            if (calReserved.getTimeInMillis() > calFree.getTimeInMillis()) {
                                startTime.setTime(dateReserved);
                            } else {
                                startTime.setTime(dateFree);
                            }
                        }
                    }
                    timeInMills = startTime.getTimeInMillis();
                }
                calendar = startTime;
                break;
            }
        }
        if (availableTimeListItems.size() == 0) {
            Toast.makeText(context, no_free_date, Toast.LENGTH_SHORT).show();
        } else {
            ReservationActivity.getInstance().showTimePicker(calendar);
        }
    }

    private Date checkReservedTime(long timeInMillis) {
        if (reservedTime.length() > 0) {
            try {
                for (int i = 0; i < reservedTime.length(); i++) {
                    JSONObject object = reservedTime.getJSONObject(i);
                    String dateTime = object.getString("date_time");
                    int reservedDuration = object.getInt("duration");
                    Calendar reservedTime = Calendar.getInstance();
                    try {
                        reservedTime.setTime(Objects.requireNonNull(simpleDateFormat.parse(dateTime)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (!(timeInMillis + (duration * ONE_MINUTE_IN_MILLIS) <= reservedTime.getTimeInMillis() || timeInMillis >= reservedTime.getTimeInMillis() + (reservedDuration * ONE_MINUTE_IN_MILLIS))) {
                        return new Date(reservedTime.getTimeInMillis() + (reservedDuration * ONE_MINUTE_IN_MILLIS));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Date checkFreeHours(long timeInMillis) {
        if (freeHours.length() > 0) {
            try {
                for (int i = 0; i < freeHours.length(); i++) {
                    JSONObject object = freeHours.getJSONObject(i);
                    String dateTimeStart = object.getString("date_time_start");
                    String dateTimeEnd = object.getString("date_time_end");
                    Calendar freeTimeStart = Calendar.getInstance();
                    Calendar freeTimeEnd = Calendar.getInstance();
                    try {
                        freeTimeStart.setTime(Objects.requireNonNull(simpleDateFormat.parse(dateTimeStart)));
                        freeTimeEnd.setTime(Objects.requireNonNull(simpleDateFormat.parse(dateTimeEnd)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (!(timeInMillis + (duration * ONE_MINUTE_IN_MILLIS) <= freeTimeStart.getTimeInMillis() || timeInMillis >= freeTimeEnd.getTimeInMillis())) {
                        return new Date(freeTimeEnd.getTimeInMillis());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Timepoint[] getDisableTime(Calendar calendar, int option) {
        ArrayList<Timepoint> disableTime = new ArrayList<>();
        for (int i = 0; i < workTimeListItems.size(); i++) {
            if (workTimeListItems.get(i).getDay() == calendar.get(Calendar.DAY_OF_WEEK) - 1) {
                Calendar startTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                try {
                    startTime.setTime(Objects.requireNonNull(simpleDateFormat.parse(simpleDateFormatWithoutHour.format(new Date(calendar.getTimeInMillis())) + " " + workTimeListItems.get(i).getTimeStart())));
                    endTime.setTime(Objects.requireNonNull(simpleDateFormat.parse(simpleDateFormatWithoutHour.format(new Date(calendar.getTimeInMillis())) + " " + workTimeListItems.get(i).getTimeEnd())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long timeInMills = startTime.getTimeInMillis();
                boolean first = true;
                while (timeInMills <= endTime.getTimeInMillis() - (duration * ONE_MINUTE_IN_MILLIS)) {
                    Date dateReserved = checkReservedTime(timeInMills);
                    Date dateFree = checkFreeHours(timeInMills);
                    if (dateReserved != null || dateFree != null) {
                        if (option == 1) {
                            if (!first) {
                                Timepoint timepoint = new Timepoint(startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE));
                                disableTime.add(timepoint);
                                first = true;
                            } else {
                                first = false;
                            }
                        } else {
                            Timepoint timepoint = new Timepoint(startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE));
                            disableTime.add(timepoint);
                        }
                    }
                    startTime.setTime(new Date(timeInMills + (minDuration * ONE_MINUTE_IN_MILLIS)));
                    timeInMills = startTime.getTimeInMillis();
                }
                int size = disableTime.size();
                return disableTime.toArray(new Timepoint[size]);
            }
        }
        return null;
    }

    public Timepoint[] getWorkingTime(int day, Timepoint[] disableTimes, Calendar calendar, int option) {
        ArrayList<Timepoint> workingTime = new ArrayList<>();
        for (int i = 0; i < workTimeListItems.size(); i++) {
            if (workTimeListItems.get(i).getDay() == day) {
                Calendar startTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                Calendar temp = Calendar.getInstance();
                try {
                    startTime.setTime(Objects.requireNonNull(simpleDateFormatWithoutDate.parse(workTimeListItems.get(i).getTimeStart())));
                    endTime.setTime(Objects.requireNonNull(simpleDateFormatWithoutDate.parse(workTimeListItems.get(i).getTimeEnd())));
                    endTime.setTime(new Date(endTime.getTimeInMillis() - (duration * ONE_MINUTE_IN_MILLIS)));
                    Calendar newStartTime = getStartTime(calendar, startTime, endTime);
                    if (newStartTime == null) {
                        Toast.makeText(context, no_free_date, Toast.LENGTH_SHORT).show();
                        return null;
                    } else {
                        startTime = newStartTime;
                    }
                    int j = 0;
                    if (disableTimes.length > 0) {
                        temp.setTime(Objects.requireNonNull(simpleDateFormatWithoutDate.parse(disableTimes[j].getHour() + ":" + disableTimes[j].getMinute())));
                        while (temp.getTimeInMillis() == startTime.getTimeInMillis()) {
                            j++;
                            startTime.setTimeInMillis(startTime.getTimeInMillis() + (minDuration * ONE_MINUTE_IN_MILLIS));
                            if (j < disableTimes.length - 1) {
                                temp.setTime(Objects.requireNonNull(simpleDateFormatWithoutDate.parse(disableTimes[j].getHour() + ":" + disableTimes[j].getMinute())));
                            }
                        }
                        j = disableTimes.length - 1;
                        temp.setTime(Objects.requireNonNull(simpleDateFormatWithoutDate.parse(disableTimes[j].getHour() + ":" + disableTimes[j].getMinute())));
                        while (temp.getTimeInMillis() == endTime.getTimeInMillis()) {
                            j--;
                            endTime.setTimeInMillis(endTime.getTimeInMillis() - (minDuration * ONE_MINUTE_IN_MILLIS));
                            temp.setTime(Objects.requireNonNull(simpleDateFormatWithoutDate.parse(disableTimes[j].getHour() + ":" + disableTimes[j].getMinute())));
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Timepoint timePointStart = new Timepoint(startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE));
                workingTime.add(timePointStart);
                if (option == 1) {
                    endTime.setTimeInMillis(endTime.getTimeInMillis() + (minDuration * ONE_MINUTE_IN_MILLIS));
                }
                Timepoint timePointEnd = new Timepoint(endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE));
                workingTime.add(timePointEnd);
                int size = workingTime.size();
                return workingTime.toArray(new Timepoint[size]);
            }
        }
        return null;
    }

    private Calendar getStartTime(Calendar calendar, Calendar startTime, Calendar endTime) {
        Calendar now = Calendar.getInstance();
        Calendar startTimeCopy = (Calendar) startTime.clone();
        Calendar endTimeCopy = (Calendar) endTime.clone();
        if (calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
            try {
                startTimeCopy.setTime(Objects.requireNonNull(simpleDateFormatWithoutDate.parse(simpleDateFormatWithoutDate.format(new Date(startTimeCopy.getTimeInMillis())))));
                endTimeCopy.setTime(Objects.requireNonNull(simpleDateFormatWithoutDate.parse(simpleDateFormatWithoutDate.format(new Date(endTimeCopy.getTimeInMillis())))));
                now.setTime(Objects.requireNonNull(simpleDateFormatWithoutDate.parse(simpleDateFormatWithoutDate.format(new Date(now.getTimeInMillis())))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (now.getTimeInMillis() < endTimeCopy.getTimeInMillis()) {
                while (startTimeCopy.getTimeInMillis() < now.getTimeInMillis()) {
                    startTimeCopy.setTimeInMillis(startTimeCopy.getTimeInMillis() + (minDuration * ONE_MINUTE_IN_MILLIS));
                }
            } else {
                return null;
            }
        }
        return startTimeCopy;
    }

    public void startGenerate() {
        Calendar today = Calendar.getInstance();
        generateDateTime(today);
    }

    private void generateDateTime(Calendar calendar) {
        String date = simpleDateFormatWithoutHour.format(calendar.getTime());
        loadReservedTime(date, calendar.get(Calendar.DAY_OF_WEEK) - 1, 1);
    }

    private void finishGenerate(String date, int day) {
        for (int i = 0; i < workTimeListItems.size(); i++) {
            if (workTimeListItems.get(i).getDay() == day) {
                Calendar startTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                try {
                    startTime.setTime(Objects.requireNonNull(simpleDateFormat.parse(date + " " + workTimeListItems.get(i).getTimeStart())));
                    endTime.setTime(Objects.requireNonNull(simpleDateFormat.parse(date + " " + workTimeListItems.get(i).getTimeEnd())));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Objects.requireNonNull(simpleDateFormatWithoutHour.parse(date)));
                    for (int j = 0; j < excludingTimeListItems.size(); j++) {
                        if (excludingTimeListItems.get(j).getDate().equals(date)) {
                            if (excludingTimeListItems.get(j).isAllDay()) {
                                do {
                                    startTime.add(Calendar.DATE, 1);
                                } while (!workdays.contains(startTime.get(Calendar.DAY_OF_WEEK) - 1));
                                generateDateTime(startTime);
                                return;
                            } else {
                                excludingTimeOnDay.add(excludingTimeListItems.get(j));
                            }
                        }
                    }
                    Calendar newStartTime = getStartTime(calendar, startTime, endTime);
                    if (newStartTime == null) {
                        do {
                            startTime.add(Calendar.DATE, 1);
                        } while (!workdays.contains(startTime.get(Calendar.DAY_OF_WEEK) - 1));
                        generateDateTime(startTime);
                        return;
                    } else {
                        startTime.setTime(Objects.requireNonNull(simpleDateFormat.parse(date + " " + simpleDateFormatWithoutDate.format(newStartTime.getTime()))));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long timeInMills = startTime.getTimeInMillis();
                while (timeInMills <= endTime.getTimeInMillis() - (duration * ONE_MINUTE_IN_MILLIS)) {
                    Date dateReserved = checkReservedTime(timeInMills);
                    Date dateFree = checkFreeHours(timeInMills);
                    Date dateExcluding = checkExcludingTime(timeInMills);
                    if (dateExcluding == null) {
                        if (dateReserved == null) {
                            if (dateFree == null) {
                                EditText editTextDateTime = activity.findViewById(R.id.editTextTime);
                                editTextDateTime.setText(simpleDateFormat.format(startTime.getTime()));
                                return;
                            } else {
                                startTime.setTime(dateFree);
                            }
                        } else {
                            if (dateFree == null) {
                                startTime.setTime(dateReserved);
                            } else {
                                Calendar calReserved = Calendar.getInstance();
                                calReserved.setTime(dateReserved);
                                Calendar calFree = Calendar.getInstance();
                                calReserved.setTime(dateFree);
                                if (calReserved.getTimeInMillis() > calFree.getTimeInMillis()) {
                                    startTime.setTime(dateReserved);
                                } else {
                                    startTime.setTime(dateFree);
                                }
                            }
                        }
                    } else {
                        if (dateReserved != null && dateFree != null) {
                            Calendar calReserved = Calendar.getInstance();
                            calReserved.setTime(dateReserved);
                            Calendar calFree = Calendar.getInstance();
                            calFree.setTime(dateFree);
                            Calendar calExcluding = Calendar.getInstance();
                            calExcluding.setTime(dateExcluding);
                            if (calExcluding.getTimeInMillis() > calReserved.getTimeInMillis() && calExcluding.getTimeInMillis() > calFree.getTimeInMillis()) {
                                startTime.setTime(dateExcluding);
                            } else if (calExcluding.getTimeInMillis() > calReserved.getTimeInMillis()) {
                                startTime.setTime(dateFree);
                            } else if (calExcluding.getTimeInMillis() > calFree.getTimeInMillis()) {
                                startTime.setTime(dateReserved);
                            } else {
                                if (calReserved.getTimeInMillis() > calFree.getTimeInMillis()) {
                                    startTime.setTime(dateReserved);
                                } else {
                                    startTime.setTime(dateFree);
                                }
                            }
                        } else if (dateReserved != null) {
                            Calendar calReserved = Calendar.getInstance();
                            calReserved.setTime(dateReserved);
                            Calendar calExcluding = Calendar.getInstance();
                            calExcluding.setTime(dateExcluding);
                            if (calExcluding.getTimeInMillis() > calReserved.getTimeInMillis()) {
                                startTime.setTime(dateExcluding);
                            } else {
                                startTime.setTime(dateReserved);
                            }
                        } else if (dateFree != null) {
                            Calendar calFree = Calendar.getInstance();
                            calFree.setTime(dateFree);
                            Calendar calExcluding = Calendar.getInstance();
                            calExcluding.setTime(dateExcluding);
                            if (calExcluding.getTimeInMillis() > calFree.getTimeInMillis()) {
                                startTime.setTime(dateExcluding);
                            } else {
                                startTime.setTime(dateFree);
                            }
                        } else {
                            startTime.setTime(dateExcluding);
                        }
                    }
                    timeInMills = startTime.getTimeInMillis();
                }
                do {
                    startTime.add(Calendar.DATE, 1);
                } while (!workdays.contains(startTime.get(Calendar.DAY_OF_WEEK) - 1));
                generateDateTime(startTime);
                return;
            }
        }
        Calendar startTime = Calendar.getInstance();
        try {
            startTime.setTime(Objects.requireNonNull(simpleDateFormatWithoutHour.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        do {
            startTime.add(Calendar.DATE, 1);
        } while (!workdays.contains(startTime.get(Calendar.DAY_OF_WEEK) - 1));
        generateDateTime(startTime);
    }

    public void saveExcludingTime(Calendar calendarStart, Calendar calendarEnd) {
        String date = simpleDateFormatWithoutHour.format(calendarStart.getTime());
        String startTime;
        String endTime;
        boolean allDay;
        if (calendarEnd != null) {
            startTime = simpleDateFormatWithoutDate.format(calendarStart.getTime());
            endTime = simpleDateFormatWithoutDate.format(calendarEnd.getTime());
            allDay = false;
        } else {
            startTime = "00:00";
            endTime = "23:59";
            allDay = true;
        }
        ExcludingTimeListItem excludingTimeListItem = new ExcludingTimeListItem(date, startTime, endTime, allDay);
        excludingTimeListItems.add(excludingTimeListItem);
    }

    private Date checkExcludingTime(long timeInMillis) {
        if (excludingTimeOnDay.size() > 0) {
            for (int i = 0; i < excludingTimeOnDay.size(); i++) {
                ExcludingTimeListItem excludingTime = excludingTimeOnDay.get(i);
                String dateTimeStart = excludingTime.getDate() + " " + excludingTime.getTimeStart();
                String dateTimeEnd = excludingTime.getDate() + " " + excludingTime.getTimeEnd();
                Calendar excludingTimeStart = Calendar.getInstance();
                Calendar excludingTimeEnd = Calendar.getInstance();
                try {
                    excludingTimeStart.setTime(Objects.requireNonNull(simpleDateFormat.parse(dateTimeStart)));
                    excludingTimeEnd.setTime(Objects.requireNonNull(simpleDateFormat.parse(dateTimeEnd)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!(timeInMillis + (duration * ONE_MINUTE_IN_MILLIS) <= excludingTimeStart.getTimeInMillis() || timeInMillis >= excludingTimeEnd.getTimeInMillis())) {
                    return new Date(excludingTimeEnd.getTimeInMillis());
                }
            }
        }
        return null;
    }
}
