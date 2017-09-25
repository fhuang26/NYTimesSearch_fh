package com.example.felixh.nytimessearch.main;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.felixh.nytimessearch.R;
import com.example.felixh.nytimessearch.data.SearchState;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends DialogFragment implements TextView.OnEditorActionListener {
    public static final int N_DATESORT = 3;
    private EditText etBeginDate;

    public SettingFragment() {
        // Empty constructor required for DialogFragment
    }

    public static SettingFragment newInstance(String title) {
        SettingFragment frag = new SettingFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public String datesort = "None";
    class DatesortOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            datesort = parent.getItemAtPosition(pos).toString();
            if (datesort.equals("None")) {
                SearchState.date_sort_sign = 0;
            } else if (datesort.equals("From Newest to Oldest")) {
                SearchState.date_sort_sign = -1;
            } else {
                SearchState.date_sort_sign = 1;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            datesort = "None";
        }
    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container);
        etBeginDate = (EditText) view.findViewById(R.id.etBeginDate);
        getDialog().setTitle("Setting");
        // Show soft keyboard automatically

        etBeginDate.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        etBeginDate.setOnEditorActionListener((TextView.OnEditorActionListener) this);
        Spinner spDatesort = (Spinner) view.findViewById(R.id.spDateSorting);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.datesort_array, android.R.layout.simple_spinner_dropdown_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spDatesort
        spDatesort.setAdapter(adapter);

        spDatesort.setScrollBarSize(N_DATESORT);
        spDatesort.setSelection(0);
        /*
        for (int k = 0; k < N_DATESORT; ++k) {
            spDatesort.setSelection(k);
            String s = spDatesort.getSelectedItem().toString();
            Toast.makeText(getContext(), "s="+s, Toast.LENGTH_LONG);
        }
       */

        spDatesort.setOnItemSelectedListener(new DatesortOnItemSelectedListener());
        Button btDone = (Button) view.findViewById(R.id.btDone);
        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_done(v);
            }
        });
        return view;
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {

            SearchActivity.currActivity.hideSoftKeyboard();

            return true;
        }
        return false;
    }
    public void setting_done(View v) {
        SearchActivity.currActivity.hideSoftKeyboard();
        String msg = etBeginDate.getText().toString();
        SearchState.beginDate = msg;
        dismiss();
    }
}
