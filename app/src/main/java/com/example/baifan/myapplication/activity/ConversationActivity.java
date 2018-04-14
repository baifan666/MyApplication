package com.example.baifan.myapplication.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.TypingMessage.TypingStatus;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

public class ConversationActivity extends FragmentActivity {
    private TextView title;
    private ImageView back;
    private String mTargetId; //目标 Id
    private Conversation.ConversationType mConversationType; //会话类型
    private final int SET_TEXT_TYPING_TITLE = 1;
    private final int SET_VOICE_TYPING_TITLE = 2;
    private final int SET_TARGETID_TITLE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        back = (ImageView) findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title = (TextView)findViewById(R.id.title);
        //会话界面 对方id
        mTargetId = getIntent().getData().getQueryParameter("targetId");
        mConversationType = Conversation.ConversationType.valueOf(getIntent().getData().getLastPathSegment().toUpperCase(Locale.US));

        if(!"SYSTEM".equals(mTargetId)) {
            title.setText("与" + mTargetId + "聊天中");
        }else {
            title.setText("系统消息");
        }

        RongIMClient.setTypingStatusListener(new RongIMClient.TypingStatusListener() {
            @Override
            public void onTypingStatusChanged(Conversation.ConversationType type, String targetId, Collection<TypingStatus> typingStatusSet) {
                //当输入状态的会话类型和targetID与当前会话一致时，才需要显示
                if (type.equals(mConversationType) && targetId.equals(mTargetId)) {
                    //count表示当前会话中正在输入的用户数量，目前只支持单聊，所以判断大于0就可以给予显示了
                    int count = typingStatusSet.size();
                    if (count > 0) {
                        Iterator iterator = typingStatusSet.iterator();
                        TypingStatus status = (TypingStatus) iterator.next();
                        String objectName = status.getTypingContentType();

                        MessageTag textTag = TextMessage.class.getAnnotation(MessageTag.class);
                        MessageTag voiceTag = VoiceMessage.class.getAnnotation(MessageTag.class);
                        //匹配对方正在输入的是文本消息还是语音消息
                        if (objectName.equals(textTag.value())) {
                            //显示“对方正在输入”
                            handler.sendEmptyMessage(SET_TEXT_TYPING_TITLE);
                        } else if (objectName.equals(voiceTag.value())) {
                            //显示"对方正在讲话"
                            handler.sendEmptyMessage(SET_VOICE_TYPING_TITLE);
                        }
                    } else {
                        //当前会话没有用户正在输入，标题栏仍显示原来标题
                        handler.sendEmptyMessage(SET_TARGETID_TITLE);
                    }
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_TEXT_TYPING_TITLE:
                    title.setText("对方正在输入");
                    break;
                case SET_VOICE_TYPING_TITLE:
                    title.setText("对方正在讲话");
                    break;
                case SET_TARGETID_TITLE:
                    title.setText("与" + mTargetId + "聊天中");
                    break;
                default:
                    break;
            }
        }
    };
}
