package com.example.tanapone.smartcashier;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Main");
        setContentView(R.layout.activity_main);
        scanBtn = (ImageView) findViewById(R.id.scanBtn);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        listItem = (TableLayout) findViewById(R.id.listItem);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigation = (NavigationView) findViewById(R.id.mNavigation);
        mNavigation.setNavigationItemSelectedListener(this);
        editText = (EditText) findViewById(R.id.editText);



      scanBtn.setOnClickListener(new View.OnClickListener() {
          @Override
           public void onClick(View view) {
              IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
              integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
              integrator.setPrompt("Scan");
              integrator.setCameraId(0);
              integrator.setBeepEnabled(false);
              integrator.setBarcodeImageEnabled(false);
              integrator.initiateScan();
          }
      });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                setQuantity();
                barcode.add(result.getContents());
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    public void putBarcodeText(){
        listItem.removeAllViews();
        TextView headerBarcode = new TextView(this);
        TextView headerName = new TextView(this);
        TextView headerQty = new TextView(this);
//
        headerBarcode.setPadding(10,10,10,10);
        headerBarcode.setBackgroundColor(Color.parseColor("#4DA2DE"));
        headerBarcode.setText(R.string.item_header_barcode);
        headerBarcode.setTextColor(Color.WHITE);

        headerName.setPadding(10,10,10,10);
        headerName.setBackgroundColor(Color.parseColor("#4DA2DE"));
        headerName.setText(R.string.item_header_name);
        headerName.setTextColor(Color.WHITE);

        headerQty.setPadding(10,10,10,10);
        headerQty.setBackgroundColor(Color.parseColor("#4DA2DE"));
        headerQty.setText(R.string.item_header_qty);
        headerQty.setTextColor(Color.WHITE);

//
        TableRow tableRow = new TableRow(this);
        tableRow.addView(headerBarcode);
        tableRow.addView(headerName);
        tableRow.addView(headerQty);
        listItem.addView(tableRow);

        for(int i = 0; i<barcode.size(); i++){
            TableRow tableRowData = new TableRow(this);
            final TextView itemID = new TextView(this);
            itemID.setText(barcode.get(i).toString());

            final TextView itemName = new TextView(this);
            itemName.setText(name.get(i).toString());

            final TextView itemQty = new TextView(this);
            itemQty.setText(quantity.get(i).toString());
            final int finalI = i;
            itemID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    barcode.remove(finalI);
                    quantity.remove(finalI);
                    name.remove(finalI);
                    putBarcodeText();
                }
            });
            tableRowData.addView(itemID);
            tableRowData.addView(itemName);
            tableRowData.addView(itemQty);
            listItem.addView(tableRowData);
        }

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
                putBarcodeText();
            }
        });
    builder.show();
    }


    public void findBuletooth(){
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
                Toast.makeText(getApplicationContext(),R.string.no_bluetooth_found,Toast.LENGTH_SHORT).show();
            }
            if(!bluetoothAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(),R.string.enable_bluetooth_adapter,Toast.LENGTH_SHORT).show();
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBluetooth);
            }

            final Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if(pairedDevices.size()>0){
                deviceList_bonded.clear();
                for(BluetoothDevice device : pairedDevices){
                    deviceList_bonded.add(device.getName()+'\n'+device.getAddress());
                }

                LayoutInflater inflater=LayoutInflater.from(this);
                dialogView=inflater.inflate(R.layout.bluetooth_select_dialog, null);
                bluetoothListView = (ListView) dialogView.findViewById(R.id.bluetoothListView);
                ArrayAdapter<String> bluetoothListViewAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,deviceList_bonded);
                bluetoothListView.setAdapter(bluetoothListViewAdapter);
                final AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.bluetooth_dialog_title).setView(dialogView).create();
                dialog.show();
                bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
                            bluetoothAdapter.cancelDiscovery();
                        }
                        String deviceName = deviceList_bonded.get(i).substring(0,deviceList_bonded.get(i).length()-18);
                        String deviceAddress = deviceList_bonded.get(i).substring(deviceList_bonded.get(i).length()-18,deviceList_bonded.get(i).length());
                        Toast.makeText(getApplicationContext(),"You selected : "+deviceName,Toast.LENGTH_SHORT).show();
                       // Toast.makeText(getApplicationContext(),"You selected : "+deviceAddress,Toast.LENGTH_SHORT).show();
                        for(BluetoothDevice device : pairedDevices){
                            if(device.getName().equals(deviceName)){
                                bluetoothDevice = device;
                                Log.e("bluetooth","connected");
                                try {
                                    openBT();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                        dialog.cancel();
                    }
                });

            }else{
                Toast.makeText(getApplicationContext(),R.string.no_bluetooth_found,Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            beginListenForData();
            Toast.makeText(getApplicationContext(),R.string.bluetooth_toast_connected,Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
* after opening a connection to bluetooth printer device,
* we have to listen and check if a data were sent to be printed.
*/
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = inputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                inputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                               Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                                                // myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        return dateTime;
    }


    public Bitmap drawText(String text, int textWidth, int color,int size) {

        // Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextSize(size);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Kanit-SemiBold.ttf");
        textPaint.setTypeface(face);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }


    public void printText(String text,int size) {
        try {
            int width=400;
            int colorWhite = Color.WHITE;
            Bitmap bmp = drawText(text,width,colorWhite,size);
            byte[] command = Utils.decodeBitmap(bmp);
            printText(command);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.cashierMenu :
                    printText(editText.getText().toString(),25);
                break;
            case R.id.settingPrinter :
                    findBuletooth();
                break;
        }
        return false;
    }

}
