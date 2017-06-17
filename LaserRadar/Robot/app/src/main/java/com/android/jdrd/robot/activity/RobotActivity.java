package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.util.Constant;

import java.util.List;
import java.util.Map;

/**
 * Created by jdrd on 2017/6/16.
 *
 */

public class RobotActivity extends Activity implements View.OnClickListener {
    private RobotDBHelper robotDBHelper;
    private int robotid;
    private Map robotconfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_robot);
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        Intent intent =getIntent();
        robotid = intent.getIntExtra("id",0);


        findViewById(R.id.setting_redact).setOnClickListener(this);
        findViewById(R.id.setting_back).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<Map> robotlist = robotDBHelper.queryListMap("select * from robot where id = '"+ robotid +"'" ,null);
        robotconfig = robotlist.get(0);
        Constant.debugLog("robot"+robotlist.toString());

        List<Map> arealist = robotDBHelper.queryListMap("select * from area where id = '"+ robotconfig.get("area") +"'" ,null);
        if(arealist != null && arealist.size()>0){
            ((TextView)findViewById(R.id.area)).setText(arealist.get(0).get("name").toString());
        }
        ((TextView)findViewById(R.id.name)).setText(robotconfig.get("name").toString());
        ((TextView)findViewById(R.id.ip)).setText(robotconfig.get("ip").toString());
        ((TextView)findViewById(R.id.outline)).setText(robotconfig.get("outline").toString());
        ((TextView)findViewById(R.id.electric)).setText(robotconfig.get("electric").toString());
        ((TextView)findViewById(R.id.state)).setText(robotconfig.get("state").toString());
        ((TextView)findViewById(R.id.robot_state)).setText(robotconfig.get("state").toString());
        ((TextView)findViewById(R.id.command_num)).setText(robotconfig.get("commandnum").toString());
        ((TextView)findViewById(R.id.excute_command)).setText(robotconfig.get("excute").toString());
        ((TextView)findViewById(R.id.excute_time)).setText(robotconfig.get("excutetime").toString());
        ((TextView)findViewById(R.id.command_state)).setText(robotconfig.get("commandstate").toString());
        ((TextView)findViewById(R.id.last_command_state)).setText(robotconfig.get("lastcommandstate").toString());
        ((TextView)findViewById(R.id.last_location)).setText(robotconfig.get("lastlocation").toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_redact:
                Intent intent = new Intent(RobotActivity.this, RobotConfigActivity.class);
                intent.putExtra("id", robotid);
                startActivity(intent);
                break;
            case R.id.setting_back:
                finish();
                break;
        }
    }
}
