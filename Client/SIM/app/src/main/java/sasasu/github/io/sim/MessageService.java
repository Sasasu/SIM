package sasasu.github.io.sim;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageService extends Service {
    private static final String ip = "192.168.0.16";
    private static final String TAG = "MessageService";
    private static Socket socket;
    private static int ID;
    private MainResultReceiver mainresultreceiver;

    public MessageService() {
    }

    public static int getID() {
        return ID;
    }

    public static void setID(int ID) {
        MessageService.ID = ID;
    }

    public static String getIp() {
        return ip;
    }

    static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    static int getInt(BufferedInputStream bufferedInputStream) throws IOException {
        byte[] b = new byte[4];
        bufferedInputStream.read(b, 0, 4);
        return ByteBuffer.wrap(b).getInt();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) throws IOException {
        if (MessageService.socket != null && MessageService.socket.isConnected()) {
            MessageService.socket.close();
            MessageService.socket = null;
        }
        MessageService.socket = socket;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: " + TAG);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("sasasu.github.io.sim.broadcast.send");
        mainresultreceiver = new MainResultReceiver();
        registerReceiver(mainresultreceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (socket != null)
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        unregisterReceiver(mainresultreceiver);
        super.onDestroy();
    }


    class getFriendThread extends Thread {
        final static int SUCCESS = 3;
        final static int DEFEAT = 4;
        private static final String TAG = "getFriendThread";
        private int UserID;

        getFriendThread(int UserID) {
            Log.d(TAG, "getFriendThread: get User ID = " + UserID);
            this.UserID = UserID;
        }

        @Override
        public void run() {
            ArrayList<UserFriend> array = null;
            try {
                Socket socket = getSocket();
                OutputStream outputStream = socket.getOutputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());

                outputStream.write(getBytes(1)); // getFriend
                outputStream.write(getBytes(4)); // sizeof int
                outputStream.write(getBytes(UserID));
                /*
                 *  int32_t type = 1;
                 *  writedate(intToBytes(type));
                 *  int32_t length = data.length();
                 *  writedate(intToBytes(length));
                 *  for(UserFriend &i:data){
                 *      writedate(intToBytes(i.UserID));
                 *      writedate(intToBytes(i.FriendID));
                 *  }
                 */
                int type = getInt(bufferedInputStream);
                int length = getInt(bufferedInputStream);

                Log.d(TAG, "run: type" + type);
                Log.d(TAG, "run: length" + length);
                array = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    UserFriend userfriend = new UserFriend(
                            getInt(bufferedInputStream),
                            getInt(bufferedInputStream)
                    );
                    array.add(userfriend);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent("sasasu.github.io.sim.broadcast.get_friend_result");
            intent.putExtra("ID", SUCCESS);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", array);
            intent.putExtras(bundle);
            sendBroadcast(intent);

            MessageReadThread messageReadThread = new MessageReadThread();
            messageReadThread.start();
        }
    }

    private class MessageReadThread extends Thread {
        @Override
        public void run() {
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                for (; ; ) {
                    try {
                        if (bufferedInputStream.available() >= 4) {
                            int type = getInt(bufferedInputStream); // writedate(intToBytes(type));
                            int from = getInt(bufferedInputStream); // writedate(intToBytes(from));
                            int to = getInt(bufferedInputStream);   // writedate(intToBytes(to));
                            int len = getInt(bufferedInputStream); // writedate(intToBytes(str.length()));
                            byte[] str = new byte[len];
                            bufferedInputStream.read(str, 0, len); // writedate(str.toLatin1());
                            String data = new String(str, "utf-8");
                            Chat chat = new Chat();
                            chat.setTo(to);
                            chat.setFrom(from);
                            chat.setText(data);
                            chat.save();
                            Intent intent = new Intent("sasasu.github.io.sim.broadcast.new_message_ready");
                            sendBroadcast(intent);
                        }
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MessageWriteThread extends Thread {
        int From;
        int To;
        String str;

        MessageWriteThread(int from, int to, String str) {
            From = from;
            To = to;
            this.str = str;
            Chat chat = new Chat();
            chat.setFrom(from);
            chat.setTo(to);
            chat.setText(str);
            chat.save();
            Intent intent = new Intent("sasasu.github.io.sim.broadcast.new_message_ready");
            sendBroadcast(intent);
        }

        @Override
        public void run() {
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(getBytes(2)); // message type
                outputStream.write(getBytes(4)); // int fromsize = bytesToInt(this->readdata(sizeof(int32_t)));
                outputStream.write(getBytes(From)); // int from = bytesToInt(this->readdata(fromsize));
                outputStream.write(getBytes(4)); // int tosize = bytesToInt(this->readdata(sizeof(int32_t)));
                outputStream.write(getBytes(To)); // int to = bytesToInt(this->readdata(tosize));
                outputStream.write(getBytes(str.getBytes().length)); // int strsize = bytesToInt(this->readdata(sizeof(int32_t)));
                outputStream.write(str.getBytes()); // QString str = this->readdata(strsize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class LoginThread extends Thread {
        final static int DEFEAT = 0;
        final static int SUCCESS = 1;
        final static int NETERROR = 2;
        private static final String TAG = "LoginThread";
        private String username;
        private String password;

        LoginThread(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            Map<String, Object> ans = new HashMap<>();
            int ID = -1;
            try {
                setSocket(new Socket(ip, 8192));
                Socket socket = getSocket();
                OutputStream outputStream = socket.getOutputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());

                outputStream.write(getBytes(0));

                outputStream.write(getBytes(username.length()));
                outputStream.write(username.getBytes());

                outputStream.write(getBytes(password.length()));
                outputStream.write(password.getBytes());

                int type = getInt(bufferedInputStream); // == 0
                ID = getInt(bufferedInputStream);

                Log.d(TAG, "run: reserve " + type);
                Log.d(TAG, "run: reserve " + ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent("sasasu.github.io.sim.broadcast.login_result");
            intent.putExtra("what", (ID == -1 ? DEFEAT : SUCCESS));
            Log.d(TAG, "run: login get ID = " + ID);
            intent.putExtra("ID", ID);
            sendBroadcast(intent);
        }
    }

    class MainResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int ID = intent.getIntExtra("ID", -1);
            switch (ID) {
                case -1:
                    stopSelf();
                    break;
                case 1:
                    String username = intent.getStringExtra("username");
                    String password = intent.getStringExtra("password");
                    LoginThread login = new LoginThread(username, password);
                    login.start();
                    break;
                case 2:
                    int UserID = intent.getIntExtra("UserID", -1);
                    getFriendThread getfriend = new getFriendThread(UserID);
                    getfriend.start();
                    break;
                case 3:
                    int From = intent.getIntExtra("From", -1);
                    int To = intent.getIntExtra("To", -1);
                    String str = intent.getStringExtra("str");
                    MessageWriteThread messageWriteThread = new MessageWriteThread(From, To, str);
                    messageWriteThread.start();
                    break;
                default:
                    break;
            }
        }
    }
}
