package cn.org.sanguodict.sanguodict.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Comparator;
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

    private List<User> mUserListRef;
    private CommonAdapter<SelectableUser> mRoleListAdaper;
    private RecyclerView mRoleListView;

    private TextView mTitle;
    private EditText mSearchInput;

    private ImageView mBackButton, mSearchButton, mAddButton, mSearchBackButton, mMoreButton, mDeleteButton;
    private LinearLayout mSearchBanner;
    private boolean mIsSelecting = false, mSearchEnabled = false;

    private void getViews() {
        mTitle = (TextView) findViewById(R.id.role_list_toolbar_title);
        mBackButton = (ImageView) findViewById(R.id.role_list_bar_icon_back);
        mSearchButton = (ImageView) findViewById(R.id.role_list_bar_icon_search);
        mAddButton = (ImageView) findViewById(R.id.role_list_bar_icon_add);
        mMoreButton = (ImageView) findViewById(R.id.role_list_bar_icon_more);
        mDeleteButton = (ImageView) findViewById(R.id.role_list_bar_icon_delete);

        mSearchBanner = (LinearLayout) findViewById(R.id.role_list_search_panel);
        mSearchBackButton = (ImageView) findViewById(R.id.role_list_search_icon_back);

        mRoleListView = (RecyclerView) findViewById(R.id.role_list_panel);
        mSearchInput = (EditText) findViewById(R.id.role_list_search_input);
    }

    private static final int REQUEST_USER_DETAIL = 10000;

    private void initListView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRoleListView.setLayoutManager(linearLayoutManager);
        mUserListRef = mSgApp.getUsers();
        mSelectableUserListRef = RoleListActivity.convertToSelectableList(mUserListRef);
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
                RoleListActivity.setCheckBox(select, isSelected);

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
                    RoleListActivity.this.setBarIcon(true);
                    RoleListActivity.this.mTitle.setText(R.string.role_list_deleting_title);
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
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "Search Text Changed");
                RoleListActivity.this.searchFilter(s.toString());
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoleListActivity.this.deleteSelected();
            }
        });
        mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(RoleListActivity.this, mMoreButton);
                popup.inflate(R.menu.role_list_delete_popup_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.role_list_popup_select_all:
                                RoleListActivity.this.selectAll();
                                break;
                            case R.id.role_list_popup_unselect_all:
                                RoleListActivity.this.unSelectAll();
                                break;
                            case R.id.role_list_popup_negative_select:
                                RoleListActivity.this.negativeSelect();
                                break;
                        }
                        RoleListActivity.this.mRoleListAdaper.notifyDataSetChanged();
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    private void editTextGetFocus(final EditText text) {
        text.requestFocus();
        text.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager) text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.showSoftInput(text, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        text.setSelection(text.getText().length());
    }

    public static List<SelectableUser> convertToSelectableList(List<User> users) {
        List<SelectableUser> selectableList = new LinkedList<>();
        for (User user : users) {
            selectableList.add(new SelectableUser(false, user));
        }
        return selectableList;
    }

    private void searchFilter(String search) {
        // 恢复现场
        mSelectableUserListRef.clear();
        for (User user : mUserListRef) {
            String gender = "";
            switch (user.gender) {
                case 1:
                    gender = "男";
                    break;
                case 2:
                    gender = "女";
                    break;
            }
            if (user.name.contains(search)
                    || user.force.contains(search)
                    || user.nativePlace.contains(search)
                    || gender.equals(search)) {
                mSelectableUserListRef.add(new SelectableUser(false, user));
            }
        }
        mRoleListAdaper.notifyDataSetChanged();
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
        editTextGetFocus(mSearchInput);
    }

    private void closeSearchLayout() {
        mSearchBanner.setVisibility(View.GONE);
        mSearchEnabled = false;
        mSelectableUserListRef.clear();
        mSelectableUserListRef.addAll(RoleListActivity.convertToSelectableList(mUserListRef));
        mRoleListAdaper.notifyDataSetChanged();
    }

    private void cancelSelect() {
        mIsSelecting = false;
        unSelectAll();
        setBarIcon();
        mTitle.setText(R.string.role_list_title);
        mRoleListAdaper.notifyDataSetChanged();
    }
    private void setBarIcon() {
        setBarIcon(false);
    }

    private void setBarIcon(boolean showSelectMore) {
        int normalIconVisibility = showSelectMore ? View.GONE : View.VISIBLE;
        int selectIconVisibility = showSelectMore ? View.VISIBLE : View.GONE;
        mAddButton.setVisibility(normalIconVisibility);
        mSearchButton.setVisibility(normalIconVisibility);
        mDeleteButton.setVisibility(selectIconVisibility);
        mMoreButton.setVisibility(selectIconVisibility);
    }

    private void selectAll() {
        for (SelectableUser user : mSelectableUserListRef) {
            user.isSelected = true;
        }
    }

    private void unSelectAll() {
        for (SelectableUser user : mSelectableUserListRef) {
            user.isSelected = false;
        }
    }

    private void negativeSelect() {
        for (SelectableUser user : mSelectableUserListRef) {
            user.isSelected = !user.isSelected;
        }
    }

    private void deleteSelected() {
        List<SelectableUser> deleteUsers = new LinkedList<>();
        for (SelectableUser user : mSelectableUserListRef) {
            if (user.isSelected) {
                deleteUsers.add(user);
            }
        }
        for (SelectableUser deleteUser : deleteUsers) {
            deleteOneUser(deleteUser);
        }
        mRoleListAdaper.notifyDataSetChanged();
    }

    private void deleteOneUser(SelectableUser suser) {
        mUserListRef.remove(suser.user);
        mSelectableUserListRef.remove(suser);
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
            mSelectableUserListRef.clear();
            mSelectableUserListRef.addAll(RoleListActivity.convertToSelectableList(mUserListRef));
            mRoleListAdaper.notifyDataSetChanged();
        }
    }

    private static void setCheckBox(final CheckBox checkBox, boolean selected) {
        if (checkBox.isChecked() != selected) {
            checkBox.setChecked(selected);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mUserListRef = mSgApp.getUsers();
        mSelectableUserListRef.clear();
        mSelectableUserListRef.addAll(RoleListActivity.convertToSelectableList(mUserListRef));
        mRoleListAdaper.notifyDataSetChanged();
    }

    static private String TAG = RoleListActivity.class.getCanonicalName();
}
