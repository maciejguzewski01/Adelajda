package com.example.adelajda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class ListContentActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView nameLanguageOneTextView;
    private TextView nameLanguageTwoTextView;
    private Button addNewWordButton;
    private Button goBackButton;

    private ListView listOfWords;
    private ArrayAdapter<String> adapter;

    private DataTransfer dataTransfer;

    private Vector<String> languageOneWords;
    private Vector<String> languageTwoWords;
    private Vector<String> comments;
    private Vector<String> colors;

    private Vector<String> contentToDisplayOnListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.list_content);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUpComponents();

        addNewWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListContentActivity.this, NewWordActivity.class);
                startActivityForResult(intent,3);}
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {finish();}
        });

        listOfWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                dataTransfer.currentWordLanguageOne=languageOneWords.elementAt(position);
                dataTransfer.currentWordLanguageTwo=languageTwoWords.elementAt(position);
                dataTransfer.currentWordComment=comments.elementAt(position);
                dataTransfer.currentWordColor=colors.elementAt(position);

                Intent intent = new Intent(ListContentActivity.this, SingleWordActivity.class);
                startActivityForResult(intent,4);
            }
        });
    }

    private void setUpComponents()
    {
        dataTransfer=DataTransfer.getInstance();
        getListContentFromFile();

        goBackButton=findViewById(R.id.go_back_button);
        addNewWordButton=findViewById(R.id.add_new_word_list_content);
        nameTextView=findViewById(R.id.text_name_list_content);
        nameTextView.setText(dataTransfer.currentListName);

        nameLanguageOneTextView=findViewById(R.id.language1);
        nameLanguageTwoTextView=findViewById(R.id.language2);

        String bufferLine=new String();

        try(FileInputStream fileInputStream = this.openFileInput(dataTransfer.currentListName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
            bufferLine=reader.readLine();
        } catch (IOException e) { e.printStackTrace(); }

        String[] buffer=bufferLine.split(";");

        nameLanguageOneTextView.setText(buffer[0]);
        nameLanguageTwoTextView.setText(buffer[1]);

        listOfWords=findViewById(R.id.list_of_words);
        adapter = new ArrayAdapter<>(this, R.layout.list_row, contentToDisplayOnListView);
        listOfWords.setAdapter(adapter);
    }


    private void getListContentFromFile()
    {
        contentToDisplayOnListView=new Vector<>();
        languageOneWords= new Vector<>();
        languageTwoWords=new Vector<>();
        comments=new Vector<>();
        colors=new Vector<>();

        String fileName= dataTransfer.currentListName;
        Vector<String> listContentBuffer=new Vector<>();

        try(FileInputStream fileInputStream = this.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
            String bufferLine;
            while ((bufferLine = reader.readLine()) != null) listContentBuffer.add(bufferLine);
        } catch (IOException e) { e.printStackTrace(); }

        if(listContentBuffer.size()>1)
        {
            for(int i=1;i<listContentBuffer.size();i++)
            {
                String[] lineBuffer=listContentBuffer.elementAt(i).split(";");

                languageOneWords.add(lineBuffer[0]);
                languageTwoWords.add(lineBuffer[1]);
                comments.add(lineBuffer[2]);
                colors.add(lineBuffer[3]);

                contentToDisplayOnListView.add(lineBuffer[0]+" - "+lineBuffer[1]);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(dataTransfer.newWordAdded==true) handleNewWord();
        if(dataTransfer.wordDeleted==true) handleWordRemoval();
        if(dataTransfer.wordEdited==true) handleWordEdited();
    }


    private void handleNewWord()
    {
        contentToDisplayOnListView.add(dataTransfer.newWordDisplay);
        adapter.notifyDataSetChanged();

        languageOneWords.add(dataTransfer.newWordLanguageOne);
        languageTwoWords.add(dataTransfer.newWordLanguageTwo);
        comments.add(dataTransfer.newComment);
        colors.add(dataTransfer.newColor);

        dataTransfer.newWordLanguageOne="";
        dataTransfer.newWordLanguageTwo="";
        dataTransfer.newComment="";
        dataTransfer.newColor="";

        dataTransfer.newWordDisplay="";
        dataTransfer.newWordAdded=false;
    }


    private void handleWordRemoval()
    {
        contentToDisplayOnListView.remove(dataTransfer.currentWordLanguageOne+" - "+dataTransfer.currentWordLanguageTwo);
        deleteWordFromListFile();
        adapter.notifyDataSetChanged();

        languageOneWords.remove(dataTransfer.currentWordLanguageOne);
        languageTwoWords.remove(dataTransfer.currentWordLanguageTwo);
        comments.remove(dataTransfer.currentWordComment);
        colors.remove(dataTransfer.currentWordColor);

        dataTransfer.currentWordLanguageOne="";
        dataTransfer.currentWordLanguageTwo="";
        dataTransfer.currentWordComment="";
        dataTransfer.currentWordColor="";

        dataTransfer.wordDeleted=false;
    }


    private void deleteWordFromListFile()
    {
        Vector<String> buffer=new Vector<>();

        try(FileInputStream fileInputStream = this.openFileInput(dataTransfer.currentListName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
            String bufferLine;
            while ((bufferLine = reader.readLine()) != null) buffer.add(bufferLine);
        } catch (IOException e) { e.printStackTrace(); }

        buffer.remove(dataTransfer.currentWordLanguageOne+";"+dataTransfer.currentWordLanguageTwo+";"+dataTransfer.currentWordComment+";"+dataTransfer.currentWordColor);

        try (FileOutputStream fileOutputStream = openFileOutput(dataTransfer.currentListName, this.MODE_PRIVATE)) {
            for(int i=0;i<buffer.size();i++)
            {
                fileOutputStream.write(buffer.elementAt(i).getBytes());
                fileOutputStream.write("\n".getBytes());
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }


    private void handleWordEdited()
    {
        int idx=contentToDisplayOnListView.indexOf(dataTransfer.currentWordLanguageOne+" - "+dataTransfer.currentWordLanguageTwo);
        if(idx<contentToDisplayOnListView.size())
        {
            contentToDisplayOnListView.set(idx, dataTransfer.newWordDisplay);
            adapter.notifyDataSetChanged();

            languageOneWords.set(idx, dataTransfer.newWordLanguageOne);
            languageTwoWords.set(idx, dataTransfer.newWordLanguageTwo);
            comments.set(idx, dataTransfer.newComment);
            colors.set(idx, dataTransfer.newColor);

            dataTransfer.newWordLanguageOne="";
            dataTransfer.newWordLanguageTwo="";
            dataTransfer.newComment="";
            dataTransfer.newColor="";

            dataTransfer.newWordDisplay="";
            dataTransfer.wordEdited=false;
        }
    }

}
