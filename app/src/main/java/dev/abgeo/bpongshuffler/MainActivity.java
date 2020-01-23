package dev.abgeo.bpongshuffler;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

import java.util.ArrayList;
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
                    addOrEditTeam(teamsList.get(acmi.position));

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
                addOrEditTeam(null);
            }
        });
    }

    /**
     * Show Alert Dialog for adding or editing team.
     */
    private void addOrEditTeam(final String oldName) {
        final boolean isNew = null == oldName;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isNew ? "Add new Team" : "Edit Team " + oldName);

        final EditText teamName = new EditText(this);
        teamName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        teamName.setText(oldName);
        builder.setView(teamName);

        builder.setPositiveButton(isNew ? "Add" : "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Validate on empty string.
                // TODO: Validate on unique value.
                String name = teamName.getText().toString();

                if (isNew) {
                    teamsList.add(name);
                } else {
                    int index = teamsList.indexOf(oldName);
                    teamsList.set(index, name);
                }

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
