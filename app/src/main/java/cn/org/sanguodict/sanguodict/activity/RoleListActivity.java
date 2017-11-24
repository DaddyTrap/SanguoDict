package cn.org.sanguodict.sanguodict.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import cn.org.sanguodict.sanguodict.R;
import cn.org.sanguodict.sanguodict.SGApplication;
import cn.org.sanguodict.sanguodict.adapter.CommonAdapter;
import cn.org.sanguodict.sanguodict.model.User;
import cn.org.sanguodict.sanguodict.viewholder.ViewHolder;

public class RoleListActivity extends AppCompatActivity {

    static private class SelectableUser  {
        public boolean isSelected;
        public User user;
        public Bitmap avatarBitmap;
        public SelectableUser(boolean isSelected, User user) {
            this.isSelected = isSelected;
            this.user = user;
            if (user.avatarBase64 != null && !user.avatarBase64.isEmpty()) {
                this.avatarBitmap = RoleListActivity.mSgApp.getBitmap(user.avatarBase64);
            } else {
                Log.w(TAG, "No avatar of user " + user.userId + ". Use default.");
                this.avatarBitmap = null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_list);
        getViews();
        initListener();
        initListView();
    }

    private static SGApplication mSgApp = SGApplication.getInstance();

    private List<SelectableUser> mSelectableUserListRef;
    private CommonAdapter<SelectableUser> mRoleListAdaper;

    private RecyclerView mRoleListView;

    private ImageView mBackButton, mSearchButton, mAddButton, mSearchBackButton;
    private LinearLayout mSearchBanner;
    private boolean mIsSelecting = false, mSearchEnabled = false;

    private void getViews() {
        mBackButton = (ImageView) findViewById(R.id.role_list_bar_icon_back);
        mSearchButton = (ImageView) findViewById(R.id.role_list_bar_icon_search);
        mAddButton = (ImageView) findViewById(R.id.role_list_bar_icon_add);

        mSearchBanner = (LinearLayout) findViewById(R.id.role_list_search);
        mSearchBackButton = (ImageView) findViewById(R.id.role_list_search_icon_back);

        mRoleListView = (RecyclerView) findViewById(R.id.role_list_panel);
    }

    private static final int REQUEST_USER_DETAIL = 10000;

    private void initListView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRoleListView.setLayoutManager(linearLayoutManager);

        mSelectableUserListRef = RoleListActivity.convertToSelectableList(mSgApp.getUsers());
        mRoleListAdaper = new CommonAdapter<SelectableUser>(this, R.layout.role_list_item, mSelectableUserListRef) {
            @Override
            public void convert(ViewHolder holder, final SelectableUser selectableUser) {
                boolean isSelected = selectableUser.isSelected;
                User user = selectableUser.user;
                final CheckBox select = holder.getView(R.id.role_list_item_check);
                ImageView avatar = holder.getView(R.id.role_list_item_avatar);
                TextView name = holder.getView(R.id.role_list_item_name);

                name.setText(user.name);
                if (selectableUser.avatarBitmap != null) {
                    avatar.setImageBitmap(selectableUser.avatarBitmap);
                }
                if (!RoleListActivity.this.mIsSelecting) {
                    select.setVisibility(View.GONE);
                    return;
                } else {
                    select.setVisibility(View.VISIBLE);
                }
                select.setChecked(isSelected);
                if (select.hasOnClickListeners()) return;
                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectableUser.isSelected = select.isChecked();
                    }
                });
            }
        };
        mRoleListAdaper.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                SelectableUser selectedUser = RoleListActivity.this.mSelectableUserListRef.get(position);
                if (RoleListActivity.this.mIsSelecting) {
                    selectedUser.isSelected = !selectedUser.isSelected;
                    RoleListActivity.this.mRoleListAdaper.notifyDataSetChanged();
                } else {
                    int userId = selectedUser.user.userId;
                    RoleListActivity.this.sendUserDetailIntent(userId);
                }
            }

            @Override
            public void onLongClick(int position) {
                if (!RoleListActivity.this.mIsSelecting) {
                    RoleListActivity.this.mIsSelecting = true;
                    RoleListActivity.this.mRoleListAdaper.notifyDataSetChanged();
                }
            }
        });
        mRoleListView.setAdapter(mRoleListAdaper);
    }

    private void initListener() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RoleListActivity.this.mIsSelecting) {
                    cancelSelect();
                } else {
                    RoleListActivity.this.finish();
                }
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoleListActivity.this.sendUserDetailIntent(-1);
            }
        });
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoleListActivity.this.showSearchLayout();
            }
        });
        mSearchBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoleListActivity.this.closeSearchLayout();
            }
        });
    }

    public static List<SelectableUser> convertToSelectableList(List<User> users) {
        List<SelectableUser> selectableList = new LinkedList<>();
        for (User user : users) {
            selectableList.add(new SelectableUser(false, user));
        }
        return selectableList;
    }

    private void showSearchLayout() {
        if (mIsSelecting) {
            cancelSelect();
            Log.i(TAG, "Start search and cancal all selected item. May not occur.");
        }
        if (mSearchEnabled) {
            Log.e(TAG, "Search layout invoke twice.");
            return;
        }
        mSearchEnabled = true;
        mSearchBanner.setVisibility(View.VISIBLE);
    }

    private void closeSearchLayout() {
        mSearchBanner.setVisibility(View.GONE);
        mSearchEnabled = false;
    }

    private void cancelSelect() {
        mIsSelecting = false;
        for (SelectableUser user : mSelectableUserListRef) {
            user.isSelected = false;
        }
        mRoleListAdaper.notifyDataSetChanged();
    }

    private void sendUserDetailIntent(int userId) {
        // 发送添加数据请求
        Intent intent = new Intent(RoleListActivity.this, EditActivity.class);
        intent.putExtra("id", userId);
        startActivityForResult(intent, REQUEST_USER_DETAIL);
    }

    @Override
    public void onBackPressed() {
        if (mSearchEnabled) {
            closeSearchLayout();
        } else if (mIsSelecting) {
            cancelSelect();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_USER_DETAIL && resultCode == RESULT_OK) {
            mRoleListAdaper.notifyDataSetChanged();
        }
    }

    static private String TAG = RoleListActivity.class.getCanonicalName();
}
