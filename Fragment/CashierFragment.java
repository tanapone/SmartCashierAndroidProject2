package com.example.tanapone.smartcashier.Fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanapone.smartcashier.DatabaseHelper.DatabaseHelperClass;
import com.example.tanapone.smartcashier.MainActivity;
import com.example.tanapone.smartcashier.Models.Category;
import com.example.tanapone.smartcashier.Models.Order;
import com.example.tanapone.smartcashier.Models.Product;
import com.example.tanapone.smartcashier.Models.Store;
import com.example.tanapone.smartcashier.Printer.PrinterHelper;
import com.example.tanapone.smartcashier.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CashierFragment extends Fragment implements View.OnClickListener {

    private DatabaseHelperClass myDB;
    private ImageButton scanButton;
    private EditText searchText;
    private ImageButton searchButton;
    private TableLayout listItem;
    private ArrayList<String> categoriesName = new ArrayList<String>();
    private ArrayList<Category> categories = new ArrayList<Category>();
    private ArrayAdapter<String> arrayAdapter;
    private String barcodeResult;
    private List<Product> list = new ArrayList<Product>();
    private List<Product> products = new ArrayList<Product>();
    private List<Product> orderProduct = new ArrayList<Product>();
    private boolean selected = false;
    private TextView totalPriceTextView;
    private ImageButton printerImgBtn;
    private PrinterHelper printerHelper;
    private Button saveOrderBtn;
    private Button resetBtn;
    private double totalPrice=0;
    private Button moneyChange;
    public CashierFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        printerHelper = new PrinterHelper(getActivity());
        View v = inflater.inflate(R.layout.fragment_cashier, container, false);
        myDB = new DatabaseHelperClass(getActivity());
        products = myDB.getProducts();
        listItem = v.findViewById(R.id.listItem);
        scanButton = (ImageButton) v.findViewById(R.id.scanButton);
        searchText = (EditText) v.findViewById(R.id.searchText);
        searchButton = (ImageButton) v.findViewById(R.id.searchButton);
        totalPriceTextView = (TextView) v.findViewById(R.id.TotalTextView);
        printerImgBtn = (ImageButton) v.findViewById(R.id.printerImgBtn);
        printerImgBtn.setOnClickListener(this);
        saveOrderBtn = (Button) v.findViewById(R.id.saveOrderBtn);
        saveOrderBtn.setOnClickListener(this);
        resetBtn = (Button) v.findViewById(R.id.resetBtn);
        moneyChange = (Button) v.findViewById(R.id.moneyChange);
        moneyChange.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        totalPriceTextView.setText(getText(R.string.total_price_textView)+" : 0 ");
        categories = myDB.getCategories();
        categoriesName.add(getString(R.string.global_world_all));
        for(Category category : categories){
            categoriesName.add(category.getCategoryName());
        }
        searchButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
        //
        clearTableLayout();
        //
        return v;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scanButton :
                scanBarcode();
                break;
            case R.id.searchButton :
                searchProduct("Word");
                break;
            case R.id.printerImgBtn :
                if(orderProduct.size()<1){
                    Toast.makeText(getActivity(),getString(R.string.product_select),Toast.LENGTH_SHORT).show();
                }else {
                    printOrder();
                }
                break;
            case R.id.saveOrderBtn:
                saveOrder();
                break;
            case R.id.resetBtn :
                orderProduct.clear();
                products.clear();
                list.clear();
                products = myDB.getProducts();
                clearTableLayout();
                totalPrice = 0;
                totalPriceTextView.setText(getString(R.string.total_price_textView)+" "+String.valueOf(totalPrice));
                break;
            case R.id.moneyChange:
                checkBill();
                break;
        }
    }

    public void clearTableLayout(){
        listItem.removeAllViews();
        TextView headerName = new TextView(getContext());
        TextView headerQty = new TextView(getContext());
        TextView headerPrice = new TextView(getContext());
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

        headerPrice.setPadding(10,10,10,10);
        headerPrice.setBackgroundColor(Color.parseColor("#4DA2DE"));
        headerPrice.setText(R.string.item_header_price);
        headerPrice.setTextColor(Color.WHITE);
        headerPrice.setGravity(Gravity.CENTER);

        headerDelete.setPadding(10,10,10,10);
        headerDelete.setBackgroundColor(Color.parseColor("#4DA2DE"));
        headerDelete.setText(R.string.item_header_delete);
        headerDelete.setTextColor(Color.WHITE);
        headerDelete.setGravity(Gravity.CENTER);

        TableRow tableRow = new TableRow(getContext());
        tableRow.addView(headerName);
        tableRow.addView(headerQty);
        tableRow.addView(headerPrice);
        tableRow.addView(headerDelete);

        listItem.addView(tableRow);
    }
    public void searchProduct(String condition){
        if(condition.equals("Word")){
            list.clear();
            list = myDB.searchProduct(searchText.getText().toString().trim());
            if (list.size() < 1) {
                Toast.makeText(getActivity(), R.string.global_world_not_found, Toast.LENGTH_SHORT).show();
            }else{

                for(int i=0;i<products.size();i++){
                    for(int j=list.size()-1;j>=0;j--){
                        if(list.get(j).getProductID().equals(products.get(i).getProductID())){
                            list.get(j).setProductQuantity(products.get(i).getProductQuantity());
                        }
                    }
                }

                showSelectProductDialog(list);
            }
        }else if(condition.equals("Barcode")){
            list.clear();
            Product product = new Product();
            product = myDB.searchProductByBarcode(barcodeResult);
            if(product.getProductID() == null){
                Toast.makeText(getActivity(),R.string.global_world_not_found,Toast.LENGTH_SHORT).show();
            }else{
                list.add(myDB.searchProductByBarcode(barcodeResult));
                for(int i=0;i<products.size();i++){
                    for(int j=list.size()-1;j>=0;j--){
                        if(list.get(j).getProductID().equals(products.get(i).getProductID())){
                            list.get(j).setProductQuantity(products.get(i).getProductQuantity());
                        }
                    }
                }
                showSelectProductDialog(list);
            }

        }

    }

    public void showSelectProductDialog(List<Product> productArrayList){
        ArrayList<String> productNameArrayList = new ArrayList<String>();
        productNameArrayList.clear();
        for(Product product : productArrayList){
            productNameArrayList.add(product.getProductName()+
                    "\n"+getString(R.string.product_stock)+
                    " "+product.getProductQuantity());
        }
        LayoutInflater inflater;
      View dialogView;
      ListView productListView;
        inflater = LayoutInflater.from(getActivity());
        dialogView = inflater.inflate(R.layout.product_select_dialog, null);
        productListView = (ListView) dialogView.findViewById(R.id.productListView);
        ArrayAdapter<String> productListViewAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,productNameArrayList);
        final AlertDialog selectProductdialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.product_select)
                .setView(dialogView).create();
        productListView.setAdapter(productListViewAdapter);
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i=0;i<products.size();i++){
                    if(list.get(position).getProductID().equals(products.get(i).getProductID())){
                        addOrder(products.get(i));
                        selectProductdialog.dismiss();
                        break;
                    }
                }
            }
        });
        selectProductdialog.show();
    }

    public void addOrder(final Product product){
        LayoutInflater inflater;
        final View dialogView;
        inflater = LayoutInflater.from(getActivity());
        dialogView = inflater.inflate(R.layout.set_quantity_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.set_quantity_dialog_title)
                .setPositiveButton(getString(R.string.set_quantity_dialog_submitBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Check quantitiy
                        int quantity = 0;
                        EditText quantityEditText = (EditText) dialogView.findViewById(R.id.qtyEditText);

                        if(quantityEditText.getText().toString().trim().equals("")){
                            quantity = 1;
                        }else{
                            quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
                        }
                        //Check Stock less then quantity
                        if(quantity > product.getProductQuantity()){
                            Toast.makeText(getActivity(),getString(R.string.product_stock_less_then_quantity),Toast.LENGTH_SHORT).show();
                        }else{
                            //Update stock
                            for(int i =0;i<products.size();i++){
                                if(product.getProductID().equals(products.get(i).getProductID())){
                                    int qty = products.get(i).getProductQuantity() - quantity;
                                    products.get(i).setProductQuantity(qty);
                                    break;
                                }
                            }
                            Product productInOrder = new Product();
                            productInOrder.setProductID(product.getProductID());
                            productInOrder.setProductName(product.getProductName());
                            productInOrder.setProductQuantity(quantity);
                            productInOrder.setCategory(product.getCategory());
                            productInOrder.setCapitalPrice(product.getCapitalPrice());
                            productInOrder.setSalePrice(product.getSalePrice());
                            productInOrder.setMinimum(product.getMinimum());

                            //Check same products
                            boolean found = false;
                            for(int i=0;i<orderProduct.size();i++){
                                if(product.getProductID().equals(orderProduct.get(i).getProductID())){
                                    found = true;
                                    //Update new quantity
                                    int newQty = Integer.valueOf(orderProduct.get(i).getProductQuantity()) + quantity;
                                    Product updateQuantityProduct = new Product();
                                    updateQuantityProduct = orderProduct.get(i);
                                    updateQuantityProduct.setProductQuantity(newQty);
                                    orderProduct.set(i,updateQuantityProduct);
                                }
                            }
                            if(found) {
                                dialog.dismiss();
                                addProductInOrder();
                            }else{
                                orderProduct.add(productInOrder);
                                dialog.dismiss();
                                addProductInOrder();
                            }
                            }
                        }
                })
                .setView(dialogView).create();
        dialog.show();
    }

    public void addProductInOrder(){
        totalPrice = 0;
        clearTableLayout();
        int index = 0;
        for(final Product product : orderProduct){
            TableRow tableRowData = new TableRow(getActivity());
            TextView productName = new TextView(getActivity());

            productName.setPadding(14,14,14,14);
            productName.setBackgroundColor(Color.parseColor("#FFFFFF"));

            String pName = product.getProductName();
            if(pName.length()>13){
                productName.setText(pName.substring(0,12)+"...");
            }else{
                productName.setText(pName);
            }
            productName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),product.getProductName(),Toast.LENGTH_SHORT).show();
                }
            });
            productName.setGravity(Gravity.CENTER);
            TextView productQuantity = new TextView(getActivity());
            productQuantity.setPadding(14,14,14,14);
            productQuantity.setBackgroundColor(Color.parseColor("#FFFFFF"));
            productQuantity.setText(String.valueOf(product.getProductQuantity()));
            productQuantity.setGravity(Gravity.CENTER);

            TextView productPrice = new TextView(getActivity());
            productPrice.setText(String.valueOf(product.getSalePrice()));
            productPrice.setPadding(14,14,14,14);
            productPrice.setBackgroundColor(Color.parseColor("#FFFFFF"));

            productPrice.setGravity(Gravity.CENTER);
            ImageView trash = new ImageView(getActivity());
            trash.setImageResource(R.drawable.ic_delete_black_24dp);
            trash.setPadding(6,6,6,6);
            trash.setBackgroundColor(Color.parseColor("#FFFFFF"));

            final int finalIndex = index;
            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Update stock
                                    for(int i=0;i<products.size();i++){
                                        if(orderProduct.get(finalIndex).getProductID().equals(products.get(i).getProductID())){
                                            int qty = orderProduct.get(finalIndex).getProductQuantity();
                                            int newQty = products.get(i).getProductQuantity() + qty;
                                            products.get(i).setProductQuantity(newQty);
                                        }
                                    }
                                    orderProduct.remove(finalIndex);
                                    addProductInOrder();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.global_word_delete_confirm))
                            .setMessage(product.getProductName())
                            .setPositiveButton(R.string.global_word_yes, dialogClickListener)
                            .setNegativeButton(R.string.global_word_no, dialogClickListener).show();
                }
            });
            tableRowData.addView(productName);
            tableRowData.addView(productQuantity);
            tableRowData.addView(productPrice);
            tableRowData.addView(trash);
            listItem.addView(tableRowData);
            index++;
            totalPrice += (product.getSalePrice()*product.getProductQuantity());
        }
        totalPriceTextView.setText(getText(R.string.total_price_textView)+" : "+String.valueOf(totalPrice));
    }
    public void saveOrder(){
        boolean result = false;
        int orderID = 0;
        if(!myDB.foundData("Orders")){
            orderID = 1;
        }else{
            orderID = Integer.parseInt(myDB.getLastOrderID());
            orderID+=1;
        }
        Order order = new Order();
        order.setOrderID(String.valueOf(orderID));
        Date date = new Date();
        order.setOrderDate(date);
        order.setOrderProduct((ArrayList<Product>) orderProduct);
        result = myDB.addOrder(order);
        if(result){
            Toast.makeText(getActivity(),getString(R.string.global_word_success),Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(),getString(R.string.global_word_failed),Toast.LENGTH_SHORT).show();
        }
    }

    public void printOrder(){
        if(printerHelper.connectedBluetooth()==false){
            printerHelper.findBuletooth();
        }else {
            Store store = new Store();
            store = myDB.getStore();
            printerHelper.resetPrint();
            printerHelper.setCenter();
            printerHelper.printText(store.getStoreName(), 25, "center");
            printerHelper.printText("-----------------------------------------", 20, "center");
            int orderID = 0;
            if(!myDB.foundData("Orders")){
                orderID = 1;
            }else{
                orderID = Integer.parseInt(myDB.getLastOrderID());
                orderID+=1;
            }
            printerHelper.printText(getString(R.string.order_id)+" : "+orderID, 25, "center");
            printerHelper.printText(getString(R.string.productName_list), 25, "left");
            for (Product product : orderProduct) {
                printerHelper.printText(product.getProductName() + " x" + product.getProductQuantity(), 20, "left");
                printerHelper.printText(getString(R.string.item_header_price) + " " + product.getSalePrice() + " " + getString(R.string.printer_per_unit), 20, "opppsite");
                printerHelper.printNewLine();
                printerHelper.printNewLine();
            }
            printerHelper.printText("-----------------------------------------", 20, "center");
            printerHelper.printText(String.valueOf(totalPriceTextView.getText().toString().trim()), 25, "opppsite");
            printerHelper.printText(getString(R.string.global_world_date) + " " + printerHelper.getDate(), 25, "center");
            printerHelper.printText(getString(R.string.contact_us), 25, "center");
            if(!store.getStoreFB().equals("")) {
                printerHelper.printText(getString(R.string.register_store_facebook) + " : " + store.getStoreFB(), 25, "left");
            }
            printerHelper.printText(getString(R.string.register_store_pnumber) + " : " + store.getStorePhoneNumber(), 25, "left");
            printerHelper.printText(getString(R.string.register_store_email) + " : " + store.getStoreEmail(), 25, "left");
        }
    }

    public void checkBill(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.money_change);
        dialog.setTitle(R.string.money_change);
        final TextView totalView = (TextView) dialog.findViewById(R.id.totalView);
        final EditText checkBill = (EditText) dialog.findViewById(R.id.checkBill);
        final TextView checkBill2 = (TextView) dialog.findViewById(R.id.Bill);
        Button buttonCancel = (Button) dialog.findViewById(R.id.button);
        Button buttonLogin = (Button) dialog.findViewById(R.id.button2);
        totalView.setText(getString(R.string.total_price_textView)+" : "+totalPrice);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBill.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.register_enter_info), Toast.LENGTH_SHORT).show();
                } else {
                    Double bill = Double.parseDouble(checkBill.getText().toString());
                    if (bill >= totalPrice) {
                        Double d = bill - totalPrice;
                        checkBill2.setText(getString(R.string.money_change) + " : " + d);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.money_chage_less), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
    }
}
