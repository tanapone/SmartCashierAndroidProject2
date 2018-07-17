package com.example.tanapone.smartcashier.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanapone.smartcashier.DatabaseHelper.DatabaseHelperClass;
import com.example.tanapone.smartcashier.Models.Category;
import com.example.tanapone.smartcashier.Models.Product;
import com.example.tanapone.smartcashier.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class SearchProduct extends Fragment implements View.OnClickListener {
    private DatabaseHelperClass myDB;
    private Spinner categorySpinner;
    private ImageButton searchBtn;
    private ImageButton scanButton;
    private EditText searchText;
    private TableLayout listItem;
    private List<Product> list = new ArrayList<Product>();
    private ArrayList<Category> categories = new ArrayList<Category>();
    private String barcodeResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myDB = new DatabaseHelperClass(getActivity());
        categories = myDB.getCategories();
        ArrayList<String> categoryName = new ArrayList<String>();
        categoryName.add(getString(R.string.global_world_all));
        for(Category category : categories){
            categoryName.add(category.getCategoryName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item,categoryName);
        View v = inflater.inflate(R.layout.activity_sharch_product, container, false);
        listItem = v.findViewById(R.id.listItem);
        categorySpinner = (Spinner) v.findViewById(R.id.categorySpinner);
        categorySpinner.setAdapter(arrayAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchProduct("Category");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                allProduct();
            }
        });
        searchText = (EditText) v.findViewById(R.id.searchText);
        searchBtn = (ImageButton) v.findViewById(R.id.searchButton);
        searchBtn.setOnClickListener(this);
        scanButton=(ImageButton) v.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);
        allProduct();
        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchButton :
                searchProduct("Word");
                break;
            case R.id.scanButton :
                scanBarcode();
                break;
        }

    }

    public void allProduct(){
        listItem.removeAllViews();
        TextView headerName = new TextView(getContext());
        TextView headerQty = new TextView(getContext());
        TextView headerDelete = new TextView(getContext());
        TextView headerEdit = new TextView(getContext());


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
        headerDelete.setText(R.string.item_header_delete);
        headerDelete.setTextColor(Color.WHITE);
        headerDelete.setGravity(Gravity.CENTER);


        headerEdit.setPadding(10,10,10,10);
        headerEdit.setBackgroundColor(Color.parseColor("#4DA2DE"));
        headerEdit.setText(R.string.item_header_update);
        headerEdit.setTextColor(Color.WHITE);
        headerEdit.setGravity(Gravity.CENTER);

        TableRow tableRow = new TableRow(getContext());
        tableRow.addView(headerName);
        tableRow.addView(headerQty);
        tableRow.addView(headerDelete);
        tableRow.addView(headerEdit);


        listItem.addView(tableRow);

        List<Product> list = myDB.getProducts();
        for (final Product product :list ){
            TableRow tableRowData = new TableRow(getActivity());
            TextView productName = new TextView(getActivity());
            productName.setGravity(Gravity.CENTER);
            productName.setPadding(14,14,14,14);
            productName.setBackgroundColor(Color.parseColor("#FFFFFF"));
            String pName = product.getProductName();
            if(pName.length()>13){
                productName.setText(pName.substring(0,12)+"...");
            }else{
            productName.setText(product.getProductName());
            }
            TextView productQTY = new TextView(getActivity());
            productQTY.setText(""+product.getProductQuantity());
            productQTY.setGravity(Gravity.CENTER);
            productQTY.setPadding(14,14,14,14);
            productQTY.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ImageView delete = new ImageView(getActivity());
            delete.setImageResource(R.drawable.ic_delete_black_24dp);
            delete.setPadding(7,7,7,7);
            delete.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ImageView upDate = new ImageView(getActivity());
            upDate.setImageResource(R.drawable.ic_build_black_24dp);
            upDate.setPadding(7,7,7,7);
            upDate.setBackgroundColor(Color.parseColor("#FFFFFF"));
            productName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),product.getProductName(),Toast.LENGTH_SHORT).show();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.global_word_delete_confirm);
                    builder.setPositiveButton(R.string.global_word_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            boolean result = myDB.deleteProduct(product);
                            Toast.makeText(getActivity(), ""+result, Toast.LENGTH_SHORT).show();
                            allProduct();
                        }
                    });
                    builder.setNegativeButton(R.string.global_word_no, null);
                    builder.create();
                    builder.show();
                }
            });

            upDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog(product);
                }
            });


            tableRowData.addView(productName);
            tableRowData.addView(productQTY);
            tableRowData.addView(delete);
            tableRowData.addView(upDate);
            listItem.addView(tableRowData);
        }
    }

    public void searchProduct(String condition){
        listItem.removeAllViews();
        TextView headerName = new TextView(getContext());
        TextView headerQty = new TextView(getContext());
        TextView headerDelete = new TextView(getContext());
        TextView headerEdit = new TextView(getContext());

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
        headerDelete.setText(R.string.item_header_delete);
        headerDelete.setTextColor(Color.WHITE);
        headerDelete.setGravity(Gravity.CENTER);

        headerEdit.setPadding(10,10,10,10);
        headerEdit.setBackgroundColor(Color.parseColor("#4DA2DE"));
        headerEdit.setText(R.string.item_header_update);
        headerEdit.setTextColor(Color.WHITE);
        headerEdit.setGravity(Gravity.CENTER);

        TableRow tableRow = new TableRow(getContext());
        tableRow.addView(headerName);
        tableRow.addView(headerQty);
        tableRow.addView(headerDelete);
        tableRow.addView(headerEdit);


        listItem.addView(tableRow);

        if(condition.equals("Barcode")){
            list.clear();
            Product product = new Product();
            product = myDB.searchProductByBarcode(barcodeResult);
            if(product.getProductID() == null){
                Toast.makeText(getActivity(),R.string.global_world_not_found,Toast.LENGTH_SHORT).show();
            }else{
                list.add(myDB.searchProductByBarcode(barcodeResult));
            }

        }else if(condition.equals("Word")) {
            list.clear();
            list = myDB.searchProduct(searchText.getText().toString().trim());
            if (list.size() < 1) {
                Toast.makeText(getActivity(), R.string.global_world_not_found, Toast.LENGTH_SHORT).show();
            }
        }else if(condition.equals("Category")){
                list.clear();
                if(categorySpinner.getSelectedItem().equals(getString(R.string.global_world_all))){
                  list.clear();
                  list = myDB.getProducts();
                }else {
                    list = myDB.getProductByCategory(categories.get(categorySpinner.getSelectedItemPosition()));
                }
            if(list.size()<1){
                Toast.makeText(getActivity(),R.string.global_world_not_found,Toast.LENGTH_SHORT).show();
            }
        }

        for (final Product product :list ){
            TableRow tableRowData = new TableRow(getActivity());
            TextView productName = new TextView(getActivity());
            productName.setGravity(Gravity.CENTER);
            productName.setPadding(14,14,14,14);
            productName.setBackgroundColor(Color.parseColor("#FFFFFF"));
            String pName = product.getProductName();
            if(pName.length()>13){
                productName.setText(pName.substring(0,12)+"...");
            }else{
                productName.setText(product.getProductName());
            }
            TextView productQTY = new TextView(getActivity());
            productQTY.setText(""+product.getProductQuantity());
            productQTY.setGravity(Gravity.CENTER);
            productQTY.setPadding(14,14,14,14);
            productQTY.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ImageView delete = new ImageView(getActivity());
            delete.setImageResource(R.drawable.ic_delete_black_24dp);
            delete.setPadding(7,7,7,7);
            delete.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ImageView upDate = new ImageView(getActivity());
            upDate.setImageResource(R.drawable.ic_build_black_24dp);
            upDate.setPadding(7,7,7,7);
            upDate.setBackgroundColor(Color.parseColor("#FFFFFF"));
            productName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),product.getProductName(),Toast.LENGTH_SHORT).show();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.global_word_delete_confirm);
                    builder.setMessage(product.getProductName());
                    builder.setPositiveButton(R.string.global_word_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            boolean result = myDB.deleteProduct(product);
                            Toast.makeText(getActivity(), ""+result, Toast.LENGTH_SHORT).show();
                            searchProduct("Word");
                        }
                    });
                    builder.setNegativeButton(R.string.global_word_no, null);
                    builder.create();
                    builder.show();
                }
            });

            upDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog(product);
                }
            });

            tableRowData.addView(productName);
            tableRowData.addView(productQTY);
            tableRowData.addView(delete);
            tableRowData.addView(upDate);

            listItem.addView(tableRowData);
        }
    }


    public void scanBarcode(){
        IntentIntegrator.forSupportFragment(this).initiateScan();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getActivity(),"Canceled scan",Toast.LENGTH_SHORT).show();
            } else {
                barcodeResult = result.getContents();
                searchProduct("Barcode");
            }
        }
    }

    public void showEditDialog(final Product product){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_product, null);
        builder.setView(view);
        final EditText barcodeEditText = (EditText) view.findViewById(R.id.barcodeEditText);
        final EditText productNameEditText = (EditText) view.findViewById(R.id.productNameEditText);
        final EditText capitalEditText = (EditText) view.findViewById(R.id.capitalEditText);
        final EditText saleEditText = (EditText) view.findViewById(R.id.saleEditText);
        final Spinner categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        final EditText amountEditText = (EditText) view.findViewById(R.id.amountEditText);
        final EditText minimumEditText = (EditText) view.findViewById(R.id.minimumEditText);

        ArrayList<String> categoryNameArrayList = new ArrayList<String>();
        ArrayList<Category> categories = new ArrayList<Category>();
        categories = myDB.getCategories();


        for(Category category : categories){
            categoryNameArrayList.add(category.getCategoryName());
        }

        int index = 0;

        for(int i = 0;i<categories.size();i++){
            if(categories.get(i).getCategoryName().equals(product.getCategory().getCategoryName())){
                index = i;
            }
        }

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item,categoryNameArrayList);
        categorySpinner.setAdapter(spinnerArrayAdapter);

        barcodeEditText.setText(product.getProductBarcodeID());
        productNameEditText.setText(product.getProductName());
        capitalEditText.setText(String.valueOf(product.getCapitalPrice()));
        saleEditText.setText(String.valueOf(product.getSalePrice()));
        categorySpinner.setSelection(index);
        amountEditText.setText(String.valueOf(product.getProductQuantity()));
        minimumEditText.setText(String.valueOf(product.getMinimum()));


        Button resetBtn = (Button) view.findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeEditText.setText("");
                productNameEditText.setText("");
                capitalEditText.setText("");
                saleEditText.setText("");
                categorySpinner.setSelection(0);
                amountEditText.setText("");
                minimumEditText.setText("");
            }
        });


        Button saveBtn = (Button) view.findViewById(R.id.saveProductBtn);
        final ArrayList<Category> finalCategories = categories;
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(
                        barcodeEditText.getText().toString().trim().equals("") ||
                        productNameEditText.getText().toString().trim().equals("") ||
                                capitalEditText.getText().toString().trim().equals("") ||
                                categorySpinner.getSelectedItem()==null ||
                                saleEditText.getText().toString().trim().equals("") ||
                                categorySpinner.equals("") ||
                                minimumEditText.getText().toString().trim().equals("")
                        )
                    {
                        Toast.makeText(getActivity(),R.string.register_enter_info,Toast.LENGTH_SHORT).show();
                    }else if(Double.parseDouble(saleEditText.getText().toString().trim()) < Double.parseDouble(capitalEditText.getText().toString().trim())) {
                        saleEditText.setError(getString(R.string.add_product_salePrice_must_be_higher_than_capitalPrice));
                }else{
                    String barcodeID = barcodeEditText.getText().toString().trim();
                    Product productEdit = new Product();
                    productEdit.setProductID(product.getProductID());
                    productEdit.setProductBarcodeID(barcodeID);
                    productEdit.setProductName(productNameEditText.getText().toString().trim());
                    productEdit.setCapitalPrice(Double.parseDouble(capitalEditText.getText().toString().trim()));
                    productEdit.setSalePrice(Double.parseDouble(saleEditText.getText().toString().trim()));
                    productEdit.setCategory(finalCategories.get(categorySpinner.getSelectedItemPosition()));
                    productEdit.setProductQuantity(Integer.parseInt(amountEditText.getText().toString().trim()));
                    productEdit.setMinimum(Integer.parseInt(minimumEditText.getText().toString().trim()));
                    boolean result = myDB.updateProduct(productEdit);
                    String resultWord = "";
                    if(result){
                        resultWord = getString(R.string.global_word_success);
                    }else{
                        resultWord = getString(R.string.global_word_failed);
                    }
                    Toast.makeText(getActivity(), resultWord, Toast.LENGTH_SHORT).show();
                    allProduct();
                }
            }
        });
        builder.show();
    }


}
