package com.example.vijay.androidlabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TestToolbar extends Activity  {

    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        fab =  findViewById(R.id.btnFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Snackbar Here", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.toolbar_menu, m);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem mi){
        //mi.getItemId();
        switch(mi.getItemId()){
            case R.id.action_one:
                Log.d("Toolbar", "Option 1 selected");
                SharedPreferences sharedPref = getSharedPreferences("newMessage", Context.MODE_PRIVATE);
                String x = sharedPref.getString("newMessage", "");
                Snackbar.make(fab, x, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.action_two:
                Log.d("Toolbar", "Option 2 selected");
                AlertDialog.Builder builder = new AlertDialog.Builder(TestToolbar.this);
                builder.setTitle("Do you want to go back?");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("User clicked", "OK Button");
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("User clicked", "Cancel Button");
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.action_three:
                Log.d("Toolbar", "Option 3 selected");
                AlertDialog.Builder build = new AlertDialog.Builder(TestToolbar.this);
                LayoutInflater inflater = TestToolbar.this.getLayoutInflater();
                View d = inflater.inflate(R.layout.dialog, null);
                final EditText newMessage =  d.findViewById(R.id.newMessage) ;
                build.setView(d)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                /**
                                 * string entered will be the new message displayed by
                                 * the Snackbar object when selecting menu item 1
                                 */
                                SharedPreferences sharedPref = getSharedPreferences("newMessage", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sharedPref.edit();
                                edit.putString("newMessage", newMessage.getText().toString());
                                edit.apply();
                            }
                        });
                build.show();
                break;
            case R.id.action_four:
                Log.d("Toolbar", "About Selected");
                Toast.makeText(getApplicationContext(), "Version 1.0 by Tu Anh Nguyen", Toast.LENGTH_LONG).show();
        }
        return true;
    }

}
