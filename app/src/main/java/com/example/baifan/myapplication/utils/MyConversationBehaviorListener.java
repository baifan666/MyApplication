package com.example.baifan.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;


import com.example.baifan.myapplication.activity.AccountManagementActivity;
import com.example.baifan.myapplication.activity.SearchActivity;
import com.example.baifan.myapplication.activity.UserActivity;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * Created by baifan on 2018/4/12.
 */

public class MyConversationBehaviorListener implements RongIM.ConversationClickListener {
    /**
     * 当点击用户头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param userInfo         被点击的用户的信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
        String imid = userInfo.getUserId();
        // if (Integer.parseInt(imid.substring(2)) != ((TeacherApp) context).getTeacher().id) {
        Toast.makeText(context,imid,Toast.LENGTH_LONG).show();
        Intent intent=new Intent();
        intent.setClass(context, UserActivity.class);
        intent.putExtra("username",userInfo.getUserId()); // 向下一个界面传递信息
        intent.putExtra("headurl",userInfo.getPortraitUri()); // 向下一个界面传递信息
        context.startActivity(intent);
        // }
        return true;
    }

    /**
     * 当长按用户头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param userInfo         被点击的用户的信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
        return false;
    }

    /**
     * 当点击消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被点击的消息的实体信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        return false;
    }

    /**
     * 当点击链接消息时执行。
     *
     * @param context 上下文。
     * @param s    被点击的链接。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLinkClick(Context context, String s, Message message) {
        return false;
    }

    /**
     * 当长按消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被长按的消息的实体信息。
     * @return 如果用户自己处理了长按后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }


}