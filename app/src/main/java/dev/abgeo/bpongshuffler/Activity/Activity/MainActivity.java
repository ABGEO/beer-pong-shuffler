package dev.abgeo.bpongshuffler.Activity.Activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dev.abgeo.bpongshuffler.Activity.Adapter.TeamsListAdapter;
import dev.abgeo.bpongshuffler.R;

public class MainActivity extends AppCompatActivity {

    // Components.
    SwipeMenuListView teamsListView;
    TextView teamsListViewEmptyText;
    FloatingActionButton addTeamFAB;
    ImageButton clearBTN;
    ImageButton shuffleBTN;

    // Objects.
    ArrayList<String> teamsList;
    TeamsListAdapter teamsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        initializeObjects();
        initializeEvents();

        teamsListView.setAdapter(teamsListAdapter);
        teamsListView.setEmptyView(teamsListViewEmptyText);
        teamsListView.setMenuCreator(swapMenuCreator);

        shuffleBTN.setVisibility(View.INVISIBLE);
        clearBTN.setVisibility(View.INVISIBLE);

        teamsList.add("Team 1");
        teamsList.add("Team 2");
        teamsList.add("Team 3");
        teamsList.add("Team 4");
        teamsList.add("Team 5");

        onTeamListUpdate();
    }

    SwipeMenuCreator swapMenuCreator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem editItem = new SwipeMenuItem(getApplicationContext());
            editItem.setBackground(new ColorDrawable(getResources().getColor(R.color.colorSuccess)));
            editItem.setWidth(150);
            editItem.setIcon(R.drawable.ic_edit_24dp);

            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.colorDanger)));
            deleteItem.setWidth(150);
            deleteItem.setIcon(R.drawable.ic_delete_24dp);

            menu.addMenuItem(editItem);
            menu.addMenuItem(deleteItem);
        }
    };

    /**
     * Initialize activity components.
     */
    private void initializeComponents() {
        teamsListView = findViewById(R.id.teams_list);
        teamsListViewEmptyText = findViewById(R.id.teams_list_empty_text);
        addTeamFAB = findViewById(R.id.add_team);
        clearBTN = findViewById(R.id.btn_clear);
        shuffleBTN = findViewById(R.id.btn_shuffle);
    }

    /**
     * Initialize activity objects.
     */
    private void initializeObjects() {
        teamsList = new ArrayList<>();
        teamsListAdapter = new TeamsListAdapter(this, teamsList);
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

        teamsListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        addOrEditTeam(teamsList.get(position));
                        break;
                    case 1:
                        teamsList.remove(position);
                        onTeamListUpdate();
                        break;
                }

                return false;
            }
        });

        teamsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                teamsListView.smoothOpenMenu(position);
                return false;
            }
        });

        clearBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamsList.clear();
                onTeamListUpdate();
            }
        });

        shuffleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = shuffleTeams();

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.shuffle_result))
                        .setMessage(Html.fromHtml(result))
                        .setPositiveButton(getString(R.string.ok), null)
                        .show();
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

                    onTeamListUpdate();

                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * Shuffle teams and get schedule.
     *
     * @return Formatted schedule.
     */
    private String shuffleTeams() {
        StringBuilder result = new StringBuilder();
        List<String> teams = new ArrayList<>(teamsList);
        int n = teams.size();

        for (int i = 0; i < n / 2; i++) {
            StringBuilder item = new StringBuilder();
            for (int j = 0; j < 2; j++) {
                int index = new Random().nextInt(teams.size());
                String currentTeam = teams.get(index);

                if (0 == j) {
                    item = item.append("<p>").append(currentTeam);
                } else {
                    item.append(" <b>VS</b> ").append(currentTeam).append("</p>");
                }

                teams.remove(index);
            }

            result.append(item.toString());
        }

        if (!teams.isEmpty()) {
            String team = teams.get(0);
            result.append("<p>").append("Standalone Team: ").append(team).append("</p>");
        }

        return result.toString();
    }

    /**
     * Actions when data is changed.
     */
    private void onTeamListUpdate() {
        int teamsCount = teamsList.size();

        if (0 == teamsCount) {
            shuffleBTN.setVisibility(View.INVISIBLE);
            clearBTN.setVisibility(View.INVISIBLE);
        } else if (1 == teamsCount) {
            shuffleBTN.setVisibility(View.INVISIBLE);
            clearBTN.setVisibility(View.VISIBLE);
        } else {
            shuffleBTN.setVisibility(View.VISIBLE);
            clearBTN.setVisibility(View.VISIBLE);
        }

        teamsListAdapter.notifyDataSetChanged();
    }
}
