package sasasu.github.io.sim;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChatActivity";
    int ID;
    int FriendID;
    ChatLogReceiver chatLogReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        ID = intent.getIntExtra("ID", -1);
        FriendID = intent.getIntExtra("FriendID", -1);

        Button button = (Button) findViewById(R.id.chat_button);
        button.setOnClickListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("sasasu.github.io.sim.broadcast.new_message_ready");
        chatLogReceiver = new ChatLogReceiver();
        registerReceiver(chatLogReceiver, intentFilter);
        Intent intent1 = new Intent("sasasu.github.io.sim.broadcast.new_message_ready");
        sendBroadcast(intent1);
    }

    @Override
    public void onClick(View v) {
        EditText editText = (EditText) findViewById(R.id.chat_text);
        String str = editText.getText().toString();
        editText.getText().clear();

        Intent intent = new Intent("sasasu.github.io.sim.broadcast.send");
        intent.putExtra("ID", 3);
        intent.putExtra("From", ID);
        intent.putExtra("To", FriendID);
        intent.putExtra("str", str);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(chatLogReceiver);
        super.onDestroy();
    }

    class ChatLogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<Chat> data = DataSupport.where("From = ? or To = ?", String.valueOf(FriendID), String.valueOf(FriendID)).find(Chat.class);
            if (data != null) {
                ChatDataAdapter chatDataAdapter = new ChatDataAdapter(ChatActivity.this, R.layout.chat_data, data);
                ListView listView = (android.widget.ListView) findViewById(R.id.chat_list_view);
                listView.setAdapter(chatDataAdapter);
            }
        }
    }
}

class ChatDataAdapter extends ArrayAdapter<Chat> {
    private static final String TAG = "ChatDataAdapter";
    private int resourceID;

    ChatDataAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Chat> objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d(TAG, "getView: ");
        Chat chat = getItem(position);
        @SuppressLint("ViewHolder") View view
                = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
        TextView from = (TextView) view.findViewById(R.id.chat_from);
        TextView sql = (TextView) view.findViewById(R.id.chat_data);
        sql.setText(chat.getText());
        from.setText(String.valueOf(chat.getFrom()));
        return view;
    }
}