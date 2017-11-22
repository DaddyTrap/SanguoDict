package cn.org.sanguodict.sanguodict.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import cn.org.sanguodict.sanguodict.R;
import cn.org.sanguodict.sanguodict.adapter.CommonAdapter;
import cn.org.sanguodict.sanguodict.model.DataManager;
import cn.org.sanguodict.sanguodict.model.Moment;
import cn.org.sanguodict.sanguodict.viewholder.ViewHolder;

public class MomentsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CommonAdapter momentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments);

        // Set Toolbar
        toolbar = (Toolbar)findViewById(R.id.activity_moments_toolbar);
        toolbar.setTitle("今日三国");
        toolbar.setNavigationIcon(R.drawable.ic_account_circle_black_24dp);
        toolbar.inflateMenu(R.menu.moments_toolbar_menu);

        // Set RecyclerView
        recyclerView = (RecyclerView)findViewById(R.id.activity_moments_recyclerview);
        momentAdapter = new CommonAdapter<Moment>(this, R.layout.moments_item, DataManager.getInstance().getMoments()) {
            @Override
            public void convert(ViewHolder holder, Moment object) {

            }
        };
    }
}
