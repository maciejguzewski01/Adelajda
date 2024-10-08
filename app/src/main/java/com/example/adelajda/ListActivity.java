package com.example.adelajda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ListActivity extends AppCompatActivity {

    private DataTransfer dataTransfer;
    private TextView nameTextView;

    private Button displayButton;
    private Button learningModeAButton;
    private Button learningModeBButton;
    private Button addNewWordButton;
    private Button settingsButton;
    private Button goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.list_options_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUpComponents();

        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, ListContentActivity.class);
                startActivityForResult(intent,2);}
        });

        learningModeAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, LearningModeAStartActivity.class);
                startActivityForResult(intent,4);}
        });

        learningModeBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, LearningModeBStartActivity.class);
                startActivityForResult(intent,5);}
        });

        addNewWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, NewWordActivity.class);
                startActivityForResult(intent,3);}
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataTransfer.listRemoved=false;
                Intent intent = new Intent(ListActivity.this, ListSettingsActivity.class);
                startActivityForResult(intent,1);}
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private  void setUpComponents()
    {
        nameTextView=findViewById(R.id.text_name);
        displayButton=findViewById(R.id.display_list);
        learningModeAButton=findViewById(R.id.learning_mode_a);
        learningModeBButton=findViewById(R.id.learning_mode_b);
        addNewWordButton=findViewById(R.id.add_new_word);
        settingsButton=findViewById(R.id.settings_button);
        goBackButton=findViewById(R.id.go_back_button);
        dataTransfer=DataTransfer.getInstance();
        nameTextView.setText(dataTransfer.currentListName);
        storeLanguageNames();
    }


    private void storeLanguageNames()
    {
        String buffer= new String();
        try(FileInputStream fileInputStream = this.openFileInput(dataTransfer.currentListName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
            buffer=reader.readLine();
        } catch (IOException e) { e.printStackTrace(); }

        String[] splitBuffer = buffer.split(";");

        dataTransfer.currentListLanguageOneName=splitBuffer[0];
        dataTransfer.currentListLanguageTwoName=splitBuffer[1];
    }


    @Override
    protected void onResume() {
        super.onResume();
        nameTextView.setText(dataTransfer.currentListName);
        if(dataTransfer.newWordAdded==true)
        {
            dataTransfer.newWordDisplay="";
            dataTransfer.newWordAdded=false;
        }
        if(dataTransfer.listRemoved==true) finish();
    }
}
