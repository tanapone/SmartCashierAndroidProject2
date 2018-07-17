package com.example.tanapone.smartcashier.Fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tanapone.smartcashier.DatabaseHelper.DatabaseHelperClass;
import com.example.tanapone.smartcashier.MainActivity;
import com.example.tanapone.smartcashier.Models.Category;
import com.example.tanapone.smartcashier.Models.Product;
import com.example.tanapone.smartcashier.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProduct extends Fragment implements View.OnClickListener {

    private DatabaseHelperClass myDB;
    private EditText barcodeEditText;
    private EditText productNameEditText;
    private EditText capitalEditText;
    private EditText saleEditText;
    private Spinner categorySpinner;
    private EditText amountEditText;
    private EditText minimumEditText;
    private ImageView addCategoryBtn;
    private Button resetBtn;
    private Button addProductBtn;
    private ArrayAdapter<String> arrayAdapter;
    private ListView categoriesListview ;
    private Button add_product_category_btn;
    private EditText categoryNameEditText;

    ArrayList<String> categoriesNameArray = new ArrayList<String>();
    ArrayList<Category> categoriesArray = new ArrayList<Category>();
    public AddProduct() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);
        barcodeEditText = (EditText) v.findViewById(R.id.barcodeEditText);
        productNameEditText = (EditText) v.findViewById(R.id.productNameEditText);
        capitalEditText = (EditText) v.findViewById(R.id.capitalEditText);
        categorySpinner = (Spinner) v.findViewById(R.id.categorySpinner);
        saleEditText = (EditText) v.findViewById(R.id.saleEditText);
        amountEditText = (EditText) v.findViewById(R.id.amountEditText);
        minimumEditText = (EditText) v.findViewById(R.id.minimumEditText);
        resetBtn = (Button) v.findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);
        addProductBtn = (Button)v.findViewById(R.id.addProductBtn);
        addProductBtn.setOnClickListener(this);
        barcodeEditText.setOnClickListener(this);
        addCategoryBtn = (ImageView) v.findViewById(R.id.addCategoryBtn);
        addCategoryBtn.setOnClickListener(this);
        myDB = new DatabaseHelperClass(getActivity());

       // Toast.makeText(getActivity(),myDB.getStore().toString(),Toast.LENGTH_SHORT).show();
        if(myDB.foundData("Categories")){
            setCategoriesArray();
        }
        return v;
    }

    public void scanBarcode(){
        IntentIntegrator.forSupportFragment(this).setBeepEnabled(true).initiateScan();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getActivity(),"Canceled scan",Toast.LENGTH_SHORT).show();
            } else {
                String toast = "Scanned : " + result.getContents();
                Toast.makeText(getActivity(),toast,Toast.LENGTH_SHORT).show();
                barcodeEditText.setText(result.getContents().toString());
            }
        }
    }


    public void setCategories() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.categories, null);
        builder.setView(view);
        add_product_category_btn  = (Button) view.findViewById(R.id.add_product_category_btn);
        add_product_category_btn.setOnClickListener(this);
        categoryNameEditText = (EditText) view.findViewById(R.id.categoryNameEditText);

        categoriesListview = (ListView) view.findViewById(R.id.categoriesListview);
        categoriesListview.setAdapter(arrayAdapter);
        categoriesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                myDB.deleteCategory(categoriesArray.get(i));
                                setCategoriesArray();
                                categoriesListview.setAdapter(arrayAdapter);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.global_word_delete_confirm).setPositiveButton(R.string.global_word_yes, dialogClickListener)
                        .setNegativeButton(R.string.global_word_no, dialogClickListener).show();
            }
        });

        builder.setPositiveButton(R.string.set_quantity_dialog_submitBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void resetBtn(){
        barcodeEditText.setText("");
        productNameEditText.setText("");
        capitalEditText.setText("");
        saleEditText.setText("");
        amountEditText.setText("");
        Toast.makeText(getActivity(),R.string.register_reseted,Toast.LENGTH_SHORT).show();
    }

    public void addCategory(){
       if(categoryNameEditText.getText().toString().trim().equals("")){
           categoryNameEditText.setError(getString(R.string.add_product_category_EditText_null_alert));
       }else{
           Category category = new Category();
           category.setCategoryName(categoryNameEditText.getText().toString());
           boolean result = myDB.addCategory(category);
           String resultWord = "";
           if(result){
               resultWord = getString(R.string.global_word_success);
           }else{
               resultWord = getString(R.string.global_word_failed);
           }
           Toast.makeText(getActivity(), resultWord, Toast.LENGTH_SHORT).show();
       }
    }

    public void setCategoriesArray(){
        categoriesNameArray.clear();
        categoriesArray.clear();
        for(Category category :myDB.getCategories()){
            categoriesNameArray.add(category.getCategoryName());
            categoriesArray.add(category);
        }
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item,categoriesNameArray);
        categorySpinner.setAdapter(arrayAdapter);
    }

    public void addProduct(){
        if(
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
            String barcodeID = "";
            if(barcodeEditText.getText().toString().trim().equals("")){
                String uuid = UUID.randomUUID().toString();
                barcodeID = uuid;
             }else{
                barcodeID = barcodeEditText.getText().toString().trim();
            }
            Product product = new Product();
            product.setProductBarcodeID(barcodeID);
            product.setProductName(productNameEditText.getText().toString().trim());
            product.setCapitalPrice(Double.parseDouble(capitalEditText.getText().toString().trim()));
            product.setSalePrice(Double.parseDouble(saleEditText.getText().toString().trim()));
            product.setCategory(categoriesArray.get(categorySpinner.getSelectedItemPosition()));
            product.setProductQuantity(Integer.parseInt(amountEditText.getText().toString().trim()));
            product.setMinimum(Integer.parseInt(minimumEditText.getText().toString().trim()));
            boolean result = myDB.addProduct(product);
            String resultWord = "";
            if(result){
                resultWord = getString(R.string.global_word_success);
            }else{
                resultWord = getString(R.string.global_word_failed);
            }
            Toast.makeText(getActivity(),resultWord,Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.barcodeEditText :
                scanBarcode();
                break;
            case R.id.addCategoryBtn :
                setCategories();
                break;
            case R.id.resetBtn :
                resetBtn();
                break;
            case R.id.add_product_category_btn :
                addCategory();
                setCategoriesArray();
                categoriesListview.setAdapter(arrayAdapter);
                break;
            case R.id.addProductBtn :
                addProduct();
                break;
        }
    }
}
