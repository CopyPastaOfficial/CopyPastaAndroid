package fr.unrealsoftwares.copypasta.activities;

import androidx.annotation.NonNull;
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

    private String currentFragment;

    FragmentInterface parentFragment;

    private Integer count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        initResources();
        initToolbar();
        init(savedInstanceState);
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
    private void init(Bundle savedInstanceState) {
        addDeviceFragment = new AddDeviceFragment();
        addDeviceQrCodeFragment = new AddDeviceQrCodeFragment();

        if(savedInstanceState != null)
        {
            currentFragment = savedInstanceState.getString("currentFragment");
        }
        count = 0;
        changeFragment(null);

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
        count++;
        if(fragment == null)
        {
            if(currentFragment == null)
            {
                fragment = addDeviceQrCodeFragment;
            }
            if(addDeviceFragment.getName().equals(currentFragment))
            {
                fragment = addDeviceFragment;
            }
            if(addDeviceQrCodeFragment.getName().equals(currentFragment))
            {
                fragment = addDeviceQrCodeFragment;
            }
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, (Fragment) fragment, fragment.getName()).commit();
        toolbar.setTitle(getString(fragment.getTitleId()));
        currentFragment = fragment.getName();
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
    public void onAddButtonClick(String name, String ip) {
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
    public void onScanButtonClick() {
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
        if(parentFragment == null || count <=1)
        {
            finish();
        } else
        {
            changeFragment(parentFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentFragment", currentFragment);
    }
}