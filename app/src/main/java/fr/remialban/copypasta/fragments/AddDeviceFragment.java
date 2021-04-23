package fr.remialban.copypasta.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.function.LongFunction;

import fr.remialban.copypasta.R;
import fr.remialban.copypasta.activities.AddDeviceActivity;
import fr.remialban.copypasta.tools.FragmentInterface;

public class AddDeviceFragment extends Fragment implements FragmentInterface {

    View view;

    EditText nameInput;
    EditText ipInput;
    Button addButton;
    ExtendedFloatingActionButton scanButton;
    Boolean cameraEnable;
    String name;
    String ip;

    private OnButtonClickedListener mCallback;

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

    private void initResources() {
        ipInput = view.findViewById(R.id.input_ip);
        nameInput = view.findViewById(R.id.input_name);
        addButton = view.findViewById(R.id.add_button);
        scanButton = view.findViewById(R.id.scan_button);
    }

    private void initEvents() {
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onScanButtonClic();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean fieldsAreCorrect = true;
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

    public interface OnButtonClickedListener {
        public void onAddButtonClic(String name, String ip);
        public void onScanButtonClic();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.createCallbackToParentActivity();
    }

    private void createCallbackToParentActivity(){
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString()+ " must implement OnButtonClickedListener");
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}