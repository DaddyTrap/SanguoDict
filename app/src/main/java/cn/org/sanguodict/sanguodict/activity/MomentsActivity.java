package cn.org.sanguodict.sanguodict.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private static final int EDIT_MOMENT_REQ_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments);

        // Set Toolbar
        toolbar = (Toolbar)findViewById(R.id.activity_moments_toolbar);
        toolbarUsericon = (ImageView)findViewById(R.id.activity_moments_toolbar_usericon);
        toolbarCamicon = (ImageView)findViewById(R.id.activity_moments_toolbar_camicon);

        toolbarCamicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MomentsActivity.this, EditMomentActivity.class);
                startActivityForResult(intent, EDIT_MOMENT_REQ_CODE);
            }
        });

        // Set RecyclerView
        SGApplication instance = SGApplication.getInstance();
        momentListRef = instance.getMoments();
        userListRef = instance.getUsers();

        recyclerView = (RecyclerView)findViewById(R.id.activity_moments_recyclerview);
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
                User resUser = null;
                for (User user : userListRef) {
                    if (user.userId == object.fromUser) {
                        resUser = user;
                        break;
                    }
                }
                if (resUser == null) {
                    Log.e("Error", "No such user");
                    return;
                }

                // Set Text
                name.setText(resUser.name);
                time.setText(object.time);
                location.setText(object.location);
                contentText.setText(object.contentText);

                // Set Img
                if (!resUser.avatarBase64.isEmpty()) {
                    byte[] bytes = Base64.decode(resUser.avatarBase64, Base64.DEFAULT);
                    Bitmap avatarBm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    avatar.setImageBitmap(avatarBm);
                } else {
                    Log.i("Info", "No avatar");
                }

                if (!object.contentImgBase64.isEmpty()) {
                    byte[] bytes = Base64.decode(object.contentImgBase64, Base64.DEFAULT);
                    Bitmap contentImgBm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    contentImg.setImageBitmap(contentImgBm);
                } else {
                    // if no image
                    Log.i("Info", "No img");
//                    contentImg.setVisibility(View.INVISIBLE);
                }
            }
        };
        recyclerView.setAdapter(momentAdapter);
    }
}
