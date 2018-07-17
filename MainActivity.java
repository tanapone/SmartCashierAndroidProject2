package com.example.tanapone.smartcashier;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.tanapone.smartcashier.DatabaseHelper.DatabaseHelperClass;
import com.example.tanapone.smartcashier.Fragment.AddProduct;
import com.example.tanapone.smartcashier.Fragment.CashierFragment;
import com.example.tanapone.smartcashier.Fragment.Dashboard;
import com.example.tanapone.smartcashier.Fragment.EditStoreFragment;
import com.example.tanapone.smartcashier.Fragment.Minimum;
import com.example.tanapone.smartcashier.Fragment.SearchProduct;
import com.example.tanapone.smartcashier.Models.Category;
import com.example.tanapone.smartcashier.Models.Product;
import com.example.tanapone.smartcashier.Models.Store;
import com.example.tanapone.smartcashier.Printer.PrinterHelper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private DatabaseHelperClass myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new DatabaseHelperClass(this);
        getSupportActionBar().setTitle("Main");
        setContentView(R.layout.activity_main);
        tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment, new CashierFragment()
        );
        tx.commit();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigation = (NavigationView) findViewById(R.id.mNavigation);
        mNavigation.setNavigationItemSelectedListener(this);
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
            case R.id.searchProductMenu :
                getSupportActionBar().setTitle("Search Product");
                SearchProduct searchProduct = new SearchProduct();
                fragmentManager.beginTransaction().replace(R.id.fragment,searchProduct).commit();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.backupMenu :
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getString(R.string.global_do_you_want_to)+" "+getString(R.string.menu_backup_data)+" ?");
                builder.setPositiveButton(R.string.global_word_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        backupDialog();
                    }
                });
                builder.setNegativeButton(R.string.global_word_no, null);
                builder.create();
                builder.show();
                break;
            case R.id.importMenu :
                retrieveData ();
                break;
            case R.id.settingMenu :
                getSupportActionBar().setTitle("Edit store");
                EditStoreFragment editStoreFragment = new EditStoreFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment,editStoreFragment).commit();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.dashboardMenu :
                getSupportActionBar().setTitle("Dashboard");
                Dashboard dashboard = new Dashboard();
                fragmentManager.beginTransaction().replace(R.id.fragment,dashboard).commit();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.lowProductMenu :
                getSupportActionBar().setTitle("Minimum");
                Minimum Minimum = new Minimum();
                fragmentManager.beginTransaction().replace(R.id.fragment,Minimum).commit();
                mDrawerLayout.closeDrawers();
                break;
        }
        return false;
    }

    public void backupDialog(){

                    List<Product> list = myDB.getProducts();
                    List<Category> list12 =myDB.getCategories();
                    String password = UUID.randomUUID().toString();
                    Firebase.setAndroidContext(MainActivity.this);
                    Firebase fb = new Firebase("https://androidproject-21500.firebaseio.com/");
                    Firebase table = fb.child("Store/"+password);
                    for (Product p:list){
                        Firebase stu1 = table.child("Product/"+p.getProductID()); //??? DB PK ???? key ??? Firebase
                        stu1.child("ProductID").setValue(p.getProductID());
                        stu1.child("ProductBarcodeID").setValue(p.getProductBarcodeID());
                        stu1.child("ProductName").setValue(p.getProductName());
                        stu1.child("ProductQuantity").setValue(p.getProductQuantity());
                        stu1.child("CategoryID").setValue(p.getCategory().getCategoryID());
                        stu1.child("CapitalPrice").setValue(p.getCapitalPrice());
                        stu1.child("SalePrice").setValue(p.getSalePrice());
                        stu1.child("Minimum").setValue(p.getMinimum());
                    }
                    for(Category c: list12){
                        Category stu3 = new Category(c.getCategoryID(),c.getCategoryName());
                        table.child("Category/"+c.getCategoryID()).setValue(stu3);

                    }
                    Toast.makeText(getApplicationContext(),getString(R.string.menu_backup_data)+" "+getString(R.string.global_word_success),Toast.LENGTH_SHORT).show();
                    sendMail(password);

            }



    public void retrieveData () {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.import_dialog, null);
        builder.setView(view);
        builder.setTitle(getString(R.string.menu_import_data));
        final EditText password = (EditText) view.findViewById(R.id.passwordRetrieveData);
        Button buttonCancel = (Button) view.findViewById(R.id.retrieveData_cancel);
        Button buttonLogin = (Button) view.findViewById(R.id.button_retrieveData);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setText("");
                builder.setCancelable(true);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!password.getText().toString().trim().equals("")) {
                    retrieveCategory(password.getText().toString().trim());
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.register_enter_info),Toast.LENGTH_SHORT).show();
                }
            }


        });

        builder.show();

    }

    //????????????category
    public void retrieveCategory(final String password){
        Firebase.setAndroidContext(MainActivity.this);
        final Firebase fb2 = new Firebase("https://androidproject-21500.firebaseio.com/");
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage(getString(R.string.menu_import_data)+" "+getString(R.string.add_product_category_TextView)+"...");
        pd.show();
        Firebase table = fb2.child("Store/"+password+"/Category");
        Query query2 = table.orderByKey();
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(getApplicationContext(),getString(R.string.global_world_not_found),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }else {
                    myDB.deleteTable();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d("test", ds.getKey() + " => " + ds.getValue());
                        Firebase table2 = fb2.child("Store/" + password + "/Category/" + ds.getKey());
                        Query query = table2.orderByKey();
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int i = 0;
                                String id = null;
                                String name = null;
                                for (DataSnapshot ds2 : dataSnapshot.getChildren()) {
                                    if (i == 0) {
                                        id = ds2.getValue(String.class);
                                    } else {
                                        name = ds2.getValue(String.class);
                                    }
                                    i++;
                                }
                                Category category = new Category(id, name);
                                myDB.retriveCategory(category);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                    }
                    pd.dismiss();
                    retrieveProduct(password);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    //????????????Product
    public void retrieveProduct(final String password){
        Firebase.setAndroidContext(MainActivity.this);
        final Firebase fb2 = new Firebase("https://androidproject-21500.firebaseio.com/");
        Firebase table = fb2.child("Store/"+password+"/Product");
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage(getString(R.string.menu_import_data)+" "+getString(R.string.productName_list)+"...");
        Query query2 = table.orderByKey();
        pd.show();
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("test", ds.getKey() + " => " + ds.getValue());
                    Firebase table2 = fb2.child("Store/"+password+"/Product/"+ds.getKey());
                    Query query = table2.orderByKey();
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int i=0;
                            Double capitalPrice=0.0;
                            Category category = null;
                            int minimum=0;
                            String ProductBarcodeID=null;
                            String ProductID = null;
                            String ProductName=null;
                            int ProductQuantity=0;
                            Double SalePrice=0.0;
                                for (DataSnapshot ds2 : dataSnapshot.getChildren()) {

                                    if (i == 0) {
                                        capitalPrice = ds2.getValue(Double.class);
                                    } else if (i == 1) {
                                        category = myDB.getCategoryByID(ds2.getValue(Integer.class));
                                    } else if (i == 2) {
                                        minimum = ds2.getValue(Integer.class);
                                    } else if (i == 3) {
                                        ProductBarcodeID = ds2.getValue(String.class);
                                    } else if (i == 4) {
                                        ProductID = ds2.getValue(String.class);
                                    } else if (i == 5) {
                                        ProductName = ds2.getValue(String.class);
                                    } else if (i == 6) {
                                        ProductQuantity = ds2.getValue(Integer.class);

                                    } else if (i == 7) {
                                        SalePrice = ds2.getValue(Double.class);

                                    }
                                    i++;
                                }
                                Product product = new Product(ProductID, ProductBarcodeID, ProductName, ProductQuantity, category, capitalPrice, SalePrice, minimum);
                                myDB.retriveProduct(product);

                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void sendMail(String backupPassword){
        Store store = new Store();
        store = myDB.getStore();
        String message = "Your password is : "+backupPassword;
        String subject = store.getStoreName()+" Backup password";
        BackgroundMail.newBuilder(this)
                .withUsername("nongtoeyproject@gmail.com")
                .withPassword("minecrafttmmk")
                .withMailto(store.getStoreEmail())
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject(subject)
                .withBody(message)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),getString(R.string.backup_sent_password_to_email),Toast.LENGTH_SHORT).show();
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();
    }

}
