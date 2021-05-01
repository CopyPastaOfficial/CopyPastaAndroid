package fr.unrealsoftwares.copypasta.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.tools.FragmentInterface;

/**
 * Fragment to add device
 */
public class AddDeviceFragment extends Fragment implements FragmentInterface {

    /**
     * View of the fragment
     */
    private View view;

    /**
     * Name input contains in the layout
     */
    private EditText nameInput;

    /**
     * IP input contains in the layout
     */
    private EditText ipInput;

    /**
     * Add device button contains in the layout
     */
    private Button addButton;

    /**
     * Scan button contains in the layout
     */
    private ExtendedFloatingActionButton scanButton;

    /**
     * Device name
     */
    private String name;

    /**
     * Device ip
     */
    private String ip;

    private Callback mCallback;

    public AddDeviceFragment()
    {
        name = null;
        ip = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_device, container, false);

        initResources();
        initEvents();

        return view;
    }

    /**
     * Init the elements from the layout
     */
    private void initResources() {
        ipInput = view.findViewById(R.id.input_ip);
        nameInput = view.findViewById(R.id.input_name);
        addButton = view.findViewById(R.id.add_button);
        scanButton = view.findViewById(R.id.scan_button);
    }

    /**
     * Init events
     */
    private void initEvents() {
        scanButton.setOnClickListener(v -> mCallback.onScanButtonClic());

        addButton.setOnClickListener(v -> {
            boolean fieldsAreCorrect = true;
            if(ipInput.getText().toString().trim().equals(""))
            {
                ipInput.setError(AddDeviceFragment.this.getString(R.string.add_device_error));
                fieldsAreCorrect = false;
                ipInput.requestFocus();
            }
            if(nameInput.getText().toString().trim().equals(""))
            {
                nameInput.setError(AddDeviceFragment.this.getString(R.string.add_device_error));
                fieldsAreCorrect = false;
                nameInput.requestFocus();
            }
            if(fieldsAreCorrect)
            {
                mCallback.onAddButtonClic(nameInput.getText().toString(), ipInput.getText().toString());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(name != null)
        {
            nameInput.setText(name);
            name = null;
        }
        if(ip != null)
        {
            ipInput.setText(ip);
            ip = null;
        }
    }

    @Override
    public String getName() {
        return "AddDevice";
    }

    @Override
    public int getTitleId() {
        return R.string.add_device_title;
    }

    public interface Callback {
        void onAddButtonClic(String name, String ip);
        void onScanButtonClic();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.createCallbackToParentActivity();
    }

    /**
     * Get callback present from the AddDeviceActivity
     */
    private void createCallbackToParentActivity(){
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (Callback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString()+ " must implement OnButtonClickedListener");
        }
    }

    /**
     * Change name before the display of the fragment
     * @param name Device name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Change IP before the display of the fragment
     * @param ip Device IP
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
}