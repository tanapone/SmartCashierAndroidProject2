package com.example.tanapone.smartcashier;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.tanapone.smartcashier.Fragment.AddProduct;
import com.example.tanapone.smartcashier.Fragment.CashierFragment;
import com.example.tanapone.smartcashier.Fragment.SearchProduct;
import com.example.tanapone.smartcashier.Printer.PrinterHelper;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigation;
    private ImageView scanBtn;
    private LinearLayout listItem;
    private String qtyValue = "";
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<String> deviceList_bonded=new ArrayList<String>();//bonded list
    ListView bluetoothListView;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private OutputStream outputStream;
    private InputStream inputStream;
    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;
    private Thread workerThread;
    private View dialogView;
    private ArrayList<String> barcode = new ArrayList<String>();
    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<Integer> quantity = new ArrayList<Integer>();
    private EditText editText;
    private FragmentTransaction tx;
    private PrinterHelper printerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Main");
        setContentView(R.layout.activity_main);
        tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment, new CashierFragment());
        tx.commit();

     //   scanBtn = (ImageView) findViewById(R.id.scanBtn);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
     //   listItem = (TableLayout) findViewById(R.id.listItem);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigation = (NavigationView) findViewById(R.id.mNavigation);
        mNavigation.setNavigationItemSelectedListener(this);
       // editText = (EditText) findViewById(R.id.editText);
        printerHelper = new PrinterHelper(this);

    }

   @Override
   public void onBackPressed() {
      return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void setQuantity() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.set_quantity_dialog, null);
        builder.setView(view);
        final EditText quantityEditText = (EditText) view.findViewById(R.id.qtyEditText);
        builder.setPositiveButton(R.string.set_quantity_dialog_submitBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                qtyValue = quantityEditText.getText().toString().trim();
                if (qtyValue == null || qtyValue.equals("") || qtyValue.charAt(0) == '0') {
                    qtyValue = String.valueOf(1);
                }
                quantity.add(Integer.parseInt(qtyValue));
                System.out.println("Size of quantity : "+quantity.size());
                name.add("ทดสอบ");
            }
        });
    builder.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        switch (id){
            case R.id.addProductMenu :
                getSupportActionBar().setTitle("Add Product");
                AddProduct addProduct = new AddProduct();
                fragmentManager.beginTransaction().replace(R.id.fragment,addProduct).commit();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.cashierMenu :
                getSupportActionBar().setTitle("Cashier");
                CashierFragment cashierFragment = new CashierFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment,cashierFragment).commit();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.settingPrinter :
                printerHelper.findBuletooth();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.searchProductMenu :
                getSupportActionBar().setTitle("Search Product");
                SearchProduct searchProduct = new SearchProduct();
                fragmentManager.beginTransaction().replace(R.id.fragment,searchProduct).commit();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.signoutMenu :
                finish();
                System.exit(0);
                break;
        }
        return false;
    }


}
