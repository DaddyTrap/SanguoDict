package cn.org.sanguodict.sanguodict.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import cn.org.sanguodict.sanguodict.R;
import cn.org.sanguodict.sanguodict.SGApplication;
import cn.org.sanguodict.sanguodict.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQ_CODE = 1002;

    private CircleImageView image = null;
    private EditText name = null;
    private Spinner gender = null;
    private EditText born = null;
    private EditText die = null;
    private EditText native_place = null;
    private EditText power = null;
    private TextView cancel = null;
    private TextView save = null;

    private int id = -1;
    private User user = new User();

    private SGApplication instance;

    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();

        id = intent.getExtras().getInt("id");
        Log.d("id", "" + id);

        Log.d("EditActivity", "Start");
        initial();
    }

    private void initial() {
        instance = SGApplication.getInstance();
        getViews();
        getInfo();
        initialClickEvent();
    }

    private void getViews() {
        image = (CircleImageView) findViewById(R.id.image);
        name = (EditText) findViewById(R.id.name_edit);
        gender = (Spinner) findViewById(R.id.gender_edit);
        born = (EditText) findViewById(R.id.born);
        die = (EditText) findViewById(R.id.die);
        native_place = (EditText) findViewById(R.id.native_place_edit);
        power = (EditText) findViewById(R.id.power_edit);
        cancel = (TextView) findViewById(R.id.cancel);
        save = (TextView) findViewById(R.id.save);
    }

    private void getInfo() {
        if (id >= 0) {
            user = instance.findUserWithId(id);
            name.setText(user.name);
            if (user.gender == 1) {
                gender.setSelection(0);
            } else if (user.gender == 2) {
                gender.setSelection(1);
            }
            image.setImageBitmap(instance.getBitmap(user.avatarBase64));
            born.setText(user.birthDate);
            die.setText(user.deathDate);
            native_place.setText(user.nativePlace);
            power.setText(user.force);
        }
    }

    private void initialClickEvent() {

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.name = name.getText().toString();
                user.birthDate = born.getText().toString();
                user.deathDate = die.getText().toString();
                user.nativePlace = native_place.getText().toString();
                user.force = power.getText().toString();
                if (id < 0) {
                    instance.addUser(user);
                } else {
                    instance.updateUser(id, user);
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQ_CODE);
            }
        });

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    user.gender = 1;
                } else if (position == 2) {
                    user.gender = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQ_CODE) {
            Log.i("Info", "Picked an image");
            if (resultCode == RESULT_OK) {
                Uri imgUri = data.getData();
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(imgUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // Save base64 result in moment
                byte[] bytes = null;
                try {
                    bytes = getBytes(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                user.avatarBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

                // Set preview image
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bm);
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (instance != null) instance.saveEverything();
    }
}
