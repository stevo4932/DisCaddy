package com.discaddy;

        import android.content.Context;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import org.w3c.dom.Text;

        import java.util.ArrayList;

public class ParViewCustomAdapter extends ArrayAdapter<String> {

    private static final String TAG = "ParCustomAdapter";
    private ArrayList<String> holes;
    private int[] default_pars;

    public ParViewCustomAdapter(Context context, ArrayList<String> holes, int[] default_pars) {
        super(context, 0, holes);
        this.holes = holes;
        this.default_pars = default_pars;
    }

    //may not need these next three.
    @Override
    public int getCount() {
        return holes.size();
    }

    public int[] getPars(){
        return default_pars;
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    public int decrementPar(int position, int[] pars) {
        return default_pars[position] -= 1;

    }
    public int incrementPar(int position, int[] pars){

        return default_pars[position] += 1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String hole_number = holes.get(position);
        int current_par = default_pars[position];

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.par_view_row, parent, false);
        // Lookup views for data population
        TextView hole_name = (TextView) convertView.findViewById(R.id.par_view_hole);
        TextView par_number = (TextView) convertView.findViewById(R.id.par_view_par);

        // Populate the data into the template view using the data object
        hole_name.setText(hole_number);
        par_number.setText("" + current_par);



        /*//Handle buttons and add onClickListeners
        Button plusBtn = (Button) convertView.findViewById(R.id.par_plus_button);
        Button minusBtn = (Button) convertView.findViewById(R.id.par_minus_button);

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementPar(position, default_pars);
                notifyDataSetChanged();
            }
        });
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementPar(position, default_pars);
                notifyDataSetChanged();
            }
        });*/

        // Return the completed view to render on screen
        return convertView;
    }
}
