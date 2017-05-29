package sasasu.github.io.sim;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by li on 17-5-28.
 */

public class Chat extends DataSupport implements Serializable {
    private String text;
    private int From;
    private int To;

    public Chat() {
    }

    public Chat(String text, int from, int to) {
        this.text = text;
        From = from;
        To = to;
    }


    public int getTo() {
        return To;
    }

    public void setTo(int to) {
        To = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFrom() {
        return From;
    }

    public void setFrom(int from) {
        From = from;
    }
}
