package dev.abgeo.bpongshuffler;

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
import android.widget.Button;
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

            MenuItem edit = menu.add(R.string.edit);
            MenuItem remove = menu.add(R.string.remove);

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

        final EditText teamName = new EditText(this);
        teamName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        teamName.setText(oldName);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(
                        isNew ? getString(R.string.add_new_team)
                                : getString(R.string.edit_team, oldName)
                )
                .setView(teamName)
                .setPositiveButton(
                        isNew ? getString(R.string.add) : getString(R.string.save), null
                )
                .setNegativeButton(getString(R.string.cancel), null)
                .show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamName.setError(null);
                String name = teamName.getText().toString();
                boolean isValid = true;

                if (name.isEmpty()) {
                    teamName.setError(getResources().getString(R.string.validation_team_name_is_empty));
                    isValid = false;
                }

                if (
                    -1 != teamsList.indexOf(name)
                    && teamsList.indexOf(oldName) != teamsList.indexOf(name)
                ) {
                    teamName.setError(getString(R.string.validation_team_already_exists, name));
                    isValid = false;
                }

                if (isValid) {
                    if (isNew) {
                        teamsList.add(name);
                    } else {
                        int index = teamsList.indexOf(oldName);
                        teamsList.set(index, name);
                    }

                    teamsArrayAdapter.notifyDataSetChanged();

                    dialog.dismiss();
                }
            }
        });
    }
}
