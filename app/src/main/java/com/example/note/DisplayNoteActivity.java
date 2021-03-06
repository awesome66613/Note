package com.example.note;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

public class DisplayNoteActivity extends AppCompatActivity {

    NoteDetailsFragment noteDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.details_activity_layout);

        Bundle extras = getIntent().getExtras();
        int ix = -1;
        if (extras != null) {
            ix = extras.getInt("id", -1);
        }

        if (savedInstanceState == null) {
            noteDetailsFragment = NoteDetailsFragment.newInstance(ix);
            getSupportFragmentManager().beginTransaction().add(R.id.note_details_fragment_container, noteDetailsFragment).commit();
        } else {
            noteDetailsFragment = (NoteDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.note_details_fragment_container);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_note_details,menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
