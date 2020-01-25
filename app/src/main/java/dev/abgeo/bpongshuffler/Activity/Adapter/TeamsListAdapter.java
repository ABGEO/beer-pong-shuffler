package dev.abgeo.bpongshuffler.Activity.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.abgeo.bpongshuffler.R;

public class TeamsListAdapter extends ArrayAdapter<String> {

    private Activity context;
    private List<String> items;

    public TeamsListAdapter(Activity mainActivity, ArrayList<String> dataArrayList) {
        super(mainActivity, 0, dataArrayList);

        this.context = mainActivity;
        this.items = dataArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView teamNameTextView;

        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.team_list, parent, false);

            teamNameTextView = (TextView) convertView.findViewById(R.id.name);

            convertView.setTag(teamNameTextView);
        } else {
            teamNameTextView = (TextView) convertView.getTag();
        }

        teamNameTextView.setText(items.get(position));

        return convertView;
    }
}
