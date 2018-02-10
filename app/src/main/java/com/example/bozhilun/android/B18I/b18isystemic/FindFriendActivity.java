package com.example.bozhilun.android.B18I.b18isystemic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent: 添加好友
 * @author： 安
 * @crateTime: 2017/9/19 11:30
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class FindFriendActivity extends WatchBaseActivity {
    private static final String TAG = "--FindFriendActivity";
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.number_edit)
    EditText numberEdit;
    @BindView(R.id.frends_list)
    ListView frendsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_frends_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.add_frendes));
        whichDevice();//判断是B18i还是H9
    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        switch (is18i){
            case "B18i":
                break;
            case "H9":
                break;
            case "B15P":
                break;
        }
    }

    @OnClick({R.id.image_back, R.id.scan_text})
    public void onClisk(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.scan_text://搜索
                String content = numberEdit.getText().toString();
                break;
        }
    }


    /**
     * 内部Adapter
     */
    public class FrendsListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }
}
