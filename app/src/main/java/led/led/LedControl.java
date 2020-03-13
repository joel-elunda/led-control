package led.led;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class LedControl extends AppCompatActivity {


    private Button btnOn;
    private Button btnOff;
    private Button btnDisconnect;

    private SeekBar bright;

    private TextView textLight;
    private String address = null;

    private ProgressDialog progressDialog;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    private boolean isConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_led_control);

        //call the widgtes
        btnOn = (Button) findViewById(R.id.btnOn);
        btnOff = (Button) findViewById(R.id.btnOff);
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        bright = (SeekBar) findViewById(R.id.seekBar);

        textLight = (TextView) findViewById(R.id.textBright);
    }


    //If the btSocket is busy
    private void Disconnect()  {
        if (bluetoothSocket != null)   {
            try  {
                //close connection
                bluetoothSocket.close();
            }
            catch (IOException e) { msg("Error");}
        }
        //return to the first layout
        finish();
        //
    }

    private void turnOffLed()   {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.getOutputStream().write("TF".toString().getBytes());
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnLed()  {
        if (bluetoothSocket != null)  {
            try  {
                bluetoothSocket.getOutputStream().write("TO".toString().getBytes());
            }
            catch (IOException e)  {
                msg("Error");
            }
        }
    }

    private void msg(String str)  {
        Toast.makeText(getApplicationContext(), str ,Toast.LENGTH_LONG).show();
    }

    // UI thread
    private class ConnectBT extends AsyncTask<Void, Void, Void>   {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LedControl.this, "Connecting...", "Please wait!!!");
        }



        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (bluetoothSocket == null || ! isConnected)  {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(address);
                    bluetoothSocket =  dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                }
            }
            catch (IOException e)  {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)  {
            super.onPostExecute(result);
            if ( ! ConnectSuccess ) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else {
                msg("Connected.");
                isConnected = true;
            }
            progressDialog.dismiss();
        }

    }
}
