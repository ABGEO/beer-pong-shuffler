package dev.abgeo.bpongshuffler;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Components.
    Toolbar toolbar;
    ListView teamsListView;
    FloatingActionButton addTeamFAB;

    // Objects.
    List<String> teamsList;
    ArrayAdapter<String> teamsArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        initializeObjects();
        initializeEvents();

        setSupportActionBar(toolbar);
        registerForContextMenu(teamsListView);

        teamsListView.setAdapter(teamsArrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (R.id.teams_list == v.getId()) {
            final AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;

            MenuItem edit = menu.add("Edit");
            MenuItem remove = menu.add("Remove");

            edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(MainActivity.this, "Edit In Development", Toast.LENGTH_LONG).show();

                    return true;
                }
            });

            remove.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    teamsList.remove(acmi.position);
                    teamsArrayAdapter.notifyDataSetChanged();

                    return true;
                }
            });
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (R.id.action_clear == id) {
            teamsList.clear();
            teamsArrayAdapter.notifyDataSetChanged();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize activity components.
     */
    private void initializeComponents() {
        toolbar = findViewById(R.id.toolbar);
        teamsListView = findViewById(R.id.teams_list);
        addTeamFAB = findViewById(R.id.add_team);
    }

    /**
     * Initialize activity objects.
     */
    private void initializeObjects() {
        teamsList = new ArrayList<>();
        teamsArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, teamsList);
    }

    /**
     * Initialize activity events.
     */
    private void initializeEvents() {
        addTeamFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTeam();
            }
        });
    }

    /**
     * Show Alert Dialog for adding new team.
     */
    private void addNewTeam() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new Team");

        final EditText teamName = new EditText(this);
        teamName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(teamName);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Validate on empty string.
                String text = teamName.getText().toString();
                teamsList.add(text);
                teamsArrayAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
