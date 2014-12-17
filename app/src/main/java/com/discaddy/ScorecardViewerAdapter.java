package com.discaddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class ScorecardViewerAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private Scorecard card;
    private Map<String, int[]> scores;
    private int currentHole;

    public ScorecardViewerAdapter(Context context, Map<String, int[]> scores, int currentHole) {
        for (Map.Entry<String, int[]> e : scores.entrySet())
            list.add(e.getKey());
        this.scores = scores;
        this.context = context;
        this.currentHole = currentHole;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    public void incrementScore(View view, String name, int position) {
        int[] scoreList = scores.get(name);
        scoreList[position] += 1;
        scores.put(name, scoreList);
    }

    public void decrementScore(View view, String name, int position) {
        int[] scoreList = scores.get(name);
        scoreList[position] -= 1;
        scores.put(name, scoreList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.par_view_row, null);
        }

        //Handle TextView and display string from your list
        TextView playerName = (TextView)view.findViewById(R.id.par_view_hole);
        playerName.setText(list.get(position));



        final String name = list.get(position);
        int[] scoreList = scores.get(name);
        int score = scoreList[currentHole];

        TextView value = (TextView) view.findViewById(R.id.par_view_par);
        value.setText(Integer.toString(score));

        return view;
    }
}
