package cn.org.sanguodict.sanguodict.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import cn.org.sanguodict.sanguodict.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    private CircleImageView image = null;
    private EditText name = null;
    private EditText gender = null;
    private EditText born = null;
    private EditText die = null;
    private EditText native_place = null;
    private EditText power = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getViews();
    }

    private void getViews() {
        image = (CircleImageView) findViewById(R.id.image);
        name = (EditText) findViewById(R.id.name_edit);
        gender = (EditText) findViewById(R.id.gender_edit);
        born = (EditText) findViewById(R.id.born);
        die = (EditText) findViewById(R.id.die);
        native_place = (EditText) findViewById(R.id.native_place_edit);
        power = (EditText) findViewById(R.id.power_edit);
    }
}
