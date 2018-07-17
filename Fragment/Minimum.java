package com.example.tanapone.smartcashier.Fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanapone.smartcashier.DatabaseHelper.DatabaseHelperClass;
import com.example.tanapone.smartcashier.Models.Product;
import com.example.tanapone.smartcashier.R;

import java.util.List;

public class Minimum extends Fragment implements View.OnClickListener {
    private DatabaseHelperClass myDB;
    private TableLayout listItem;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_minimum, container, false);
        myDB = new DatabaseHelperClass(getActivity());
        listItem = v.findViewById(R.id.listItemMinimum);

        minimumItem();

        return v;
    }

    @Override
    public void onClick(View v) {

    }

    public void minimumItem(){
        listItem.removeAllViews();
        TextView headerName = new TextView(getContext());
        TextView headerQty = new TextView(getContext());
        TextView headerDelete = new TextView(getContext());

        headerName.setPadding(10,10,10,10);
        headerName.setBackgroundColor(Color.parseColor("#4DA2DE"));
        headerName.setText(R.string.item_header_name);
        headerName.setTextColor(Color.WHITE);
        headerName.setGravity(Gravity.CENTER);

        headerQty.setPadding(10,10,10,10);
        headerQty.setBackgroundColor(Color.parseColor("#4DA2DE"));
        headerQty.setText(R.string.item_header_qty);
        headerQty.setTextColor(Color.WHITE);
        headerQty.setGravity(Gravity.CENTER);

        headerDelete.setPadding(10,10,10,10);
        headerDelete.setBackgroundColor(Color.parseColor("#4DA2DE"));
        headerDelete.setText(R.string.item_header_update);
        headerDelete.setTextColor(Color.WHITE);
        headerDelete.setGravity(Gravity.CENTER);



        TableRow tableRow = new TableRow(getContext());
        tableRow.addView(headerName);
        tableRow.addView(headerQty);
        tableRow.addView(headerDelete);



        listItem.addView(tableRow);

        List<Product> products =myDB.getProductsMinimum();
        for(final Product product : products){
            TableRow tableRowData = new TableRow(getActivity());
            TextView productName = new TextView(getActivity());
            productName.setGravity(Gravity.CENTER);
            productName.setText(product.getProductName());
            productName.setPadding(14,14,14,14);
            productName.setBackgroundColor(Color.parseColor("#FFFFFF"));

            TextView productQTY = new TextView(getActivity());
            productQTY.setText(""+product.getProductQuantity());
            productQTY.setGravity(Gravity.CENTER);
            productQTY.setPadding(14,14,14,14);
            productQTY.setBackgroundColor(Color.parseColor("#FFFFFF"));

            ImageView edit = new ImageView(getActivity());
            edit.setImageResource(R.drawable.ic_edit_black_24dp);
            edit.setPadding(6,6,6,6);
            edit.setBackgroundColor(Color.parseColor("#FFFFFF"));

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.dialog_custom, null);
                    builder.setView(view);
                    final EditText qty = (EditText) view.findViewById(R.id.quantity);
                    builder.setPositiveButton(getString(R.string.set_quantity_dialog_submitBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Check username password
                            if (!qty.getText().toString().equals("")){
                               boolean res =  myDB.updateProductQTY(product.getProductID(),qty.getText().toString());
                                Toast.makeText(getActivity(),getString(R.string.global_word_success), Toast.LENGTH_SHORT).show();
                                minimumItem();
                            }
                            else {
                                Toast.makeText(getActivity(),getString(R.string.register_enter_info), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton(getString(R.string.global_word_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();

                }
            });

            tableRowData.addView(productName);
            tableRowData.addView(productQTY);
            tableRowData.addView(edit);
            listItem.addView(tableRowData);
        }
    }
}
