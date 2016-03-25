package cn.studyou.autoscrollbanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import cn.studyou.library.view.BannerLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> urls = new ArrayList<>();
        urls.add("http://a.hiphotos.baidu.com/image/pic/item/b3fb43166d224f4a58fcfc540ef790529822d114.jpg");
        urls.add("http://att2.citysbs.com/hangzhou/sns01/forum/2010/10/09-10/20101009_42172338386c23dddb03zQ695asbeKu4.jpg");
        urls.add("http://e.hiphotos.baidu.com/image/pic/item/14ce36d3d539b600be63e95eed50352ac75cb7ae.jpg");
        urls.add("http://f.hiphotos.baidu.com/image/pic/item/242dd42a2834349b7eaf886ccdea15ce37d3beaa.jpg");
        urls.add("http://b.hiphotos.baidu.com/image/pic/item/9922720e0cf3d7ca021ac950f71fbe096b63a92c.jpg");

        BannerLayout banner = (BannerLayout) findViewById(R.id.banner);
        //网络地址
        banner.setViewUrls(urls);
        //本地资源
//        banner.setViewRes(viewRes);
        //添加点击监听
        banner.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
