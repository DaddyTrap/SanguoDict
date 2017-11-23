package cn.org.sanguodict.sanguodict.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.INotificationSideChannel;
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
import android.widget.Toast;

import java.io.Serializable;
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
    private TextView toolbarTitle;
    private RecyclerView recyclerView;
    private CommonAdapter momentAdapter;

    private List<Moment> momentListRef;
    private List<User> userListRef;

    private AlertDialog.Builder delMomentDialogBuilder;
    private AlertDialog.Builder noPermissionDialogBuilder;

    public static final int EDIT_MOMENT_REQ_CODE = 1001;

    public static final int REQ_PERMISSION_CODE = 101;

    public static final String READ_PERMISSION = "android.permission.READ_EXTERNAL_STORAGE";

    private LinearLayoutManager layoutManager;

    private SGApplication instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SGApplication.getInstance().requestPermission(this, READ_PERMISSION, REQ_PERMISSION_CODE))
            initEveryThing();

        // Init nothing here because permission hasn't been got
    }

    static boolean begOnce = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // ok
            initEveryThing();
            // Set permission
            SGApplication.getInstance().hasReadStoragePermission = true;
        } else {
            // No permission got
            Log.i("Info", "No permission got");
            initEveryThing();
            if (begOnce) return;
            begOnce = true;
            noPermissionDialogBuilder.create().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_MOMENT_REQ_CODE) {
            Log.i("Info", "Result from edit moment activity");
            if (resultCode == RESULT_OK) {
//                Moment moment = (Moment)data.getSerializableExtra("moment");
                Moment moment = (Moment) instance.getTempObj();
                Log.i("Info", "Result OK, Got: " + moment.toString());
//                momentListRef.add(moment);
                instance.addMoment(moment);
                momentAdapter.notifyItemInserted(momentListRef.size() - 1);
                recyclerView.smoothScrollToPosition(momentListRef.size() - 1);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Info", "On Pause, should save everything");
        instance.saveEverything();
    }

    // Util Functions

    private void initEveryThing() {
        setContentView(R.layout.activity_moments);

        getViews();
        setupDialogBuilder();
        instance = SGApplication.getInstance();

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

        // Set toolbar title -- Just for debug
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            int counter = 0;
            @Override
            public void onClick(View v) {
                ++counter;
                if (counter % 20 == 0) {
                    instance._debugDontSave = false;
                    Toast.makeText(MomentsActivity.this, "DEBUG: 不保存数据", Toast.LENGTH_SHORT).show();
                } else if (counter % 10 == 0) {
                    instance._debugDontSave = true;
                    Toast.makeText(MomentsActivity.this, "DEBUG: 保存数据", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set RecyclerView
        momentListRef = instance.getMoments();
        userListRef = instance.getUsers();

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

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
                if (resUser != null && !resUser.avatarBase64.isEmpty()) {
                    avatar.setImageBitmap(instance.getBitmap(resUser.avatarBase64));
                } else {
                    Log.i("Info", "No avatar");
                }

                if (object.contentImgBase64 != null && !object.contentImgBase64.isEmpty()) {
                    contentImg.setImageBitmap(instance.getBitmap(object.contentImgBase64));
                } else {
                    // if no image
                    Log.i("Info", "No img, set visibility -> gone");
                    contentImg.setVisibility(View.GONE);
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
        toolbarTitle = (TextView)findViewById(R.id.activity_moments_toolbar_title);
        recyclerView = (RecyclerView)findViewById(R.id.activity_moments_recyclerview);
    }

    private void setupDialogBuilder() {
        delMomentDialogBuilder = new AlertDialog.Builder(this);
        delMomentDialogBuilder.setTitle("删除朋友圈");
        delMomentDialogBuilder.setMessage("是否确定删除该朋友圈？");

        noPermissionDialogBuilder = new AlertDialog.Builder(this);
        noPermissionDialogBuilder.setTitle("没有给权限？");
        noPermissionDialogBuilder.setMessage("这可能会造成该应用的运行异常，如不能选择图片等");
        noPermissionDialogBuilder.setPositiveButton("给你权限", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SGApplication.getInstance().requestPermission(MomentsActivity.this, READ_PERMISSION, REQ_PERMISSION_CODE))
                    initEveryThing();
            }
        });
        noPermissionDialogBuilder.setNegativeButton("滚", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
    }
}
