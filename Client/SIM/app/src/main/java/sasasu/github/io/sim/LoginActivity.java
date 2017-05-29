package sasasu.github.io.sim;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static sasasu.github.io.sim.MessageService.LoginThread.DEFEAT;
import static sasasu.github.io.sim.MessageService.LoginThread.NETERROR;
import static sasasu.github.io.sim.MessageService.LoginThread.SUCCESS;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private LoginResultReceiver loginResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acrivity);
        Button button = (Button) findViewById(R.id.login_button);
        button.setOnClickListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("sasasu.github.io.sim.broadcast.login_result");
        loginResultReceiver = new LoginResultReceiver();
        registerReceiver(loginResultReceiver, intentFilter);

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MessageService.class);
//        intent.putExtra("username", username.getText().toString());
        // TODO 可换服务器地址
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) findViewById(R.id.login_button);
        button.setEnabled(false);

        Intent LoginIntent = new Intent("sasasu.github.io.sim.broadcast.send");
        LoginIntent.putExtra("ID", 1);
        EditText username = (EditText) findViewById(R.id.login_edit_text_username);
        EditText password = (EditText) findViewById(R.id.login_edit_text_password);
        LoginIntent.putExtra("username", username.getText().toString());
        LoginIntent.putExtra("password", password.getText().toString());
        sendBroadcast(LoginIntent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(loginResultReceiver);
        Intent intent = new Intent("sasasu.github.io.sim.broadcast.send");
        intent.putExtra("ID", -1);
        sendBroadcast(intent);
        super.onDestroy();
    }

    class LoginResultReceiver extends BroadcastReceiver {
        @SuppressLint("CommitPrefEdits")
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("what", NETERROR)) {
                case SUCCESS: {
                    int ID = intent.getIntExtra("ID", -1);
                    MessageService.setID(ID);
                    Toast.makeText(getApplicationContext(), "登录成功 用户ID " + ID, Toast.LENGTH_LONG).show();

                    Intent intent_ext = new Intent();
                    intent_ext.setClass(getApplicationContext(), ListFriendActivity.class);
                    startActivity(intent_ext);
                    break;
                }
                case DEFEAT: {
                    Button button = (Button) findViewById(R.id.login_button);
                    button.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                    break;
                }
                case NETERROR: {
                    Button button = (Button) findViewById(R.id.login_button);
                    button.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "网络错误 ", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }
}

