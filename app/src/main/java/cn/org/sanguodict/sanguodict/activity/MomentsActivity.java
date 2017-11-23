package cn.org.sanguodict.sanguodict.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.org.sanguodict.sanguodict.R;
import cn.org.sanguodict.sanguodict.adapter.CommonAdapter;
import cn.org.sanguodict.sanguodict.SGApplication;
import cn.org.sanguodict.sanguodict.model.Moment;
import cn.org.sanguodict.sanguodict.model.User;
import cn.org.sanguodict.sanguodict.viewholder.ViewHolder;

public class MomentsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView toolbarUsericon;
    private ImageView toolbarCamicon;
    private RecyclerView recyclerView;
    private CommonAdapter momentAdapter;

    private List<Moment> momentListRef;
    private List<User> userListRef;

    private AlertDialog.Builder delMomentDialogBuilder;

    public static final int EDIT_MOMENT_REQ_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments);

        getViews();
        setupDialogBuilder();

        // Set Toolbar Cam
        toolbarCamicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Info", "Clicked camicon, go to edit moment activity");
                Intent intent = new Intent(MomentsActivity.this, EditMomentActivity.class);
                startActivityForResult(intent, EDIT_MOMENT_REQ_CODE);
            }
        });

        // Set Toolbar User
        toolbarUsericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Info", "Clicked usericon, go to user list activity");
                // TODO: Click usericon will go to user list activity
            }
        });

        // Set RecyclerView
        final SGApplication instance = SGApplication.getInstance();
        momentListRef = instance.getMoments();
        userListRef = instance.getUsers();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        momentAdapter = new CommonAdapter<Moment>(this, R.layout.moments_item, momentListRef) {
            @Override
            public void convert(ViewHolder holder, Moment object) {
                ImageView avatar = holder.getView(R.id.moments_item_avatar);
                TextView name = holder.getView(R.id.moments_item_name);
                TextView time = holder.getView(R.id.moments_item_time);
                TextView location = holder.getView(R.id.moments_item_location);
                TextView contentText = holder.getView(R.id.moments_item_content_text);
                ImageView contentImg = holder.getView(R.id.moments_item_content_img);

                // Get "from whom"
                User resUser = instance.findUserWithId(object.fromUser);

                if (resUser == null) {
                    Log.e("Error", "No such user");
                    return;
                }

                // Avatar/Name clicked event
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("Info", "Clicked avatar/name, go to user detail page");
                        // TODO: Go to user detail
                    }
                };
                name.setOnClickListener(listener);
                avatar.setOnClickListener(listener);

                // Set Text
                name.setText(resUser.name);
                time.setText(object.time);
                location.setText(object.location);
                contentText.setText(object.contentText);

                // Set Img
                if (!resUser.avatarBase64.isEmpty()) {
                    avatar.setImageBitmap(instance.getBitmap(resUser.avatarBase64));
                } else {
                    Log.i("Info", "No avatar");
                }

                if (!object.contentImgBase64.isEmpty()) {
                    contentImg.setImageBitmap(instance.getBitmap(object.contentImgBase64));
                } else {
                    // if no image
                    Log.i("Info", "No img");
                    contentImg.setVisibility(View.INVISIBLE);
                }
            }
        };
        momentAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {}

            @Override
            public void onLongClick(final int position) {
                Log.i("Info", "LongClicked moment item, ask if delete it");
                delMomentDialogBuilder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Info", "Chosen yes, delete it");
                        MomentsActivity.this.momentListRef.remove(position);
                        momentAdapter.notifyItemRemoved(position);
                    }
                });
                delMomentDialogBuilder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Info", "Chosen no, do nothing");
                    }
                });
                delMomentDialogBuilder.create().show();
            }
        });
        recyclerView.setAdapter(momentAdapter);
    }

    private void getViews() {
        toolbar = (Toolbar)findViewById(R.id.activity_moments_toolbar);
        toolbarUsericon = (ImageView)findViewById(R.id.activity_moments_toolbar_usericon);
        toolbarCamicon = (ImageView)findViewById(R.id.activity_moments_toolbar_camicon);
        recyclerView = (RecyclerView)findViewById(R.id.activity_moments_recyclerview);
    }

    private void setupDialogBuilder() {
        delMomentDialogBuilder = new AlertDialog.Builder(this);
        delMomentDialogBuilder.setTitle("删除朋友圈");
        delMomentDialogBuilder.setMessage("是否确定删除该朋友圈？");

    }
}
