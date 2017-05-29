package sasasu.github.io.sim;

import java.io.Serializable;

/**
 * Created by li on 17-5-28.
 */

public class UserFriend  implements Serializable {
    private int UserID;
    private int FriendID;

    public UserFriend(int userID, int friendID) {
        UserID = userID;
        FriendID = friendID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getFriendID() {
        return FriendID;
    }

    public void setFriendID(int friendID) {
        FriendID = friendID;
    }
}
