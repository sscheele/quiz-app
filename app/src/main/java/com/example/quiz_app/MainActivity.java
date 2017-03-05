package com.example.quiz_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ArrayAdapter<String> deckListAdapater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView deckList = (ListView)findViewById(R.id.deckList);
        deckListAdapater = new ArrayAdapter<String>(this, R.layout.row, R.id.name);
        deckList.setAdapter(deckListAdapater);
        deckList.setLongClickable(true);
        deckList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                String  itemValue    = (String) deckList.getItemAtPosition(pos);
                showDeletePrompt(itemValue);
                return true;
            }
        });
        deckList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item value
                String  itemValue    = (String) deckList.getItemAtPosition(position);
                Intent i = new Intent(getApplicationContext(), DeckViewActivity.class);
                i.putExtra("deckName", itemValue);
                startActivity(i);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.deckAddFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeckPrompt();
            }
        });
        checkDeckFolder();
    }

    void showDeletePrompt(String name) {
        //fuck that - I hate nested methods
        final String n = name;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteDeck(n);
                        checkDeckFolder();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete '" + name + "'?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    void showDeckPrompt() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                String name = userInput.getText().toString();
                                createDeck(name);
                                checkDeckFolder();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    void checkDeckFolder() {
        File deckDirectory = new File(this.getFilesDir().toString() + "/decks");
        if (!deckDirectory.exists()) {
            deckDirectory.mkdirs();
            File newDeck = new File(this.getFilesDir().toString() + "/decks/intro");
            try{
                newDeck.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File[] decks = deckDirectory.listFiles();
        deckListAdapater.clear();
        for (int i = 0; i < decks.length; i++){
            deckListAdapater.add(decks[i].getName());
        }
        deckListAdapater.notifyDataSetChanged();
    }

    public boolean createDeck(String deckName){
        File deck = new File(this.getFilesDir().toString() + "/decks/" + deckName);
        try {
            deck.createNewFile();
            Toast.makeText(getApplicationContext(), "Created file: " + deck.toString(), Toast.LENGTH_LONG);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Couldn't create file: " + deck.toString(), Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }

    public void deleteDeck(String deckName) {
        File deck = new File(this.getFilesDir().toString() + "/decks/" + deckName);
        deck.delete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
