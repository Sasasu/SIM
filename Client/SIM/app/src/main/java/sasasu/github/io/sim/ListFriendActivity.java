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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListFriendActivity extends AppCompatActivity {
    private static final String TAG = "ListFriendActivity";
    private GetFriendReceiver getFriendReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friend);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("sasasu.github.io.sim.broadcast.get_friend_result");
        getFriendReceiver = new GetFriendReceiver();
        registerReceiver(getFriendReceiver, intentFilter);

        Intent intent = new Intent("sasasu.github.io.sim.broadcast.send");
        int UserID = MessageService.getID();
        intent.putExtra("ID", 2);
        intent.putExtra("UserID", UserID);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(getFriendReceiver);
    }

    class GetFriendReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int what = intent.getIntExtra("ID", -1);
            switch (what) {
                case MessageService.getFriendThread.SUCCESS:
                    ArrayList<UserFriend> data = (ArrayList<UserFriend>) intent.getExtras().getSerializable("data");
                    if (data != null) {
                        for (int i = 0; i < data.size(); ++i) {
                            Log.d(TAG, "onReceive: " + data.get(i).getFriendID());
                            Log.d(TAG, "onReceive: " + data.get(i).getUserID());
                        }
                        UserFriendAdapter adapter = new UserFriendAdapter(
                                ListFriendActivity.this,
                                R.layout.user_friend,
                                data
                        );
                        ListView listView = (android.widget.ListView) findViewById(R.id.ListFriendListView);
                        listView.setAdapter(adapter);
                    }
                    Toast.makeText(getApplicationContext(), "获取好友列表成功", Toast.LENGTH_LONG).show();
                    break;
                case MessageService.getFriendThread.DEFEAT:
                    Toast.makeText(getApplicationContext(), "获取好友列表失败", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }
}

class UserFriendAdapter extends ArrayAdapter<UserFriend> {

    private int resourceID;

    UserFriendAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<UserFriend> objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserFriend userFriend = getItem(position);
        @SuppressLint("ViewHolder") View view =
                LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
        TextView userID = (TextView) view.findViewById(R.id.user_id);
        final TextView friendID = (TextView) view.findViewById(R.id.user_friend_id);
        Button StartChat = (Button) view.findViewById(R.id.start_chat);
        final int ID = userFriend.getUserID();
        final int FriendID = userFriend.getFriendID();
        friendID.setText(Integer.toString(FriendID));
        userID.setText(Integer.toString(ID));

        StartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ChatActivity.class);
                intent.putExtra("ID", ID);
                intent.putExtra("FriendID", FriendID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        return view;
    }
}