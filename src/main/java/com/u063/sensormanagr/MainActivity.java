package com.u063.sensormanagr;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    int sensorWork=0;
    SensorManager sm;
    Sensor sens;
    Sensor Temp;
    Connection mConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sm=(SensorManager) getSystemService(SENSOR_SERVICE);
        sens=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        /*sm=(SensorManager) getSystemService(SENSOR_SERVICE);
        Light=sm.getDefaultSensor(Sensor.TYPE_LIGHT);*/
    }


    byte[] coord=new byte[3];
    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView tx = findViewById(R.id.ids);
        tx.setText(""+event.values[0]+"\n"+event.values[1]+"\n"+event.values[2]);
        for(int i=0; i<3; i++) {
            if(event.values[i]<0){
                event.values[i]*=-1;
                event.values[i]+=125;
            }
        }
        coord[0]= (byte) event.values[0];
        coord[1]= (byte) event.values[1];
        coord[2]= (byte) event.values[2];
    }
    public void sentData(View v) throws Exception {
        Timer timer = new Timer();
        Handler handler = new Handler(Looper.getMainLooper());
        EditText etx = (EditText) findViewById(R.id.etx);
        EditText etx1 = (EditText) findViewById(R.id.etx1);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Здесь вызываем вашу функцию
                        sendData();
                    }
                });
            }
        }, 0, 750); // Здесь укажите интервал в миллисекундах (например, каждую секунду)

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, sens, SensorManager.SENSOR_DELAY_NORMAL);
    }
    void sendData(){
        EditText etx = (EditText) findViewById(R.id.etx);
        EditText etx1 = (EditText) findViewById(R.id.etx1);
        mConnect=new Connection(etx.getText().toString(), Integer.parseInt(etx1.getText().toString()));
        byte[] bs={coord[0],coord[1],coord[2]};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mConnect.sendData(bs);
                    mConnect.finalize();
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tx = findViewById(R.id.txd);
                            tx.setText("fuck error send: "+e.getMessage());
                        }
                    });

                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        mConnect.closeConnection();
    }
}