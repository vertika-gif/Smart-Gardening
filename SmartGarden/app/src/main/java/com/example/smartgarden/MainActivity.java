package com.example.smartgarden;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pl.droidsonroids.gif.GifImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;




public class MainActivity extends AppCompatActivity {
    FirebaseDatabase db;
    DatabaseReference ref;
    Boolean shed_onn = false;
    private final String CHANNEL_ID = "TANK EMPTY";

    public void sendNot(String title , String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
        managerCompat.notify(1, builder.build());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView temp = findViewById(R.id.tempval);
        TextView hum = findViewById(R.id.humidval);
        TextView sunlight = findViewById(R.id.sunlightval);
        TextView soil = findViewById(R.id.soilval);
        TextView tankT = findViewById(R.id.tankT);
        GifImageView tankg = findViewById(R.id.tank);
        TextView shed = findViewById(R.id.shed);
        TextView lastWater = findViewById(R.id.lastwater);

        NotificationChannel channel = new NotificationChannel("TANK EMPTY", "TANK EMPTY",  NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        db = FirebaseDatabase.getInstance();
        db.getReference("temp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val = snapshot.getValue().toString();
                temp.setText(val+" °F");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db.getReference("waterTankEmpty").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String waterTa = snapshot.getValue().toString();
                if(waterTa.equals("1")){
                    tankT.setText("Tank Empty");
                    tankg.setImageResource(R.drawable.water_empty);
                    Toast.makeText(getApplicationContext(), "WATER TANK EMPTY", Toast.LENGTH_LONG).show();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID);
                    builder.setContentTitle("TANK EMPTY");
                    builder.setSmallIcon(R.drawable.logo);
                    builder.setContentText("please refill the water tank");
                    builder.setAutoCancel(true);
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                    managerCompat.notify(1, builder.build());
                }
                else{
                    tankT.setText("Sufficient Water");
                    tankg.setImageResource(R.drawable.water);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db.getReference("humd").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val = snapshot.getValue().toString();
                hum.setText(val+" %");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db.getReference("sunll").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val = snapshot.getValue().toString();
                sunlight.setText(val+"0 %");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db.getReference("soil").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val = snapshot.getValue().toString();
//                int vali = 100 -(Integer.parseInt(val)*100/1024);
                soil.setText(val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db.getReference("watr").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val = snapshot.getValue().toString();
                lastWater.setText(val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db.getReference("shed_on").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val = Objects.requireNonNull(snapshot.getValue()).toString();
                shed_onn = val.equals("1");
                if(shed_onn) {
                    sendNot("Holy Basil", "Plants are protected against sun");
                    shed.setText("shed on");
                }
                else {
                    sendNot("Holy Basil", "Plants are recieving sunlight");
                    shed.setText("shed off");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db.getReference("motor_on").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sendNot("Holy Basil", "Plants are just watered");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void water(View view) {
        db.getReference("motor_on").setValue(1);
        Toast.makeText(getApplicationContext(), "Plant watered successfully", Toast.LENGTH_LONG).show();
    }

    public void shed_on(View view) {
//        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();
        if(!shed_onn) {
            db.getReference("shed_on").setValue(1);
            Toast.makeText(getApplicationContext(), "Plant covered successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            db.getReference("shed_on").setValue(0);
            Toast.makeText(getApplicationContext(), "Plant uncovered successfully", Toast.LENGTH_SHORT).show();
        }
//        shed_onn = !shed_onn;
    }
}