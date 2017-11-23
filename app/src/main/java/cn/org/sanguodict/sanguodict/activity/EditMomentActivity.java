package cn.org.sanguodict.sanguodict.activity;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.org.sanguodict.sanguodict.R;
import cn.org.sanguodict.sanguodict.SGApplication;
import cn.org.sanguodict.sanguodict.adapter.CommonAdapter;
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

    private AlertDialog.Builder dialogBuilder;

    SGApplication instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_moments);

        getViews();
        instance = SGApplication.getInstance();

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
        final CommonAdapter<User> adapter = new CommonAdapter<User>(EditMomentActivity.this, R.layout.choose_user_item, instance.getUsers()) {
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
                RecyclerView usersRecyclerView = (RecyclerView) chooseUserView.findViewById(R.id.alertdialog_choose_user_recyclerview);
                usersRecyclerView.setLayoutManager(new LinearLayoutManager(EditMomentActivity.this));
                usersRecyclerView.setAdapter(adapter);

                // Show dialog
                dialogBuilder.create().show();
            }
        });
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
