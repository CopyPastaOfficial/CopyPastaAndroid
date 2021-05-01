package fr.unrealsoftwares.copypasta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.fragments.AddDeviceFragment;
import fr.unrealsoftwares.copypasta.fragments.AddDeviceQrCodeFragment;
import fr.unrealsoftwares.copypasta.tools.FragmentInterface;

public class AddDeviceActivity extends AppCompatActivity implements AddDeviceFragment.Callback, AddDeviceQrCodeFragment.AddDeviceQrCodeFragmentCallback {

    /**
     * Activity toolbar
     */
    private Toolbar toolbar;

    /**
     * Frame layout contains fragments
     */
    private FrameLayout frameLayout;

    private AddDeviceFragment addDeviceFragment;
    private AddDeviceQrCodeFragment addDeviceQrCodeFragment;


    FragmentInterface parentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        initResources();
        initToolbar();
        init();
    }

    /**
     * Init the elements from the layout
     */
    private void initResources()
    {
        toolbar = findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.frame_layout);
    }

    /**
     * Initialization
     */
    private void init() {
        addDeviceFragment = new AddDeviceFragment();
        addDeviceQrCodeFragment = new AddDeviceQrCodeFragment();
        changeFragment(addDeviceFragment);
    }

    /**
     * Init toolbar
     */
    private void initToolbar()
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Change fragment of the FrameLayout
     * @param fragment Fragment to show
     */
    private void changeFragment(FragmentInterface fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, (Fragment) fragment).commit();
        toolbar.setTitle(getString(fragment.getTitleId()));
        if(fragment.getName().equals("AddDevice"))
        {
            parentFragment = null;
        } else
        {
            parentFragment = addDeviceFragment;
        }
    }

    /**
     * Called by a fragment when the user add a button
     * @param name Device name
     * @param ip Device IP
     * @see AddDeviceFragment
     */
    @Override
    public void onAddButtonClic(String name, String ip) {
        Intent intent = new Intent();
        intent.putExtra("name", name);
        intent.putExtra("ip", ip);
        setResult(1,intent);
        finish();
    }

    /**
     * Function called when clic on scan button to scan QR Code on computer
     */
    @Override
    public void onScanButtonClic() {
        changeFragment(addDeviceQrCodeFragment);
    }

    /**
     * Called after scanning the QR Code
     * @param name Device name
     * @param ip Device IP
     */
    @Override
    public void onQrCodeScanned(String name, String ip) {
        addDeviceFragment.setName(name);
        addDeviceFragment.setIp(ip);
        changeFragment(addDeviceFragment);
    }

    /**
     * Called when back button is called
     */
    @Override
    public void onBackPressed() {
        if(parentFragment == null)
        {
            finish();
        } else
        {
            changeFragment(parentFragment);
        }
    }
}