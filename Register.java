package com.example.tanapone.smartcashier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tanapone.smartcashier.DatabaseHelper.DatabaseHelperClass;
import com.example.tanapone.smartcashier.Models.Store;

public class Register extends AppCompatActivity {
    private DatabaseHelperClass myDB;
    private Store store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Register");
        setContentView(R.layout.activity_register);
        myDB = new DatabaseHelperClass(this);
        store = new Store();
        //
        Button resetBtn = (Button) findViewById(R.id.storeResetBtn);
        Button registerBtn = (Button) findViewById(R.id.storeRegisterBtn);
        final EditText storeNameEditText = (EditText) findViewById(R.id.storeNameEditText);
        final EditText storeEamilEditText = (EditText) findViewById(R.id.storeEamilEditText);
        final  EditText storePnumberEditText = (EditText) findViewById(R.id.storePnumberEditText);
        final  EditText storeFacebookEditText = (EditText) findViewById(R.id.storeFacebookEditText);
        //


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeNameEditText.setText("");
                storeEamilEditText.setText("");
                storePnumberEditText.setText("");
                storeFacebookEditText.setText("");
                Toast.makeText(getApplicationContext(),R.string.register_reseted,Toast.LENGTH_SHORT).show();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(
                        storeNameEditText.getText().toString().trim().equals("") ||
                                storeEamilEditText.getText().toString().trim().equals("") ||
                                storePnumberEditText.getText().toString().trim().equals("")
                        ){
                    Toast.makeText(getApplicationContext(),R.string.register_enter_info,Toast.LENGTH_SHORT).show();
                }else{
                    boolean result = myDB.addStore(new Store(storeNameEditText.getText().toString().trim(),
                            storePnumberEditText.getText().toString().trim(),
                            storeEamilEditText.getText().toString().trim(),
                            storeFacebookEditText.getText().toString().trim()));
                    if(result){
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
