package com.example.bozhilun.android.B18I.b18isystemic;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.B18I.b18iutils.AppBarStateChangeListener;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @aboutContent: 排行榜
 * @author： 安
 * @crateTime: 2017/9/5 08:57
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18IRankingListActivity extends WatchBaseActivity {
    private final String TAG = "----->>>" + this.getClass().toString();
    @BindView(R.id.rankingRecyle)
    RecyclerView rankingRecyle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_appbar)
    AppBarLayout layoutAppbar;
    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.toobarText)
    TextView toobarText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_ranking_list_layout);
        ButterKnife.bind(this);
        setToobar();
        whichDevice();//判断是B18i还是H9
        setContentViews();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
                startActivity(NewSearchActivity.class);
                finish();
                break;
            case "STATE_TURNING_ON":
                break;
            case "STATE_OFF":
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
                break;
            case "STATE_TURNING_OFF":
                Toast.makeText(this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        whichDevice();//判断是B18i还是H9
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        Log.d(TAG, is18i);
        if (TextUtils.isEmpty(is18i)) finish();
        switch (is18i){
            case "B18i":
                getNetworkDatas();//测试假数据
                break;
            case "H9":
                getNetworkDatas();//测试假数据
                break;
            case "B15P":
                getNetworkDatas();//测试假数据
                break;
        }

    }

    /**
     * toolbar显示与隐藏的设置
     */
    private void setToobar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.backs);
//        toolbar.setTitle("");
        toobarText.setText("排行榜");
        toolbar.setVisibility(View.GONE);
        getSupportActionBar().setTitle("");
        imageBack.setVisibility(View.VISIBLE);
        layoutAppbar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.e(TAG, "B18IRankingListActivity----toolbar状态------STATE" + state.name());
                if (state == State.EXPANDED) {
                    //展开状态
                    toolbar.setVisibility(View.GONE);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    toolbar.setVisibility(View.VISIBLE);
                } else {
                    //中间状态
                    toolbar.setVisibility(View.GONE);
                }
            }
        });
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * rankingRecyle初始设置
     */
    private void setContentViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rankingRecyle.setLayoutManager(linearLayoutManager);
        rankingRecyle.setAdapter(new RankingAdapter());
    }

    /**
     * 获取网络数据
     */
    List<String> stringList;
    List<String> strings;

    private void getNetworkDatas() {

        /****** 假数据 *****/
        stringList = new ArrayList<>();
        strings = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            stringList.add("NO." + i);
            strings.add("Baby 0" + i);
        }
    }

    /**
     * 内部Adapter
     */
    public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHodler> {

        @Override
        public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.b18i_ranking_item, parent, false);
            return new ViewHodler(view);
        }

        @Override
        public void onBindViewHolder(ViewHodler holder, int position) {
            holder.textRanking.setText(stringList.get(position));
            holder.textName.setText(strings.get(position));
            setRankingBackgraoud(holder, position);
        }

        /**
         * 设置排名背景
         *
         * @param holder
         * @param position
         */
        private void setRankingBackgraoud(ViewHodler holder, int position) {
            if (position >= 4) {
//                holder.imageHeadBottom.setBackgroundResource(R.mipmap.no_four);
                holder.imageHeadBottom.setImageResource(R.mipmap.no_four);
                holder.textRanking.setBackgroundResource(R.mipmap.race_no_four);

            } else {
                switch (position) {
                    case 0:
//                        holder.imageHeadBottom.setBackgroundResource(R.mipmap.no_one);
                        holder.imageHeadBottom.setImageResource(R.mipmap.no_one);
                        holder.textRanking.setBackgroundResource(R.mipmap.race_no_one);
                        break;
                    case 1:
//                        holder.imageHeadBottom.setBackgroundResource(R.mipmap.no_two);
                        holder.imageHeadBottom.setImageResource(R.mipmap.no_two);
                        holder.textRanking.setBackgroundResource(R.mipmap.race_no_two);
                        break;
                    case 2:
//                        holder.imageHeadBottom.setBackgroundResource(R.mipmap.no_three);
                        holder.imageHeadBottom.setImageResource(R.mipmap.no_three);
                        holder.textRanking.setBackgroundResource(R.mipmap.race_no_three);
                        break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        public class ViewHodler extends RecyclerView.ViewHolder {
            TextView textRanking, textName, textSteoNumber;
            ImageView imageHead, imageHeadBottom;

            public ViewHodler(View itemView) {
                super(itemView);
                this.textName = (TextView) itemView.findViewById(R.id.text_name);
                this.textRanking = (TextView) itemView.findViewById(R.id.text_ranking);
                this.textSteoNumber = (TextView) itemView.findViewById(R.id.text_stepnumber);
                this.imageHead = (ImageView) itemView.findViewById(R.id.image_head);
                this.imageHeadBottom = (ImageView) itemView.findViewById(R.id.image_head_bottom);
            }
        }
    }
}
