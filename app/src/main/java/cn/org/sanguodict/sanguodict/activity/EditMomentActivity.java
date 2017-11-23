package cn.org.sanguodict.sanguodict.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.midi.MidiInputPort;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import cn.org.sanguodict.sanguodict.R;
import cn.org.sanguodict.sanguodict.SGApplication;
import cn.org.sanguodict.sanguodict.adapter.CommonAdapter;
import cn.org.sanguodict.sanguodict.model.Moment;
import cn.org.sanguodict.sanguodict.model.User;
import cn.org.sanguodict.sanguodict.viewholder.ViewHolder;

public class EditMomentActivity extends AppCompatActivity {

    private ImageView toolbarBack;
    private TextView toolbarName;
    private ImageView toolbarAvatar;
    private ImageView toolbarChange;

    private EditText contentEditText;
    private RelativeLayout addphotoButton;
    private ImageView afterAddphoto;

    private EditText locationEditText;
    private EditText timeEditText;
    private Button postButton;

    private User thisUser;
    private Moment momentForRes;

    private List<User> userSearchRes;

    private AlertDialog.Builder dialogBuilder;

    private SGApplication instance;

    private static final int PICK_IMAGE_REQ_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_moments);

        getViews();
        instance = SGApplication.getInstance();
        momentForRes = new Moment();
        userSearchRes = (List<User>) ((LinkedList<User>) instance.getUsers()).clone();

        // Set back button
        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMomentActivity.this.finish();
            }
        });

        // Set current user
        thisUser = instance.getCurrentUser();
        updateUserView();

        // Set up alert dialog builder
        dialogBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final CommonAdapter<User> adapter = new CommonAdapter<User>(EditMomentActivity.this, R.layout.choose_user_item, userSearchRes) {
            @Override
            public void convert(ViewHolder holder, User object) {
                TextView name = holder.getView(R.id.choose_user_item_name);
                ImageView avatar = holder.getView(R.id.choose_user_item_avatar);
                name.setText(object.name);
                avatar.setImageBitmap(instance.getBitmap(object.avatarBase64));
            }
        };
        adapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                User user = instance.getUsers().get(position);
                instance.setCurrentUserId(user.userId);
                EditMomentActivity.this.thisUser = user;
                Log.i("Info", "Chosen user id: " + user.userId);
                updateUserView();
            }

            @Override
            public void onLongClick(int position) {}
        });

        // Set change icon
        toolbarChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // View should be get here, otherwise, it will crash
                final View chooseUserView = inflater.inflate(R.layout.alertdialog_choose_user, null);

                dialogBuilder.setView(chooseUserView);

                // RecyclerView in alertdialog
                final RecyclerView usersRecyclerView = (RecyclerView) chooseUserView.findViewById(R.id.alertdialog_choose_user_recyclerview);
                usersRecyclerView.setLayoutManager(new LinearLayoutManager(EditMomentActivity.this));
                usersRecyclerView.setAdapter(adapter);

                // Search edit text in alertdialog
                final EditText searchEditText = (EditText)chooseUserView.findViewById(R.id.alertdialog_choose_user_search_edittext);
                searchEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.i("Info", "Text changed");
                        userSearchRes.clear();
                        userSearchRes.addAll(instance.searchUserWithPartOfName(s.toString()));
                        adapter.notifyDataSetChanged();
                    }
                });

                // Show dialog
                dialogBuilder.create().show();
            }
        });

        // Set add photo button
        View.OnClickListener selectPhotoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQ_CODE);
            }
        };
        addphotoButton.setOnClickListener(selectPhotoListener);
        afterAddphoto.setOnClickListener(selectPhotoListener);

        // Set post button
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                momentForRes.contentText = contentEditText.getText().toString();
                momentForRes.fromUser = thisUser.userId;
                momentForRes.location = locationEditText.getText().toString();
                momentForRes.time = timeEditText.getText().toString();

//                Intent data = new Intent();
//                data.putExtra("moment", momentForRes);
                instance.setTempObj(momentForRes);
                setResult(RESULT_OK);
                finish();
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
                momentForRes.contentImgBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

                // Set preview image
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                afterAddphoto.setImageBitmap(bm);
                afterAddphoto.setVisibility(View.VISIBLE);
                addphotoButton.setVisibility(View.INVISIBLE);
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

    private void getViews() {
        toolbarBack = (ImageView) findViewById(R.id.activity_edit_moments_toolbar_back);
        toolbarName = (TextView) findViewById(R.id.activity_edit_moments_toolbar_name);
        toolbarAvatar = (ImageView) findViewById(R.id.activity_edit_moments_toolbar_avatar);
        toolbarChange = (ImageView) findViewById(R.id.activity_edit_moments_toolbar_change_icon);

        contentEditText = (EditText) findViewById(R.id.activity_edit_moments_content_edittext);
        addphotoButton = (RelativeLayout) findViewById(R.id.activity_edit_moments_addphoto);
        afterAddphoto = (ImageView) findViewById(R.id.activity_edit_moments_addphoto_after);

        locationEditText = (EditText) findViewById(R.id.activity_edit_moments_location_edittext);
        timeEditText = (EditText) findViewById(R.id.activity_edit_moments_time_edittext);
        postButton = (Button) findViewById(R.id.activity_edit_moments_post_button);
    }

    private void updateUserView() {
        toolbarName.setText(thisUser.name);
        toolbarAvatar.setImageBitmap(instance.getBitmap(thisUser.avatarBase64));
    }
}
