package xyz.alejandoreba.pointsofinterestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class addPOI extends Activity implements View.OnClickListener {
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi);

        Button bAdd = (Button) findViewById(R.id.btnAddPoi);
        bAdd.setOnClickListener(this);
        Button bBack = (Button) findViewById(R.id.btnBackAddPoi);
        bBack.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        this.lat = bundle.getDouble("poilat");
        this.lon = bundle.getDouble("poilon");

        ((EditText) findViewById(R.id.poilat)).setText(String.valueOf(lat));
        ((EditText) findViewById(R.id.poilon)).setText(String.valueOf(lon));

    }


    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnAddPoi) {
            Intent intent = new Intent();

            Bundle bundle = new Bundle();
            EditText name = (EditText) findViewById(R.id.poiname);
            EditText description = (EditText) findViewById(R.id.poidescription);

            if ((name.getText().toString()).trim().equals("") || (description.getText().toString()).trim().equals("")){
                TextView error = (TextView) findViewById(R.id.addpoierror);
                error.setText("ERROR: Please fill both name and description fields");
            }else {

                bundle.putString("poiname", name.getText().toString().trim());
                bundle.putString("poidescription", description.getText().toString().trim());

                Spinner spinner = (Spinner) findViewById(R.id.poitype);
                String type = spinner.getSelectedItem().toString();
                bundle.putString("poitype", type);

                bundle.putDouble("poilat", this.lat);
                bundle.putDouble("poilon", this.lon);

                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);
                finish();
            }

        }else{
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
