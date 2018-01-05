package com.budget.abudget;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import java.util.Calendar;


public class AlarmActivity extends AppCompatActivity {

    private PendingIntent pendingIntent;

    private RadioButton secondsRadioButton, minutesRadioButton, hoursRadioButton;

    Button btmStart, btmStop;

    private static final int ALARM_REQUEST_CODE = 133;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        secondsRadioButton = (RadioButton) findViewById(R.id.seconds_radio_button);
        minutesRadioButton = (RadioButton) findViewById(R.id.minutes_radio_button);
        hoursRadioButton = (RadioButton) findViewById(R.id.hours_radio_button);
        btmStart = (Button) findViewById(R.id.start_alarm_button);
        btmStop = (Button) findViewById(R.id.stop_alarm_button);
        final EditText editText = (EditText) findViewById(R.id.input_interval_time);

        // Получаем PendingIntent для рассылки
        Intent alarmIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this,
                ALARM_REQUEST_CODE, alarmIntent, 0);

        // Лисенер на старт
        btmStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getInterval = editText.getText().toString().trim();
                // Проверка - интервал не должен быть пустым
                if (!getInterval.equals("") && !getInterval.equals("0"))
                    // Вызов аларм менеджера
                    triggerAlarmManager(getTimeInterval(getInterval));

            }
        });

        // Лисенер на стоп
        btmStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarmManager();
            }
        });

    }

    // Получение интервала
    private int getTimeInterval(String getInterval) {
        // Строку в число
        int interval = Integer.parseInt(getInterval);

        // Проверка типа интервала и преобразование при необходимости
        if (secondsRadioButton.isChecked())
            return interval;
        if (minutesRadioButton.isChecked())
            return interval * 60;
        if (hoursRadioButton.isChecked()) return interval * 60 * 60;

        return 0;
    }


    // Аларм менеджер
    public void triggerAlarmManager(int alarmTriggerTime) {
        // Получение календаря с текущем временем
        Calendar cal = Calendar.getInstance();
        // Добавление времени в календарь
        cal.add(Calendar.SECOND, alarmTriggerTime);

        // Присвоение аларм менеджеру введеный таймер в милисекундах
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        Toast.makeText(this, "Оповещение запустится через " + alarmTriggerTime +
                " секунд.", Toast.LENGTH_SHORT).show();
    }

    // Выключение аларм
    public void stopAlarmManager() {

        // Отмена аларм менеджеру
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);

        // Выключение сервиса медиа плеера
        stopService(new Intent(AlarmActivity.this, AlarmSoundService.class));

        // Удаление нотификации из трея
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AlarmNotificationService.NOTIFICATION_ID);

        Toast.makeText(this, "Оповещение выключено", Toast.LENGTH_SHORT).show();
    }
}

