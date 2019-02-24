package com.example.cristhian.aplicativos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myutils.AbiDni;
import com.example.myutils.Lectora;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView estado;
    EditText editDNI;
    EditText editPNombre;
    EditText editApellido;
    EditText editGrupoV;
    EditText editUbigeo;
    EditText editSexo;
    BroadcastReceiver mUsbAttachReceiver,mUsbDetachReceiver;
    Lectora lectora;
    AbiDni abiDni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        estado=findViewById(R.id.Estado);
        editDNI=findViewById(R.id.editDNI);
        editApellido=findViewById(R.id.editApellido);
        editPNombre=findViewById(R.id.editNombre);
        editGrupoV =findViewById(R.id.editGrupoV);
        editUbigeo=findViewById(R.id.editUbigeo);
        editSexo=findViewById(R.id.editSexo);
        lectora=new Lectora(getBaseContext());
        abiDni=new AbiDni();

        if (lectora.existeDispositivoConectado()){
            lectora.getLectoraParams();
            EsperarDatos2 esperarDatos= new EsperarDatos2();
            esperarDatos.execute();
        }

        mUsbAttachReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                try {
                    if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                        lectora.getLectoraParams();
                        estado.setText("Dispositivo conectado");
                        EsperarDatos2 esperarDatos= new EsperarDatos2();
                        esperarDatos.execute();
                    }
                }catch (Exception e){
                    Toast.makeText(context, "Dispositivo no identificado:"+e.toString()+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        };

        mUsbDetachReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        estado.setText("Conecte Lectora");
                        //  esperarLectora.onCancelled();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbAttachReceiver , filter);
        filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbDetachReceiver , filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbDetachReceiver);
        unregisterReceiver(mUsbAttachReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            limpiar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void limpiar(){
        editDNI.setText("");
        editApellido.setText("");
        editPNombre.setText("");
        editSexo.setText("");
        editGrupoV.setText("");
        editUbigeo.setText("");
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_offline) {
            limpiar();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class EsperarDatos2 extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            int aux=1;
            do {
                aux=lectora.EsperarTarjetaConectada();
                abiDni=lectora.getAbiDni();
                publishProgress();
            }while (aux!=0);
            return false;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        editDNI.setText(abiDni.getDNI()+"-"+abiDni.getValDni());
                        editApellido.setText(abiDni.getPApellido()+" "+abiDni.getSApellido());
                        editPNombre.setText(abiDni.getPNombre());
                        editSexo.setText(abiDni.getSexo());
                        editGrupoV.setText(abiDni.getGrupoVotacion());
                        editUbigeo.setText(abiDni.getUbigeoActual());
                    }catch (Exception e){

                    }

                }
            });


        }
    }
}
