package com.sumonkmr.quicknotes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView myListView;
    HashMap<String, String> notesTemp;
    ArrayList<HashMap<String, String>> notes;
    String savedTitle, savedMassage;
    EditText titleEd, massageEd;
    FloatingActionButton floatingBtn;
    AlertDialog alertDialog;
    private NoteAdapter noteAdapter;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREFERENCE_NAME = "Quick note";
    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //Method's
        HookUps();

        floatingBtn.setOnClickListener(v -> {
            FloatingDialog(this);
        });
        notes = new ArrayList<>();
        noteAdapter = new NoteAdapter();
        myListView.setAdapter(noteAdapter);
        loadData();
    }

    private void FloatingDialog(Context context) {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate the custom layout for the dialog
        View customView = inflater.inflate(R.layout.alert_dialog, null);

        // Set the custom view to the AlertDialog
        builder.setView(customView);

        // Get references to views in the custom layout if needed
        Button okButton = customView.findViewById(R.id.submit);
        Button cancelButton = customView.findViewById(R.id.cencel);


        // Set click listeners for buttons in the custom layout
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle OK button click
                EditText titleEd = customView.findViewById(R.id.titleEd);
                EditText massageEd = customView.findViewById(R.id.massageEd);
                String tempTitle = titleEd.getText().toString();
                String tempMassage = massageEd.getText().toString();
//                String tempTitle ="This is Default title.";
//                String tempMassage ="This is Default massage.";
                // You can add code here to handle the OK button click'
                if (String.valueOf(titleEd).isEmpty() && String.valueOf(titleEd).length() == 0 && String.valueOf(massageEd).isEmpty() && String.valueOf(massageEd).length() == 0) {
                    ShowToast("Please Write your note properly!");
                } else {
                    saveData(titleEd, massageEd);
                    ShowToast("Note add successfully!");
                    alertDialog.dismiss();
                }

            }
        });

        cancelButton.setOnClickListener(view -> {
            // Handle Cancel button click
            // You can add code here to handle the Cancel button click
            alertDialog.dismiss();
        });

        // Create and show the AlertDialog
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void HookUps() {
        myListView = findViewById(R.id.myListView);
        floatingBtn = findViewById(R.id.floatingBtn);
    }

    // Method to retrieve SharedPreferences data and store it in a HashMap
    private void saveData(EditText edTitle, EditText edMassage) {
        String title = edTitle.getText().toString();
        String message = edMassage.getText().toString();

        if (!title.isEmpty() && !message.isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_TITLE, title);
            editor.putString(KEY_MESSAGE, message);
            editor.apply();

            // Reload data after saving
            loadData();

        }
    }

    private void loadData() {
//        notes.clear();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String title = sharedPreferences.getString(KEY_TITLE, "");
        String message = sharedPreferences.getString(KEY_MESSAGE, "");

        if (!title.isEmpty() && !message.isEmpty()) {
            // Add data to the list
            notes.add(ValueTemp(title, message));
        }
        // Notify the adapter that the data has changed
        noteAdapter.notifyDataSetChanged();
        Log.d("sharedMsg", "getView: " + sharedPreferences.getAll());
    }

    private HashMap<String, String> ValueTemp(String title, String massage) {
        notesTemp = new HashMap<>();
        notesTemp.put("title", title);
        notesTemp.put("message", massage);
        return notesTemp;
    }


    public void ShowToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class NoteAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return notes.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.list_item, parent, false);


            TextView titleTV = myView.findViewById(R.id.titleTV);
            TextView massageTV = myView.findViewById(R.id.massageTV);
            notesTemp = (notes.get(position));
            savedTitle = notesTemp.get("title");
            savedMassage = notesTemp.get("message");


            // Get current date and time
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            // Format the date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a", Locale.getDefault());

            String formattedDateTime = dateFormat.format(currentDate);
            String formattedTime = timeFormat.format(calendar.getTime());
            String time12Hour = convertTo12HourFormat(formattedTime);

            // Display the date and time in a TextView
            TextView dateTimeTextView = myView.findViewById(R.id.dateTv);

            dateTimeTextView.setText(time12Hour+" ~ "+ formattedDateTime);


            titleTV.setText(savedTitle);
            massageTV.setText(savedMassage);


            return myView;
        }
    }//Adapter class





    private static String convertTo12HourFormat(String time24Hour) {
        SimpleDateFormat sdf24Hour = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat sdf12Hour = new SimpleDateFormat("h:mm a", Locale.getDefault());

        try {
            // Parse the input time string
            Date date = sdf24Hour.parse(time24Hour);

            // Format the date in 12-hour format
            assert date != null;
            return sdf12Hour.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid time format";
        }
    }
}