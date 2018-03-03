package com.example.tanapone.smartcashier.Fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanapone.smartcashier.DatabaseHelper.DatabaseHelperClass;
import com.example.tanapone.smartcashier.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * A simple {@link Fragment} subclass.
 */
public class CashierFragment extends Fragment implements View.OnClickListener {

    private DatabaseHelperClass myDB;
    private Button scanBtn;
    private EditText barcodeEditText;
    private ListView categories;

    private Button add_product_category_btn;
    public CashierFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cashier, container, false);
        myDB = new DatabaseHelperClass(getActivity());
        //Toast.makeText(getActivity(),myDB.getStore().toString(),Toast.LENGTH_SHORT).show();
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
        }
    }
}
