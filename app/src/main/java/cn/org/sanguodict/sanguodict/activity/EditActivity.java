package cn.org.sanguodict.sanguodict.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    private TextView cancel = null;
    private TextView save = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getViews();
        initialClickEvent();
    }

    private void getViews() {
        image = (CircleImageView) findViewById(R.id.image);
        name = (EditText) findViewById(R.id.name_edit);
        gender = (EditText) findViewById(R.id.gender_edit);
        born = (EditText) findViewById(R.id.born);
        die = (EditText) findViewById(R.id.die);
        native_place = (EditText) findViewById(R.id.native_place_edit);
        power = (EditText) findViewById(R.id.power_edit);
        cancel = (TextView) findViewById(R.id.cancel);
        save = (TextView) findViewById(R.id.save);
    }

    private void initialClickEvent() {

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
